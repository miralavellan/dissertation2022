package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.*;
import static ro.ase.csie.mydissertation.utilities.Utilities.hideKeyboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ro.ase.csie.mydissertation.adapters.AccountAdapter;
import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class ViewAccountsActivity extends AppCompatActivity {

    private ListView lvAccounts;
    private FloatingActionButton btnAddAccount;

    private AccountAdapter mAdapter;

    private DatabaseInstance db;
    private AccountDao accountDao;
    private AccountService accountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accounts);

        setTitle("Accounts");

        lvAccounts = findViewById(R.id.lvAccounts);
        lvAccounts.setEmptyView(findViewById(R.id.list_view_empty_list));
        btnAddAccount = findViewById(R.id.btnAddAccount);

        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAccountActivity();
            }
        });

        db = DatabaseInstance.getInstance(getApplicationContext());
        accountDao = db.getAccountDao();
        accountService = new AccountService(accountDao);

        mAdapter = new AccountAdapter(this, accountService.getAccounts(), R.layout.list_view_adapter_account);
        lvAccounts.setAdapter(mAdapter);

        lvAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewAccountActivity(position);
            }
        });

        lvAccounts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getAccountPassword(position);
                return true;
            }
        });
    }

    private void getAccountPassword(int position) {
        ByteAccount acc = (ByteAccount) lvAccounts.getItemAtPosition(position);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        clip = ClipData.newPlainText("pw", CryptoUtilities.decryptAES(this, acc.getAccAESPass()));
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Password for " + acc.getAccHost() + " copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    private void openAddAccountActivity() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivityForResult(intent, ADD_ACC);
    }

    private void openViewAccountActivity(int position) {
        Intent intent = new Intent(this, ViewAccountActivity.class);
        intent.putExtra("account id",
                ((ByteAccount) lvAccounts.getItemAtPosition(position)).getAccID());
        startActivityForResult(intent, VIEW_ACC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_ACC && resultCode == RESULT_OK) {
            mAdapter.updateList();
            setResult(RESULT_ADDED);
        }

        if(requestCode == VIEW_ACC && resultCode == RESULT_EDITED){
            mAdapter.updateList();
        }

        if(requestCode == VIEW_ACC && resultCode == RESULT_DELETED){
            mAdapter.updateList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_accounts_dots, menu);

        MenuItem menuItem = menu.findItem(R.id.menuItemSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setIconified(false);
        searchView.setQueryHint("search");

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                hideKeyboard(ViewAccountsActivity.this);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.updateList(accountService.filterByHostOrUser(newText));
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuItemSortAccountsDefault:
                mAdapter.sortListDefault();
                return true;
            case R.id.menuItemSortAccountsAlphabetically:
                mAdapter.sortListAlphabetically();
                return true;
            case R.id.menuItemSortAccountsByDate:
                mAdapter.sortListByDate();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
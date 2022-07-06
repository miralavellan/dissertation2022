package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.*;
import static ro.ase.csie.mydissertation.utilities.Utilities.hideKeyboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ro.ase.csie.mydissertation.adapters.CardAdapter;
import ro.ase.csie.mydissertation.database.CardDao;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteCard;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class ViewCardsActivity extends AppCompatActivity {

    private ListView lvCards;
    private FloatingActionButton btnAddCard;

    private CardAdapter mAdapter;

    private DatabaseInstance db;
    private CardDao cardDao;
    private CardService cardService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cards);

        setTitle("Cards");

        lvCards = findViewById(R.id.lvCards);
        lvCards.setEmptyView(findViewById(R.id.list_view_empty_list));
        btnAddCard = findViewById(R.id.btnAddCard);

        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCardActivity();
            }
        });

        db = DatabaseInstance.getInstance(getApplicationContext());
        cardDao = db.getCardDao();
        cardService = new CardService(cardDao);

        mAdapter = new CardAdapter(this, cardService.getCards(), R.layout.list_view_adapter_account);
        lvCards.setAdapter(mAdapter);
        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewCardActivity(position);
            }
        });

        lvCards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getCardNumber(position);
                return true;
            }
        });
    }

    private void getCardNumber(int position) {
        ByteCard card = (ByteCard) lvCards.getItemAtPosition(position);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        clip = ClipData.newPlainText("pw", CryptoUtilities.decryptAES(this, card.getCardNumber()));
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Card number for " + card.getCardHost() + " copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    private void openAddCardActivity() {
        Intent intent = new Intent(this, AddCardActivity.class);
        startActivityForResult(intent, ADD_CARD);
    }

    private void openViewCardActivity(int position) {
        Intent intent = new Intent(this, ViewCardActivity.class);
        intent.putExtra("card id",
                ((ByteCard) lvCards.getItemAtPosition(position)).getCardID());
        startActivityForResult(intent, VIEW_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_CARD && resultCode == RESULT_OK) {
            mAdapter.updateList();
            setResult(RESULT_ADDED);
        }

        if(requestCode == VIEW_CARD && resultCode == RESULT_EDITED){
            mAdapter.updateList();
        }

        if(requestCode == VIEW_CARD && resultCode == RESULT_DELETED){
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
                hideKeyboard(ViewCardsActivity.this);
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
                mAdapter.updateList(cardService.filterByHostOrName(newText));
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
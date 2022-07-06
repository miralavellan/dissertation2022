package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.ADD_ACC;
import static ro.ase.csie.mydissertation.Constants.ADD_CARD;
import static ro.ase.csie.mydissertation.Constants.COUNT_ACCOUNTS;
import static ro.ase.csie.mydissertation.Constants.COUNT_ALL;
import static ro.ase.csie.mydissertation.Constants.COUNT_CARDS;
import static ro.ase.csie.mydissertation.Constants.RESULT_ADDED;
import static ro.ase.csie.mydissertation.Constants.RESULT_DELETED;
import static ro.ase.csie.mydissertation.Constants.RESULT_EDITED;
import static ro.ase.csie.mydissertation.Constants.VIEW_ACC;
import static ro.ase.csie.mydissertation.Constants.VIEW_CARD;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ro.ase.csie.mydissertation.adapters.AccountAdapter;
import ro.ase.csie.mydissertation.adapters.CardAdapter;
import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.CardDao;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.database.FolderDao;
import ro.ase.csie.mydissertation.database.FolderService;
import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteCard;
import ro.ase.csie.mydissertation.models.ByteFolder;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class ViewFolderActivity extends AppCompatActivity {

    private ListView lvFolderAccounts;
    private ListView lvFolderCards;
    private FloatingActionButton btnAddAccountOrCard;

    private TextView tvFolderAccounts;
    private TextView tvFolderCards;

    private AccountAdapter mAdapterAcc;
    private CardAdapter mAdapterCard;

    private int folderID;
    private ByteFolder currentFolder;

    private DatabaseInstance db;
    private AccountDao accountDao;
    private AccountService accountService;
    private CardDao cardDao;
    private CardService cardService;
    private FolderDao folderDao;
    private FolderService folderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_folder);

        lvFolderAccounts = findViewById(R.id.lvFolderAccounts);
        lvFolderCards = findViewById(R.id.lvFolderCards);
        tvFolderAccounts = findViewById(R.id.tvFolderAccounts);
        tvFolderCards = findViewById(R.id.tvFolderCards);

        View emptyLayout = findViewById(R.id.list_view_empty_list);

        btnAddAccountOrCard = findViewById(R.id.btnAddAccountOrCard);

        if(getIntent().getSerializableExtra("folder id") != null) {
            folderID = (int) getIntent().getSerializableExtra("folder id");

            db = DatabaseInstance.getInstance(getApplicationContext());
            folderDao = db.getFolderDao();
            folderService = new FolderService(folderDao);

            currentFolder = folderService.getFolder(folderID);
            setTitle(folderService.getFolder(folderID).getFolderName());

            accountDao = db.getAccountDao();
            accountService = new AccountService(accountDao);
            cardDao = db.getCardDao();
            cardService = new CardService(cardDao);

            if(currentFolder.getFolderEntriesCount(this, COUNT_ACCOUNTS) != 0){
                tvFolderAccounts.setVisibility(View.VISIBLE);
            }

            if(currentFolder.getFolderEntriesCount(this, COUNT_CARDS) == 0){
                mAdapterAcc = new AccountAdapter(this, accountService.getAccountsFromFolder(folderID), R.layout.list_view_adapter_account);
                LinearLayout layout = findViewById(R.id.layoutFolderAccounts);
                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                lvFolderCards.setVisibility(View.GONE);
            } else {
                mAdapterAcc = new AccountAdapter(this, accountService.getAccountsFromFolder(folderID), R.layout.list_view_folder_adapter_account);
            }

            lvFolderAccounts.setAdapter(mAdapterAcc);

            lvFolderAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openViewAccountActivity(position);
                }
            });
            lvFolderAccounts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    getAccountPassword(position);
                    return true;
                }
            });

            if(currentFolder.getFolderEntriesCount(this, COUNT_CARDS) != 0){
                tvFolderCards.setVisibility(View.VISIBLE);
            }

            if(currentFolder.getFolderEntriesCount(this, COUNT_ACCOUNTS) == 0){
                mAdapterCard = new CardAdapter(this, cardService.getCardsFromFolder(folderID), R.layout.list_view_adapter_account);
                LinearLayout layout = findViewById(R.id.layoutFolderAccounts);
                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 2));

                LinearLayout layout2 = findViewById(R.id.layoutFolderCards);
                layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            } else {
                mAdapterCard = new CardAdapter(this, cardService.getCardsFromFolder(folderID), R.layout.list_view_folder_adapter_account);
            }

            lvFolderCards.setAdapter(mAdapterCard);
            lvFolderCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openViewCardActivity(position);
                }
            });
            lvFolderCards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    getCardNumber(position);
                    return true;
                }
            });

            if(currentFolder.getFolderEntriesCount(this, COUNT_ALL) == 0){
                emptyLayout.setVisibility(View.VISIBLE);
            }

        }

        btnAddAccountOrCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ViewFolderActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.acc_or_card, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menuItemAccount:
                                openAddAccountActivity();
                                return true;
                            case R.id.menuItemCard:
                                openAddCardActivity();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void getAccountPassword(int position) {
        ByteAccount acc = (ByteAccount) lvFolderAccounts.getItemAtPosition(position);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        clip = ClipData.newPlainText("pw", CryptoUtilities.decryptAES(this, acc.getAccAESPass()));
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Password for " + acc.getAccHost() + " copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    private void getCardNumber(int position) {
        ByteCard card = (ByteCard) lvFolderCards.getItemAtPosition(position);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        clip = ClipData.newPlainText("pw", CryptoUtilities.decryptAES(this, card.getCardNumber()));
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Card number for " + card.getCardHost() + " copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    private void openAddAccountActivity() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        intent.putExtra("folder id", folderID);
        startActivityForResult(intent, ADD_ACC);
    }

    private void openAddCardActivity() {
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra("folder id", folderID);
        startActivityForResult(intent, ADD_CARD);
    }

    private void openViewAccountActivity(int position) {
        Intent intent = new Intent(this, ViewAccountActivity.class);
        intent.putExtra("account id",
                ((ByteAccount) lvFolderAccounts.getItemAtPosition(position)).getAccID());
        startActivityForResult(intent, VIEW_ACC);
    }

    private void openViewCardActivity(int position) {
        Intent intent = new Intent(this, ViewCardActivity.class);
        intent.putExtra("card id",
                ((ByteCard) lvFolderCards.getItemAtPosition(position)).getCardID());
        startActivityForResult(intent, VIEW_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_ACC && resultCode == RESULT_OK){
            mAdapterAcc.updateListFolder(folderID);
            setResult(RESULT_ADDED);
        }

        if(requestCode == VIEW_ACC && resultCode == RESULT_EDITED){
            mAdapterAcc.updateListFolder(folderID);
        }

        if(requestCode == VIEW_ACC && resultCode == RESULT_DELETED){
            mAdapterAcc.updateListFolder(folderID);
        }

        if(requestCode == ADD_CARD && resultCode == RESULT_OK){
            mAdapterCard.updateListFolder(folderID);
            setResult(RESULT_ADDED);
        }

        if(requestCode == VIEW_CARD && resultCode == RESULT_EDITED){
            mAdapterCard.updateListFolder(folderID);
        }

        if(requestCode == VIEW_CARD && resultCode == RESULT_DELETED){
            mAdapterCard.updateListFolder(folderID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.view_folder_dots, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuItemEditFolder:
                openEditFolderDialog();
                return true;
            case R.id.menuItemDeleteFolder:
                openDeleteFolderDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDeleteFolderDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        for(ByteAccount account : accountService.getAccountsFromFolder(folderID)) {
                            // todo e hardcodat
                            account.setFolderID(1);
                            accountService.updateAccount(account);
                        }

                        for(ByteCard card : cardService.getCardsFromFolder(folderID)){
                            card.setFolderID(1);
                            cardService.updateCard(card);
                        }

                        folderService.deleteFolder(currentFolder);
                        setResult(RESULT_DELETED);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void openEditFolderDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit folder");
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        input.setText(currentFolder.getFolderName());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(folderService.getFolderByName(input.getText().toString()) != null){
                    if(folderService.getFolderByName(input.getText().toString()).getFolderID() != folderID){
                        Toast.makeText(getApplicationContext(), "Folder already exists!", Toast.LENGTH_SHORT).show();
                    } else{
                        // nothing changed
                    }
                } else{
                    currentFolder.setFolderName(input.getText().toString());
                    folderService.updateFolder(currentFolder);
                    setTitle(folderService.getFolder(folderID).getFolderName());

                    setResult(RESULT_EDITED);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
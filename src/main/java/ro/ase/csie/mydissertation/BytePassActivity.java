package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ro.ase.csie.mydissertation.adapters.CategoryAdapter;
import ro.ase.csie.mydissertation.adapters.FolderAdapter;
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


public class BytePassActivity extends AppCompatActivity {

    private ListView lvCategories;
    private ListView lvFolders;

    private CategoryAdapter mAdapterCategory;
    private FolderAdapter mAdapter;

    DatabaseInstance db;
    FolderDao folderDao;
    FolderService folderService;

    Uri importedUri;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_byte_pass);

        lvCategories = findViewById(R.id.lvCategories);
        lvFolders = findViewById(R.id.lvFolders);

        mAdapterCategory = new CategoryAdapter(this);
        lvCategories.setAdapter(mAdapterCategory);

        lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    openViewAccountsActivity();
                } else
                    if(position == 1){
                        openViewCardsActivity();
                    }
            }
        });

        db = DatabaseInstance.getInstance(this);
        folderDao = db.getFolderDao();
        folderService = new FolderService(folderDao);

        mAdapter = new FolderAdapter(this, folderService.getFolders());
        lvFolders.setAdapter(mAdapter);

        lvFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewFolderActivity(position);
            }
        });

    }

    private void openViewFolderActivity(int position) {
        Intent intent = new Intent(this, ViewFolderActivity.class);
        intent.putExtra("folder id", ((ByteFolder) lvFolders.getItemAtPosition(position)).getFolderID());
        startActivityForResult(intent, VIEW_FOLDER);
    }

    private void openViewAccountsActivity() {
        Intent intent = new Intent(this, ViewAccountsActivity.class);
        startActivityForResult(intent, VIEW_ACCS);
    }

    private void openViewCardsActivity() {
        Intent intent = new Intent(this, ViewCardsActivity.class);
        startActivityForResult(intent, VIEW_CARDS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.byte_pass_dots, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuItemAddFolder:
                openAddFolderDialog();
                return true;
            case R.id.menuItemExportDatabase:
                shareDb();
                return true;
                //todo change this; only for testing
            case R.id.menuItemImportDatabase:
                importDatabase();
                return true;
            case R.id.menuItemChangeUsername:
                changeUsername();
                return true;
            case R.id.menuItemChangePassword:
                changePassword();
                return true;
            case R.id.menuItemChangeBiometrics:
                changeBiometrics();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeBiometrics() {
        sp = this.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(sp.getBoolean(SHARED_PREFS_1_BIOMETRICS, false)){
            editor.putBoolean(SHARED_PREFS_1_BIOMETRICS, false);
            Toast.makeText(this, "Biometrics turned off.", Toast.LENGTH_SHORT).show();
        } else{
            editor.putBoolean(SHARED_PREFS_1_BIOMETRICS, true);
            Toast.makeText(this, "Biometrics turned on.", Toast.LENGTH_SHORT).show();
        }

        editor.apply();
    }

    private void updateMenuTitle(){
        MenuItem changeBiometrics = findViewById(R.id.menuItemChangeBiometrics);
        sp = this.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
        if(sp.getBoolean(SHARED_PREFS_1_BIOMETRICS, false)){
            changeBiometrics.setTitle("Turn biometrics off");
        } else{
            changeBiometrics.setTitle("Turn biometrics on");
        }
    }

    public void changeUsername(){

        sp = this.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change username");
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(sp.getString(SHARED_PREFS_1_USER, "default"));
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if("".equals(input.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            "Please provide a username. ex: LeiaOrgana77",
                            Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(SHARED_PREFS_1_USER, input.getText().toString());
                    editor.apply();
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

    public void changePassword(){

        sp = this.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change password");

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(input);

        EditText input2 = new EditText(this);
        input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(input2);

        EditText input3 = new EditText(this);
        input3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        input3.setHint("new password hint");
        layout.addView(input3);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if("".equals(input.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            "Please provide a password.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if("".equals(input2.getText().toString())){
                        Toast.makeText(getApplicationContext(),
                                "Please retype the password.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sp.edit();

                        if(!"".equals(input3.getText().toString())){
                            editor.putString(SHARED_PREFS_1_HINT, input3.getText().toString());
                        }

                        byte[] salt = CryptoUtilities.getRandomString().getBytes(StandardCharsets.UTF_8);

                        byte[] digest = CryptoUtilities.hashSHA256Salt(input.getText().toString()
                                , salt
                                , getApplicationContext()
                                , SHARED_PREFS_1
                                , SHARED_PREFS_1_SALT);

                        if(digest != null){
                            editor.putString(SHARED_PREFS_1_PASSWORD, new String(digest));
                            editor.apply();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
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

    public void shareDb(){
        db = DatabaseInstance.getInstance(this);

        FolderService folderService = new FolderService(db.getFolderDao());
        AccountService accountService = new AccountService(db.getAccountDao());
        CardService cardService = new CardService(db.getCardDao());

        List<ByteFolder> byteFolders = folderService.getFolders();
        List<ByteAccount> byteAccounts = accountService.getAccounts();
        List<ByteCard> byteCards = cardService.getCards();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(byteFolders.toString());
        stringBuilder.append("*****");
        stringBuilder.append(byteAccounts.toString());
        stringBuilder.append("*****");
        stringBuilder.append(byteCards.toString());

        stringBuilder.deleteCharAt(stringBuilder.indexOf("["));
        stringBuilder.deleteCharAt(stringBuilder.indexOf("]"));
        stringBuilder.deleteCharAt(stringBuilder.indexOf("["));
        stringBuilder.deleteCharAt(stringBuilder.indexOf("]"));
        stringBuilder.deleteCharAt(stringBuilder.indexOf("["));
        stringBuilder.deleteCharAt(stringBuilder.indexOf("]"));

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "share");
        startActivity(shareIntent);
    }

    public void importDb(String data){
        db = DatabaseInstance.getInstance(this);

        FolderService folderService = new FolderService(db.getFolderDao());
        AccountService accountService = new AccountService(db.getAccountDao());
        CardService cardService = new CardService(db.getCardDao());

        StringTokenizer tokenizer = new StringTokenizer(data, "*****");
        String foldersList = tokenizer.nextToken();
        String accountsList = tokenizer.nextToken();
        String cardsList = tokenizer.nextToken();

        String[] folders = foldersList.split(", ");
        String[] accounts = accountsList.split(", ");
        String[] cards = cardsList.split(", ");

        List<ByteFolder> byteFolders = new ArrayList<ByteFolder>();
        ArrayList<ByteAccount> byteAccounts = new ArrayList<ByteAccount>();
        ArrayList<ByteCard> byteCards = new ArrayList<ByteCard>();

        //folders
        for(String folder : folders){
            String[] details = folder.split("%#%#%");

            ByteFolder byteFolder = new ByteFolder();
            // todo hardcoded
            byteFolder.setFolderID(Integer.parseInt(details[0].trim()));
            byteFolder.setFolderName(details[1]);

            byteFolders.add(byteFolder);
        }

        //accounts
        for(String account : accounts){
            String[] details = account.split("%#%#%");

            ByteAccount byteAccount = new ByteAccount();
            byteAccount.setAccHost(details[1]);
            byteAccount.setAccUser(details[2]);
            byteAccount.setAccAESPass(details[3]);
            byteAccount.setAccUnixTime(Long.parseLong(details[4]));
            byteAccount.setFolderID(Integer.parseInt(details[5]));

            byteAccounts.add(byteAccount);
        }

        for(String card : cards){
            String[] details = card.split("%#%#%");

            ByteCard byteCard = new ByteCard();
            byteCard.setCardHost(details[1]);
            byteCard.setCardName(details[2]);
            byteCard.setCardAESPIN(details[3]);
            byteCard.setCardNumber(details[4]);
            byteCard.setCardValidThru(details[5]);
            byteCard.setCardCVV(details[6]);
            byteCard.setCardUnixTime(Long.parseLong(details[7]));
            byteCard.setFolderID(Integer.parseInt(details[8]));

            byteCards.add(byteCard);
        }

        for(ByteFolder folder : byteFolders){

            ByteFolder existingFolder = folderService.getFolderByName(folder.getFolderName());

            if(existingFolder != null){
                Log.d("FIND_ME", "EXISTING FOLDER: " + existingFolder.getFolderName());

                // case 1: same id, same name
                if(existingFolder.getFolderID() == folder.getFolderID()){
                    for(ByteAccount account : byteAccounts){
                        if(account.getFolderID() == folder.getFolderID()){
                            accountService.insertAccount(account);
                        }
                    }
                    for(ByteCard card : byteCards){
                        if(card.getFolderID() == folder.getFolderID()){
                            cardService.insertCard(card);
                        }
                    }
                    // case 2: different id, same name
                } else {
                    for(ByteAccount account : byteAccounts){
                        if(account.getFolderID() == folder.getFolderID()){
                            account.setFolderID(existingFolder.getFolderID());
                            accountService.insertAccount(account);
                        }
                    }
                    for(ByteCard card : byteCards){
                        if(card.getFolderID() == folder.getFolderID()){
                            card.setFolderID(existingFolder.getFolderID());
                            cardService.insertCard(card);
                        }
                    }
                } // case 3: same id, different name
            } else {
                if(folderService.getFolder(folder.getFolderID()) != null){
                    folderService.updateFolder(folder);

                    for(ByteAccount account : byteAccounts){
                        if(account.getFolderID() == folder.getFolderID()){
                            accountService.insertAccount(account);
                        }
                    }
                    for(ByteCard card : byteCards){
                        if(card.getFolderID() == folder.getFolderID()){
                            cardService.insertCard(card);
                        }
                    }

                } else{ // case 4: different id, different name
                    folderService.insertFolder(folder);

                    for(ByteAccount account : byteAccounts){
                        if(account.getFolderID() == folder.getFolderID()){
                            accountService.insertAccount(account);
                        }
                    }
                    for(ByteCard card : byteCards){
                        if(card.getFolderID() == folder.getFolderID()){
                            cardService.insertCard(card);
                        }
                    }
                }
            }
        }

        mAdapter.updateList();
    }

    private void importDatabase() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");

        startActivityForResult(Intent.createChooser(intent, "Import your database:"), CHOOSE_DATABASE);
    }

    private void openAddFolderDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add folder");
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(folderService.getFolderByName(input.getText().toString()) != null){
                    Toast.makeText(getApplicationContext(), "Folder already exists!", Toast.LENGTH_SHORT).show();
                } else{
                    folderService.insertFolder(new ByteFolder(input.getText().toString()));
                    mAdapter.updateList();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == VIEW_ACCS && resultCode == RESULT_ADDED){
            mAdapterCategory.notifyDataSetChanged();
        }

        if(requestCode == VIEW_CARDS && resultCode == RESULT_ADDED){
            mAdapterCategory.notifyDataSetChanged();
        }

        if(requestCode == VIEW_FOLDER && resultCode == RESULT_ADDED){
            mAdapter.updateList();
        }

        if(requestCode == VIEW_FOLDER && resultCode == RESULT_EDITED) {
            mAdapter.updateList();
        }

        if(requestCode == VIEW_FOLDER && resultCode == RESULT_DELETED) {
            mAdapter.updateList();
        }

        if(requestCode == CHOOSE_DATABASE && resultCode == RESULT_OK){

            importedUri = data.getData();
            String fileContent = readTextFile(importedUri);
            importDb(fileContent);
        }
    }

    private String readTextFile(Uri uri){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
    }
}
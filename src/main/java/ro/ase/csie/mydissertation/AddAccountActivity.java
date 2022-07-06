package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.ACCOUNT_ID;
import static ro.ase.csie.mydissertation.Constants.HIDE_PASSWORD;
import static ro.ase.csie.mydissertation.Constants.SEE_PASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.database.FolderDao;
import ro.ase.csie.mydissertation.database.FolderService;
import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteFolder;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class AddAccountActivity extends AppCompatActivity {

    private EditText etAccHost;
    private EditText etAccUser;
    private EditText etAccPassword;
    private Spinner spAccFolder;
    private Button btnAddAccOK;
    private ImageButton btnSeeAddAccPassword;

    private DatabaseInstance db;

    private FolderDao folderDao;
    private FolderService folderService;
    private int folderID;

    private AccountDao accountDao;
    private AccountService accountService;

    private int accountID;
    private ByteAccount oldAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        etAccHost = findViewById(R.id.etAccHost);
        etAccUser = findViewById(R.id.etAccUser);
        etAccPassword = findViewById(R.id.etAccPassword);
        spAccFolder = findViewById(R.id.spAccFolder);
        btnAddAccOK = findViewById(R.id.btnAddAccOK);
        btnSeeAddAccPassword = findViewById(R.id.btnSeeAddAccPassword);

        db = DatabaseInstance.getInstance(this);
        folderDao = db.getFolderDao();
        folderService = new FolderService(folderDao);

        accountDao = db.getAccountDao();
        accountService = new AccountService(accountDao);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccFolder.setAdapter(spinnerAdapter);

        spinnerAdapter.addAll(folderService.getFoldersNames());
        spinnerAdapter.notifyDataSetChanged();

        if(getIntent().getSerializableExtra("folder id") != null){
            folderID = (int) getIntent().getSerializableExtra("folder id");
            // todo e hardcodat
            spAccFolder.setSelection(folderService.getFolder(folderID).getFolderID() - 1);
        }

        if(getIntent().getSerializableExtra(ACCOUNT_ID) != null){

            setTitle("Edit Account");

            accountID = (int) getIntent().getSerializableExtra(ACCOUNT_ID);
            oldAccount = accountService.getAccount(accountID);

            etAccHost.setText(oldAccount.getAccHost());
            etAccUser.setText(oldAccount.getAccUser());
            // todo
            etAccPassword.setText(oldAccount.getAccAESPass());

            etAccPassword.setFocusable(false);
            etAccPassword.setFocusableInTouchMode(false);

            // todo e hardcodat
            spAccFolder.setSelection(oldAccount.getFolderID() - 1);

            btnAddAccOK.setText(R.string.ok);

            btnSeeAddAccPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etAccPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)){
                        onSeeField(HIDE_PASSWORD);
                    } else{
                        onSeeField(SEE_PASSWORD);
                    }
                }
            });
        } else {
            setTitle("Add New Account");

            btnSeeAddAccPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etAccPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)){
                        etAccPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        btnSeeAddAccPassword.setImageResource(R.drawable.ic_eye_24);
                    } else{
                        etAccPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                        btnSeeAddAccPassword.setImageResource(R.drawable.ic_eye_off_24);
                    }
                }
            });
        }

        btnAddAccOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAcc();
            }
        });
    }

    private void onSeeField(int val) {
        // todo auth for decrypt nu mergeee i'd have to copy it everywhere stoopid
        switch(val){
            case SEE_PASSWORD:
                //CryptoUtilities.checkPassword(this);
                etAccPassword.setText(CryptoUtilities.decryptAES(this, etAccPassword.getText().toString()));
                etAccPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                btnSeeAddAccPassword.setVisibility(View.INVISIBLE);
                etAccPassword.setFocusable(true);
                etAccPassword.setFocusableInTouchMode(true);
                break;
            case HIDE_PASSWORD:
                break;
        }
    }

    private void addAcc() {
        ByteAccount newAccount = new ByteAccount();

        if (oldAccount != null) {
            newAccount = oldAccount;
        }

        if (etAccHost != null && etAccUser != null && etAccPassword != null) {
            if ("".equals(etAccHost.getText().toString())) {
                Toast.makeText(this,
                        "Please provide a host. ex: Google",
                        Toast.LENGTH_SHORT).show();
            } else {
                if ("".equals(etAccUser.getText().toString())) {
                    Toast.makeText(this,
                            "Please provide a host. ex: leiaorgana@gmail.com",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if ("".equals(etAccPassword.getText().toString())) {
                        Toast.makeText(this,
                                "Please provide a password.",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        newAccount.setAccHost(etAccHost.getText().toString().trim());
                        newAccount.setAccUser(etAccUser.getText().toString().trim());
                        // todo encrypt password here
                        if(!(oldAccount!= null && btnSeeAddAccPassword.getVisibility() == View.VISIBLE)) {
                            newAccount.setAccAESPass(CryptoUtilities.encryptAES(this, etAccPassword.getText().toString()));
                        }

                        newAccount.setFolderID(folderService.getFolderByName(spAccFolder.getSelectedItem().toString()).getFolderID());

                        if(oldAccount != null){
                            newAccount.setAccID(oldAccount.getAccID());
                            newAccount.setFolderID(oldAccount.getFolderID());
                            newAccount.setAccUnixTime(System.currentTimeMillis()/1000);

                            accountService.updateAccount(newAccount);
                        } else{
                            accountService.insertAccount(newAccount);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }
        }
    }
}
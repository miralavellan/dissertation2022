package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class ViewAccountActivity extends AppCompatActivity {

    private EditText etViewAccHost;
    private EditText etViewAccUser;
    private EditText etViewAccPassword;
    private ImageButton btnCopyAccHost;
    private ImageButton btnCopyAccUser;
    private ImageButton btnCopyAccPassword;
    private ImageButton btnSeeAccPassword;
    private Button btnEditAccount;
    private Button btnDeleteAccount;

    private int accountID;
    private ByteAccount currentAccount;

    private DatabaseInstance db;
    private AccountDao accountDao;
    private AccountService accountService;

    private TextView tvLastChangedAccount;

    private boolean passwordIsEncrypted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        etViewAccHost = findViewById(R.id.etViewAccHost);
        etViewAccUser = findViewById(R.id.etViewAccUser);
        etViewAccPassword = findViewById(R.id.etViewAccPassword);
        btnEditAccount = findViewById(R.id.btnEditAccount);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnCopyAccHost = findViewById(R.id.btnCopyAccHost);
        btnCopyAccUser = findViewById(R.id.btnCopyAccUser);
        btnCopyAccPassword = findViewById(R.id.btnCopyAccPassword);
        btnSeeAccPassword = findViewById(R.id.btnSeeAccPassword);

        tvLastChangedAccount = findViewById(R.id.tvLastChangedAccount);

        db = DatabaseInstance.getInstance(getApplicationContext());

        if(getIntent().getSerializableExtra("account id") != null) {
            accountID = (int) getIntent().getSerializableExtra("account id");

            accountDao = db.getAccountDao();
            accountService = new AccountService(accountDao);

            currentAccount = accountService.getAccount(accountID);

            if (currentAccount != null) {
                etViewAccHost.setText(currentAccount.getAccHost());
                etViewAccUser.setText(currentAccount.getAccUser());
                //todo decrypt password
                etViewAccPassword.setText(currentAccount.getAccAESPass());
                passwordIsEncrypted = true;

                StringBuilder builder = buildDate();
                tvLastChangedAccount.setText(builder);

                btnCopyAccUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCopyField(1);
                    }
                });

                btnCopyAccPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCopyField(2);
                    }
                });

                btnEditAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openEditAccountActivity();
                    }
                });

                btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteAccount();
                    }
                });
            }
        }

        btnSeeAccPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etViewAccPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)){
                    onSeeField(HIDE_PASSWORD);
                } else{
                    onSeeField(SEE_PASSWORD);
                }

            }
        });
    }

    private StringBuilder buildDate() {
        //todo vezi de ce nu merge cum tb
        Date lastChangedAccount = new java.util.Date((long)currentAccount.getAccUnixTime()*1000);
        Date date = new Date();

        int years, months, days, hours, minutes, seconds;
        years = date.getYear() - lastChangedAccount.getYear();
        months = date.getMonth() - lastChangedAccount.getMonth();
        days = date.getDay() - lastChangedAccount.getDay();
        hours = date.getHours() - lastChangedAccount.getHours();
        minutes = date.getMinutes() - lastChangedAccount.getMinutes();
        seconds = date.getSeconds() - lastChangedAccount.getSeconds();

        StringBuilder builder = new StringBuilder();
        builder.append("Last changed: ");

        if(years > 0){
            builder.append(years);
            if(years == 1){
                builder.append(" year ");
            } else{
                builder.append(" years ");
            }
        }

        if(months > 0){
            builder.append(months);
            if(months == 1){
                builder.append(" month ");
            } else{
                builder.append(" months ");
            }
        }

        if(days > 0){
            builder.append(days);
            if(days == 1){
                builder.append(" day ");
            } else{
                builder.append(" days ");
            }
        }

        if(hours > 0){
            builder.append(hours);
            if(hours == 1){
                builder.append(" hour ");
            } else{
                builder.append(" hours ");
            }
        }

        if(minutes > 0){
            builder.append(minutes);
            if(minutes == 1){
                builder.append(" minute ");
            } else{
                builder.append(" minutes ");
            }
        }

        if(seconds > 0){
            builder.append(seconds);
            if(seconds == 1){
                builder.append(" second ");
            } else{
                builder.append(" seconds ");
            }
        }

        builder.append("ago.");

        return builder;
    }

    private void onSeeField(int val) {
        switch(val){
            case SEE_PASSWORD:
                // todo auth for decrypt
                etViewAccPassword.setText(CryptoUtilities.decryptAES(this, etViewAccPassword.getText().toString()));
                etViewAccPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                btnSeeAccPassword.setImageResource(R.drawable.ic_eye_off_24);
                passwordIsEncrypted = false;
                break;
            case HIDE_PASSWORD:
                etViewAccPassword.setText(currentAccount.getAccAESPass());
                etViewAccPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnSeeAccPassword.setImageResource(R.drawable.ic_eye_24);
                passwordIsEncrypted = true;
                break;
        }
    }

    private void onCopyField(int val) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        switch (val){
            case 1:
                clip = ClipData.newPlainText("user", etViewAccUser.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "User copied to clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                // todo auth for decrypt
                if(passwordIsEncrypted){
                    clip = ClipData.newPlainText("pw", CryptoUtilities.decryptAES(this, etViewAccPassword.getText().toString()));
                } else {
                    clip = ClipData.newPlainText("pw", etViewAccPassword.getText().toString());
                }

                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Password copied to clipboard!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void onDeleteAccount() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        accountService.deleteAccount(currentAccount);
                        setResult(RESULT_DELETED);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void openEditAccountActivity() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        intent.putExtra("account id",
                currentAccount.getAccID());
        startActivityForResult(intent, EDIT_ACC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_ACC && resultCode == RESULT_OK){
            currentAccount = accountService.getAccount(currentAccount.getAccID());
            etViewAccHost.setText(currentAccount.getAccHost());
            etViewAccUser.setText(currentAccount.getAccUser());
            //todo decrypt password
            etViewAccPassword.setText(currentAccount.getAccAESPass());
            setResult(RESULT_EDITED);
        }
    }
}
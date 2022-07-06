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

import ro.ase.csie.mydissertation.database.CardDao;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteCard;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class ViewCardActivity extends AppCompatActivity {

    private EditText etViewCardHost;
    private EditText etViewCardName;
    private EditText etViewCardPIN;
    private EditText etViewCardNumber;
    private EditText etViewCardValidThru;
    private EditText etViewCardCVV;
    private ImageButton btnCopyCardHost;
    private ImageButton btnCopyCardName;
    private ImageButton btnCopyCardPIN;
    private ImageButton btnCopyCardNumber;
    private ImageButton btnCopyCardValidThru;
    private ImageButton btnCopyCardCVV;
    private ImageButton btnSeeCardPIN;
    private ImageButton btnSeeCardNumber;
    private ImageButton btnSeeCardValidThru;
    private ImageButton btnSeeCardCVV;

    private Button btnEditCard;
    private Button btnDeleteCard;

    private int cardID;
    private ByteCard currentCard;

    private DatabaseInstance db;
    private CardDao cardDao;
    private CardService cardService;

    private TextView tvLastChangedCard;

    private boolean PINIsEncrypted;
    private boolean numberIsEncrypted;
    private boolean validThruIsEncrypted;
    private boolean CVVIsEncrypted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);

        etViewCardHost = findViewById(R.id.etViewCardHost);
        etViewCardName = findViewById(R.id.etViewCardName);
        etViewCardPIN = findViewById(R.id.etViewCardPIN);

        etViewCardNumber = findViewById(R.id.etViewCardNumber);
        etViewCardValidThru = findViewById(R.id.etViewCardValidThru);
        etViewCardCVV = findViewById(R.id.etViewCardCVV);

        btnEditCard = findViewById(R.id.btnEditCard);
        btnDeleteCard = findViewById(R.id.btnDeleteCard);

        btnCopyCardHost = findViewById(R.id.btnCopyCardHost);
        btnCopyCardName = findViewById(R.id.btnCopyCardName);
        btnCopyCardPIN = findViewById(R.id.btnCopyCardPIN);

        btnCopyCardNumber = findViewById(R.id.btnCopyCardNumber);
        btnCopyCardValidThru = findViewById(R.id.btnCopyCardValidThru);
        btnCopyCardCVV = findViewById(R.id.btnCopyCardCVV);

        btnSeeCardPIN = findViewById(R.id.btnSeeCardPIN);
        btnSeeCardNumber = findViewById(R.id.btnSeeCardNumber);
        btnSeeCardValidThru = findViewById(R.id.btnSeeCardValidThru);
        btnSeeCardCVV = findViewById(R.id.btnSeeCardCVV);

        tvLastChangedCard = findViewById(R.id.tvLastChangedCard);

        db = DatabaseInstance.getInstance(getApplicationContext());

        if (getIntent().getSerializableExtra("card id") != null) {
            cardID = (int) getIntent().getSerializableExtra("card id");

            cardDao = db.getCardDao();
            cardService = new CardService(cardDao);

            currentCard = cardService.getCard(cardID);

            if(currentCard != null){
                etViewCardHost.setText(currentCard.getCardHost());
                etViewCardName.setText(currentCard.getCardName());
                // todo decrypt
                etViewCardPIN.setText(currentCard.getCardAESPIN());
                etViewCardNumber.setText(currentCard.getCardNumber());
                etViewCardValidThru.setText(currentCard.getCardValidThru());
                etViewCardCVV.setText(currentCard.getCardCVV());

                PINIsEncrypted = true;
                numberIsEncrypted = true;
                validThruIsEncrypted = true;
                CVVIsEncrypted = true;

                StringBuilder builder = buildDate();
                tvLastChangedCard.setText(builder);

                btnCopyCardName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCopyField(COPY_NAME);
                    }
                });

                btnCopyCardPIN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCopyField(COPY_PIN);
                    }
                });

                btnCopyCardNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCopyField(COPY_NUMBER);
                    }
                });

                btnCopyCardValidThru.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCopyField(COPY_VALID_THRU);
                    }
                });

                btnCopyCardCVV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCopyField(COPY_CVV);
                    }
                });

                btnSeeCardPIN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(etViewCardPIN.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                            onSeeField(HIDE_PIN);
                        } else {
                            onSeeField(SEE_PIN);
                        }
                    }
                });
                btnSeeCardNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(etViewCardNumber.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                            onSeeField(HIDE_NUMBER);
                        } else {
                            onSeeField(SEE_NUMBER);
                        }
                    }
                });
                btnSeeCardValidThru.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(etViewCardValidThru.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)){
                            onSeeField(HIDE_VALID_THRU);
                        } else {
                            onSeeField(SEE_VALID_THRU);
                        }
                    }
                });
                btnSeeCardCVV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(etViewCardCVV.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                            onSeeField(HIDE_CVV);
                        } else {
                            onSeeField(SEE_CVV);
                        }
                    }
                });

                btnEditCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openEditCardActivity();
                    }
                });

                btnDeleteCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteCard();
                    }
                });
            }
        }
    }

    private StringBuilder buildDate(){
        //todo vezi de ce nu merge cum tb
        //todo add in baza de date niste entries de acum 6 luni ani whatever
        Date lastChangedCard = new java.util.Date((long)currentCard.getCardUnixTime()*1000);
        Date date = new Date();

        int years, months, days, hours, minutes, seconds;
        years = date.getYear() - lastChangedCard.getYear();
        months = date.getMonth() - lastChangedCard.getMonth();
        days = date.getDay() - lastChangedCard.getDay();
        hours = date.getHours() - lastChangedCard.getHours();
        minutes = date.getMinutes() - lastChangedCard.getMinutes();
        seconds = date.getSeconds() - lastChangedCard.getSeconds();

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

    private void onCopyField(int val) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        switch (val){
            case COPY_NAME:
                clip = ClipData.newPlainText("name", etViewCardName.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Name copied to clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case COPY_PIN:
                if(PINIsEncrypted){
                    clip = ClipData.newPlainText("pin", CryptoUtilities.decryptAES(this, etViewCardPIN.getText().toString()));
                } else {
                    clip = ClipData.newPlainText("pin", etViewCardPIN.getText().toString());
                }

                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "PIN copied to clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case COPY_NUMBER:
                if(numberIsEncrypted){
                    clip = ClipData.newPlainText("number", CryptoUtilities.decryptAES(this, etViewCardNumber.getText().toString()));
                } else {
                    clip = ClipData.newPlainText("number", etViewCardNumber.getText().toString());
                }

                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Number copied to clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case COPY_VALID_THRU:
                if(validThruIsEncrypted){
                    clip = ClipData.newPlainText("valid thru", CryptoUtilities.decryptAES(this, etViewCardValidThru.getText().toString()));
                } else {
                    clip = ClipData.newPlainText("valid thru", etViewCardValidThru.getText().toString());
                }

                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Valid thru copied to clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case COPY_CVV:
                if(CVVIsEncrypted){
                    clip = ClipData.newPlainText("cvv", CryptoUtilities.decryptAES(this, etViewCardCVV.getText().toString()));
                } else {
                    clip = ClipData.newPlainText("cvv", etViewCardCVV.getText().toString());
                }

                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "CVV copied to clipboard!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void onSeeField(int val){
        // todo decrypt si aici
        // todo switch back to password
        switch(val){
            case SEE_PIN:
                etViewCardPIN.setText(CryptoUtilities.decryptAES(this, etViewCardPIN.getText().toString()));
                etViewCardPIN.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                btnSeeCardPIN.setImageResource(R.drawable.ic_eye_off_24);
                PINIsEncrypted = false;
                break;
            case HIDE_PIN:
                etViewCardPIN.setText(currentCard.getCardAESPIN());
                etViewCardPIN.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                btnSeeCardPIN.setImageResource(R.drawable.ic_eye_24);
                PINIsEncrypted = true;
                break;
            case SEE_NUMBER:
                etViewCardNumber.setText(CryptoUtilities.decryptAES(this, etViewCardNumber.getText().toString()));
                etViewCardNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                btnSeeCardNumber.setImageResource(R.drawable.ic_eye_off_24);
                numberIsEncrypted = false;
                break;
            case HIDE_NUMBER:
                etViewCardNumber.setText(currentCard.getCardNumber());
                etViewCardNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                btnSeeCardNumber.setImageResource(R.drawable.ic_eye_24);
                numberIsEncrypted = true;
                break;
            case SEE_VALID_THRU:
                etViewCardValidThru.setText(CryptoUtilities.decryptAES(this, etViewCardValidThru.getText().toString()));
                etViewCardValidThru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                btnSeeCardValidThru.setImageResource(R.drawable.ic_eye_off_24);
                validThruIsEncrypted = false;
                break;
            case HIDE_VALID_THRU:
                etViewCardValidThru.setText(currentCard.getCardValidThru());
                etViewCardValidThru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnSeeCardValidThru.setImageResource(R.drawable.ic_eye_24);
                validThruIsEncrypted = true;
                break;
            case SEE_CVV:
                etViewCardCVV.setText(CryptoUtilities.decryptAES(this, etViewCardCVV.getText().toString()));
                etViewCardCVV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                btnSeeCardCVV.setImageResource(R.drawable.ic_eye_off_24);
                CVVIsEncrypted = false;
                break;
            case HIDE_CVV:
                etViewCardCVV.setText(currentCard.getCardCVV());
                etViewCardCVV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                btnSeeCardCVV.setImageResource(R.drawable.ic_eye_24);
                CVVIsEncrypted = true;
                break;
        }
    }

    private void onDeleteCard() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        cardService.deleteCard(currentCard);
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

    private void openEditCardActivity() {
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra("card id",
                currentCard.getCardID());
        startActivityForResult(intent, EDIT_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_CARD && resultCode == RESULT_OK){
            currentCard = cardService.getCard(currentCard.getCardID());
            etViewCardHost.setText(currentCard.getCardHost());
            etViewCardName.setText(currentCard.getCardName());
            //todo decrypt password
            etViewCardPIN.setText(currentCard.getCardAESPIN());
            etViewCardNumber.setText(currentCard.getCardNumber());
            etViewCardValidThru.setText(currentCard.getCardValidThru());
            etViewCardCVV.setText(currentCard.getCardCVV());
            setResult(RESULT_EDITED);
        }
    }
}
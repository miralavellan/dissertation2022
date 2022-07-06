package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.CardDao;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.database.FolderDao;
import ro.ase.csie.mydissertation.database.FolderService;
import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteCard;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class AddCardActivity extends AppCompatActivity {


    private EditText etCardHost;
    private EditText etCardName;
    private EditText etCardPIN;
    private Spinner spCardFolder;

    private EditText etCardNumber;
    private EditText etCardValidThru;
    private EditText etCardCVV;

    private Button btnAddCardOK;

    private ImageButton btnSeeAddCardPIN;
    private ImageButton btnSeeAddCardNumber;
    private ImageButton btnSeeAddCardValidThru;
    private ImageButton btnSeeAddCardCVV;

    private DatabaseInstance db;

    private FolderDao folderDao;
    private FolderService folderService;
    private int folderID;

    private CardDao cardDao;
    private CardService cardService;

    private int cardID;
    private ByteCard oldCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        etCardHost = findViewById(R.id.etCardHost);
        etCardName = findViewById(R.id.etCardName);
        etCardPIN = findViewById(R.id.etCardPIN);
        spCardFolder = findViewById(R.id.spCardFolder);
        etCardNumber = findViewById(R.id.etCardNumber);
        etCardValidThru = findViewById(R.id.etCardValidThru);
        etCardCVV = findViewById(R.id.etCardCVV);
        btnAddCardOK = findViewById(R.id.btnAddCardOK);
        btnSeeAddCardPIN = findViewById(R.id.btnSeeAddCardPIN);
        btnSeeAddCardNumber = findViewById(R.id.btnSeeAddCardNumber);
        btnSeeAddCardValidThru = findViewById(R.id.btnSeeAddCardValidThru);
        btnSeeAddCardCVV = findViewById(R.id.btnSeeAddCardCVV);

        db = DatabaseInstance.getInstance(this);
        folderDao = db.getFolderDao();
        folderService = new FolderService(folderDao);

        cardDao = db.getCardDao();
        cardService = new CardService(cardDao);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCardFolder.setAdapter(spinnerAdapter);

        spinnerAdapter.addAll(folderService.getFoldersNames());
        spinnerAdapter.notifyDataSetChanged();

        if(getIntent().getSerializableExtra("folder id") != null){
            folderID = (int) getIntent().getSerializableExtra("folder id");
            // todo e hardcodat
            spCardFolder.setSelection(folderService.getFolder(folderID).getFolderID() - 1);
        }

        if(getIntent().getSerializableExtra(CARD_ID) != null){

            setTitle("Edit Card");

            cardID = (int) getIntent().getSerializableExtra(CARD_ID);
            oldCard = cardService.getCard(cardID);

            etCardHost.setText(oldCard.getCardHost());
            etCardName.setText(oldCard.getCardName());
            //todo
            etCardPIN.setText(oldCard.getCardAESPIN());

            etCardNumber.setText(oldCard.getCardNumber());
            etCardValidThru.setText(oldCard.getCardValidThru());
            etCardCVV.setText(oldCard.getCardCVV());

            etCardPIN.setFocusable(false);
            etCardPIN.setFocusableInTouchMode(false);

            etCardNumber.setFocusable(false);
            etCardNumber.setFocusableInTouchMode(false);

            etCardValidThru.setFocusable(false);
            etCardValidThru.setFocusableInTouchMode(false);

            etCardCVV.setFocusable(false);
            etCardCVV.setFocusableInTouchMode(false);

            // todo e hardcodat
            spCardFolder.setSelection(oldCard.getFolderID() - 1);

            btnAddCardOK.setText(R.string.ok);

            btnSeeAddCardPIN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etCardPIN.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                        onSeeField(HIDE_PIN);
                    } else {
                        onSeeField(SEE_PIN);
                    }
                }
            });
            btnSeeAddCardNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etCardNumber.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                        onSeeField(HIDE_NUMBER);
                    } else {
                        onSeeField(SEE_NUMBER);
                    }
                }
            });
            btnSeeAddCardValidThru.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(etCardValidThru.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)){
                        onSeeField(HIDE_VALID_THRU);
                    } else {
                        onSeeField(SEE_VALID_THRU);
                    }
                }
            });
            btnSeeAddCardCVV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etCardCVV.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                        onSeeField(HIDE_CVV);
                    } else {
                        onSeeField(SEE_CVV);
                    }
                }
            });
        } else {
            setTitle("Add New Card");

            btnSeeAddCardPIN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etCardPIN.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                        etCardPIN.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        btnSeeAddCardPIN.setImageResource(R.drawable.ic_eye_24);
                    } else {
                        etCardPIN.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        btnSeeAddCardPIN.setImageResource(R.drawable.ic_eye_off_24);
                    }
                }
            });
            btnSeeAddCardNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etCardNumber.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                        etCardNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        btnSeeAddCardNumber.setImageResource(R.drawable.ic_eye_24);
                    } else {
                        etCardNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        btnSeeAddCardNumber.setImageResource(R.drawable.ic_eye_off_24);
                    }
                }
            });
            btnSeeAddCardValidThru.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etCardValidThru.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)){
                        etCardValidThru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        btnSeeAddCardValidThru.setImageResource(R.drawable.ic_eye_24);
                    } else {
                        etCardValidThru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                        btnSeeAddCardValidThru.setImageResource(R.drawable.ic_eye_off_24);
                    }
                }
            });
            btnSeeAddCardCVV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etCardCVV.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL)){
                        etCardCVV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        btnSeeAddCardCVV.setImageResource(R.drawable.ic_eye_24);
                    } else {
                        etCardCVV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        btnSeeAddCardCVV.setImageResource(R.drawable.ic_eye_off_24);
                    }
                }
            });
        }

        btnAddCardOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
            }
        });
    }

    private void onSeeField(int val) {
        // todo decrypt daca e edit
        // todo switch back
        switch(val){
            case SEE_PIN:
                etCardPIN.setText(CryptoUtilities.decryptAES(this, etCardPIN.getText().toString()));
                etCardPIN.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                btnSeeAddCardPIN.setVisibility(View.INVISIBLE);
                etCardPIN.setFocusable(true);
                etCardPIN.setFocusableInTouchMode(true);
                break;
            case HIDE_PIN:
                break;
            case SEE_NUMBER:
                etCardNumber.setText(CryptoUtilities.decryptAES(this, etCardNumber.getText().toString()));
                etCardNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                btnSeeAddCardNumber.setVisibility(View.INVISIBLE);
                etCardNumber.setFocusable(true);
                etCardNumber.setFocusableInTouchMode(true);
                break;
            case HIDE_NUMBER:
                break;
            case SEE_VALID_THRU:
                etCardValidThru.setText(CryptoUtilities.decryptAES(this, etCardValidThru.getText().toString()));
                etCardValidThru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                btnSeeAddCardValidThru.setVisibility(View.INVISIBLE);
                etCardValidThru.setFocusable(true);
                etCardValidThru.setFocusableInTouchMode(true);
                break;
            case HIDE_VALID_THRU:
                break;
            case SEE_CVV:
                etCardCVV.setText(CryptoUtilities.decryptAES(this, etCardCVV.getText().toString()));
                etCardCVV.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                btnSeeAddCardCVV.setVisibility(View.INVISIBLE);
                etCardCVV.setFocusable(true);
                etCardCVV.setFocusableInTouchMode(true);
                break;
            case HIDE_CVV:
                break;
        }
    }

    private void addCard() {
        ByteCard newCard = new ByteCard();

        if (oldCard != null) {
            newCard = oldCard;
        }

        if (etCardHost != null && etCardName != null && etCardPIN != null) {
            if ("".equals(etCardHost.getText().toString())) {
                Toast.makeText(this,
                        "Please provide a host. ex: ING",
                        Toast.LENGTH_SHORT).show();
            } else {
                if ("".equals(etCardName.getText().toString())) {
                    Toast.makeText(this,
                            "Please provide a name. ex: Leia Organa",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if ("".equals(etCardPIN.getText().toString())) {
                        Toast.makeText(this,
                                "Please provide a PIN.",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        newCard.setCardHost(etCardHost.getText().toString().trim());
                        newCard.setCardName(etCardName.getText().toString().trim());
                        // todo encrypt password here

                        if(!(oldCard!= null && btnSeeAddCardPIN.getVisibility() == View.VISIBLE)){
                            newCard.setCardAESPIN(CryptoUtilities.encryptAES(this, etCardPIN.getText().toString()));
                        }

                        if(!(oldCard!= null && btnSeeAddCardNumber.getVisibility() == View.VISIBLE)){
                            newCard.setCardNumber(CryptoUtilities.encryptAES(this, etCardNumber.getText().toString()));
                        }

                        if(!(oldCard!= null && btnSeeAddCardValidThru.getVisibility() == View.VISIBLE)){
                            newCard.setCardValidThru(CryptoUtilities.encryptAES(this, etCardValidThru.getText().toString()));
                        }

                        if(!(oldCard!= null && btnSeeAddCardValidThru.getVisibility() == View.VISIBLE)){
                            newCard.setCardCVV(CryptoUtilities.encryptAES(this, etCardCVV.getText().toString()));
                        }

                        newCard.setFolderID(folderService.getFolderByName(spCardFolder.getSelectedItem().toString()).getFolderID());

                        if(oldCard != null){
                            newCard.setCardID(oldCard.getCardID());
                            newCard.setFolderID(oldCard.getFolderID());
                            newCard.setCardUnixTime(System.currentTimeMillis()/1000);

                            cardService.updateCard(newCard);
                        } else{
                            cardService.insertCard(newCard);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }
        }
    }
}
package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class SignInActivity extends AppCompatActivity {

    //todo put the password hint somewhere
    private EditText etSignInUser;
    private EditText etSignInPassword;
    private Button btnSignInOK;

    private SharedPreferences sp;

    private TextView tvSignInHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etSignInUser = findViewById(R.id.etSignInUser);
        etSignInPassword = findViewById(R.id.etSignInPassword);
        btnSignInOK = findViewById(R.id.btnSignInOK);
        tvSignInHint = findViewById(R.id.tvSignInHint);

        setTitle("Sign In");

        btnSignInOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });

        sp = this.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
        if(!sp.getString(SHARED_PREFS_1_USER, "default").equals("default")){
            etSignInUser.setText(sp.getString(SHARED_PREFS_1_USER, "default"));

            tvSignInHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!sp.getString(SHARED_PREFS_1_HINT, "default").equals("default")){
                        Toast.makeText(getApplicationContext()
                                , sp.getString(SHARED_PREFS_1_HINT, "default"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext()
                                , "O-oh! Looks like you didn't set any hint!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            tvSignInHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext()
                            , "You haven't created an account yet!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkUser() {
        // todo hash here
        String pw = etSignInPassword.getText().toString();
        byte[] salt = sp.getString(SHARED_PREFS_1_SALT, "default").getBytes(StandardCharsets.UTF_8);
        byte[] digest = CryptoUtilities.hashSHA256Salt(pw, salt);
        String salted = new String(digest);

        if(sp.getString(SHARED_PREFS_1_USER, "default").equals(etSignInUser.getText().toString())
        && sp.getString(SHARED_PREFS_1_PASSWORD, "default")
                .equals(salted)){
            Intent intent = new Intent(this, BytePassActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    "Incorrect username or password!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_ENTER:
                checkUser();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
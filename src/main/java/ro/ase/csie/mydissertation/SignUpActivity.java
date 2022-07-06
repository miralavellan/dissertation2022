package ro.ase.csie.mydissertation;

import static ro.ase.csie.mydissertation.Constants.SHARED_PREFS_1;
import static ro.ase.csie.mydissertation.Constants.SHARED_PREFS_1_HINT;
import static ro.ase.csie.mydissertation.Constants.SHARED_PREFS_1_PASSWORD;
import static ro.ase.csie.mydissertation.Constants.SHARED_PREFS_1_SALT;
import static ro.ase.csie.mydissertation.Constants.SHARED_PREFS_1_USER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ro.ase.csie.mydissertation.utilities.CryptoUtilities;

public class SignUpActivity extends AppCompatActivity {

    private EditText etSignUpUser;
    private EditText etSignUpPassword;
    private EditText etSignUpPasswordRepeat;
    private EditText etSignUpPasswordHint;
    private Button btnSignUpOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etSignUpUser = findViewById(R.id.etSignUpUser);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etSignUpPasswordRepeat = findViewById(R.id.etSignUpPasswordRepeat);
        etSignUpPasswordHint = findViewById(R.id.etSignUpPasswordHint);
        btnSignUpOK = findViewById(R.id.btnSignUpOK);

        setTitle("Sign Up");

        btnSignUpOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser() {
        if(etSignUpUser != null && etSignUpPassword != null && etSignUpPasswordRepeat != null){
            if("".equals(etSignUpUser.getText().toString())){
                Toast.makeText(this,
                        "Please provide a username. ex: LeiaOrgana77",
                        Toast.LENGTH_SHORT).show();
            } else{
                if("".equals(etSignUpPassword.getText().toString())){
                    Toast.makeText(this,
                            "Please provide a password.",
                            Toast.LENGTH_SHORT).show();
                } else{
                    if("".equals(etSignUpPasswordRepeat.getText().toString())){
                        Toast.makeText(this,
                                "Please retype the password.",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        if(!etSignUpPassword.getText().toString().equals(etSignUpPasswordRepeat.getText().toString())){
                            Toast.makeText(this,
                                    "Passwords must be the same.",
                                    Toast.LENGTH_SHORT).show();

                        } else{
                            SharedPreferences sp = this.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(SHARED_PREFS_1_USER, etSignUpUser.getText().toString());

                            if("".equals(etSignUpPasswordHint.getText().toString())){
                                // todo nu merge dialogu
                            } else {
                                editor.putString(SHARED_PREFS_1_HINT, etSignUpPasswordHint.getText().toString());
                            }

                            // todo hash here
                            // todo find out how to add swirly loading thing
                            byte[] salt = CryptoUtilities.getRandomString().getBytes(StandardCharsets.UTF_8);

                            byte[] digest = CryptoUtilities.hashSHA256Salt(etSignUpPassword.getText().toString()
                                    , salt
                                    , this
                                    , SHARED_PREFS_1
                                    , SHARED_PREFS_1_SALT);

                            if(digest != null){
                                editor.putString(SHARED_PREFS_1_PASSWORD, new String(digest));
                                editor.apply();
                                finish();
                            } else {
                                Toast.makeText(this,
                                        "Something went wrong.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }
    }

//    public String getRandomString(){
//        OkHttpClient client = new OkHttpClient();
//
//        String url = "https://api.random.org/json-rpc/4/invoke";
//        String requestBody = "{\"jsonrpc\":\"2.0\",\"method\":\"generateSignedStrings\",\"params\":{\"apiKey\":\"83bb207a-8dd0-4c18-951a-9e4cccc0fda6\",\"n\":1,\"length\":8,\"characters\":\"abcdefghijklmnopqrstuvwxyz\",\"replacement\":true,\"pregeneratedRandomization\":null,\"licenseData\":null,\"userData\":null,\"ticketId\":null},\"id\":10261}";
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//
//        RequestBody body = RequestBody.create(requestBody, mediaType);
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//
//        // todo try to do it async
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(@NonNull Call call, @NonNull IOException e) {
////                e.printStackTrace();
////            }
////
////            @Override
////            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
////                if(response.isSuccessful()){
////                    response = client.newCall(request).execute();
////
////                    String result = response.body().string();
////                    int index = result.indexOf("[\"");
////                    result = result.substring(index);
////                    result = result.substring(2, 10);
////
////                    final byte[] random = result.getBytes(StandardCharsets.UTF_8);
////
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            salt = random;
////                        }
////                    });
////                }
////            }
////        });
//
//        Response response = null;
//
//        try {
//            response = client.newCall(request).execute();
//
//            String result = response.body().string();
//            int index = result.indexOf("[\"");
//            result = result.substring(index);
//            result = result.substring(2, 10);
//
//            return result;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //todo ugly
//        return "nope";
//    }
//
//    // generate and save
//    public byte[] hashSHA256Salt (String string, byte[] salt, Context context, String sharedPrefFile, String sharedPref){
//        byte[] message = string.getBytes(StandardCharsets.UTF_8);
//
//        // add to shared pref
//        SharedPreferences sp = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString(sharedPref, new String(salt));
//        editor.apply();
//
//        byte[] salted = new byte[message.length + salt.length];
//
//        System.arraycopy(message, 0, salted,0, message.length);
//        System.arraycopy(salt, 0, salted, message.length, salt.length);
//
//        byte[] digest = null;
//
//        MessageDigest md = null;
//
//        try {
//            md = MessageDigest.getInstance("SHA-256");
//            digest = md.digest(salted);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        return digest;
//    }
}
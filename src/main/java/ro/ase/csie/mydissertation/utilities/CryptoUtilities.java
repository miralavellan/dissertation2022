package ro.ase.csie.mydissertation.utilities;


import static ro.ase.csie.mydissertation.Constants.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.InputType;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.security.crypto.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ro.ase.csie.mydissertation.BytePassActivity;
import ro.ase.csie.mydissertation.models.ByteFolder;

public abstract class CryptoUtilities {

    public static boolean checkPassword(Context context) {

        final boolean[] isCorrect = new boolean[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please input master password!");
        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);

                String pw = input.getText().toString();
                byte[] salt = sp.getString(SHARED_PREFS_1_SALT, "default").getBytes(StandardCharsets.UTF_8);
                byte[] digest = CryptoUtilities.hashSHA256Salt(pw, salt);
                String salted = new String(digest);

                if(sp.getString(SHARED_PREFS_1_PASSWORD, "default")
                        .equals(salted)){
                    isCorrect[0] = true;
                } else {
                    isCorrect[0] = false;
                    Toast.makeText(context,
                            "Password is incorrect!",
                            Toast.LENGTH_SHORT).show();
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

        return isCorrect[0];
    }

    public static void generateSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(MASTER_KEY)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE);
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(MASTER_KEY,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .build());
                keyGenerator.generateKey();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public static Key getSecretKey() {

        KeyStore keyStore = null;
        Key key = null;

        try {
            keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            key = keyStore.getKey(MASTER_KEY, null);

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return key;
    }

    public static String encryptAES(Context context, String string) {

        byte[] plaintext = string.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = null;
        byte[] ciphertext = null;

        try {
            cipher = Cipher.getInstance(AES_MODE);
            // todo change the IV do it w keygen
            byte[] IV = {0x00, 0x00, 0x00, 0x00
                    , 0x00, 0x00, 0x00, 0x00
                    , 0x00, 0x00, 0x00, 0x00};

            // add to sharedprefs
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(TEST_IV, new String(IV));
            editor.apply();

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey()
                    , new GCMParameterSpec(128, IV));

            ciphertext = cipher.doFinal(plaintext);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(ciphertext, Base64.DEFAULT);
    }

    public static String decryptAES(Context context, String string) {

        byte[] ciphertext = Base64.decode(string, Base64.DEFAULT);
        byte[] plaintext = null;

        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);

            SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
            byte[] IV = (sp.getString(TEST_IV, "default")).getBytes();

            cipher.init(Cipher.DECRYPT_MODE, getSecretKey()
                    , new GCMParameterSpec(128, IV));

            plaintext = cipher.doFinal(ciphertext);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return new String(plaintext);
    }

    // generate with given salt
    public static byte[] hashSHA256Salt(String string, byte[] salt) {
        byte[] message = string.getBytes(StandardCharsets.UTF_8);

        byte[] salted = new byte[message.length + salt.length];

        System.arraycopy(message, 0, salted, 0, message.length);
        System.arraycopy(salt, 0, salted, message.length, salt.length);

        byte[] digest = null;

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
            digest = md.digest(salted);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return digest;
    }

    public static String getRandomString(){
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.random.org/json-rpc/4/invoke";
        String requestBody = "{\"jsonrpc\":\"2.0\",\"method\":\"generateSignedStrings\",\"params\":{\"apiKey\":\"83bb207a-8dd0-4c18-951a-9e4cccc0fda6\",\"n\":1,\"length\":8,\"characters\":\"abcdefghijklmnopqrstuvwxyz\",\"replacement\":true,\"pregeneratedRandomization\":null,\"licenseData\":null,\"userData\":null,\"ticketId\":null},\"id\":10261}";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(requestBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // todo try to do it async

        Response response = null;

        try {
            response = client.newCall(request).execute();

            String result = response.body().string();
            int index = result.indexOf("[\"");
            result = result.substring(index);
            result = result.substring(2, 10);

            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }

        //todo ugly
        return "nope";
    }

    public static byte[] hashSHA256Salt (String string, byte[] salt, Context context, String sharedPrefFile, String sharedPref){
        byte[] message = string.getBytes(StandardCharsets.UTF_8);

        // add to shared pref
        SharedPreferences sp = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(sharedPref, new String(salt));
        editor.apply();

        byte[] salted = new byte[message.length + salt.length];

        System.arraycopy(message, 0, salted,0, message.length);
        System.arraycopy(salt, 0, salted, message.length, salt.length);

        byte[] digest = null;

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
            digest = md.digest(salted);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return digest;
    }

}

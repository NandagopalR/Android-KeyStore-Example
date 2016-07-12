package com.example.raja.keystoreexample;

import android.accounts.AccountManager;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private KeyStore keyStore;
    private AccountManager manager;
    private Button onGenerate, onDelete, onGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (onGenerate = (Button) findViewById(R.id.button_set)).setOnClickListener(this);
        (onGet = (Button) findViewById(R.id.button_get)).setOnClickListener(this);
        (onDelete = (Button) findViewById(R.id.delete)).setOnClickListener(this);
        manager = AccountManager.get(this);
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            refreshKeys();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

    }

    private void deleteKey() {
        ArrayList keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            String valueTest = keyStore.aliases().nextElement();
            Log.e("Deleted", " - " + keyStore.aliases().nextElement());
            keyStore.deleteEntry(valueTest);
            while (aliases.hasMoreElements()) {
                String value = aliases.nextElement();
                keyAliases.add(value);
                Log.e("Delete Key", aliases + " - " + value);
            }
        } catch (Exception e) {
        }
    }

    private void getKeyStore() {
        ArrayList keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String value = aliases.nextElement();
                keyAliases.add(value);
                Log.e("Keystore Key", aliases + " - " + value);
            }
        } catch (Exception e) {
        }
    }

    private void setKeyStoreUpdated() {
        try {

            String alias = ((EditText) findViewById(R.id.editText)).getText().toString();
            try {
                if (!keyStore.containsAlias(alias)) {

                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 1);

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this)
                                .setAlias(alias)
                                .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                                .setSerialNumber(BigInteger.ONE)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build();
                        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                        generator.initialize(spec);
                        generator.generateKeyPair();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        keyPairGenerator.initialize(
                                new KeyGenParameterSpec.Builder(
                                        alias,
                                        KeyProperties.PURPOSE_SIGN)
                                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                                        .build());
                        KeyPair keyPair = keyPairGenerator.generateKeyPair();
                        Signature signature = Signature.getInstance("SHA256withRSA/PSS");

                        signature.initSign(keyPair.getPrivate());
                    }
                }
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }


    private void refreshKeys() {
        ArrayList keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        try {

            switch (v.getId()) {
                case R.id.button_set:
                    setKeyStoreUpdated();
                    break;
                case R.id.delete:
                    deleteKey();
                    break;
                case R.id.button_get:
                    getKeyStore();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

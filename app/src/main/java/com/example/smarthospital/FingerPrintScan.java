package com.example.smarthospital;

import static com.example.smarthospital.FingerPrintAuthHelper.getHelper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.Objects;

public class FingerPrintScan extends AppCompatActivity implements FingerPrintAuthCallback {

    private EditText pinCode;
    private TextView tVAuthMessage;
    private ViewSwitcher main_Switcher;
    private Button btn_OpenSettings;
    private FingerPrintAuthHelper mFingerPrintAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_scan);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Fingerprint Scan");

        pinCode = findViewById(R.id.etPinCode);
        tVAuthMessage = (TextView) findViewById(R.id.tvAuthMessage);
        main_Switcher = findViewById(R.id.mainSwitcher);
        btn_OpenSettings = (Button) findViewById(R.id.btnOpenSettings);
        mFingerPrintAuthHelper = getHelper(this, this);

        pinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("1234")) {
                    Toast.makeText(FingerPrintScan.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FingerPrintScan.this, LoginBy.class));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_OpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerPrintUtils.openSecuritySettings(FingerPrintScan.this);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        btn_OpenSettings.setVisibility(View.GONE);

        tVAuthMessage.setText("Scan your finger");

        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    //disabled for now req API 16 minimum
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mFingerPrintAuthHelper.stopAuth();
//    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onNoFingerPrintHardwareFound() {
        tVAuthMessage.setText("Your device does not have finger print scanner. Please enter 1234 and type your credential to authenticate.");
        main_Switcher.showNext();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onNoFingerPrintRegistered() {
        tVAuthMessage.setText("There are no finger prints registered on this device. Please register your finger from settings.");
        btn_OpenSettings.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBelowMarshmallow() {
        tVAuthMessage.setText("You are running older version of android that does not support finger print authentication. Please type 1234 to authenticate.");
        main_Switcher.showNext();
    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(FingerPrintScan.this, "Authentication succeeded.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(FingerPrintScan.this, CheckUniqueCode.class));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                tVAuthMessage.setText("Cannot recognize your finger print. Please try again.");
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                tVAuthMessage.setText("Cannot initialize finger print authentication. Please type 1234 to authenticate.");
                main_Switcher.showNext();
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                tVAuthMessage.setText(errorMessage);
                break;
        }
    }
}
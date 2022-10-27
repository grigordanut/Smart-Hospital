package com.example.smarthospital;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.Objects;

public class LoginBy extends AppCompatActivity {

    private Button btn_PersonalDetails, btn_FingerPrint;

    private CheckBox cBPersonalDetails, cBFingerPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by);

        Objects.requireNonNull(getSupportActionBar()).setTitle("User Log in By: ");

        btn_PersonalDetails = findViewById(R.id.btnPersonalDetails);
        btn_FingerPrint = findViewById(R.id.btnFingerPrint);

        cBPersonalDetails = findViewById(R.id.cbPersonalDetails);
        cBPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (cBPersonalDetails.isChecked()) {
                    cBFingerPrint.setChecked(false);
                    btn_PersonalDetails.setEnabled(true);
                    btn_PersonalDetails.setText("Enter");
                    btn_FingerPrint.setText("Disabled");
                    btn_FingerPrint.setEnabled(false);
                    btn_PersonalDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(LoginBy.this, Login.class));
                        }
                    });
                }
                else{
                    btn_PersonalDetails.setText("Disabled");
                    btn_PersonalDetails.setEnabled(false);
                }
            }
        });

        cBFingerPrint = findViewById(R.id.cbFingerPrint);
        cBFingerPrint.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (cBFingerPrint.isChecked()) {
                    cBPersonalDetails.setChecked(false);
                    btn_FingerPrint.setEnabled(true);
                    btn_FingerPrint.setText("Enter");
                    btn_PersonalDetails.setText("Disabled");
                    btn_PersonalDetails.setEnabled(false);
                    btn_FingerPrint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(LoginBy.this, FingerPrintScan.class));
                        }
                    });
                }

                else{
                    btn_FingerPrint.setText("Disabled");
                    btn_FingerPrint.setEnabled(false);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();

        cBPersonalDetails.setChecked(false);
        cBFingerPrint.setChecked(false);

        btn_PersonalDetails.setEnabled(false);
        btn_PersonalDetails.setText("Disabled");

        btn_FingerPrint.setEnabled(false);
        btn_FingerPrint.setText("Disabled");
    }
}
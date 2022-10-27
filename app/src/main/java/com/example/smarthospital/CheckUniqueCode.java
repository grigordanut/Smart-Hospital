package com.example.smarthospital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class CheckUniqueCode extends AppCompatActivity {

    private TextInputEditText eTCheckUniqueCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_unique_code);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Check Unique Code");

        eTCheckUniqueCode = findViewById(R.id.etCheckUniqueCode);

        Button btn_CheckCode = findViewById(R.id.btnCheckCode);

        btn_CheckCode.setOnClickListener(view -> {
            String editText_CheckCode = Objects.requireNonNull(eTCheckUniqueCode.getText()).toString();
            if (editText_CheckCode.isEmpty()) {
                eTCheckUniqueCode.setError("Please enter your Unique code");
                eTCheckUniqueCode.requestFocus();
            }

            else{
                if(editText_CheckCode.charAt(0) =='h'||editText_CheckCode.charAt(0)=='H'){
                    Toast.makeText(CheckUniqueCode.this, "This is a correct code for Hospitals",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CheckUniqueCode.this, HospitalRegistration.class));
                }

                else if(editText_CheckCode.charAt(0) =='d'||editText_CheckCode.charAt(0)=='D'){
                    Toast.makeText(CheckUniqueCode.this, "This is a correct code for Doctors",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CheckUniqueCode.this, HospitalListAddDoctor.class));
                }

                else{
                    Toast.makeText(CheckUniqueCode.this, "Please enter a correct Unique Code",Toast.LENGTH_SHORT).show();
                    eTCheckUniqueCode.setError("Enter a correct Unique Code");
                    eTCheckUniqueCode.requestFocus();
                }
            }
        });
    }
}
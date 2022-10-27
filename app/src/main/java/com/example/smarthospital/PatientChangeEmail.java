package com.example.smarthospital;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class PatientChangeEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_change_email);

        Objects.requireNonNull(getSupportActionBar()).setTitle("PATIENT: change Email");
    }
}
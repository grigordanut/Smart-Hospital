package com.example.smarthospitals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class DoctorChangeEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_change_email);

        Objects.requireNonNull(getSupportActionBar()).setTitle("DOCTOR: change Email");
    }
}
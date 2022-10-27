package com.example.smarthospital;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class DoctorChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_change_password);

        Objects.requireNonNull(getSupportActionBar()).setTitle("DOCTOR: change Password");
    }
}
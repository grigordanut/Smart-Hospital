package com.example.smarthospitals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class DoctorEditProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("DOCTOR: edit Profile");
    }
}
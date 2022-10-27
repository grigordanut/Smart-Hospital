package com.example.smarthospital;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;

import java.util.Objects;

public class PatientNFC extends AppCompatActivity {

    private EditText patFirstName, patLastName, patDoctorName, patHospName;

    private String patient_FirstName = "";
    private String patient_LastName = "";
    private String patient_DocName = "";
    private String patient_HospName = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_nfc);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patient Identification");

        patFirstName = findViewById(R.id.etPatNFCFirstName);
        patFirstName.setEnabled(false);
        patLastName = findViewById(R.id.etPatNFCLastName);
        patLastName.setEnabled(false);
        patDoctorName = findViewById(R.id.etPatNFCDocName);
        patDoctorName.setEnabled(false);
        patHospName = findViewById(R.id.etPatNFCHospName);
        patHospName.setEnabled(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            patient_FirstName = bundle.getString("FIRSTNAME");
            patient_LastName = bundle.getString("LASTNAME");
            patient_DocName = bundle.getString("DOCTORNAME");
            patient_HospName = bundle.getString("HOSPNAME");
        }

        patFirstName.setText(patient_FirstName);
        patLastName.setText(patient_LastName);
        patDoctorName.setText(patient_DocName);
        patHospName.setText(patient_HospName);
    }
}
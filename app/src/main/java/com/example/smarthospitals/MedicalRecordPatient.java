package com.example.smarthospitals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MedicalRecordPatient extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private TextView tVMedRecListPatDocName, tVMedRecListPatPatName;

    private RecyclerView medicalRecPatRecyclerView;
    private MedicalRecordAdapter medicalRecordAdapter;

    private List<MedicalRecords> medicalRecordsListPat;

    private String recDoctorName = "";
    private String recPatientName = "";
    private String recPatientKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_patient);

        Objects.requireNonNull(getSupportActionBar()).setTitle("PATIENT: Medical Records");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVMedRecListPatDocName = findViewById(R.id.tvMedRecListPatDocName);
        tVMedRecListPatPatName = findViewById(R.id.tvMedRecListPatPatName);

        medicalRecordsListPat = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recDoctorName = bundle.getString("DOCName");
            recPatientName = bundle.getString("PATName");
            recPatientKey = bundle.getString("PATKey");
        }

        tVMedRecListPatDocName.setText("Doctor: " + recDoctorName);
        tVMedRecListPatPatName.setText("Patient: " + recPatientName);

        medicalRecPatRecyclerView = findViewById(R.id.medRecPatRecyclerView);
        medicalRecPatRecyclerView.setHasFixedSize(true);
        medicalRecPatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicalRecordAdapter = new MedicalRecordAdapter(MedicalRecordPatient.this, medicalRecordsListPat);
        medicalRecPatRecyclerView.setAdapter(medicalRecordAdapter);

        Button buttonBackPatientPage = findViewById(R.id.btnBackPatientPage);
        buttonBackPatientPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MedicalRecordPatient.this, PatientPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMedRecListPatient();
    }

    public void loadMedRecListPatient() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Records");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicalRecordsListPat.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    MedicalRecords medRecord = postSnapshot.getValue(MedicalRecords.class);
                    if(medRecord != null) {
                        if (medRecord.getRecordPat_Key().equals(recPatientKey)) {
                            medRecord.setRecord_Key(postSnapshot.getKey());
                            medicalRecordsListPat.add(medRecord);
                        }
                    }
                }

                medicalRecordAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MedicalRecordPatient.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
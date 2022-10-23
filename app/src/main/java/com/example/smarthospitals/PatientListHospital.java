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

public class PatientListHospital extends AppCompatActivity {

    //Retrieve data from Patients database
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private TextView tVPatListHospName;

    private RecyclerView patientHospRecyclerView;
    private PatientAdapter patientAdapter;

    private List<Patients> patientsListHosp;

    private String hospitalName = "";
    private String hospitalKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_hospital);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patients available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVPatListHospName = findViewById(R.id.tvPatListHospName);

        patientsListHosp = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            hospitalName = bundle.getString("HOSPName");
            hospitalKey = bundle.getString("HOSPKey");
        }

        tVPatListHospName.setText("No registered Patients!!");

        patientHospRecyclerView = findViewById(R.id.patHospRecyclerView);
        patientHospRecyclerView.setHasFixedSize(true);
        patientHospRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        patientAdapter = new PatientAdapter(PatientListHospital.this, patientsListHosp);
        patientHospRecyclerView.setAdapter(patientAdapter);

        Button buttonBackHospitalPage = findViewById(R.id.btnBackHospitalPage);
        buttonBackHospitalPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientListHospital.this, HospitalPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPatientsListHospital();
    }

    private void loadPatientsListHospital(){

        //Retrieve data from Patients database
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Patients patHosp_Data = postSnapshot.getValue(Patients.class);

                    if (patHosp_Data != null){
                        if (patHosp_Data.getPatHosp_Key().equals(hospitalKey)){
                            patientsListHosp.add(patHosp_Data);
                            tVPatListHospName.setText("Patients list: " + hospitalName + " Hospital");
                        }
                    }
                }

                patientAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientListHospital.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
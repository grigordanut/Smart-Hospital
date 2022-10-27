package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;;
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

public class PatientListDoctor extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private TextView tVPatListHospName, tVPatListDoctorName;

    private RecyclerView patientDocRecyclerView;
    private PatientAdapter patientAdapter;
    private List<Patients> patDocList;

    private String hospital_Name = "";
    private String hospital_Key = "";
    private String doctor_Name = "";
    private String doctor_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_doctor);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patients available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Patients database
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        tVPatListHospName = findViewById(R.id.tvPatListHospName);
        tVPatListDoctorName = findViewById(R.id.tvPatListDoctorName);

        patDocList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            hospital_Name = bundle.getString("HOSPName");
            hospital_Key = bundle.getString("HOSPKey");
            doctor_Name = bundle.getString("DOCName");
            doctor_Key = bundle.getString("DOCKey");
        }

        tVPatListHospName.setText(hospital_Name + " Hospital");
        tVPatListDoctorName.setText("No registered Patients!!");

        patientDocRecyclerView = findViewById(R.id.patDocRecyclerView);
        patientDocRecyclerView.setHasFixedSize(true);
        patientDocRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        patientAdapter = new PatientAdapter(PatientListDoctor.this, patDocList);
        patientDocRecyclerView.setAdapter(patientAdapter);

        //Action button new Patients
        Button buttonNewPatient = (Button) findViewById(R.id.btnNewPatient);
        buttonNewPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent new_Pat = new Intent(PatientListDoctor.this, PatientRegistration.class);
                new_Pat.putExtra("HOSPName", hospital_Name);
                new_Pat.putExtra("HOSPKey", hospital_Key);
                new_Pat.putExtra("DOCName", doctor_Name);
                new_Pat.putExtra("DOCKey", doctor_Key);
                startActivity(new_Pat);
            }
        });

        Button buttonBackDoctor = (Button)findViewById(R.id.btnBackDoctor);
        buttonBackDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(PatientListDoctor.this, DoctorPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPatientsListDoctor();
    }

    private void loadPatientsListDoctor() {

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patDocList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Patients patDoc_Data = postSnapshot.getValue(Patients.class);

                    if (patDoc_Data != null){
                        if (patDoc_Data.getPatDoc_Key().equals(doctor_Key)){
                            patDoc_Data.setPatient_Key(postSnapshot.getKey());
                            patDocList.add(patDoc_Data);
                            tVPatListDoctorName.setText("Patients of Dr: " + patDoc_Data.getPatDoc_Name());
                        }
                    }
                }

                patientAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientListDoctor.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
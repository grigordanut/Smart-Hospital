package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class PatientListAddMedicalRecord extends AppCompatActivity implements PatientAdapter.OnItemClickListener{

    //Declare variables
    private DatabaseReference databaseRefAddRecord;
    private ValueEventListener evListenerAddRecord;
    private List<Patients> patientsListAddRec;

    private TextView tVDoctorNameAddRec, tVPatListAddRec;

    private RecyclerView medRecordRecyclerView;
    private PatientAdapter patientAdapter;

    private String doctor_Name = "";
    private String doctor_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_add_medical_record);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patients available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        databaseRefAddRecord = FirebaseDatabase.getInstance().getReference("Patients");

        patientsListAddRec = new ArrayList<>();

        tVDoctorNameAddRec = findViewById(R.id.tvDoctorNameAddRec);
        tVPatListAddRec = findViewById(R.id.tvPatListAddRec);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            doctor_Name = bundle.getString("DOCName");
            doctor_Key = bundle.getString("DOCKey");
        }

        tVDoctorNameAddRec.setText("Doctor: " + doctor_Name);
        tVPatListAddRec.setText("No registered Patients!!");

        medRecordRecyclerView = findViewById(R.id.patListRecyclerView);
        medRecordRecyclerView.setHasFixedSize(true);
        medRecordRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        patientAdapter = new PatientAdapter(PatientListAddMedicalRecord.this, patientsListAddRec);
        medRecordRecyclerView.setAdapter(patientAdapter);
        patientAdapter.setOnItmClickListener(PatientListAddMedicalRecord.this);
    }

    @Override
    public void onStart(){
        super.onStart();
        loadPatientData();
    }

    private void loadPatientData(){

        evListenerAddRecord = databaseRefAddRecord.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientsListAddRec.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Patients patAddRec = postSnapshot.getValue(Patients.class);

                    if(patAddRec!=null){
                        if(patAddRec.getPatDoc_Key().equals(doctor_Key)) {
                            patAddRec.setPatient_Key(postSnapshot.getKey());
                            patientsListAddRec.add(patAddRec);
                            tVPatListAddRec.setText("Select your Patient!");
                        }
                    }
                }

                patientAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientListAddMedicalRecord.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Patients onClick
    @Override
    public void onItemClick(int position) {
        Patients selected_Pat = patientsListAddRec.get(position);

        Intent intent_AddRec = new Intent(PatientListAddMedicalRecord.this,AddMedicalRecord.class);
        intent_AddRec.putExtra("PATName", selected_Pat.getPatFirst_Name()+ " " + selected_Pat.getPatLast_Name());
        intent_AddRec.putExtra("PATKey", selected_Pat.getPatient_Key());
        startActivity(intent_AddRec);
        patientsListAddRec.clear();
    }
}
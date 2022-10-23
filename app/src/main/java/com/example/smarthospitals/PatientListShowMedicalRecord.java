package com.example.smarthospitals;

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

public class PatientListShowMedicalRecord extends AppCompatActivity implements PatientAdapter.OnItemClickListener {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private List<Patients> patListShowRec;

    private TextView tVPatListShowMedRecDocName, tVPatListShowMedRecPatName;

    private RecyclerView patientListShowMedRecRecyclerView;
    private PatientAdapter patientAdapter;

    private String doctorName = "";
    private String doctorKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_show_medical_record);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patients available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        patListShowRec = new ArrayList<>();

        tVPatListShowMedRecDocName = findViewById(R.id.tvPatListShowMedRecDocName);
        tVPatListShowMedRecPatName = findViewById(R.id.tvPatListShowMedRecPatName);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            doctorName = bundle.getString("DOCName");
            doctorKey = bundle.getString("DOCKey");
        }

        tVPatListShowMedRecDocName.setText("Doctor: " + doctorName);
        tVPatListShowMedRecPatName.setText("No registered Medical Records!!");

        patientListShowMedRecRecyclerView = findViewById(R.id.patListShowMedRecRecyclerView);
        patientListShowMedRecRecyclerView.setHasFixedSize(true);
        patientListShowMedRecRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        patientAdapter = new PatientAdapter(PatientListShowMedicalRecord.this, patListShowRec);
        patientListShowMedRecRecyclerView.setAdapter(patientAdapter);
        patientAdapter.setOnItmClickListener(PatientListShowMedicalRecord.this);
    }

    @Override
    public void onStart(){
        super.onStart();
        loadPatientDataMedRec();
    }

    private void loadPatientDataMedRec(){

        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patListShowRec.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    final Patients patAddRec = postSnapshot.getValue(Patients.class);

                    if(patAddRec!=null){
                        if(patAddRec.getPatDoc_Key().equals(doctorKey)) {
                            patAddRec.setPatient_Key(postSnapshot.getKey());
                            patListShowRec.add(patAddRec);
                            tVPatListShowMedRecPatName.setText("Select your Patient");
                        }
                    }
                }

                patientAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientListShowMedicalRecord.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position){
        Patients selected_Pat = patListShowRec.get(position);
        Intent intent_ShowRec = new Intent(PatientListShowMedicalRecord.this, MedicalRecordList.class);
        intent_ShowRec.putExtra("DOCName", selected_Pat.getPatDoc_Name());
        intent_ShowRec.putExtra("PATName", selected_Pat.getPatFirst_Name()+ " " + selected_Pat.getPatLast_Name());
        intent_ShowRec.putExtra("PATKey", selected_Pat.getPatient_Key());
        startActivity(intent_ShowRec);
        patListShowRec.clear();
    }
}
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

public class DoctorListHospital extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private TextView tVDocListHospName;

    private RecyclerView doctorHospRecyclerView;
    private DoctorAdapter doctorAdapter;

    private List<Doctors> doctorsListHosp;

    private String hospitalName = "";
    private String hospitalKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list_hospital);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Doctors available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVDocListHospName = findViewById(R.id.tvDocListHospName);

        doctorsListHosp = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            hospitalName = bundle.getString("HOSPName");
            hospitalKey = bundle.getString("HOSPKey");
        }

        tVDocListHospName.setText("No registered Doctors!!");

        doctorHospRecyclerView = findViewById(R.id.docHospRecyclerView);
        doctorHospRecyclerView.setHasFixedSize(true);
        doctorHospRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorAdapter = new DoctorAdapter(DoctorListHospital.this, doctorsListHosp);
        doctorHospRecyclerView.setAdapter(doctorAdapter);


        Button buttonHospListAddDoctor = findViewById(R.id.btnHospListAddDoctor);
        buttonHospListAddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_Doctors = new Intent(DoctorListHospital.this, DoctorRegistration.class);
                add_Doctors.putExtra("HOSPName", hospitalName);
                add_Doctors.putExtra("HOSPKey", hospitalKey);
                startActivity(add_Doctors);
            }
        });

        Button buttonHospListBackHosp = findViewById(R.id.btnHospListBackHosp);
        buttonHospListBackHosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorListHospital.this,HospitalPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadDoctorsListHospital();
    }

    public void loadDoctorsListHospital() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorsListHosp.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Doctors doctors_Data = postSnapshot.getValue(Doctors.class);

                    if (doctors_Data != null) {
                        if(doctors_Data.getDocHosp_Key().equals(hospitalKey)){
                            doctors_Data.setDoc_Key(postSnapshot.getKey());
                            doctorsListHosp.add(doctors_Data);
                            tVDocListHospName.setText("Doctors list: " + doctors_Data.getDocHosp_Name() + " Hospital");
                        }
                    }
                }

                doctorAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorListHospital.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
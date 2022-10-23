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

public class HospitalListAddDoctor extends AppCompatActivity implements HospitalAdapter.OnItemClickListener {

    private DatabaseReference hospitalDatabaseReference;
    private ValueEventListener hospitalEventListener;
    private List<Hospitals> hospitalListAddDoc;

    private TextView tVHospListAddDoctor;

    private RecyclerView hospitalRecyclerView;
    private HospitalAdapter hospitalAdapter;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list_add_doctor);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Hospitals available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        hospitalListAddDoc = new ArrayList<>();

        tVHospListAddDoctor = findViewById(R.id.tvHospListAddDoctor);

        tVHospListAddDoctor.setText("No Hospitals; Click to add Hosp!");
        tVHospListAddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HospitalListAddDoctor.this, HospitalRegistration.class));
            }
        });

        hospitalRecyclerView = findViewById(R.id.hospRecyclerView);
        hospitalRecyclerView.setHasFixedSize(true);
        hospitalRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        hospitalAdapter = new HospitalAdapter(HospitalListAddDoctor.this, hospitalListAddDoc);
        hospitalRecyclerView.setAdapter(hospitalAdapter);
        hospitalAdapter.setOnItmClickListener(HospitalListAddDoctor.this);
    }

    @Override
    public void onStart(){
        super.onStart();
        loadHospitalData();
    }

    private void loadHospitalData(){

        //initialize the Hospitals database
        hospitalDatabaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        hospitalEventListener = hospitalDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalListAddDoc.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Hospitals hospitals_Data = postSnapshot.getValue(Hospitals.class);

                    if (hospitals_Data != null) {
                        hospitals_Data.setHosp_Key(postSnapshot.getKey());
                        hospitalListAddDoc.add(hospitals_Data);
                        tVHospListAddDoctor.setText("Select your Hospital!");
                        tVHospListAddDoctor.setEnabled(false);
                    }
                }
                hospitalAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalListAddDoctor.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Hospital onClick
    @Override
    public void onItemClick(int position) {
        Hospitals selected_Hosp = hospitalListAddDoc.get(position);

        Intent intent_docReg = new Intent(HospitalListAddDoctor.this,DoctorRegistration.class);
        intent_docReg.putExtra("HOSPName", selected_Hosp.getHosp_Name());
        intent_docReg.putExtra("HOSPKey", selected_Hosp.getHosp_Key());
        startActivity(intent_docReg);
        hospitalListAddDoc.clear();
    }
}
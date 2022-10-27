package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class MedicalRecordList extends AppCompatActivity implements MedicalRecordAdapter.OnItemClickListener {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private TextView tVMedRecListDocName, tVMedRecListPatName;

    private RecyclerView medRecordRecyclerView;
    private MedicalRecordAdapter medicalRecordAdapter;

    public List<MedicalRecords> medRecList;

    private String doctorName ="";
    private String patientName = "";
    private String patientKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Medical Records");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        medRecList = new ArrayList<>();

        tVMedRecListDocName = findViewById(R.id.tvMedRecListDocName);
        tVMedRecListPatName = findViewById(R.id.tvMedRecListPatName);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            doctorName = bundle.getString("DOCName");
            patientName = bundle.getString("PATName");
            patientKey = bundle.getString("PATKey");
        }

        tVMedRecListDocName.setText("Doctor: " + doctorName);
        tVMedRecListPatName.setText("Patient: " + patientName);

        medRecordRecyclerView = findViewById(R.id.medRecRecyclerView);
        medRecordRecyclerView.setHasFixedSize(true);
        medRecordRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicalRecordAdapter = new MedicalRecordAdapter(MedicalRecordList.this,medRecList);
        medRecordRecyclerView.setAdapter(medicalRecordAdapter);
        medicalRecordAdapter.setOnItmClickListener(MedicalRecordList .this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Records");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medRecList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    MedicalRecords medRecord = postSnapshot.getValue(MedicalRecords.class);
                    if(medRecord != null) {
                        if (medRecord.getRecordPat_Key().equals(patientKey)) {
                            medRecord.setRecord_Key(postSnapshot.getKey());
                            medRecList.add(medRecord);
                        }
                    }
                }

                medicalRecordAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MedicalRecordList.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateClick(int position) {
        Intent intent_Update = new Intent(MedicalRecordList.this, UpdateMedicalRecord.class);

        MedicalRecords medRec_update = medRecList.get(position);
        intent_Update.putExtra("MEDRECDateBirth", medRec_update.getMedRecord_DateBirth());
        intent_Update.putExtra("MEDRECPPSNo", medRec_update.getMedRecord_PPS());
        intent_Update.putExtra("MEDRECAddress", medRec_update.getMedRecord_Address());
        intent_Update.putExtra("MEDRECKey", medRec_update.getRecord_Key());
        startActivity(intent_Update);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(MedicalRecordList.this);
        builderAlert.setMessage("Are sure to delete this item?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MedicalRecords selectedMedRec = medRecList.get(position);
                        final String selectedKey = selectedMedRec.getRecord_Key();

                        databaseReference.child(selectedKey).removeValue();
                        Toast.makeText(MedicalRecordList.this, "The item has been deleted successfully ", Toast.LENGTH_SHORT).show();
                    }
                });

        builderAlert.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builderAlert.create();
        alertDialog.show();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        databaseReference.removeEventListener(eventListener);
    }
}
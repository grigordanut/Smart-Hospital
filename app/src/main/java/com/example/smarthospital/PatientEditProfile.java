package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PatientEditProfile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseRefUpdate;
    private DatabaseReference databaseRefLoad;
    private ValueEventListener eventListener;

    private TextInputEditText patCardCodeUp, patUniqueCodeUp, patFirstNameUp, patLastNameUp, patEmailUp;

    private String pat_CardCodeUp, pat_UniqueCodeUp, pat_FirstNameUp, pat_LastNameUp, pat_EmailUp;

    private TextView tVPatientEditProfile;

    private String patHosp_Name = "";
    private String patHosp_Key = "";

    private String patDoctorName = "";
    private String patDoctorKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("PATIENT: edit Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Retrieve data from Patients database and load the user details in the edit texts
        databaseRefLoad = FirebaseDatabase.getInstance().getReference("Patients");

        //Upload Hospital updated data
        databaseRefUpdate = FirebaseDatabase.getInstance().getReference("Patients");

        //initialise the variables
        tVPatientEditProfile = findViewById(R.id.tvPatEditProfile);

        patCardCodeUp = findViewById(R.id.etPatCardCodeUp);
        patUniqueCodeUp = findViewById(R.id.etPatUniqueCodeUp);
        patFirstNameUp = findViewById(R.id.etPatFirstNameUp);
        patLastNameUp = findViewById(R.id.etPatLastNameUp);
        patEmailUp = findViewById(R.id.etPatEmailUp);

        patEmailUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                alertEmailChangePlace();
                return true;
            }
        });

        //save the user details in the database
        Button btn_PatSaveUp = (Button) findViewById(R.id.btnPatSaveUp);
        btn_PatSaveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatientDetails();
            }
        });
    }

    private void updatePatientDetails() {

        if (validateHospUpdatedData()) {

            pat_CardCodeUp = Objects.requireNonNull(patCardCodeUp.getText()).toString().trim();
            pat_UniqueCodeUp = Objects.requireNonNull(patUniqueCodeUp.getText()).toString().trim();
            pat_FirstNameUp = Objects.requireNonNull(patFirstNameUp.getText()).toString().trim();
            pat_LastNameUp = Objects.requireNonNull(patLastNameUp.getText()).toString().trim();
            pat_EmailUp = Objects.requireNonNull(patEmailUp.getText()).toString().trim();

            String user_Id = firebaseUser.getUid();

            Patients pat_Data = new Patients(pat_CardCodeUp, pat_UniqueCodeUp, pat_FirstNameUp, pat_LastNameUp, pat_EmailUp, patHosp_Name, patHosp_Key, patDoctorName, patDoctorKey);

            databaseRefUpdate.child(user_Id).setValue(pat_Data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        Toast.makeText(PatientEditProfile.this, "Your details has been successfully changed!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PatientEditProfile.this, PatientPage.class));
                        finish();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(PatientEditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private Boolean validateHospUpdatedData() {

        boolean result = false;

        pat_UniqueCodeUp = Objects.requireNonNull(patUniqueCodeUp.getText()).toString().trim();
        pat_FirstNameUp = Objects.requireNonNull(patFirstNameUp.getText()).toString().trim();
        pat_LastNameUp = Objects.requireNonNull(patLastNameUp.getText()).toString().trim();

        if (TextUtils.isEmpty(pat_UniqueCodeUp)) {
            patUniqueCodeUp.setError("Enter Patient's Unique Code");
            patUniqueCodeUp.requestFocus();
        } else if (TextUtils.isEmpty(pat_FirstNameUp)) {
            patFirstNameUp.setError("Enter Patient's First Name");
            patFirstNameUp.requestFocus();
        } else if (TextUtils.isEmpty(pat_LastNameUp)) {
            patLastNameUp.setError("Enter Patient's Last Name");
            patLastNameUp.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadHospitalData();
    }

    private void loadHospitalData() {

        eventListener = databaseRefLoad.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Patients pat_Data = postSnapshot.getValue(Patients.class);

                    if (pat_Data != null) {
                        if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                            patCardCodeUp.setText(pat_Data.getPatCard_Code());
                            patUniqueCodeUp.setText(pat_Data.getPatUnique_Code());
                            patFirstNameUp.setText(pat_Data.getPatFirst_Name());
                            patLastNameUp.setText(pat_Data.getPatLast_Name());
                            patEmailUp.setText(pat_Data.getPatEmail_Address());
                            tVPatientEditProfile.setText("Edit profile of: " + pat_Data.getPatFirst_Name() + " " + pat_Data.getPatLast_Name());

                            patHosp_Name = pat_Data.getPatHosp_Name();
                            patHosp_Key = pat_Data.getPatHosp_Key();

                            patDoctorName = pat_Data.getPatDoc_Name();
                            patDoctorKey = pat_Data.getPatDoc_Key();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientEditProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alertEmailChangePlace() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("The Email Address cannot be change here.\nPlease use Change Email option!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_edit_profile, menu);
        return true;
    }

    private void patEditProfileGoBack() {
        startActivity(new Intent(PatientEditProfile.this, HospitalPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.patientEditProfile_goBack) {
            patEditProfileGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}
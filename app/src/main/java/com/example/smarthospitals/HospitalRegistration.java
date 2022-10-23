package com.example.smarthospitals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HospitalRegistration extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference dbRefHospUpload;
    private DatabaseReference dbRefHospCheck;

    private TextInputEditText hospUniqueCode, hospNameReg, hospEmailReg, hospPassReg, hospConfPassReg;

    private String hosp_UniqueCodeReg, hosp_NameReg, hosp_EmailReg, hosp_PassReg, hosp_ConfPassReg;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_registration);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Hospital Registration");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //Create Hospitals table into database
        dbRefHospUpload = FirebaseDatabase.getInstance().getReference("Hospitals");

        //Checks if the Hospital name already exists
        dbRefHospCheck = FirebaseDatabase.getInstance().getReference("Hospitals");

        hospUniqueCode = findViewById(R.id.etHospUniqueCodeReg);
        hospNameReg = findViewById(R.id.etHospNameReg);
        hospEmailReg = findViewById(R.id.etHospEmailReg);
        hospPassReg = findViewById(R.id.etHospPassReg);
        hospConfPassReg = findViewById(R.id.etHospConfPassReg);

        Button btn_hospLogReg = findViewById(R.id.btnHospLogReg);
        btn_hospLogReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HospitalRegistration.this, Login.class));
            }
        });

        Button btn_hospReg = findViewById(R.id.btnHospReg);
        btn_hospReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkHospitalName();
            }
        });
    }

    private void checkHospitalName() {

        final String hosp_NameCheck = Objects.requireNonNull(hospNameReg.getText()).toString().trim();

        dbRefHospCheck.orderByChild("hosp_Name").equalTo(hosp_NameCheck).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    alertHospitalExists();
                } else {
                    registerHospital();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalRegistration.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerHospital() {

        if (validateHospitalData()) {

            progressDialog.setMessage("Registering Hospital details");
            progressDialog.show();

            //Create new Hospital user into database
            firebaseAuth.createUserWithEmailAndPassword(hosp_EmailReg, hosp_PassReg).addOnCompleteListener(HospitalRegistration.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        uploadHospitalData();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(HospitalRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    private void uploadHospitalData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            String hosp_Id = firebaseUser.getUid();
            Hospitals hosp_Data = new Hospitals(hosp_UniqueCodeReg, hosp_NameReg, hosp_EmailReg);

            dbRefHospUpload.child(hosp_Id).setValue(hosp_Data).addOnCompleteListener(HospitalRegistration.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        firebaseUser.sendEmailVerification();

                        Toast.makeText(HospitalRegistration.this, "Hospital successfully registered.\nVerification email has been sent!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HospitalRegistration.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(HospitalRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    private boolean validateHospitalData() {

        boolean result = false;

        hosp_UniqueCodeReg = Objects.requireNonNull(hospUniqueCode.getText()).toString().trim();
        hosp_NameReg = Objects.requireNonNull(hospNameReg.getText()).toString().trim();
        hosp_EmailReg = Objects.requireNonNull(hospEmailReg.getText()).toString().trim();
        hosp_PassReg = Objects.requireNonNull(hospPassReg.getText()).toString().trim();
        hosp_ConfPassReg = Objects.requireNonNull(hospConfPassReg.getText()).toString().trim();

        if (TextUtils.isEmpty(hosp_UniqueCodeReg)) {
            hospUniqueCode.setError("Enter Hospital's Unique Code");
            hospUniqueCode.requestFocus();
        } else if (TextUtils.isEmpty(hosp_NameReg)) {
            hospNameReg.setError("Enter Hospital's Name");
            hospNameReg.requestFocus();
        } else if (TextUtils.isEmpty(hosp_EmailReg)) {
            hospEmailReg.setError("Enter Hospital's Email Address");
            hospEmailReg.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(hosp_EmailReg).matches()) {
            hospEmailReg.setError("Enter a valid Email Address");
            hospEmailReg.requestFocus();
        } else if (TextUtils.isEmpty(hosp_PassReg)) {
            hospPassReg.setError("Enter Password");
            hospPassReg.requestFocus();
        } else if (hosp_PassReg.length() < 6) {
            hospPassReg.setError("Password too short, enter minimum 6 character long");
            hospPassReg.requestFocus();
        } else if (TextUtils.isEmpty(hosp_ConfPassReg)) {
            hospConfPassReg.setError("Enter Confirm Password");
            hospConfPassReg.requestFocus();
        } else if (!hosp_ConfPassReg.equals(hosp_PassReg)) {
            hospConfPassReg.setError("The Confirm Password does not match Password");
            hospConfPassReg.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void alertHospitalExists() {

        final String hosp_NameCheckAlert = Objects.requireNonNull(hospNameReg.getText()).toString().trim();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Hospital Registration")
                .setMessage("The Hospital: " + hosp_NameCheckAlert + " already exists!")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
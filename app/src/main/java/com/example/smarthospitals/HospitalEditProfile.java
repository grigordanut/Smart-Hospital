package com.example.smarthospitals;

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
import com.google.android.gms.tasks.OnFailureListener;
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

public class HospitalEditProfile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseRefUpdate;
    private DatabaseReference databaseRefLoad;
    private ValueEventListener eventListener;

    private TextInputEditText hospUniqueCodeUp, hospNameUp, hospEmailUp;

    private String hosp_UniqueCodeUp, hosp_NameUp, hosp_EmailUp;

    private TextView tVHospitalEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("HOSPITAL: edit Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Retrieve data from Hospitals database and load the user details in the edit texts
        databaseRefLoad = FirebaseDatabase.getInstance().getReference("Hospitals");

        //Upload Hospital updated data
        databaseRefUpdate = FirebaseDatabase.getInstance().getReference("Hospitals");

        //initialise the variables
        tVHospitalEditProfile = findViewById(R.id.tvHospitalEditProfile);

        hospUniqueCodeUp = findViewById(R.id.etHospUniqueCodeUp);
        hospNameUp = findViewById(R.id.etHospNameUp);
        hospEmailUp = findViewById(R.id.etHospEmailUp);

        hospEmailUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                alertEmailChangePlace();
                return true;
            }
        });

        //save the user details in the database
        Button buttonHospSaveUp = (Button) findViewById(R.id.btnHospSaveUp);
        buttonHospSaveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHospitalDetails();
            }
        });
    }

    private void updateHospitalDetails() {

        if (validateHospUpdatedData()) {

            hosp_UniqueCodeUp = Objects.requireNonNull(hospUniqueCodeUp.getText()).toString().trim();
            hosp_NameUp = Objects.requireNonNull(hospNameUp.getText()).toString().trim();
            hosp_EmailUp = Objects.requireNonNull(hospEmailUp.getText()).toString().trim();

            String user_Id = firebaseUser.getUid();

            Hospitals hosp_Data = new Hospitals(hosp_UniqueCodeUp, hosp_NameUp, hosp_EmailUp);

            databaseRefUpdate.child(user_Id).setValue(hosp_Data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(HospitalEditProfile.this, "Your details has been successfully changed!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(HospitalEditProfile.this, HospitalPage.class));
                                finish();
                            }

                            else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(HospitalEditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HospitalEditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private Boolean validateHospUpdatedData() {

        boolean result = false;

        hosp_UniqueCodeUp = Objects.requireNonNull(hospUniqueCodeUp.getText()).toString().trim();
        hosp_NameUp = Objects.requireNonNull(hospNameUp.getText()).toString().trim();

        if (TextUtils.isEmpty(hosp_UniqueCodeUp)) {
            hospUniqueCodeUp.setError("Enter Hospital's Unique Code");
            hospUniqueCodeUp.requestFocus();
        } else if (TextUtils.isEmpty(hosp_NameUp)) {
            hospNameUp.setError("Enter Hospital's Name");
            hospNameUp.requestFocus();
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

                    Hospitals hosp_Data = postSnapshot.getValue(Hospitals.class);

                    if (hosp_Data != null) {
                        if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                            hospUniqueCodeUp.setText(hosp_Data.getHosp_UniqueCode());
                            hospNameUp.setText(hosp_Data.getHosp_Name());
                            hospEmailUp.setText(hosp_Data.getHosp_Email());
                            tVHospitalEditProfile.setText("Edit profile: " + hosp_Data.getHosp_Name() + " Hospital");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalEditProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.menu_hospital_edit_profile, menu);
        return true;
    }

    private void hospEditProfileGoBack() {
        startActivity(new Intent(HospitalEditProfile.this, HospitalPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.hospitalEditProfile_goBack) {
            hospEditProfileGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}
package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PatientPage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseRefPatient;
    private ValueEventListener evListenerPatient;

    private DatabaseReference databaseRefMedRec;

    private TextView tVWelcomePatient;

    private Button btnSee_MedRecord;

    private String pat_firstName = "";
    private String pat_lastName = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("PATIENT: maim page");

        progressDialog = new ProgressDialog(this);

        //initialise the variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tVWelcomePatient = findViewById(R.id.tvWelcomePatient);

        btnSee_MedRecord = findViewById(R.id.btnSeeMedRecord);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPatientData();
    }

    private void loadPatientData() {

        //retrieve data from database into text views
        databaseRefPatient = FirebaseDatabase.getInstance().getReference("Patients");

        evListenerPatient = databaseRefPatient.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final Patients pat_Data = postSnapshot.getValue(Patients.class);

                    if (pat_Data != null) {
                        if (firebaseUser != null) {
                            if (firebaseUser.getUid().equals(postSnapshot.getKey())) {

                                pat_firstName = pat_Data.getPatFirst_Name();
                                pat_lastName = pat_Data.getPatLast_Name();

                                tVWelcomePatient.setText("Welcome: " + pat_firstName + " " + pat_lastName);

                                btnSee_MedRecord.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent rec_Pat = new Intent(PatientPage.this, MedicalRecordPatient.class);
                                        rec_Pat.putExtra("DOCName", pat_Data.getPatDoc_Name());
                                        rec_Pat.putExtra("PATName", pat_Data.getPatFirst_Name() + " " + pat_Data.getPatLast_Name());
                                        rec_Pat.putExtra("PATKey", firebaseAuth.getUid());
                                        startActivity(rec_Pat);
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Patients log out
    private void patientLogOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(PatientPage.this, MainActivity.class));
        finish();
    }

    private void patientEditProfile() {
        startActivity(new Intent(PatientPage.this, PatientEditProfile.class));
        finish();
    }

    private void patientChangeEmail() {
        startActivity(new Intent(PatientPage.this, PatientChangeEmail.class));
        finish();
    }

    private void patientChangePassword() {
        startActivity(new Intent(PatientPage.this, PatientChangePassword.class));
        finish();
    }

    //Delete Patient
    public void confirmDeletionPatient() {

        progressDialog.setMessage("The Patient is deleting!");
        progressDialog.show();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Are sure to delete the Patient:\n" + pat_firstName + " " + pat_lastName + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {

                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                firebaseAuth.signOut();
                                final String selectedPat_Key = firebaseUser.getUid();
                                databaseRefPatient.child(selectedPat_Key).removeValue();

                                deleteMedicalRecord();

                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(PatientPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    });

                })

                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        progressDialog.dismiss();

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteMedicalRecord() {

        databaseRefMedRec = FirebaseDatabase.getInstance().getReference("Medical Records");
        databaseRefMedRec.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MedicalRecords medRec_Data = postSnapshot.getValue(MedicalRecords.class);

                    if (medRec_Data != null) {

                        if (medRec_Data.getRecordPat_Key().equals(firebaseUser.getUid())) {
                            databaseRefMedRec.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(PatientPage.this, "The Patient has been successfully deleted!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(PatientPage.this, MainActivity.class));
                                                finish();

                                            } else {
                                                try {
                                                    throw Objects.requireNonNull(task.getException());
                                                } catch (Exception e) {
                                                    Toast.makeText(PatientPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PatientPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.patient_logOut) {
            alertDialogPatientLogout();
        }

        if (item.getItemId() == R.id.patient_editProfile) {
            patientEditProfile();
        }

        if (item.getItemId() == R.id.patient_changeEmail) {
            patientChangeEmail();
        }

        if (item.getItemId() == R.id.patient_changePassword) {
            patientChangePassword();
        }

        if (item.getItemId() == R.id.patient_deleteAccount) {
            confirmDeletionPatient();
        }


        return super.onOptionsItemSelected(item);
    }

    private void alertDialogPatientLogout() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientPage.this);
        alertDialogBuilder
                .setMessage("Are sure to Log Out?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                patientLogOut();
                            }
                        })

                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }
}
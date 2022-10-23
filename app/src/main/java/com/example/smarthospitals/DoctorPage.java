package com.example.smarthospitals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorPage extends AppCompatActivity {

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    //Retrieve and Display data from Doctors database
    private DatabaseReference doctorDatabaseReference;
    private ValueEventListener doctorEventListener;

    //Retrieve and Display data from Patients database
    private DatabaseReference patientDatabaseReference;
    private ValueEventListener patientEventListener;
    private List<Patients> patientsList;

    private TextView tVWelcomeDoctor, tVShowDoctorDetails, tVDoctorPatientsAv;

    private int numberPatientsAv;

    private String patHospital_Key;

    //Declaring some objects
    private DrawerLayout drawerLayoutDoctor;
    private ActionBarDrawerToggle drawerToggleDoctor;
    private NavigationView navigationViewDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("DOCTOR: main page");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        //Retrieve data from Patients database
        patientDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        patientsList = new ArrayList<>();

        tVWelcomeDoctor = findViewById(R.id.tvWelcomeDoctor);
        tVShowDoctorDetails = findViewById(R.id.tvShowDoctorDetails);
        tVDoctorPatientsAv = findViewById(R.id.tvDoctorPatientsAv);

        drawerLayoutDoctor = findViewById(R.id.activity_doctor_page);
        navigationViewDoctor = findViewById(R.id.navViewDoctorPage);

        drawerToggleDoctor = new ActionBarDrawerToggle(this, drawerLayoutDoctor, R.string.open_doctorPage, R.string.close_doctorPage);

        drawerLayoutDoctor.addDrawerListener(drawerToggleDoctor);
        drawerToggleDoctor.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //retrieve data from database into text views
        doctorDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        doctorEventListener = doctorDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_Doctor : dataSnapshot.getChildren()) {
                    final FirebaseUser doctor_Db = firebaseAuth.getCurrentUser();

                    final Doctors doctors_Data = ds_Doctor.getValue(Doctors.class);

                    assert doctor_Db != null;
                    assert doctors_Data != null;
                    if (doctor_Db.getUid().equals(ds_Doctor.getKey())) {
                        tVWelcomeDoctor.setText("Welcome: " + doctors_Data.getDocFirst_Name() + " " + doctors_Data.getDocLast_Name());
                        tVShowDoctorDetails.setText("Doctors Name: \n" + doctors_Data.getDocFirst_Name() + " "
                                + doctors_Data.getDocLast_Name() + "\n\nEmail: \n" + doctors_Data.getDocEmail_Address());

                        //Adding Click Events to our navigation drawer item
                        navigationViewDoctor.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @SuppressLint("NonConstantResourceId")
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    //Add Patients
                                    case R.id.doctor_addPatients:
                                        Intent add_Patients = new Intent(DoctorPage.this, PatientRegistration.class);

                                        add_Patients.putExtra("HOSPName", doctors_Data.getDocHosp_Name());
                                        add_Patients.putExtra("HOSPKey", doctors_Data.getDocHosp_Key());

                                        add_Patients.putExtra("DOCName", doctors_Data.getDocFirst_Name() + " " + doctors_Data.getDocLast_Name());
                                        add_Patients.putExtra("DOCKey", doctor_Db.getUid());

                                        startActivity(add_Patients);
                                        break;

                                    //Show Patients List
                                    case R.id.doctorShow_patientsList:
                                        Intent pat_List = new Intent(DoctorPage.this, PatientListDoctor.class);
                                        pat_List.putExtra("HOSPName", doctors_Data.getDocHosp_Name());
                                        pat_List.putExtra("HOSPKey", doctors_Data.getDocHosp_Key());

                                        pat_List.putExtra("DOCName", doctors_Data.getDocFirst_Name() + " " + doctors_Data.getDocLast_Name());
                                        pat_List.putExtra("DOCKey", doctor_Db.getUid());
                                        startActivity(pat_List);
                                        break;

                                    //Add Medical Records
                                    case R.id.doctor_addMedicalRecords:
                                        Intent add_Record = new Intent(DoctorPage.this, PatientListAddMedicalRecord.class);
                                        add_Record.putExtra("DOCName", doctors_Data.getDocFirst_Name() + " " + doctors_Data.getDocLast_Name());
                                        add_Record.putExtra("DOCKey", doctor_Db.getUid());
                                        startActivity(add_Record);
                                        break;

                                    //Show Medical Records
                                    case R.id.doctorShow_recordsList:
                                        Intent show_Record = new Intent(DoctorPage.this, PatientListShowMedicalRecord.class);
                                        show_Record.putExtra("DOCName", doctors_Data.getDocFirst_Name() + " " + doctors_Data.getDocLast_Name());
                                        show_Record.putExtra("DOCKey", doctor_Db.getUid());
                                        startActivity(show_Record);
                                        break;

                                    default:
                                        return true;
                                }
                                return true;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor_page, menu);
        return true;
    }

    public void doctorLogOut(){
        firebaseAuth.signOut();
        startActivity(new Intent(DoctorPage.this, MainActivity.class));
        finish();
    }

    public void doctorEditProfile(){
        startActivity(new Intent(DoctorPage.this, DoctorEditProfile.class));
        finish();
    }

    public void doctorChangeEmail(){
        startActivity(new Intent(DoctorPage.this, DoctorChangeEmail.class));
        finish();
    }

    public void doctorChangePassword(){
        startActivity(new Intent(DoctorPage.this, DoctorChangePassword.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleDoctor.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.doctor_logOut){
            alertDialogDoctorLogout();
        }

        if (item.getItemId() == R.id.doctor_editProfile){
            doctorEditProfile();
        }

        if (item.getItemId() == R.id.doctor_changeEmail){
            doctorChangeEmail();
        }

        if (item.getItemId() == R.id.doctor_changePassword){
            doctorChangePassword();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPatientsAv();
    }

    private void loadPatientsAv() {

        patientEventListener = patientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Patients pat_Data = postSnapshot.getValue(Patients.class);

                    assert pat_Data != null;
                    patHospital_Key = pat_Data.getPatHosp_Key();

                    if (pat_Data.getPatHosp_Key().equals(patHospital_Key)){
                        pat_Data.setPatient_Key(postSnapshot.getKey());
                        patientsList.add(pat_Data);
                        numberPatientsAv = patientsList.size();
                        tVDoctorPatientsAv.setText(String.valueOf(numberPatientsAv));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alertDialogDoctorLogout(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DoctorPage.this);
        alertDialogBuilder
                .setMessage("Are sure to Log Out?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                doctorLogOut();
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
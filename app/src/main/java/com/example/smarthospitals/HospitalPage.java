package com.example.smarthospitals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

public class HospitalPage extends AppCompatActivity {

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //Retrieve and Display data from Hospitals database
    private DatabaseReference hospitalDatabaseReference;
    private ValueEventListener hospitalEventListener;

    //Retrieve and Display data from Doctors database
    private DatabaseReference doctorDatabaseReference;
    private ValueEventListener doctorEventListener;
    private List<Doctors> doctorsList;

    //Retrieve and display data from Patients database
    private DatabaseReference patientDatabaseReference;
    private ValueEventListener patientEventListener;
    private List<Patients> patientsList;

    private int numberDoctorsAv;
    private int numberPatientsAv;

    private TextView tVWelcomeHospital, tVShowHospitalDetails, tVHospDoctorsAv, tVHospPatientsAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutHospital;
    private ActionBarDrawerToggle drawerToggleHospital;
    private NavigationView navigationViewUserRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("HOSPITAL: maim page");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Retrieve data from Hospitals database
        hospitalDatabaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        //Retrieve data from Doctors database
        doctorDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        //Retrieve data from Patients database
        patientDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        doctorsList = new ArrayList<>();
        patientsList = new ArrayList<>();

        tVWelcomeHospital = findViewById(R.id.tvWelcomeHospital);
        tVShowHospitalDetails = findViewById(R.id.tvShowHospitalDetails);
        tVHospDoctorsAv = findViewById(R.id.tvHospDoctorsAv);
        tVHospPatientsAv = findViewById(R.id.tvHospPatientsAv);

        drawerLayoutHospital = findViewById(R.id.activity_hospital_page);
        navigationViewUserRent = findViewById(R.id.navViewHospPage);

        drawerToggleHospital = new ActionBarDrawerToggle(this, drawerLayoutHospital, R.string.open_doctorPage, R.string.close_doctorPage);

        drawerLayoutHospital.addDrawerListener(drawerToggleHospital);
        drawerToggleHospital.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        hospitalEventListener = hospitalDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Hospitals hosp_Data = postSnapshot.getValue(Hospitals.class);

                    if (hosp_Data != null) {
                        if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                            tVWelcomeHospital.setText("Welcome to: " + hosp_Data.getHosp_Name() + " Hospital!");
                            tVShowHospitalDetails.setText("Hospital Name: \n" + hosp_Data.getHosp_Name() + "\n\nEmail: \n" + hosp_Data.getHosp_Email());

                            //Adding Click Events to our navigation drawer item
                            navigationViewUserRent.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                @SuppressLint("NonConstantResourceId")
                                @Override
                                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                    int id = item.getItemId();
                                    switch (id) {
                                        //Add Doctors
                                        case R.id.hospital_addDoctors:
                                            Intent add_Doctors = new Intent(HospitalPage.this, DoctorRegistration.class);
                                            add_Doctors.putExtra("HOSPName", hosp_Data.getHosp_Name());
                                            add_Doctors.putExtra("HOSPKey", firebaseUser.getUid());
                                            startActivity(add_Doctors);
                                            break;

                                        //Show Doctors List
                                        case R.id.hospitalShow_doctorsList:
                                            Intent doc_List = new Intent(HospitalPage.this, DoctorListHospital.class);
                                            doc_List.putExtra("HOSPName", hosp_Data.getHosp_Name());
                                            doc_List.putExtra("HOSPKey", firebaseUser.getUid());
                                            startActivity(doc_List);
                                            break;

                                        //Show Patients List
                                        case R.id.hospShow_patientList:
                                            Intent pat_List = new Intent(HospitalPage.this, PatientListHospital.class);
                                            pat_List.putExtra("HOSPName", hosp_Data.getHosp_Name());
                                            pat_List.putExtra("HOSPKey", firebaseUser.getUid());
                                            startActivity(pat_List);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hospital_page, menu);
        return true;
    }

    public void hospitalLogOut() {
        alertDialogHospLogout();
    }

    public void hospitalEditProfile() {
        startActivity(new Intent(HospitalPage.this, HospitalEditProfile.class));
        finish();
    }

    public void hospitalChangeEmail() {
        startActivity(new Intent(HospitalPage.this, HospitalChangeEmail.class));
        finish();
    }

    public void hospitalChangePassword() {
        startActivity(new Intent(HospitalPage.this, HospitalChangePassword.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleHospital.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.hospital_logOut) {
            hospitalLogOut();
        }

        if (item.getItemId() == R.id.hospital_editProfile) {
            hospitalEditProfile();
        }

        if (item.getItemId() == R.id.hospital_changeEmail) {
            hospitalChangeEmail();
        }

        if (item.getItemId() == R.id.hospital_changePassword) {
            hospitalChangePassword();
        }

        return super.onOptionsItemSelected(item);
    }

    private void alertDialogHospLogout() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HospitalPage.this);
        alertDialogBuilder
                .setTitle("Log out Hospital")
                .setMessage("Are sure to Log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    firebaseAuth.signOut();
                    startActivity(new Intent(HospitalPage.this, MainActivity.class));
                    finish();
                })

                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadDoctorsAv();
        loadPatientsAv();
    }

    private void loadDoctorsAv() {

        doctorEventListener = doctorDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Doctors doctors_Data = postSnapshot.getValue(Doctors.class);

                    if (doctors_Data != null) {
                        if (doctors_Data.getDocHosp_Key().equals(firebaseUser.getUid())) {
                            doctors_Data.setDoc_Key(postSnapshot.getKey());
                            doctorsList.add(doctors_Data);
                            numberDoctorsAv = doctorsList.size();
                            tVHospDoctorsAv.setText(String.valueOf(numberDoctorsAv));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPatientsAv() {

        patientEventListener = patientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Patients pat_Data = postSnapshot.getValue(Patients.class);

                    if (pat_Data != null) {
                        if (pat_Data.getPatHosp_Key().equals(firebaseUser.getUid())) {
                            pat_Data.setPatient_Key(postSnapshot.getKey());
                            patientsList.add(pat_Data);
                            numberPatientsAv = patientsList.size();
                            tVHospPatientsAv.setText(String.valueOf(numberPatientsAv));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class AddMedicalRecord extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private TextView tVMedRecPatName;
    private RadioGroup radioGender;
    private RadioButton genderMale, genderFemale;

    private EditText medRecPPSNo, medRecAddress, medRecDateBirth;

    private String medRec_PPSNo, medRec_Address, medRec_DateBirth;

    private String medRec_Gender;

    String patientNameMedRec = "";
    String patientKeyMedRec = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_record);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Medical Record!");

        progressDialog = new ProgressDialog(this);

        //Create table Medical Record into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Records");

        tVMedRecPatName = findViewById(R.id.tvMedRecPatName);

        radioGender = findViewById(R.id.radioGroupGender);
        genderMale = findViewById(R.id.radioButtonMale);
        genderFemale = findViewById(R.id.radioButtonFemale);

        medRecDateBirth = findViewById(R.id.etDateBirthMedRecord);
        medRecDateBirth.setFocusable(false);
        medRecPPSNo = findViewById(R.id.etPPSMedRecord);
        medRecAddress = findViewById(R.id.etAddressMedRecord);

        medRecDateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDateOfBirth();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patientNameMedRec = bundle.getString("PATName");
            patientKeyMedRec = bundle.getString("PATKey");
        }

        tVMedRecPatName.setText("Patient: " + patientNameMedRec);

        Button btn_SaveMedRecord = findViewById(R.id.btnSaveMedRecord);
        btn_SaveMedRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMedicalRecordData();
            }
        });
    }

    private void uploadMedicalRecordData() {

        if (validateMedRecordData()) {

            progressDialog.setMessage("The Medical Record is adding!");
            progressDialog.show();

            String record_ID = databaseReference.push().getKey();
            MedicalRecords medRec = new MedicalRecords(medRec_Gender, medRec_DateBirth, medRec_PPSNo, medRec_Address, patientNameMedRec, patientKeyMedRec);

            if (record_ID != null) {
                databaseReference.child(record_ID).setValue(medRec).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AddMedicalRecord.this, "Medical Record successfully added!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddMedicalRecord.this, DoctorPage.class));
                            finish();

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Toast.makeText(AddMedicalRecord.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        progressDialog.dismiss();
                    }
                });
            }
        }
    }

    //validate data in the input fields
    @SuppressLint("NonConstantResourceId")
    private Boolean validateMedRecordData() {

        boolean result = false;

        if (genderMale.isChecked()) {
            medRec_Gender = "Male";
        }
        if (genderFemale.isChecked()) {
            medRec_Gender = "Female";
        }

        medRec_DateBirth = medRecDateBirth.getText().toString().trim();
        medRec_PPSNo = medRecPPSNo.getText().toString().trim();
        medRec_Address = medRecAddress.getText().toString().trim();

        if (TextUtils.isEmpty(medRec_Gender)) {
            alertDialogGender();
        } else if (TextUtils.isEmpty(medRec_DateBirth)) {
            alertDialogDateOfBirthEmpty();
            medRecDateBirth.requestFocus();
        } else if (TextUtils.isEmpty(medRec_PPSNo)) {
            medRecPPSNo.setError("Please enter patient's PPS");
            medRecPPSNo.requestFocus();
        } else if (TextUtils.isEmpty(medRec_Address)) {
            medRecAddress.setError("Please enter patient's Address");
            medRecAddress.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    //Notify if the Gender is missing
    private void alertDialogGender() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Patient Gender")
                .setMessage("Please select the Gender!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    radioGender.requestFocus();
                    dialog.dismiss();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Pick the Patients date of birth
    private void selectDateOfBirth() {

        Calendar calendar = Calendar.getInstance();
        int day  = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog dialog = new DatePickerDialog(AddMedicalRecord.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String picked_Date = dayOfMonth + "/" + (month+1) + "/" + year;
                medRecDateBirth.setText(picked_Date);
            }
        }, year, month, day);
        //dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void alertDialogDateOfBirthEmpty() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Patient Date of Birth")
                .setMessage("The Date of Birth cannot be empty!")
                .setPositiveButton("OK", (dialog, id) -> {
                    selectDateOfBirth();
                    dialog.dismiss();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
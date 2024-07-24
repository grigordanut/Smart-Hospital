package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DoctorRegistration extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    //Declare variables
    private TextInputEditText docUniqueCode, docFirstName, docLastName, docPhone, docEmailReg, docPassReg, docConfPassReg;
    private TextView tVHospNameDoctorReg;

    private String doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, doc_PassReg, doc_ConfPassReg;

    private String docHospital_Name = "";
    private String docHospital_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Doctor Registration");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //Create Doctors table into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        tVHospNameDoctorReg = findViewById(R.id.tvHospNameDoctorReg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            docHospital_Name = bundle.getString("HOSPName");
            docHospital_Key = bundle.getString("HOSPKey");
        }

        tVHospNameDoctorReg.setText("Add doctor to: " + docHospital_Name + " Hospital");

        docUniqueCode = findViewById(R.id.etDocUniqueCode);
        docFirstName = findViewById(R.id.etDocFirstName);
        docLastName = findViewById(R.id.etDocLastName);
        docPhone = findViewById(R.id.etDocPhoneNumber);
        docEmailReg = findViewById(R.id.etDocEmailReg);
        docPassReg = findViewById(R.id.etDocPassReg);
        docConfPassReg = findViewById(R.id.etDocConfPassReg);

        Button btn_docLogReg = findViewById(R.id.btnDocLogReg);
        btn_docLogReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorRegistration.this, Login.class));
                finish();
            }
        });


        Button btn_doctorReg = findViewById(R.id.btnDoctorReg);
        btn_doctorReg.setOnClickListener(v -> registerDoctor());
    }

    private void registerDoctor() {

        if (validateDoctorData()) {

            progressDialog.setMessage("Registering Doctor details!");
            progressDialog.show();

            //Create new Doctor user into database
            firebaseAuth.createUserWithEmailAndPassword(doc_EmailReg, doc_PassReg).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    uploadDoctorData();

                }
                else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {

                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                        TextView text = layout.findViewById(R.id.tvToast);
                        ImageView imageView = layout.findViewById(R.id.imgToast);
                        text.setText(e.getMessage());
                        imageView.setImageResource(R.drawable.baseline_report_gmailerrorred_24);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                }

                progressDialog.dismiss();
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void uploadDoctorData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        String doc_Id = firebaseUser.getUid();

        Doctors doc_Data = new Doctors(doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, docHospital_Name, docHospital_Key);

        databaseReference.child(doc_Id).setValue(doc_Data).addOnCompleteListener(DoctorRegistration.this, task -> {

            if (task.isSuccessful()) {

                firebaseUser.sendEmailVerification();

                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                TextView text = layout.findViewById(R.id.tvToast);
                ImageView imageView = layout.findViewById(R.id.imgToast);
                text.setText("Registration successful. Verification email sent!!");
                imageView.setImageResource(R.drawable.baseline_person_add_alt_1_24);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                //Toast.makeText(DoctorRegistration.this, "Doctor successfully registered.\nVerification Email has been sent!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DoctorRegistration.this, Login.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
            else {
                try {
                    throw Objects.requireNonNull(task.getException());
                }
                catch (Exception e) {
                    Toast.makeText(DoctorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            //progressDialog.dismiss();
        });
    }

    private Boolean validateDoctorData() {

        boolean result = false;

        doc_UniqueCode = Objects.requireNonNull(docUniqueCode.getText()).toString().trim();
        doc_FirstName = Objects.requireNonNull(docFirstName.getText()).toString().trim();
        doc_LastName = Objects.requireNonNull(docLastName.getText()).toString().trim();
        doc_Phone = Objects.requireNonNull(docPhone.getText()).toString().trim();
        doc_EmailReg = Objects.requireNonNull(docEmailReg.getText()).toString().trim();
        doc_PassReg = Objects.requireNonNull(docPassReg.getText()).toString();
        doc_ConfPassReg = Objects.requireNonNull(docConfPassReg.getText()).toString().trim();

        if (TextUtils.isEmpty(doc_UniqueCode)) {
            docUniqueCode.setError("Enter Doctor's Unique Code");
            docUniqueCode.requestFocus();
        } else if (TextUtils.isEmpty(doc_FirstName)) {
            docFirstName.setError("Enter Doctor's First Name");
            docFirstName.requestFocus();
        } else if (TextUtils.isEmpty(doc_LastName)) {
            docLastName.setError("Enter Doctor's Last Name");
            docLastName.requestFocus();
        } else if (TextUtils.isEmpty(doc_Phone)) {
            docPhone.setError("Enter Doctor's Phone Number");
            docPhone.requestFocus();
        } else if (TextUtils.isEmpty(doc_EmailReg)) {
            docEmailReg.setError("Enter Doctor's Email Address");
            docEmailReg.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(doc_EmailReg).matches()) {
            docEmailReg.setError("Enter a valid Email Address");
            docEmailReg.requestFocus();
        } else if (TextUtils.isEmpty(doc_PassReg)) {
            docPassReg.setError("Enter the Password");
            docPassReg.requestFocus();
        } else if (doc_PassReg.length() < 6) {
            docPassReg.setError("Password too short, enter minimum 6 character long");
            docPassReg.requestFocus();
        } else if (TextUtils.isEmpty(doc_ConfPassReg)) {
            docConfPassReg.setError("Enter the Confirm Password");
            docConfPassReg.requestFocus();
        } else if (!doc_ConfPassReg.equals(doc_PassReg)) {
            docConfPassReg.setError("The Confirm Password does not match Password");
            docConfPassReg.requestFocus();
        } else {
            result = true;
        }
        return result;
    }
}
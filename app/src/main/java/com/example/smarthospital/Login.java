package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    //declare variables
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseRefHosp;
    private DatabaseReference databaseRefDoc;
    private DatabaseReference databaseRefPat;

    private TextInputEditText emailLogUser, passwordLogUser;
    private String emailLog_User, passwordLog_User;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Log in User");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //Initialize variables
        emailLogUser = findViewById(R.id.etEmailLogUser);
        passwordLogUser = findViewById(R.id.etPassLogUser);

        //Action button log in user
        Button buttonRegNewUser = (Button) findViewById(R.id.btnRegNewUser);
        buttonRegNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CheckUniqueCode.class));
            }
        });

        //Action TextView Forgotten Password
        TextView tVForgotPassUser = findViewById(R.id.tvForgotPasswordUser);
        tVForgotPassUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fPassword = new Intent(Login.this, ResetPassword.class);
                startActivity(fPassword);
            }
        });

        //Action button LogIn
        Button buttonLogIn = findViewById(R.id.btnLogInUser);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateLogInData()) {

                    progressDialog.setMessage("Log in user");
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(emailLog_User, passwordLog_User).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                checkEmailVerification();

                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidUserException e) {
                                    emailLogUser.setError("This email is not registered.");
                                    emailLogUser.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    passwordLogUser.setError("Invalid Password");
                                    passwordLogUser.requestFocus();
                                } catch (Exception e) {
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    //validate input data into the editText
    private Boolean validateLogInData() {

        boolean result = false;

        emailLog_User = Objects.requireNonNull(emailLogUser.getText()).toString().trim();
        passwordLog_User = Objects.requireNonNull(passwordLogUser.getText()).toString().trim();

        if (emailLog_User.isEmpty()) {
            emailLogUser.setError("Enter your Email Address");
            emailLogUser.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailLog_User).matches()) {
            emailLogUser.setError("Enter a valid Email Address");
            emailLogUser.requestFocus();
        } else if (passwordLog_User.isEmpty()) {
            passwordLogUser.setError("Enter your Password");
            passwordLogUser.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    //check if the email has been verified
    private void checkEmailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            if (firebaseUser.isEmailVerified()) {
                checkUserAccount();

            } else {
                Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();
        }
    }

    private void checkUserAccount() {

        //Check if the user Hospital try to log in
        final String hosp_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefHosp = FirebaseDatabase.getInstance().getReference("Hospitals");

        databaseRefHosp.orderByChild("hosp_Email").equalTo(hosp_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Hospital successfully Log in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, HospitalPage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Check if the user Doctor try to log in
        final String doc_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefDoc = FirebaseDatabase.getInstance().getReference("Doctors");

        databaseRefDoc.orderByChild("docEmail_Address").equalTo(doc_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Doctor successfully Log in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, DoctorPage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Check if the user Patient try to log in
        final String pat_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefPat = FirebaseDatabase.getInstance().getReference("Patients");

        databaseRefPat.orderByChild("patEmail_Address").equalTo(pat_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Patient successfully Log in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, PatientPage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
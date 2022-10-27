package com.example.smarthospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HospitalChangeEmail extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView tVHospAuthEmail;

    private EditText hospOdlEmail, hospPassword, hospNewEmail;

    private String hospOdl_Email, hosp_Password, hospNew_Email;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_change_email);

        Objects.requireNonNull(getSupportActionBar()).setTitle("HOSPITAL: Change Email");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        hospOdlEmail = findViewById(R.id.etHospOldEmail);
        hospOdlEmail.setEnabled(false);
        hospPassword = findViewById(R.id.etHospPassword);
        hospNewEmail = findViewById(R.id.etHospNewEmail);

        tVHospAuthEmail = findViewById(R.id.tvHospAuthEmail);
        tVHospAuthEmail.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the Email!!");
        tVHospAuthEmail.setTextColor(Color.RED);

        hospOdl_Email = firebaseUser.getEmail();
        hospOdlEmail.setText(hospOdl_Email);

        Button buttonChangeEmail = findViewById(R.id.btnHospChangeEmail);
        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertUserNotAuthEmail();
            }
        });

        Button buttonAuthHospEmail = findViewById(R.id.btnAuthHospEmail);
        buttonAuthHospEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hospOdl_Email =  hospOdlEmail.getText().toString().trim();
                hosp_Password = hospPassword.getText().toString().trim();

                if (TextUtils.isEmpty(hosp_Password)) {
                    hospPassword.setError("Enter your password");
                    hospPassword.requestFocus();
                }
                else{

                    progressDialog.setMessage("The Hospital is authenticating!");
                    progressDialog.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(hospOdl_Email, hosp_Password);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                progressDialog.dismiss();

                                tVHospAuthEmail.setText("Your profile is authenticated.\nNow you can change the Email!");
                                tVHospAuthEmail.setTextColor(Color.BLACK);

                                hospPassword.setEnabled(false);

                                hospPassword.setOnKeyListener(new View.OnKeyListener() {
                                    @Override
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                                        alertPassChangeEmail();
                                        hospNewEmail.requestFocus();
                                        return true;
                                    }
                                });

                                buttonAuthHospEmail.setEnabled(false);
                                buttonAuthHospEmail.setText("Disabled");
                                hospNewEmail.requestFocus();

                                buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        hospNew_Email = hospNewEmail.getText().toString().trim();

                                        if (TextUtils.isEmpty(hospNew_Email)){
                                            hospNewEmail.setError("Enter your new Email Address");
                                            hospNewEmail.requestFocus();
                                        }
                                        else if (!Patterns.EMAIL_ADDRESS.matcher(hospNew_Email).matches()) {
                                            Toast.makeText(HospitalChangeEmail.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                                            hospNewEmail.setError("Enter a valid Email Address");
                                            hospNewEmail.requestFocus();
                                        }
                                        else{

                                            progressDialog.setMessage("The hospital Email is changing!");
                                            progressDialog.show();

                                            firebaseUser.updateEmail(hospNew_Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        sendEmailVerification();
                                                    }

                                                    else{
                                                        try{
                                                            throw Objects.requireNonNull(task.getException());
                                                        } catch (Exception e) {
                                                            Toast.makeText(HospitalChangeEmail.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            else{
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidCredentialsException e){
                                    hospPassword.setError("Invalid Password");
                                    hospPassword.requestFocus();
                                    tVHospAuthEmail.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the email!!");
                                    tVHospAuthEmail.setTextColor(Color.RED);
                                } catch (Exception e) {
                                    Toast.makeText(HospitalChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void alertUserNotAuthEmail(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Your profile is not authenticated yet.\nPlease authenticate your profile first and then change the Email!!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertPassChangeEmail(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Password cannot be changed after user authentication!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void sendEmailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserChangeEmailData();
                    } else {
                        Toast.makeText(HospitalChangeEmail.this, "Email verification  has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserChangeEmailData() {

        hospNew_Email = hospNewEmail.getText().toString().trim();

        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final FirebaseUser user_Key = firebaseAuth.getCurrentUser();

                    if (user_Key != null) {
                        if (user_Key.getUid().equals(postSnapshot.getKey())){
                            postSnapshot.getRef().child("hosp_Email").setValue(hospNew_Email);
                        }
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(HospitalChangeEmail.this, "Email was changed. Email verification has been sent", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(HospitalChangeEmail.this, LoginBy.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalChangeEmail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_change_email, menu);
        return true;
    }

    private void hospChangeEmailGoBack(){
        startActivity(new Intent(HospitalChangeEmail.this, HospitalPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.hospChangeEmailGoBack) {
            hospChangeEmailGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}
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

import java.util.Objects;

public class HospitalChangePassword extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView tVHospAuthPass;

    private EditText hospEmail, hospOldPassword, hospNewPassword, hospConfNewPassword;

    private String hosp_Email, hospOld_Password, hospNew_Password, hospConf_NewPassword;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_change_password);

        Objects.requireNonNull(getSupportActionBar()).setTitle("HOSPITAL: change Password");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        hospEmail = findViewById(R.id.etHospEmailPass);
        hospEmail.setEnabled(false);
        hospOldPassword = findViewById(R.id.etHospOldPass);
        hospNewPassword = findViewById(R.id.etHospNewPass);
        hospConfNewPassword = findViewById(R.id.etHospConfNewPass);

        tVHospAuthPass = findViewById(R.id.tvHospAuthPass);
        tVHospAuthPass.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the Password!!");
        tVHospAuthPass.setTextColor(Color.RED);

        hosp_Email = firebaseUser.getEmail();
        hospEmail.setText(hosp_Email);

        Button buttonChangePassword = findViewById(R.id.btnHospChangePass);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertUserNotAuthPassword();
            }
        });

        Button buttonAuthUserPass = findViewById(R.id.btnAuthHospPass);
        buttonAuthUserPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hosp_Email =  hospEmail.getText().toString().trim();
                hospOld_Password = hospOldPassword.getText().toString().trim();

                if (TextUtils.isEmpty(hospOld_Password)) {
                    hospOldPassword.setError("Enter your password");
                    hospOldPassword.requestFocus();
                }
                else{

                    progressDialog.setMessage("The Hospital is authenticating!");
                    progressDialog.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(hosp_Email, hospOld_Password);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                progressDialog.dismiss();

                                tVHospAuthPass.setText("Your profile is authenticated.\nNow you can change the Password!");
                                tVHospAuthPass.setTextColor(Color.BLACK);

                                hospOldPassword.setOnKeyListener(new View.OnKeyListener() {
                                    @Override
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                                        alertPassChangePassword();
                                        hospNewPassword.requestFocus();
                                        return true;
                                    }
                                });

                                hospOldPassword.setEnabled(false);
                                buttonAuthUserPass.setEnabled(false);
                                buttonAuthUserPass.setText("Disabled");
                                hospNewPassword.requestFocus();

                                buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        hospNew_Password = hospNewPassword.getText().toString().trim();
                                        hospConf_NewPassword = hospConfNewPassword.getText().toString().trim();

                                        if (TextUtils.isEmpty(hospNew_Password)){
                                            hospNewPassword.setError("Enter your new Password");
                                            hospNewPassword.requestFocus();
                                        }
                                        else if (hospNew_Password.length() < 6) {
                                            hospNewPassword.setError("The password is too short.\nEnter minimum 6 character long");
                                            hospNewPassword.requestFocus();
                                        }
                                        else if (TextUtils.isEmpty(hospConf_NewPassword)) {
                                            hospConfNewPassword.setError("Confirm your new Password");
                                            hospConfNewPassword.requestFocus();
                                        }
                                        else if (!hospConf_NewPassword.equals(hospNew_Password)) {
                                            hospConfNewPassword.setError("The Password does not match");
                                            hospConfNewPassword.requestFocus();
                                        }
                                        else{

                                            progressDialog.setMessage("The Hospital password is changing!");
                                            progressDialog.show();

                                            firebaseUser.updatePassword(hospNew_Password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        firebaseAuth.signOut();
                                                        Toast.makeText(HospitalChangePassword.this, "The password will be changed.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(HospitalChangePassword.this, LoginBy.class));
                                                        finish();

                                                    }

                                                    else{
                                                        try {
                                                            throw Objects.requireNonNull(task.getException());
                                                        } catch (Exception e) {
                                                            Toast.makeText(HospitalChangePassword.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    progressDialog.dismiss();
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
                                    hospOldPassword.setError("Invalid Password");
                                    hospOldPassword.requestFocus();
                                    tVHospAuthPass.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the Password!!");
                                    tVHospAuthPass.setTextColor(Color.RED);
                                } catch (Exception e) {
                                    Toast.makeText(HospitalChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void alertUserNotAuthPassword(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Your profile is not authenticated yet.\nPlease authenticate your profile first and then change the Password!!")
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

    private void alertPassChangePassword(){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_change_password, menu);
        return true;
    }

    private void hospChangePassGoBack(){
        startActivity(new Intent(HospitalChangePassword.this, HospitalPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.hospChangePassGoBack) {
            hospChangePassGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}
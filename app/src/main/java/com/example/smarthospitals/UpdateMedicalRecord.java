package com.example.smarthospitals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateMedicalRecord extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private EditText medRecDateBirthUp, medRecPPSNoUp, medRecAddressUp;
    private AutoCompleteTextView tVMedRecGenderUp;

    private String medRec_GenderUp, medRec_DateBirthUp, medRec_PPSNoUp, medRec_AddressUp;

    //data received to be updated
    private String medRecDateBirthUpdate;
    private String medRecPPsNoUpdate;
    private String medRecAddressUpdate;
    private String medRecKeyUpdate;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medical_record);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Medical Record");

        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Records");

        tVMedRecGenderUp  = findViewById(R.id.tvMedRecGenderUp);
        medRecDateBirthUp = findViewById(R.id.etRecDateBirthUp);
        medRecPPSNoUp = findViewById(R.id.etMedRecPPSNoUp);
        medRecAddressUp = findViewById(R.id.etMedRecAddressUp);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null ){
            medRecDateBirthUpdate = bundle.getString("MEDRECDateBirth");
            medRecPPsNoUpdate = bundle.getString("MEDRECPPSNo");
            medRecAddressUpdate = bundle.getString("MEDRECAddress");
            medRecKeyUpdate = bundle.getString("MEDRECKey");
        }

        String[] gender = getResources().getStringArray(R.array.Gender);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item_gender, gender);
        tVMedRecGenderUp.setAdapter(genderAdapter);

        medRecDateBirthUp.setText(medRecDateBirthUpdate);
        medRecPPSNoUp.setText(medRecPPsNoUpdate);
        medRecAddressUp.setText(medRecAddressUpdate);

        Button buttonMedRecSaveUp =findViewById(R.id.btnMedRecSaveUp);
        buttonMedRecSaveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUpdatedMedRecData();
            }
        });
    }

    private void saveUpdatedMedRecData(){

        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Records");

        if (validateMedRecDataUp()){

            medRec_GenderUp = tVMedRecGenderUp.getText().toString().trim();
            medRec_DateBirthUp = medRecDateBirthUp.getText().toString().trim();
            medRec_PPSNoUp =  medRecPPSNoUp.getText().toString().trim();
            medRec_AddressUp = medRecAddressUp.getText().toString().trim();

            progressDialog.setMessage("The Medical Record is updating!!");
            progressDialog.show();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()){
                        String medRec_key = postSnapshot.getKey();

                        if (medRec_key != null){
                            if(medRec_key.equals(medRecKeyUpdate)){
                                postSnapshot.getRef().child("medRecord_Gender").setValue(medRec_GenderUp);
                                postSnapshot.getRef().child("medRecord_DateBirth").setValue(medRec_DateBirthUp);
                                postSnapshot.getRef().child("medRecord_PPS").setValue(medRec_PPSNoUp);
                                postSnapshot.getRef().child("medRecord_Address").setValue(medRec_AddressUp);
                            }
                        }
                    }
                    //progressDialog.dismiss();
                    Toast.makeText(UpdateMedicalRecord.this,"The Medical Record has been updated", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(UpdateMedicalRecord.this, DoctorPage.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateMedicalRecord.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Boolean validateMedRecDataUp(){
        boolean result = false;

        medRec_GenderUp = tVMedRecGenderUp.getText().toString().trim();
        medRec_DateBirthUp = medRecDateBirthUp.getText().toString().trim();
        medRec_PPSNoUp =  medRecPPSNoUp.getText().toString().trim();
        medRec_AddressUp = medRecAddressUp.getText().toString().trim();

        if (TextUtils.isEmpty(medRec_GenderUp)){
            alertDialogGender();
        }
        else if (TextUtils.isEmpty(medRec_DateBirthUp)){
            medRecDateBirthUp.setError("Enter patient's Date of Birth");
            medRecDateBirthUp.requestFocus();
        }
        else if (TextUtils.isEmpty(medRec_PPSNoUp)){
            medRecPPSNoUp.setError("Please enter patient's PPS");
            medRecPPSNoUp.requestFocus();
        }
        else if (TextUtils.isEmpty(medRec_AddressUp)){
            medRecAddressUp.setError("Please enter patient's Address");
            medRecAddressUp.requestFocus();
        }
        else{
            result = true;
        }
        return result;
    }

    //Notify if Gender is missing
    private void alertDialogGender() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please select the Gender");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
package com.example.smarthospitals;

import static android.provider.Settings.ACTION_NFC_SETTINGS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthospitals.parser.NdefMessageParser;
import com.example.smarthospitals.record.ParsedNdefRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NFCReadCard extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private DatabaseReference databaseRefCheckPat;
    private DatabaseReference databaseRefNFCId;
    private ValueEventListener eventListener;

    private TextView tVShowNFC, tVSaveCode;

    private String save_Code;

    private Button btn_Save, btn_Clear;

    private List<Tag> mTags;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcread_card);

        Objects.requireNonNull(getSupportActionBar()).setTitle("NFC Read Card");

        progressDialog = new ProgressDialog(this);

        databaseRefCheckPat = FirebaseDatabase.getInstance().getReference("Patients");
        databaseRefNFCId = FirebaseDatabase.getInstance().getReference("Patients");

        mTags = new ArrayList<>();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        tVShowNFC = findViewById(R.id.tvShowNFC);
        tVSaveCode = findViewById(R.id.tvSaveCode);

        tVShowNFC.setText("Hold the card in the back of your device");
        tVSaveCode.setText("No patient NFC Id");

        btn_Save = findViewById(R.id.btnSave);
        btn_Clear = findViewById(R.id.btnClear);

        tVSaveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertNoNFCId();
            }
        });

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertNoNFCId();
            }
        });

        btn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertNothingToClear();
            }
        });

        Button btn_Check = findViewById(R.id.btnCheck);
        btn_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_Code = tVSaveCode.getText().toString().trim();
                if (save_Code.equals("No patient NFC Id") || save_Code.equals("Click SAVE to get the Patient Id")){
                    alertNoNFCFound();
                }

                else {
                    checkCodePatient();
                }
            }
        });

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    private void alertNothingToClear(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("There is nothing to clear!!")
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

    //For Button Save and TextView saveCode
    private void alertNoNFCId(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Hold the card in the back of your device to get a Patient NFC Id, and then press the button SAVE!")
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

    //for Button Chek
    private void alertNoNFCFound(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("No Patient NFC id found.\nHold the card in the back of your device.\nPress the button SAVE and then press Check Id")
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
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                showNFCSettings();
            }
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void showNFCSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ACTION_NFC_SETTINGS);
        startActivity(intent);
    }

    //Tag data is converted to string to display
    //return the data from this tag in String format
    @SuppressLint("SetTextI18n")
    private String dumpTagData(Tag tag){
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        tVSaveCode.setClickable(false);
        tVSaveCode.setText("Click SAVE to get the Patient Id");

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tVSaveCode.setText(String.valueOf(toReversedDec(id)));
                tVSaveCode.setClickable(false);
                btn_Save.setClickable(false);
            }
        });

        btn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_Code = tVSaveCode.getText().toString().trim();
                if (save_Code.equals("Click SAVE to get the Patient Id")){
                    alertNothingToClear();
                }

                else{
                    tVSaveCode.setText("Click SAVE to get the Patient Id");
                    btn_Save.setClickable(true);
                    Toast.makeText(NFCReadCard.this, "The Button SAVE is clickable", Toast.LENGTH_SHORT).show();
                }

            }
        });

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize()).append(" bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: ").append(e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    public static long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (byte aByte : bytes) {
            long value = aByte & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    public static long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs =  new NdefMessage[] {msg};
                mTags.add(tag);
            }
            displayNfcMessages(msgs);
        }
    }

    private void displayNfcMessages(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);

        final int size = records.size();

        for (int i=0; i <size; i++) {

            ParsedNdefRecord record = records.get(i);

            String str = record.str();

            builder.append(str).append("\n");
        }

        tVShowNFC.setText(builder.toString());
    }

    private void checkCodePatient(){

        progressDialog.setMessage("The Patient is identified!!");
        progressDialog.show();

        databaseRefCheckPat.orderByChild("Patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    checkPatientNFCId();

                }
                else{
                    alertNoPatientRegisteredFound();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NFCReadCard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPatientNFCId() {

//        progressDialog.setMessage("The Patient is identified!!");
//        progressDialog.show();

        eventListener = databaseRefNFCId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()){

                    Patients pat_data = postSnapshot.getValue(Patients.class);
                    String save_Code = tVSaveCode.getText().toString().trim();

                    if (pat_data != null) {
                        if (save_Code.equals(pat_data.getPatCard_Code())){
                            pat_data.setPatient_Key(postSnapshot.getKey());
                            Intent intent = new Intent(NFCReadCard.this, PatientNFC.class);
                            intent.putExtra("FIRSTNAME", pat_data.getPatFirst_Name());
                            intent.putExtra("LASTNAME", pat_data.getPatLast_Name());
                            intent.putExtra("DOCTORNAME", pat_data.getPatDoc_Name());
                            intent.putExtra("HOSPNAME", pat_data.getPatHosp_Name());
                            startActivity(intent);
                        }

                        else{

                            alertNoPatientFond();
                        }

                        //progressDialog.dismiss();
                    }
                }

                //progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NFCReadCard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alertNoPatientRegisteredFound(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("No Patients registered in the database were found.\nPlease register Patients and then check the NFC id!!")
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

    private void alertNoPatientFond() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("No Patient found to be registered with the NCF Id: " + save_Code)
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
}
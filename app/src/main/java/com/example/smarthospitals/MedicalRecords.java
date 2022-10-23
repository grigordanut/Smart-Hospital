package com.example.smarthospitals;

import com.google.firebase.database.Exclude;

public class MedicalRecords {

    private String medRecord_Gender;
    private String medRecord_DateBirth;
    private String medRecord_PPS;
    private String medRecord_Address;
    private String recordPat_Name;
    private String recordPat_Key;
    private String record_Key;

    public MedicalRecords(){

    }

    public MedicalRecords(String medRecord_Gender, String medRecord_DateBirth, String medRecord_PPS, String medRecord_Address, String recordPat_Name, String recordPat_Key) {
        this.medRecord_Gender = medRecord_Gender;
        this.medRecord_DateBirth = medRecord_DateBirth;
        this.medRecord_PPS = medRecord_PPS;
        this.medRecord_Address = medRecord_Address;
        this.recordPat_Name = recordPat_Name;
        this.recordPat_Key = recordPat_Key;
    }

    public String getMedRecord_Gender() {
        return medRecord_Gender;
    }

    public void setMedRecord_Gender(String medRecord_Gender) {
        this.medRecord_Gender = medRecord_Gender;
    }

    public String getMedRecord_DateBirth() {
        return medRecord_DateBirth;
    }

    public void setMedRecord_DateBirth(String medRecord_DateBirth) {
        this.medRecord_DateBirth = medRecord_DateBirth;
    }

    public String getMedRecord_PPS() {
        return medRecord_PPS;
    }

    public void setMedRecord_PPS(String medRecord_PPS) {
        this.medRecord_PPS = medRecord_PPS;
    }

    public String getMedRecord_Address() {
        return medRecord_Address;
    }

    public void setMedRecord_Address(String medRecord_Address) {
        this.medRecord_Address = medRecord_Address;
    }

    public String getRecordPat_Name() {
        return recordPat_Name;
    }

    public void setRecordPat_Name(String recordPat_Name) {
        this.recordPat_Name = recordPat_Name;
    }

    public String getRecordPat_Key() {
        return recordPat_Key;
    }

    public void setRecordPat_Key(String recordPat_Key) {
        this.recordPat_Key = recordPat_Key;
    }

    @Exclude
    public String getRecord_Key() {
        return record_Key;
    }

    @Exclude
    public void setRecord_Key(String record_Key) {
        this.record_Key = record_Key;
    }
}

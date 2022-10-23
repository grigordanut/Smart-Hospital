package com.example.smarthospitals;

import com.google.firebase.database.Exclude;

public class Doctors {

    private String docUnique_Code;
    private String docFirst_Name;
    private String docLast_Name;
    private String docPhone_Number;
    private String docEmail_Address;
    private String docHosp_Name;
    private String docHosp_Key;
    private String doc_Key;

    public Doctors() {

    }

    public Doctors(String docUnique_Code, String docFirst_Name, String docLast_Name, String docPhone_Number, String docEmail_Address, String docHosp_Name, String docHosp_Key) {
        this.docUnique_Code = docUnique_Code;
        this.docFirst_Name = docFirst_Name;
        this.docLast_Name = docLast_Name;
        this.docPhone_Number = docPhone_Number;
        this.docEmail_Address = docEmail_Address;
        this.docHosp_Name = docHosp_Name;
        this.docHosp_Key = docHosp_Key;
    }

    public String getDocUnique_Code() {
        return docUnique_Code;
    }

    public void setDocUnique_Code(String docUnique_Code) {
        this.docUnique_Code = docUnique_Code;
    }

    public String getDocFirst_Name() {
        return docFirst_Name;
    }

    public void setDocFirst_Name(String docFirst_Name) {
        this.docFirst_Name = docFirst_Name;
    }

    public String getDocLast_Name() {
        return docLast_Name;
    }

    public void setDocLast_Name(String docLast_Name) {
        this.docLast_Name = docLast_Name;
    }

    public String getDocPhone_Number() {
        return docPhone_Number;
    }

    public void setDocPhone_Number(String docPhone_Number) {
        this.docPhone_Number = docPhone_Number;
    }

    public String getDocEmail_Address() {
        return docEmail_Address;
    }

    public void setDocEmail_Address(String docEmail_Address) {
        this.docEmail_Address = docEmail_Address;
    }

    public String getDocHosp_Name() {
        return docHosp_Name;
    }

    public void setDocHosp_Name(String docHosp_Name) {
        this.docHosp_Name = docHosp_Name;
    }

    public String getDocHosp_Key() {
        return docHosp_Key;
    }

    public void setDocHosp_Key(String docHosp_Key) {
        this.docHosp_Key = docHosp_Key;
    }

    @Exclude
    public String getDoc_Key() {
        return doc_Key;
    }

    @Exclude
    public void setDoc_Key(String doc_Key) {
        this.doc_Key = doc_Key;
    }
}

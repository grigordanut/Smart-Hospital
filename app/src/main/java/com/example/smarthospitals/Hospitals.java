package com.example.smarthospitals;

import com.google.firebase.database.Exclude;

public class Hospitals {

    private String hosp_UniqueCode;
    private String hosp_Name;
    private String hosp_Email;
    private String hosp_Key;

    public Hospitals(){

    }

    public Hospitals(String hosp_UniqueCode, String hosp_Name, String hosp_Email) {
        this.hosp_UniqueCode = hosp_UniqueCode;
        this.hosp_Name = hosp_Name;
        this.hosp_Email = hosp_Email;
    }

    public String getHosp_UniqueCode() {
        return hosp_UniqueCode;
    }

    public void setHosp_UniqueCode(String hosp_UniqueCode) {
        this.hosp_UniqueCode = hosp_UniqueCode;
    }

    public String getHosp_Name() {
        return hosp_Name;
    }

    public void setHosp_Name(String hosp_Name) {
        this.hosp_Name = hosp_Name;
    }

    public String getHosp_Email() {
        return hosp_Email;
    }

    public void setHosp_Email(String hosp_Email) {
        this.hosp_Email = hosp_Email;
    }

    @Exclude
    public String getHosp_Key() {
        return hosp_Key;
    }

    @Exclude
    public void setHosp_Key(String hosp_Key) {
        this.hosp_Key = hosp_Key;
    }
}

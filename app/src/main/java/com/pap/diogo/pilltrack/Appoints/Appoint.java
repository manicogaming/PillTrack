package com.pap.diogo.pilltrack.Appoints;

public class Appoint {
    private String name, hospital, date;

    public Appoint(){

    }

    public Appoint(String name, String hospital, String date) {
        this.name = name;
        this.hospital = hospital;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

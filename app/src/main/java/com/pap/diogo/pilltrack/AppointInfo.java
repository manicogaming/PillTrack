package com.pap.diogo.pilltrack;

public class AppointInfo {
    public String name;
    public String hospital;
    public String date;

    public AppointInfo(String name, String hospital, String date) {
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

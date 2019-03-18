package com.pap.diogo.pilltrack.Appoints;

public class AppointInfo {
    public String name;
    public String hospital;
    public String date;
    public String hlocation;

    public AppointInfo(String name, String hospital, String date, String hlocation) {
        this.name = name;
        this.hospital = hospital;
        this.date = date;
        this.hlocation = hlocation;
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

    public String getHlocation() {
        return hlocation;
    }

    public void setHlocation(String hlocation) {
        this.hlocation = hlocation;
    }
}

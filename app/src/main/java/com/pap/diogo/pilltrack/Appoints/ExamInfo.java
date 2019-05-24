package com.pap.diogo.pilltrack.Appoints;

public class ExamInfo {
    public String name;
    public String hospital;
    public String prep;
    public String date;
    public String hour;
    public String hlocation;

    public ExamInfo(String name, String hospital, String prep, String date, String hour, String hlocation) {
        this.name = name;
        this.hospital = hospital;
        this.prep = prep;
        this.date = date;
        this.hour = hour;
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

    public String getPrep() {
        return prep;
    }

    public void setPrep(String prep) {
        this.prep = prep;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getHlocation() {
        return hlocation;
    }

    public void setHlocation(String hlocation) {
        this.hlocation = hlocation;
    }
}

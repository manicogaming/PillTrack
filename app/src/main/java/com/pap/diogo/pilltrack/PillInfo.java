package com.pap.diogo.pilltrack;

public class PillInfo {
    public String name;
    public String pillfunc;
    public String interval;

    public PillInfo(String name, String pillfunc, String interval) {
        this.name = name;
        this.pillfunc = pillfunc;
        this.interval = interval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPillfunc() {
        return pillfunc;
    }

    public void setPillfunc(String pillfunc) {
        this.pillfunc = pillfunc;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}

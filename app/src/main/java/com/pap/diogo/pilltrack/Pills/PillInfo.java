package com.pap.diogo.pilltrack.Pills;

public class PillInfo {
    public String name;
    public String pillfunc;
    public String interval;
    public String pilltype;
    public String pillhour;
    public String pillstartdate;
    public String pillenddate;

    public PillInfo(String name, String pillfunc, String interval, String pilltype, String pillhour, String pillstartdate, String pillenddate) {
        this.name = name;
        this.pillfunc = pillfunc;
        this.interval = interval;
        this.pilltype = pilltype;
        this.pillhour = pillhour;
        this.pillstartdate = pillstartdate;
        this.pillenddate = pillenddate;
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

    public String getPilltype() {
        return pilltype;
    }

    public void setPilltype(String pilltype) {
        this.pilltype = pilltype;
    }

    public String getPillhour() {
        return pillhour;
    }

    public void setPillhour(String pillhour) {
        this.pillhour = pillhour;
    }

    public String getPillstartdate() {
        return pillstartdate;
    }

    public void setPillstartdate(String pillstartdate) {
        this.pillstartdate = pillstartdate;
    }

    public String getPillenddate() {
        return pillenddate;
    }

    public void setPillenddate(String pillenddate) {
        this.pillenddate = pillenddate;
    }
}

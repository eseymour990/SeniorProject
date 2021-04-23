package com.example.trollsmarter.HelperClasses;

import android.content.DialogInterface;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class History implements Serializable {
    @JsonProperty("Depth")
    private String Depth;
    @JsonProperty("LureName")
    private String LureName;
    @JsonProperty("Date")
    private String Date;

    public void setDepth(String depth) {
        this.Depth = depth;
    }

    public String getDepth() {
        return Depth;
    }

    public String getDateTime() {
        return Date;
    }

    public void setDateTime(String dateTime) {
        this.Date = dateTime;
    }

    public String getLureName() {
        return LureName;
    }

    public void setLureName(String lureName) {
        this.LureName = lureName;
    }


    @Override
    public String toString(){
        return "Fish caught at " + Date + " at a depth of " + Depth + " with " + LureName;
    }
}

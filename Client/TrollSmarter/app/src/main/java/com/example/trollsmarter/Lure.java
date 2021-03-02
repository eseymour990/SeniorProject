package com.example.trollsmarter;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONObject;

public class Lure {
    @JsonProperty("DiveEquation")
    private String DiveEquation;
    @JsonProperty("Name")
    private String Name;


    public String getDiveCurve(){
        return DiveEquation;
    }
    public String getName(){
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDiveEquation(String diveEquation) {
        DiveEquation = diveEquation;
    }
    @Override
    public String toString(){
        return Name;
    }
}

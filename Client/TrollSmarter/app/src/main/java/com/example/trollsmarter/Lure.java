package com.example.trollsmarter;

import org.json.JSONObject;

public class Lure {
    private JSONObject diveCurve;
    private String name;

    public JSONObject getDiveCurve(){
        return diveCurve;
    }
    public String getName(){
        return name;
    }
    public Lure(String Name, JSONObject DiveCurve){
        name = Name;
        diveCurve = DiveCurve;
    }
}

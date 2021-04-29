package com.example.trollsmarter.HelperClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Line implements Serializable {
    @JsonProperty("Thickness")
    private String Thickness;
    @JsonProperty("Name")
    private String Name;


    @Override
    public String toString(){
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLineThickness() {
        return Thickness;
    }

    public void setLineThickness(String lineThickness) {
        Thickness = lineThickness;
    }
}
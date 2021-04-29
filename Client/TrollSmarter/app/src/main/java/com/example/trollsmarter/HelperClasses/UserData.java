package com.example.trollsmarter.HelperClasses;

import com.example.trollsmarter.HelperClasses.Lure;

import java.io.Serializable;

public class UserData implements Serializable {
    public Lure _lure;
    public Lure _trollingDevice;
    public Line _line;
    public String _leaderLength;
    public double _lineThicknessMultiplier;


    public double GetLineThicknessMultiplier(){
        Double thickness = Double.parseDouble(_line.getLineThickness());

        return 1+ ((thickness - .3)*.3);
    }
    public void SetLineThicknessMultiplier(double lineThicknessMultiplier){
        _lineThicknessMultiplier = lineThicknessMultiplier;
    }

    public Lure GetLure(){
        return _lure;
    }
    public void SetLure(Lure lure){
        _lure = lure;
    }

    public Lure GetTrollingDevice(){
        return _trollingDevice;
    }
    public void SetTrollingDevice(Lure trollingDevice){
        _trollingDevice = trollingDevice;
    }

    public Line GetLineThickness(){
        return _line;
    }
    public void SetLineThickness(Line lineThickness){
        _line = lineThickness;
    }

    public String GetLeaderLength(){
        return _leaderLength;
    }
    public void SetLeaderLength(String leaderLength){
        _leaderLength = leaderLength;
    }


    public UserData() {
    }
}

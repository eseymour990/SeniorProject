package com.example.trollsmarter.HelperClasses;

import com.example.trollsmarter.HelperClasses.Lure;

import java.io.Serializable;

public class UserData implements Serializable {
    public Lure _lure;
    public Lure _trollingDevice;
    public String _lineThickness;
    public String _leaderLength;
    public String _lineThicknessMultiplier;


    public String GetLineThicknessMultiplier(){
        return _lineThicknessMultiplier;
    }
    public void SetLineThicknessMultiplier(String lineThicknessMultiplier){
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

    public String GetLineThickness(){
        return _lineThickness;
    }
    public void SetLineThickness(String lineThickness){
        _lineThickness = lineThickness;
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

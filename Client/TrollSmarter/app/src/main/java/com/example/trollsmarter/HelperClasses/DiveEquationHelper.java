package com.example.trollsmarter.HelperClasses;

public class DiveEquationHelper {
    private double A;
    private double B;

    public DiveEquationHelper(String equation){
        String diveEquation = equation.substring(2);
        A = Double.parseDouble(diveEquation.substring(0,diveEquation.indexOf("x") - 1));
        B = Double.parseDouble(diveEquation.substring(diveEquation.indexOf("x") + 3));
    }

    public double FindLineOut(double Depth){
        double top = -Depth+B;
        double bottom = A;
        double logx = top/-bottom;
        return Math.pow(10,logx);
    }

    public double FindDepth(double lineOut){
        return (A*Math.log10(lineOut)) + B;
    }
}

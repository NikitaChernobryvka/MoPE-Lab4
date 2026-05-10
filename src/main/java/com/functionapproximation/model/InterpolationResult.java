package com.functionapproximation.model;

import java.util.List;

public class InterpolationResult {
    private double[] coefficients;
    private List<Point> curvePoints;
    private double[][] dividedDifferencesTable;

    public InterpolationResult(double[] coefficients, List<Point> curvePoints, double[][]dividedDifferencesTable) {
        this.coefficients = coefficients;
        this.curvePoints = curvePoints;
        this.dividedDifferencesTable = dividedDifferencesTable;
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public List<Point> getCurvePoints() {
        return curvePoints;
    }

    public double[][] getDividedDifferencesTable() {
        return dividedDifferencesTable;
    }

    public void setCoefficients(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public void setCurvePoints(List<Point> curvePoints) {
        this.curvePoints = curvePoints;
    }

    public void setDividedDifferencesTable(double[][] dividedDifferencesTable) {
        this.dividedDifferencesTable = dividedDifferencesTable;
    }
}

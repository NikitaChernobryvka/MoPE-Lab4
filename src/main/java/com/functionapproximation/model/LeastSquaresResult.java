package com.functionapproximation.model;

import java.util.List;

public class LeastSquaresResult {
    private double[] coefficients;
    private List<Point> curvePoints;
    private double[] residuals;

    public LeastSquaresResult(double[] coefficients, List<Point> curvePoints, double[] residuals) {
        this.coefficients = coefficients;
        this.curvePoints = curvePoints;
        this.residuals = residuals;
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public List<Point> getCurvePoints() {
        return curvePoints;
    }

    public double[] getResiduals() {
        return residuals;
    }

    public void setCoefficients(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public void setCurvePoints(List<Point> curvePoints) {
        this.curvePoints = curvePoints;
    }

    public void setResiduals(double[] residuals) {
        this.residuals = residuals;
    }
}

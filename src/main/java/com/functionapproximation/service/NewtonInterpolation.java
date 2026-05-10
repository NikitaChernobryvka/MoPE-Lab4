package com.functionapproximation.service;

import com.functionapproximation.model.InterpolationResult;
import com.functionapproximation.model.Point;

import java.util.ArrayList;
import java.util.List;

public class NewtonInterpolation {

    public InterpolationResult interpolate(List<Point> points) {
        int n = points.size();
        double[] x = points.stream().mapToDouble(Point::getX).toArray();
        double[] y = points.stream().mapToDouble(Point::getY).toArray();

        double[][] table = new double[n][n];
        for (int i = 0; i < n; i++) table[i][0] = y[i];

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                table[i][j] = (table[i + 1][j - 1] - table[i][j - 1])
                        / (x[i + j] - x[i]);
            }
        }

        double[] coefficients = new double[n];
        for (int j = 0; j < n; j++) coefficients[j] = table[0][j];

        double xMin = x[0], xMax = x[n - 1];
        List<Point> curve = new ArrayList<>();
        for (int k = 0; k <= 300; k++) {
            double t = xMin + k * (xMax - xMin) / 300.0;
            curve.add(new Point(t, evaluate(x, coefficients, t)));
        }

        return new InterpolationResult(coefficients, curve, table);
    }

    private double evaluate(double[] x, double[] coef, double t) {
        double result = coef[0];
        double product = 1.0;
        for (int j = 1; j < coef.length; j++) {
            product *= (t - x[j - 1]);
            result += coef[j] * product;
        }
        return result;
    }
}

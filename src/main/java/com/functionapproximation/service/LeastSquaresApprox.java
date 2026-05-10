package com.functionapproximation.service;

import com.functionapproximation.model.LeastSquaresResult;
import com.functionapproximation.model.Point;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.List;

public class LeastSquaresApprox {

    public LeastSquaresResult approximate(List<Point> points, int degree) {
        int n = points.size();
        double[] x = points.stream().mapToDouble(Point::getX).toArray();
        double[] y = points.stream().mapToDouble(Point::getY).toArray();

        double[][] designData = new double[n][degree + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= degree; j++) {
                designData[i][j] = Math.pow(x[i], j);
            }
        }

        RealMatrix A = new Array2DRowRealMatrix(designData);
        RealVector b = new ArrayRealVector(y);

        QRDecomposition qr = new QRDecomposition(A);
        double[] coefficients = qr.getSolver().solve(b).toArray();

        double xMin = x[0], xMax = x[n - 1];
        List<Point> curve = new ArrayList<>();
        for (int k = 0; k <= 300; k++) {
            double t = xMin + k * (xMax - xMin) / 300.0;
            curve.add(new Point(t, evaluate(coefficients, t)));
        }

        double[] residuals = new double[n];
        for (int i = 0; i < n; i++) {
            residuals[i] = y[i] - evaluate(coefficients, x[i]);
        }

        return new LeastSquaresResult(coefficients, curve, residuals);
    }

    private double evaluate(double[] coef, double t) {
        double result = 0.0;
        for (int j = 0; j < coef.length; j++) {
            result += coef[j] * Math.pow(t, j);
        }
        return result;
    }
}

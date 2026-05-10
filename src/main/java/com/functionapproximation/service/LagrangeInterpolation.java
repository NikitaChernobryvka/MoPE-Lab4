package com.functionapproximation.service;

import com.functionapproximation.model.InterpolationResult;
import com.functionapproximation.model.Point;

import java.util.ArrayList;
import java.util.List;

public class LagrangeInterpolation {

    public InterpolationResult interpolate(List<Point> points) {
        int n = points.size();
        double[] xs = points.stream().mapToDouble(Point::getX).toArray();
        double[] ys = points.stream().mapToDouble(Point::getY).toArray();

        List<Point> curvePoints = buildCurve(xs, ys, n);

        return new InterpolationResult(ys, curvePoints, null);
    }

    public double evaluate(double[] xs, double[] ys, double x) {
        int n = xs.length;
        double result = 0.0;

        for (int k = 0; k < n; k++) {
            double term = ys[k];
            for (int j = 0; j < n; j++) {
                if (j != k) {
                    term *= (x - xs[j]) / (xs[k] - xs[j]);
                }
            }
            result += term;
        }
        return result;
    }

    private List<Point> buildCurve(double[] xs, double[] ys, int n) {
        List<Point> curve = new ArrayList<>();
        double xMin = xs[0];
        double xMax = xs[0];

        for (double x : xs) {
            if (x < xMin) xMin = x;
            if (x > xMax) xMax = x;
        }

        int steps = 300;
        double step = (xMax - xMin) / steps;

        for (int i = 0; i <= steps; i++) {
            double x = xMin + i * step;
            double y = evaluate(xs, ys, x);
            curve.add(new Point(x, y));
        }
        return curve;
    }
}

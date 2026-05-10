package com.functionapproximation.model;

import java.util.ArrayList;
import java.util.List;

public class InputData {
    private List<Point> points;
    private int degree;

    public InputData() {
        this.points = new ArrayList<>();
        this.degree = 2;
    }

    public InputData(List<Point> points, int degree) {
        this.points = points;
        this.degree = degree;
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getDegree() {
        return degree;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public void removePoint(int index) {
        this.points.remove(index);
    }

    public int getSize() {
        return points.size();
    }
}

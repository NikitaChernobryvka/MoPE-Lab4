package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.InterpolationResult;
import com.functionapproximation.model.Point;
import com.functionapproximation.service.LagrangeInterpolation;
import com.functionapproximation.service.NewtonInterpolation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.List;

public class InterpolationController {

    @FXML private LineChart<Number, Number> interpolationChart;
    @FXML private RadioButton lagrangeRadio;
    @FXML private TitledPane dividedDifferencesPane;
    @FXML private TableView<?> dividedDifferencesTable;

    private InputData inputData;
    private final LagrangeInterpolation lagrangeInterpolation = new LagrangeInterpolation();
    private final NewtonInterpolation newtonInterpolation = new NewtonInterpolation();

    private List<Point> lastCurvePoints;
    private String lastCurveName;
    private Timeline animationTimeline;

    @FXML private void initialize() {
        interpolationChart.setAnimated(false);
        interpolationChart.setCreateSymbols(true);
        dividedDifferencesPane.setVisible(false);
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

    @FXML private void onCalculate() {
        List<Point> points = inputData.getPoints();
        if (points.size() < 2) return;

        if (animationTimeline != null) animationTimeline.stop();
        interpolationChart.setCreateSymbols(true);
        interpolationChart.getData().clear();

        if (lagrangeRadio.isSelected()) {
            InterpolationResult result = lagrangeInterpolation.interpolate(points);
            addCurveSeries(result.getCurvePoints(), "Лагранж");
            addPointsSeries(points);
            dividedDifferencesPane.setVisible(false);
        } else {
            InterpolationResult result = newtonInterpolation.interpolate(points);
            addCurveSeries(result.getCurvePoints(), "Ньютон");
            addPointsSeries(points);
            buildDividedDiffTable(points.size(), result.getDividedDifferencesTable());
            dividedDifferencesPane.setVisible(true);
        }
    }

    @FXML private void onAnimate() {
        List<Point> points = inputData.getPoints();
        if (points.size() < 2) return;

        if (animationTimeline != null) animationTimeline.stop();

        InterpolationResult result = lagrangeRadio.isSelected()
                ? lagrangeInterpolation.interpolate(points)
                : newtonInterpolation.interpolate(points);

        String name = lagrangeRadio.isSelected() ? "Лагранж" : "Ньютон";
        List<Point> curvePoints = result.getCurvePoints();

        interpolationChart.getData().clear();
        interpolationChart.setCreateSymbols(false);

        XYChart.Series<Number, Number> animSeries = new XYChart.Series<>();
        animSeries.setName(name);
        interpolationChart.getData().add(animSeries);

        addPointsSeries(points);

        if (lagrangeRadio.isSelected()) {
            dividedDifferencesPane.setVisible(false);
        } else {
            buildDividedDiffTable(points.size(), result.getDividedDifferencesTable());
            dividedDifferencesPane.setVisible(true);
        }

        int[] index = {0};
        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(10), e -> {
                    if (index[0] < curvePoints.size()) {
                        Point p = curvePoints.get(index[0]);
                        animSeries.getData().add(new XYChart.Data<>(p.getX(), p.getY()));
                        index[0]++;
                    }
                })
        );
        animationTimeline.setCycleCount(curvePoints.size());
        animationTimeline.setOnFinished(e -> {
            interpolationChart.setCreateSymbols(true);
            if (interpolationChart.getData().size() > 1) {
                for (XYChart.Data<Number, Number> data : interpolationChart.getData().get(0).getData()) {
                    if (data.getNode() != null) data.getNode().setVisible(false);
                }
            }
        });
        animationTimeline.play();
    }

    @SuppressWarnings("unchecked")
    private void buildDividedDiffTable(int n, double[][] table) {
        TableView<double[]> tv = (TableView<double[]>) dividedDifferencesTable;
        tv.getColumns().clear();
        tv.getItems().clear();

        for (int j = 0; j < n; j++) {
            final int col = j;
            String header = j == 0 ? "f[xᵢ]" : "f[x₀.." + j + "]";
            TableColumn<double[], String> column = new TableColumn<>(header);
            column.setCellValueFactory(data -> {
                double[] row = data.getValue();
                return new SimpleStringProperty(
                        col < row.length ? String.format("%.4f", row[col]) : ""
                );
            });
            column.setPrefWidth(90);
            tv.getColumns().add(column);
        }

        for (int i = 0; i < n; i++) {
            double[] row = new double[n - i];
            System.arraycopy(table[i], 0, row, 0, n - i);
            tv.getItems().add(row);
        }
    }

    private void addPointsSeries(List<Point> points) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Точки");
        for (Point p : points) {
            series.getData().add(new XYChart.Data<>(p.getX(), p.getY()));
        }
        interpolationChart.getData().add(series);
        series.getNode().setStyle("-fx-stroke: transparent;");
    }

    private void addCurveSeries(List<Point> curvePoints, String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (Point p : curvePoints) {
            series.getData().add(new XYChart.Data<>(p.getX(), p.getY()));
        }
        interpolationChart.getData().add(series);
        for (XYChart.Data<Number, Number> data : series.getData()) {
            data.getNode().setVisible(false);
        }
    }
}

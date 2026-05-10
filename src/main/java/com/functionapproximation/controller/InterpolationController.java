package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.InterpolationResult;
import com.functionapproximation.model.Point;
import com.functionapproximation.service.LagrangeInterpolation;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;

import java.util.List;

public class InterpolationController {

    @FXML private LineChart<Number, Number> interpolationChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private RadioButton lagrangeRadio;
    @FXML private RadioButton newtonRadio;
    @FXML private ToggleGroup methodToggleGroup;
    @FXML private TitledPane dividedDifferencesPane;
    @FXML private TableView<?> dividedDifferencesTable;

    private InputData inputData;
    private final LagrangeInterpolation lagrangeInterpolation = new LagrangeInterpolation();

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

        interpolationChart.getData().clear();
        addPointsSeries(points);

        if (lagrangeRadio.isSelected()) {
            InterpolationResult result = lagrangeInterpolation.interpolate(points);
            addCurveSeries(result.getCurvePoints(), "Лагранж");
            dividedDifferencesPane.setVisible(false);
        } else {
            dividedDifferencesPane.setVisible(true);
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

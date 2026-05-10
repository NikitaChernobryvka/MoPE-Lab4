package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.LeastSquaresResult;
import com.functionapproximation.model.Point;
import com.functionapproximation.service.LeastSquaresApprox;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class LeastSquaresController {

    @FXML private LineChart<Number, Number> leastSquaresChart;
    @FXML private TableView<double[]> coefficientsTable;
    @FXML private TableColumn<double[], String> coeffIndexColumn;
    @FXML private TableColumn<double[], String> coeffValueColumn;

    private InputData inputData;
    private final LeastSquaresApprox approximator = new LeastSquaresApprox();

    @FXML private void initialize() {
        leastSquaresChart.setAnimated(false);
        leastSquaresChart.setCreateSymbols(true);

        coeffIndexColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(coefficientsTable.getItems().indexOf(data.getValue()))
                )
        );
        coeffValueColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.6f", data.getValue()[0]))
        );
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

    @FXML private void onCalculate() {
        List<Point> points = inputData.getPoints();
        if (points.size() < 2) return;

        int degree = inputData.getDegree();
        LeastSquaresResult result = approximator.approximate(points, degree);

        leastSquaresChart.getData().clear();
        addPointsSeries(points);
        addCurveSeries(result.getCurvePoints());
        fillCoefficientsTable(result.getCoefficients());
    }

    private void fillCoefficientsTable(double[] coefficients) {
        coefficientsTable.getItems().clear();
        for (double coef : coefficients) {
            coefficientsTable.getItems().add(new double[]{coef});
        }
    }

    private void addPointsSeries(List<Point> points) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Точки");
        for (Point p : points) {
            series.getData().add(new XYChart.Data<>(p.getX(), p.getY()));
        }
        leastSquaresChart.getData().add(series);
        series.getNode().setStyle("-fx-stroke: transparent;");
    }

    private void addCurveSeries(List<Point> curvePoints) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("МНК");
        for (Point p : curvePoints) {
            series.getData().add(new XYChart.Data<>(p.getX(), p.getY()));
        }
        leastSquaresChart.getData().add(series);
        for (XYChart.Data<Number, Number> data : series.getData()) {
            data.getNode().setVisible(false);
        }
    }
}

package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.InterpolationResult;
import com.functionapproximation.model.Point;
import com.functionapproximation.service.LagrangeInterpolation;
import com.functionapproximation.service.NewtonInterpolation;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.List;

public class InterpolationController {

    @FXML private LineChart<Number, Number> interpolationChart;
    @FXML private RadioButton lagrangeRadio;
    @FXML private TitledPane dividedDifferencesPane;
    @FXML private TableView<?> dividedDifferencesTable;

    private InputData inputData;
    private final LagrangeInterpolation lagrangeInterpolation = new LagrangeInterpolation();
    private final NewtonInterpolation newtonInterpolation = new NewtonInterpolation();

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
            InterpolationResult result = newtonInterpolation.interpolate(points);
            addCurveSeries(result.getCurvePoints(), "Ньютон");
            buildDividedDiffTable(points.size(), result.getDividedDifferencesTable());
            dividedDifferencesPane.setVisible(true);
        }
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

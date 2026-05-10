package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.LeastSquaresResult;
import com.functionapproximation.model.Point;
import com.functionapproximation.service.LeastSquaresApprox;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ResidualsController {

    @FXML private BarChart<String, Number> residualsChart;
    @FXML private TableView<double[]> residualsTable;
    @FXML private TableColumn<double[], String> pointColumn;
    @FXML private TableColumn<double[], String> actualColumn;
    @FXML private TableColumn<double[], String> calculatedColumn;
    @FXML private TableColumn<double[], String> residualColumn;

    private InputData inputData;
    private final LeastSquaresApprox approximator = new LeastSquaresApprox();

    @FXML private void initialize() {
        residualsChart.setAnimated(false);
        residualsChart.setLegendVisible(false);

        pointColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.4f", data.getValue()[0]))
        );
        actualColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.4f", data.getValue()[1]))
        );
        calculatedColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.4f", data.getValue()[2]))
        );
        residualColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.4f", data.getValue()[3]))
        );
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

    @FXML private void onUpdate() {
        List<Point> points = inputData.getPoints();
        if (points.size() < 2) return;

        LeastSquaresResult result = approximator.approximate(points, inputData.getDegree());

        buildChart(points, result);
        buildTable(points, result);
    }

    private void buildChart(List<Point> points, LeastSquaresResult result) {
        residualsChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        double[] residuals = result.getResiduals();

        for (int i = 0; i < points.size(); i++) {
            String label = String.format("%.2f", points.get(i).getX());
            series.getData().add(new XYChart.Data<>(label, residuals[i]));
        }

        residualsChart.getData().add(series);
    }

    private void buildTable(List<Point> points, LeastSquaresResult result) {
        residualsTable.getItems().clear();
        double[] residuals = result.getResiduals();

        for (int i = 0; i < points.size(); i++) {
            double xi = points.get(i).getX();
            double yi = points.get(i).getY();
            double yHat = yi - residuals[i];
            double ri = residuals[i];
            residualsTable.getItems().add(new double[]{xi, yi, yHat, ri});
        }
    }
}

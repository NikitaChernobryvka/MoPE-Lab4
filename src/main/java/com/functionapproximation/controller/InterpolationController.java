package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.InterpolationResult;
import com.functionapproximation.model.Point;
import com.functionapproximation.service.LagrangeInterpolation;
import com.functionapproximation.service.NewtonInterpolation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.util.List;

public class InterpolationController {

    @FXML private LineChart<Number, Number> interpolationChart;
    @FXML private RadioButton lagrangeRadio;
    @FXML private TitledPane dividedDifferencesPane;
    @FXML private TableView<?> dividedDifferencesTable;

    private InputData inputData;
    private final LagrangeInterpolation lagrangeInterpolation = new LagrangeInterpolation();
    private final NewtonInterpolation newtonInterpolation = new NewtonInterpolation();
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
        if (!validatePoints(points)) return;

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

        Platform.runLater(() -> {
            XYChart.Series<Number, Number> pointsSeries =
                    interpolationChart.getData().get(interpolationChart.getData().size() - 1);
            addPopoverToSeries(pointsSeries);
        });
    }

    @FXML private void onAnimate() {
        List<Point> points = inputData.getPoints();
        if (!validatePoints(points)) return;

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
            Platform.runLater(() -> {
                XYChart.Series<Number, Number> pointsSeries =
                        interpolationChart.getData().get(interpolationChart.getData().size() - 1);
                addPopoverToSeries(pointsSeries);
            });
        });
        animationTimeline.play();
    }

    private boolean validatePoints(List<Point> points) {
        if (points.size() < 2) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText("Спочатку введіть хоча б 2 точки на вкладці 'Введення точок'");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void addPopoverToSeries(XYChart.Series<Number, Number> series) {
        for (XYChart.Data<Number, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node == null) continue;

            PopOver popOver = new PopOver();
            popOver.setDetachable(false);
            popOver.setAutoHide(true);
            popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);

            Label content = new Label(
                    String.format("x = %.4f\ny = %.4f",
                            data.getXValue().doubleValue(),
                            data.getYValue().doubleValue())
            );
            content.setStyle("-fx-padding: 6 10 6 10; -fx-font-size: 13px;");
            popOver.setContentNode(content);

            node.setOnMouseEntered(e -> {
                if (!popOver.isShowing()) popOver.show(node);
            });
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

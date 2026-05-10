package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.LeastSquaresResult;
import com.functionapproximation.model.Point;
import com.functionapproximation.service.LeastSquaresApprox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.util.List;

public class LeastSquaresController {

    @FXML private LineChart<Number, Number> leastSquaresChart;
    @FXML private TableView<double[]> coefficientsTable;
    @FXML private TableColumn<double[], String> coeffIndexColumn;
    @FXML private TableColumn<double[], String> coeffValueColumn;

    private InputData inputData;
    private final LeastSquaresApprox approximator = new LeastSquaresApprox();
    private Timeline animationTimeline;

    @FXML private void initialize() {
        leastSquaresChart.setAnimated(false);
        leastSquaresChart.setCreateSymbols(true);

        coeffIndexColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(coefficientsTable.getItems().indexOf(data.getValue()))
                )
        );
        coeffValueColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.6f", data.getValue()[0] + 0.0))
        );
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

    @FXML private void onCalculate() {
        List<Point> points = inputData.getPoints();
        if (points.size() < 2) return;

        if (animationTimeline != null) animationTimeline.stop();
        leastSquaresChart.setCreateSymbols(true);

        int degree = inputData.getDegree();
        LeastSquaresResult result = approximator.approximate(points, degree);

        leastSquaresChart.getData().clear();
        addCurveSeries(result.getCurvePoints());
        addPointsSeries(points, result.getResiduals());
        fillCoefficientsTable(result.getCoefficients());

        Platform.runLater(() -> {
            fixLegendOrientation(leastSquaresChart);
        });
    }

    @FXML private void onAnimate() {
        List<Point> points = inputData.getPoints();
        if (points.size() < 2) return;

        if (animationTimeline != null) animationTimeline.stop();

        int degree = inputData.getDegree();
        LeastSquaresResult result = approximator.approximate(points, degree);
        List<Point> curvePoints = result.getCurvePoints();

        leastSquaresChart.getData().clear();
        leastSquaresChart.setCreateSymbols(false);

        XYChart.Series<Number, Number> animSeries = new XYChart.Series<>();
        animSeries.setName("МНК");
        leastSquaresChart.getData().add(animSeries);

        addPointsSeries(points, result.getResiduals());
        fillCoefficientsTable(result.getCoefficients());

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
            leastSquaresChart.setCreateSymbols(true);
            if (!leastSquaresChart.getData().isEmpty()) {
                for (XYChart.Data<Number, Number> data : leastSquaresChart.getData().get(0).getData()) {
                    if (data.getNode() != null) data.getNode().setVisible(false);
                }
            }
            Platform.runLater(() -> {
                fixLegendOrientation(leastSquaresChart);
                XYChart.Series<Number, Number> pointsSeries =
                        leastSquaresChart.getData().get(leastSquaresChart.getData().size() - 1);
                addPopoverToSeries(pointsSeries, result.getResiduals());
            });
        });
        animationTimeline.play();
    }

    private void addPopoverToSeries(XYChart.Series<Number, Number> series, double[] residuals) {
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<Number, Number> data = series.getData().get(i);
            Node node = data.getNode();
            if (node == null) continue;

            double residual = residuals[i];

            PopOver popOver = new PopOver();
            popOver.setDetachable(false);
            popOver.setAutoHide(true);
            popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);

            Label content = new Label(
                    String.format("x = %.4f\ny = %.4f\nrᵢ = %.4f",
                            data.getXValue().doubleValue(),
                            data.getYValue().doubleValue(),
                            residual)
            );
            content.setStyle("-fx-padding: 6 10 6 10; -fx-font-size: 13px;");
            popOver.setContentNode(content);

            node.setOnMouseEntered(e -> {
                if (!popOver.isShowing()) popOver.show(node);
            });
        }
    }

    private void fillCoefficientsTable(double[] coefficients) {
        coefficientsTable.getItems().clear();
        for (double coef : coefficients) {
            coefficientsTable.getItems().add(new double[]{coef});
        }
    }

    private void addPointsSeries(List<Point> points, double[] residuals) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Точки");
        for (Point p : points) {
            series.getData().add(new XYChart.Data<>(p.getX(), p.getY()));
        }
        leastSquaresChart.getData().add(series);
        series.getNode().setStyle("-fx-stroke: transparent;");

        Platform.runLater(() -> addPopoverToSeries(series, residuals));
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

    private void fixLegendOrientation(LineChart<?, ?> chart) {
        chart.applyCss();
        chart.layout();
        for (Node node : chart.lookupAll(".chart-legend")) {
            if (node instanceof FlowPane legend) {
                legend.setOrientation(Orientation.HORIZONTAL);
            }
        }
    }
}

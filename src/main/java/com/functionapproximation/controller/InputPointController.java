package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import com.functionapproximation.model.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class InputPointController {

    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private ComboBox<Integer> degreeComboBox;
    @FXML private TableView<Point> pointsTable;
    @FXML private TableColumn<Point, Double> indexColumn;
    @FXML private TableColumn<Point, Double> xColumn;
    @FXML private TableColumn<Point, Double> yColumn;
    @FXML private Label errorLabel;

    private InputData inputData;
    private final ObservableList<Point> pointsList = FXCollections.observableArrayList();

    @FXML private void initialize() {
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));

        indexColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        degreeComboBox.setItems(FXCollections.observableArrayList(2, 3, 4));
        degreeComboBox.setValue(2);

        pointsTable.setItems(pointsList);
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
        degreeComboBox.setOnAction(e -> inputData.setDegree(degreeComboBox.getValue()));
    }

    @FXML private void onAddPoint() {
        errorLabel.setVisible(false);
        if (nameField_getText_isBlank()) return;

        try {
            double x = Double.parseDouble(xField.getText().trim());
            double y = Double.parseDouble(yField.getText().trim());

            for (Point p : pointsList) {
                if (p.getX() == x) {
                    showError("Точка з таким значенням x вже існує!");
                    return;
                }
            }

            Point point = new Point(x, y);
            pointsList.add(point);
            inputData.addPoint(point);

            xField.clear();
            yField.clear();
            xField.requestFocus();

        } catch (NumberFormatException e) {
            showError("Введіть коректні числові значення!");
        }
    }

    private boolean nameField_getText_isBlank() {
        if (xField.getText().isBlank() || yField.getText().isBlank()) {
            showError("Заповніть всі поля!");
            return true;
        }
        return false;
    }

    @FXML private void onDeletePoint() {
        Point selected = pointsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            pointsList.remove(selected);
            inputData.getPoints().remove(selected);
        } else {
            showError("Оберіть точку для видалення!");
        }
    }

    @FXML private void onClearPoints() {
        pointsList.clear();
        inputData.getPoints().clear();
        errorLabel.setVisible(false);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}

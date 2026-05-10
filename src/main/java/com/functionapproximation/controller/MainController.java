package com.functionapproximation.controller;

import com.functionapproximation.model.InputData;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {

    @FXML private TabPane tabPane;
    @FXML private InputPointController inputPointController;
    @FXML private InterpolationController interpolationController;

    private final InputData inputData = new InputData();

    @FXML private void initialize() {
        inputPointController.setInputData(inputData);
        interpolationController.setInputData(inputData);
    }

    public InputData getInputData() {
        return inputData;
    }
}
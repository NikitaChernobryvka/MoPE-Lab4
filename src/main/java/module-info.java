module com.functionapproximation {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.math3;

    requires org.controlsfx.controls;
    requires jdk.compiler;

    opens com.functionapproximation to javafx.fxml;
    opens com.functionapproximation.controller to javafx.fxml;
    opens com.functionapproximation.model to javafx.base;
    exports com.functionapproximation;
}
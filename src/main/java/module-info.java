module com.functionapproximation {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.math3;

    requires org.controlsfx.controls;

    opens com.functionapproximation to javafx.fxml;
    exports com.functionapproximation;
}
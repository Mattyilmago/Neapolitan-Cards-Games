module com.example.demopiudemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demopiudemo to javafx.fxml;
    exports com.example.demopiudemo;
}
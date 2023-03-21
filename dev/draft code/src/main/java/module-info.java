module com.buccaneer.buccaneer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.management;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;

    opens com.buccaneer.buccaneer to javafx.fxml;
    exports com.buccaneer.buccaneer;
    exports com.buccaneer.controllers;
    opens com.buccaneer.controllers to javafx.fxml;
    exports com.buccaneer.backend;
    opens com.buccaneer.backend to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.buccaneer.backend.cards to com.fasterxml.jackson.databind;
    opens com.buccaneer.backend.cards.chance to com.fasterxml.jackson.databind;
    exports com.buccaneer.models;
    opens com.buccaneer.models to com.fasterxml.jackson.databind;
    opens com.buccaneer.models.commodities to com.fasterxml.jackson.databind;
}
package com.buccaneer.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class InstructionController {

    private Scene menu;

    /**
     * Returns to the main menu
     * @param event
     */
    @FXML
    void onBackPressed(MouseEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(menu);
    }

    /**
     * @param menu
     */
    public void setMenu(Scene menu) {
        this.menu = menu;
    }
}

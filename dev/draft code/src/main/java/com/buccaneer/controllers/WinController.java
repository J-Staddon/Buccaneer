package com.buccaneer.controllers;

import com.buccaneer.buccaneer.Buccaneer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WinController extends Application {

    @FXML
    private Label win_player;
    private Scene menu;

    /**
     * @param winner
     */
    public void setWinner(String winner) {
        win_player.setText(winner);
    }

    /**
     * @param menu
     */
    public void setMenu(Scene menu) {
        this.menu = menu;
    }

    /**
     * Exits the game
     * @param event
     */
    public void exit(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
    }

    public void restart(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Buccaneer.fxmlLoad(stage);
    }
}

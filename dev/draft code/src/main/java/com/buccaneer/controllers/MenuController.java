package com.buccaneer.controllers;

import com.buccaneer.backend.SaveData;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;

public class MenuController {

    private Scene input;
    private Scene instructions;
    private Scene play;
    private BoardController boardController;

    /**
     * Loads the player name selection
     * @param event
     */
    @FXML
    void playPressed(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (new File("save.dat").exists()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save File Found");
            alert.setHeaderText("A save file was found. Would you like to load it?");

            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                stage.setScene(play);
                try {
                    boardController.loadSave(SaveData.load());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert2 = new Alert(Alert.AlertType.WARNING, "Save file could not be loaded.");
                    alert2.show();
                }
            }
            new File("save.dat").delete();
        }
        stage.setScene(input);
    }

    /**
     * Loads the instruction page
     * @param event
     */
    @FXML
    void instructionsPressed(MouseEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(instructions);
    }

    /**
     * @param input
     */
    public void setInput(Scene input) { this.input = input; }

    /**
     * @param instructions
     */
    public void setInstructions(Scene instructions) {
        this.instructions = instructions;
    }

    /**
     * @param play
     * @param boardController
     */
    public void setBoard(Scene play, BoardController boardController) {
        this.play = play;
        this.boardController = boardController;
    }
}

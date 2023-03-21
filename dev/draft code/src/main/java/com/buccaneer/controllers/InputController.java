package com.buccaneer.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class InputController {

    private Scene play;
    private BoardController boardController;

    @FXML
    TextField one;

    @FXML
    TextField two;

    @FXML
    TextField three;

    @FXML
    TextField four;

    /**
     * Checks to see if names are valid
     * @param event
     */
    @FXML
    void playPressed(MouseEvent event) {

        //checks if debug mode is active
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("jdwp");

        String[] names = new String[4];

        if(!isDebug) {
            for (var field : new TextField[]{one, two, three, four}) {
                if (field.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Each player must have a name.");
                    alert.show();
                    return;
                } else if (field.getText().length() < 3) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Each name must be at least 3 characters.");
                    alert.show();
                    return;
                } else if (field.getText().length() >= 10) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Each name must be less than 10 characters.");
                    alert.show();
                    return;
                } else if (!field.getText().matches("[a-zA-Z]+")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Player names can only contain letters.");
                    alert.show();
                    return;
                }
                int multipleNames = 0;
                for (var tempName : new TextField[]{one, two, three, four}) {
                    if (field.getText().equals(tempName.getText())) {
                        multipleNames++;
                    }
                }
                if (multipleNames > 1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Each player must have a unique name.");
                    alert.show();
                    return;
                }
            }

            //assign names to text fields
            for(int i=0; i<4; i++) names[i] = (new TextField[]{one, two, three, four})[i].getText();

        }
        //if debug mode is on
        else {
            System.arraycopy(new String[]{"One", "Two", "Three", "Four"}, 0, names, 0, 4);
        }


        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(play);
        boardController.startGame(new String[] {names[0], names[1], names[2], names[3]});
    }

    /**
     * @param play
     * @param boardController
     */
    public void setPlay(Scene play, BoardController boardController) {
        this.play = play;
        this.boardController = boardController;
    }
}

package com.buccaneer.buccaneer;

import com.buccaneer.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Buccaneer extends Application {

    /**
     * Loads the game
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoad(stage);
    }

    public static void fxmlLoad(Stage stage) throws IOException {
        //Load Menu
        FXMLLoader menuLoader = new FXMLLoader(Buccaneer.class.getResource("menu-view.fxml"));
        Scene menu = new Scene(menuLoader.load());

        //Load Input
        FXMLLoader inputLoader = new FXMLLoader(Buccaneer.class.getResource("input-view.fxml"));
        Scene input = new Scene(inputLoader.load());

        //Load Instructions
        FXMLLoader instructionLoader = new FXMLLoader(Buccaneer.class.getResource("instruction-view.fxml"));
        Scene instruction = new Scene(instructionLoader.load());

        //Load Board
        FXMLLoader boardLoader = new FXMLLoader(Buccaneer.class.getResource("board-view.fxml"));
        Scene board = new Scene(boardLoader.load());
        board.getStylesheets().add(Buccaneer.class.getResource("style.css").toExternalForm());

        //Load Win
        FXMLLoader winLoader = new FXMLLoader(Buccaneer.class.getResource("end-view.fxml"));
        Scene win = new Scene(winLoader.load());


        //Allow them to access each other
        ((MenuController)menuLoader.getController()).setInput(input);
        ((MenuController)menuLoader.getController()).setInstructions(instruction);
        ((MenuController)menuLoader.getController()).setBoard(board, boardLoader.getController());
        ((InstructionController)instructionLoader.getController()).setMenu(menu);
        ((InputController)inputLoader.getController()).setPlay(board, boardLoader.getController());
        ((BoardController)boardLoader.getController()).setWin(win, winLoader.getController());
        ((WinController)winLoader.getController()).setMenu(menu);

        stage.setTitle("Buccaneer v1.0.0");
        stage.getIcons().add(new Image(Buccaneer.class.getResourceAsStream("image/icon.png")));
        stage.setResizable(false);
        stage.setScene(menu);
        stage.show();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}
package com.Fritz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public Button playButton;

    @FXML
    private Button instructionsButton;

    @FXML
    public Button exitButtonMainMenu;

    public void handleStartGame(ActionEvent event) throws IOException {
        if (event.getSource().equals(playButton)) {
            root = FXMLLoader.load(getClass().getResource("GamePlay.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root, 990, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void handleInstructionsButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(instructionsButton)) {
            root = FXMLLoader.load(getClass().getResource("Instructions.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root, 990, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void handleExitButton(ActionEvent event) {
        if (event.getSource().equals(exitButtonMainMenu)) {
            Platform.exit();
        }
    }
}
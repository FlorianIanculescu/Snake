package com.Fritz;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class GamePlayController implements Initializable {

    Random random = new Random();

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private GridPane gridPane;
    @FXML
    private Button backToMain;
    @FXML
    private Button exit;
    @FXML
    private Button playAgain;
    @FXML
    private Label infoLabel;
    @FXML
    private Label gameOverLabel;
    @FXML
    private Label scoreLabel;

    ArrayList<Label> snake = new ArrayList<>();
    Label headLabel = new Label();
    private int bodyParts = 3;

    int randomPositionY;
    int randomPositionX;

    private final String bodyStyle = "-fx-background-color: TRANSPARENT; -fx-background-radius: 20px; -fx-border-width: 4px; -fx-border-color: #00aeef; -fx-border-radius: 30";

    private MediaPlayer mediaPlayer;
    private Timeline timeline;

    private final int maxPositionX = 32;
    private final int maxPositionY = 19;

    ImageView headImageView = new ImageView();
    Image headImage;
    String headImageName;

    ImageView fruitImageView;
    Image fruitImage;
    String fruitImageName;
    private final String[] fruitName= {"apple.png", "orange.png", "banana.png", "cherry.png"};

    String eventString;
    private String direction;

    private int countSpace;
    private boolean gamePaused = false;
    private boolean firstPress = true;

    private int scoreValue = 0;
    private int speed = 130;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawGridPane();
    }

    private void run() {
            timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> startGame()));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    }

    private void startGame() {
        adjustPosition();
        changeDirection(eventString);
        verifyEat();
        checkCollision();
    }

    private int randomPosition(int min, int maxPosition) {
        return random.nextInt(min, maxPosition);
    }

    private void drawGridPane() {
        for (int i = 0; i <= maxPositionX; i++) {
            for (int j = 0; j <= maxPositionY; j++) {
                Label label = new Label("");
                gridPane.add(label, i, j);
                label.setPrefSize(30, 30);
                if ((i + j) % 2 == 0) {
                    label.setStyle("-fx-background-color: #AAD751;");
                } else {
                    label.setStyle("-fx-background-color: #A2D149;");
                }
            }
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE && firstPress) {
            countSpace++;
            infoLabel.setVisible(false);
            eventString = "RIGHT";
            firstPress = false;
            drawSnake();
            drawFruit();
            run();
        } else if (event.getCode() == KeyCode.SPACE && countSpace % 2 != 0) {
            countSpace++;
            pauseGame();
            gamePaused = true;
        } else if (event.getCode() == KeyCode.SPACE && countSpace % 2 == 0) {
            countSpace++;
            restartGame();
            gamePaused = false;
        }
        else if (!gamePaused) {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                if (!firstPress) {
                    eventString = event.getCode().toString();
                }
            }
        }
    }

    private void drawHead(int snakeHeadX, int snakeHeadY) {
        snake.add(headLabel);
        headLabel.setPrefSize(29, 29);
        headLabel.setAlignment(Pos.CENTER);
        GridPane.setHalignment(headLabel, HPos.CENTER);
        GridPane.setValignment(headLabel, VPos.CENTER);
        gridPane.add(headLabel, snakeHeadX, snakeHeadY);
        headLabel.setGraphic(changeHeadImage(eventString));
    }

    private ImageView changeHeadImage(String direction) {
        headImageName = direction.toLowerCase() + "mouth.png";
        headImage = new Image(getClass().getResourceAsStream(headImageName));
        headImageView.setImage(headImage);
        return headImageView;
    }

    private void drawBody(int snakeHeadX, int snakeHeadY) {
        Label label = new Label();
        snake.add(label);
        label.setPrefSize(28, 28);
        label.setAlignment(Pos.CENTER);
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setValignment(label, VPos.CENTER);
        gridPane.add(label, snakeHeadX, snakeHeadY);
        label.setStyle(bodyStyle);
    }

    private void drawSnake() {
        int snakeHeadX = randomPosition(3, maxPositionX);
        int snakeHeadY = randomPosition(3, maxPositionY);

        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                drawHead(snakeHeadX, snakeHeadY);
            } else {
                drawBody(snakeHeadX, snakeHeadY);
            }
            snakeHeadX = snakeHeadX - 1;
        }
    }

    @FXML
    private void adjustPosition() {
        for(int i = bodyParts - 1; i >= 1; i--) {
            GridPane.setRowIndex(snake.get(i), GridPane.getRowIndex(snake.get(i - 1)));
            GridPane.setColumnIndex(snake.get(i), GridPane.getColumnIndex(snake.get(i - 1)));
        }
    }

    private void changeDirection(String eventString) {
        int snakeHeadX = GridPane.getColumnIndex(snake.get(0));
        int snakeHeadY = GridPane.getRowIndex(snake.get(0));
        switch (eventString) {
            case "LEFT" -> {
                if (direction != "RIGHT") {
                    moveLeft(snakeHeadX);
                    direction = "LEFT";
                }
            }
            case "RIGHT" -> {
                if (direction != "LEFT") {
                    moveRight(snakeHeadX);
                    direction = "RIGHT";
                }
            }
            case "UP" -> {
                if (direction != "DOWN") {
                    moveUp(snakeHeadY);
                    direction = "UP";
                }
            }
            case "DOWN" -> {
                if (direction != "UP") {
                    moveDown(snakeHeadY);
                    direction = "DOWN";
                }
            }
        }
        headLabel.setGraphic(changeHeadImage(eventString));
    }

    private void verifyEat() {
        if (GridPane.getRowIndex(snake.get(0)).equals(GridPane.getRowIndex(fruitImageView))) {
            if (GridPane.getColumnIndex(snake.get(0)).equals(GridPane.getColumnIndex(fruitImageView))) {
                adjustScoreLabel();
                newFruit();
                timeline.setRate(timeline.getCurrentRate() + 0.01);
                bodyParts++;
                addPoint();
                playMedia();
            }
        }
    }

    private void drawFruit() {
        fruitImageView = new ImageView();
        fruitImageName = randomFruit();
        fruitImage = new Image(getClass().getResourceAsStream(fruitImageName));
        randomPositionY = randomPosition(0, maxPositionY);
        randomPositionX = randomPosition(0, maxPositionX);
        fruitImageView.setImage(fruitImage);
        fruitImageView.setFitWidth(30);
        fruitImageView.setFitHeight(30);
        gridPane.add(fruitImageView, randomPositionX, randomPositionY);
    }

    private String randomFruit() {
        return fruitName[random.nextInt(0, 4)];
    }

    private void playMedia() {
        mediaPlayer = new MediaPlayer(new Media(this.getClass().getResource("eat.wav").toExternalForm()));
        mediaPlayer.play();
    }

    private void addPoint() {
        drawBody(GridPane.getColumnIndex(snake.get(snake.size() - 2)), GridPane.getRowIndex(snake.get(snake.size() - 2)));
    }

    private void adjustScoreLabel() {

        switch (fruitImageName) {
            case "apple.png" -> scoreValue += 2;
            case "orange.png" -> scoreValue += 3;
            case "banana.png" -> scoreValue += 4;
            case "cherry.png" -> scoreValue += 5;
        }

        scoreLabel.setText(String.valueOf(scoreValue));
    }

    private void newFruit() {
        gridPane.getChildren().remove(fruitImageView);
        drawFruit();
    }

    private void moveRight(int dir) {
        GridPane.setColumnIndex(snake.get(0), dir + 1);
    }

    private void moveLeft(int dir) {
        GridPane.setColumnIndex(snake.get(0), dir - 1);
    }

    private void moveUp(int dir) {
        GridPane.setRowIndex(snake.get(0), dir - 1);
    }

    private void moveDown(int dir) {
        GridPane.setRowIndex(snake.get(0), dir + 1);
    }

    private void checkCollision() {
        if (GridPane.getColumnIndex(snake.get(0)) < 0 || GridPane.getColumnIndex(snake.get(0)) > maxPositionX ||
            GridPane.getRowIndex(snake.get(0)) < 0 || GridPane.getRowIndex(snake.get(0)) > maxPositionY) {
            gameOver();
        }

        for(int i = 1; i < snake.size(); i++) {
            if (GridPane.getRowIndex(snake.get(0)).equals(GridPane.getRowIndex(snake.get(i))) &&
                GridPane.getColumnIndex(snake.get(0)).equals(GridPane.getColumnIndex(snake.get(i)))) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        timeline.stop();
        playAgain.setVisible(true);
        gameOverLabel.setVisible(true);
        backToMain.setVisible(true);
        exit.setVisible(true);
    }

    private void pauseGame() {
        infoLabel.setText("Pause");
        infoLabel.setVisible(true);
        timeline.pause();
    }

    private void restartGame() {
        infoLabel.setVisible(false);
        timeline.play();
    }

    @FXML
    private void handleBackToMain(ActionEvent event) throws IOException {
        if (event.getSource().equals(backToMain)) {
            root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void handlePlayAgain(ActionEvent event) throws IOException {
        if (event.getSource().equals(playAgain)) {
            root = FXMLLoader.load(getClass().getResource("GamePlay.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root, 990, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        if (event.getSource().equals(exit)) {
            Platform.exit();
        }
    }
}

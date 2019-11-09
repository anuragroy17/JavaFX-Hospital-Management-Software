package com.anuragroy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/mainPage.fxml")); //load the main page view
            primaryStage.setTitle("Welcome");
            primaryStage.setScene(new Scene(root, 1200, 700));

            primaryStage.getIcons().add(new Image("media/icon.png"));
            primaryStage.getScene().getStylesheets().addAll(getClass().getResource("/style/style.css").toExternalForm());

            ////**********Move Window on Mouse Drag anywhere on the screen*****/////
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            //move around here
            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            });

            primaryStage.show();
            primaryStage.setMaximized(false);
            primaryStage.setResizable(false);
            primaryStage.sizeToScene();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main (String[]args){
            launch(args);
        }
}
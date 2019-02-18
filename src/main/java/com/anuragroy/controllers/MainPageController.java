package com.anuragroy.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.anuragroy.model.FxmlLoader;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private Pane pane1, pane2, pane3, pane4;
    @FXML
    private JFXButton btnPatient, btnDoctor, btnAbout;
    @FXML
    private Label lblAbout;

    //Constructor
    public MainPageController(){

    }

    //Method to open respective login pages on button press
    @FXML
    public void handleButtonClicks(javafx.event.ActionEvent ae) {
        if (ae.getSource() == btnDoctor) {
            FxmlLoader.loadStage("/views/doctorLogin.fxml"); //loads doctor login page
            ((Node)(ae.getSource())).getScene().getWindow().hide(); //hides the main selection page
        } else if (ae.getSource() == btnPatient) {
            FxmlLoader.loadStage("/views/patientLogin.fxml"); //loads patients login page
            ((Node)(ae.getSource())).getScene().getWindow().hide();
        } else if (ae.getSource() == btnAbout) {
            showToast("Leads to Website!");
        }
    }

    @FXML
    private void closeApp() {
        System.exit(0);
    }

    //Method to show message on "About Us" button press
    private void showToast(String message) {
        lblAbout.setText(message);
        lblAbout.setTextFill(Color.TOMATO);
        lblAbout.setStyle("-fx-background-color: #fff; -fx-background-radius: 50px;");

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), lblAbout);  //show the message
        fadeIn.setToValue(1);
        fadeIn.setFromValue(0);
        fadeIn.play();

        fadeIn.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));    //let the message stay
            pause.play();
            pause.setOnFinished(event2 -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), lblAbout);    //fadeout the message
                fadeOut.setToValue(0);
                fadeOut.setFromValue(1);
                fadeOut.play();
            });
        });
    }


    //Init method to load the images into panes and call the method to control transitions
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        pane1.setStyle("-fx-background-image: url(\"media/1.jpg\")");
        pane2.setStyle("-fx-background-image: url(\"media/2.jpg\")");
        pane3.setStyle("-fx-background-image: url(\"media/3.jpg\")");
        pane4.setStyle("-fx-background-image: url(\"media/4.jpg\")");

        lblAbout.setText("");
        backgroundAnimation();
    }

    //Method to control image transitions
    private void backgroundAnimation() {

        FadeTransition fadeTransition=new FadeTransition(Duration.seconds(5),pane4);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        //**********starts fadein-fadeout animations of images
        fadeTransition.setOnFinished(event -> {

            FadeTransition fadeTransition1=new FadeTransition(Duration.seconds(4),pane3);
            fadeTransition1.setFromValue(1);
            fadeTransition1.setToValue(0);
            fadeTransition1.play();

            fadeTransition1.setOnFinished(event1 -> {
                FadeTransition fadeTransition2=new FadeTransition(Duration.seconds(4),pane2);
                fadeTransition2.setFromValue(1);
                fadeTransition2.setToValue(0);
                fadeTransition2.play();

                fadeTransition2.setOnFinished(event2 -> {

                    FadeTransition fadeTransition0 =new FadeTransition(Duration.seconds(4),pane2);
                    fadeTransition0.setToValue(1);
                    fadeTransition0.setFromValue(0);
                    fadeTransition0.play();

                    fadeTransition0.setOnFinished(event3 -> {

                        FadeTransition fadeTransition11 =new FadeTransition(Duration.seconds(4),pane3);
                        fadeTransition11.setToValue(1);
                        fadeTransition11.setFromValue(0);
                        fadeTransition11.play();

                        fadeTransition11.setOnFinished(event4 -> {

                            FadeTransition fadeTransition22 =new FadeTransition(Duration.seconds(4),pane4);
                            fadeTransition22.setToValue(1);
                            fadeTransition22.setFromValue(0);
                            fadeTransition22.play();

                            fadeTransition22.setOnFinished(event5 -> {
                                backgroundAnimation();                                 /////Infinite loop to animate images continuously by recursively calling same method
                            });

                        });

                    });

                });
            });

        });
    }
}

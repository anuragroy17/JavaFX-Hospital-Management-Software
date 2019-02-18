package com.anuragroy.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import com.anuragroy.entities.DBConnection;
import com.anuragroy.model.SessionSaver;
import com.anuragroy.model.FxmlLoader;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PatientDashboard implements Initializable {

    private Connection con = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @FXML
    private JFXButton btnMain, btnNo, btnYes;
    @FXML
    private Label lblName, lblDisease, lblMed, lblComplain, lblThank, lblErrors, lblReport;


    ///Constructor call on opening page
    public PatientDashboard(){
        try {
            con = DBConnection.getConnection();  //establishing connection to database
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeApp() throws SQLException {
        if (con != null) {
            con.close();
        }
        System.exit(0);
    }

    @FXML
    public void handleButtonClicks(javafx.event.ActionEvent ae) throws SQLException {
        if (ae.getSource() == btnMain) {    ////signout to main page
            if (con != null) {
                con.close();
            }
            FxmlLoader.loadStage("/views/mainPage.fxml");
            ((Node) (ae.getSource())).getScene().getWindow().hide();
        } else if(ae.getSource() == btnYes) {     /////yes is pressed when patient feels he is cured
            yesPressed();
            buttonCheck("Yes");
        } else if(ae.getSource() == btnNo) {      ////no is pressed when patient is not cured
            noPressed();
            buttonCheck("No");
        }
    }

    //method to clear all the fields
    private void clearField(){
        lblThank.setText("");
        lblComplain.setText("");
        lblName.setText("");
        lblReport.setText("");
        lblDisease.setText("");
        lblDisease.setText("");
    }

    //Data to show on dashboard after login as per the patient
    private void loginData() throws SQLException {
        lblThank.setText("");
        String username = SessionSaver.getUsername();  //pull username who has logged in
        String sql = "SELECT * FROM patients Where username = ?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String complain = resultSet.getString("complain");
                lblComplain.setText(complain);
                String Name = resultSet.getString("first_name")+" "+resultSet.getString("last_name");
                lblName.setText(Name);
                String report = resultSet.getString("report");
                lblReport.setText(report);
                String disease = resultSet.getString("disease");
                lblDisease.setText(disease);
                String medicine = resultSet.getString("medicine");
                lblMed.setText(medicine);
                String cured = resultSet.getString("cured");
                buttonCheck(cured);
            } else {
                clearField();
                lblErrors.setTextFill(Color.TOMATO);
                lblErrors.setText("Cannot Retreive Data. Server Error");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    //"Are You Cured" section handler
    private void buttonCheck(String cured){
        switch (cured) {
            case "Yes":
                btnNo.setDisable(true);
                btnYes.setDisable(true);
                lblThank.setTextFill(Color.GREEN);
                lblThank.setText("Thanks for letting us know that you're cured!");
                break;
            case "No":
                btnYes.setDisable(true);
                btnNo.setDisable(true);
                lblThank.setTextFill(Color.TOMATO);
                lblThank.setText("We've updated the Doctor. Your appointment is at 2 P.M.");
                break;
            case "Not Set":
                btnYes.setDisable(true);
                btnNo.setDisable(true);
                lblThank.setTextFill(Color.TOMATO);
                lblThank.setText("Wait for your Report to enable this section!");
                break;
            case "Report Sent":                   //enable "Are you cured" section
                btnYes.setDisable(false);
                btnNo.setDisable(false);
                lblThank.setTextFill(Color.CADETBLUE);
                lblThank.setText("Your Report is ready. Please follow instructions set and then give your feedback");
                break;
        }
    }

    //When patient presses "yes" -- Doctor dashboard gets the access to delete the patient
    private void yesPressed() throws SQLException {
        lblThank.setText("");
        String cured = "Yes";
        String username = SessionSaver.getUsername();
        String sql = "UPDATE patients SET cured=? where username=?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, cured);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {

            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    //When patient presses "No" fields in left pane is updated ---- Doctor has to recheck and update details from doctor dashboard
    private void noPressed() throws SQLException {
        lblThank.setText("");
        String cured = "No";
        String report = "New Report Request";
        String disease = "Update Disease";
        String medicine = "Update Medicine";
        String username = SessionSaver.getUsername();
        String sql = "UPDATE patients SET cured=?, report=?, disease=?, medicine=? where username=?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, cured);
            preparedStatement.setString(2, report);
            preparedStatement.setString(3, disease);
            preparedStatement.setString(4, medicine);
            preparedStatement.setString(5, username);
            preparedStatement.executeUpdate();
            lblReport.setText("New Report Requested");
            lblDisease.setText("Disease will be updated");
            lblMed.setText("New Medicines will be assigned");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally {

            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (con == null) {
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Server Error : Check");  //let user know error in establishing connection to database
        } else {
            lblErrors.setTextFill(Color.GREEN);
            lblErrors.setText("Server is up, All Good!");  //let the user know connection to database is established
        }

        try {
            loginData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

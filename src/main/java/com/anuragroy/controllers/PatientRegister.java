package com.anuragroy.controllers;

import com.anuragroy.model.DeptSelector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.anuragroy.entities.DBConnection;
import com.anuragroy.model.FxmlLoader;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PatientRegister implements Initializable {

    private Connection con = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @FXML
    private JFXTextField txtPhoneNumber, txtUsername, txtLastName, txtFirstName;
    @FXML
    private JFXPasswordField txtPassword, txtCPassword;
    @FXML
    private JFXComboBox<String> dropDown;
    @FXML
    private JFXButton btnMain, btnCreate, btnAbout, btnSignin;
    @FXML
    private DatePicker txtDob;
    @FXML
    private Label lblErrors, lblSaved, lblAbout;


    ///Constructor call on opening page
    public PatientRegister() {
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
        if (ae.getSource() == btnMain) {    ///button to go back to main page
            if (con != null) {
                con.close();
            }
            FxmlLoader.loadStage("/views/mainPage.fxml");
            ((Node) (ae.getSource())).getScene().getWindow().hide();
        } else if (ae.getSource() == btnSignin) {    ///button to go back to signin page
            if (con != null) {
                con.close();
            }
            FxmlLoader.loadStage("/views/patientLogin.fxml");
            ((Node) (ae.getSource())).getScene().getWindow().hide();
        } else if (ae.getSource() == btnCreate) {      ///button to create patient account
            createPressed();
        }else if(ae.getSource() == btnAbout){  ///button to show About Us
            showAbout("Leads to Website!");
        }
    }

    //method when Create button is pressed
    private void createPressed() throws SQLException {
        //check if all fields are filled
        if (dropDown.getSelectionModel().isEmpty() || txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtDob.getValue().toString().equals("")
                || txtPhoneNumber.getText().isEmpty() || txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty() || txtCPassword.getText().isEmpty()) {
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Enter all details");
        }
        //check if password and confirm password is equal
        else if (!txtPassword.getText().equals(txtCPassword.getText())) {
            txtPassword.clear();
            txtCPassword.clear();
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Passwords donot match!");
        }
        //check if username already exists in database
        else if(checkUsername(txtUsername.getText())){
            txtUsername.clear();
            txtPassword.clear();
            txtCPassword.clear();
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Username Exists!");
        }
        //create the account if all above conditions are fulfilled
        else {
            String verify = saveData();
            if (verify.equals("Success")){
                if (con != null) {
                    con.close();
                }
            }
        }
    }

    //Method to check if same username exists
    private boolean checkUsername(String text) throws SQLException {
        String sql = "SELECT * FROM patients Where username = ?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, text);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return true;
        }finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    //method to take the data entered and create an account
    private String saveData() throws SQLException {
        try {
            String st = "INSERT INTO patients ( complain, department, first_name, last_name, dob, phone_number, username, password, report, disease, medicine, cured) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            String patientComplain = dropDown.getValue();
            String coressDept= DeptSelector.setDepartment(patientComplain);

            preparedStatement = con.prepareStatement(st);
            preparedStatement.setString(1, patientComplain);
            preparedStatement.setString(2, coressDept);
            preparedStatement.setString(3, txtFirstName.getText());
            preparedStatement.setString(4, txtLastName.getText());
            preparedStatement.setString(5, txtDob.getValue().toString());
            preparedStatement.setString(6, txtPhoneNumber.getText());
            preparedStatement.setString(7, txtUsername.getText());
            preparedStatement.setString(8, txtPassword.getText());
            preparedStatement.setString(9, "No Result Yet");
            preparedStatement.setString(10, "No Result Yet");
            preparedStatement.setString(11, "No Result Yet");
            preparedStatement.setString(12, "Not Set");

            preparedStatement.executeUpdate();

            lblErrors.setTextFill(Color.GREEN);
            lblErrors.setText("Added Successfully");
            reset();

            //*********to show the patient which Doctor has been assigned*********//
                String sql = "SELECT * FROM doctors Where department = ?";
                try {
                    preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setString(1, coressDept);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String doctorName = resultSet.getString(3);
                        String message = "Thanks for Registering. You have been Assigned to Dr. " +doctorName+ " of "+coressDept+ " Department." +
                                " Your Appointment Has Been Set For 2 P.M. today. Please SIGN-IN to check your Report after Appoinment.";
                        showToast(message);
                    } else {
                        lblSaved.setTextFill(Color.TOMATO);
                        lblSaved.setText("No Doctor found");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            //******************************************************************************************//

            return "Success";
        } catch (SQLException ex) {
            ex.printStackTrace();
            if(ex.toString().contains("Incorrect integer")){
                lblErrors.setTextFill(Color.TOMATO);
                lblErrors.setText("Please Enter Valid Phone Number");
            }else {
                lblErrors.setTextFill(Color.TOMATO);
                lblErrors.setText(ex.getMessage());
            }
            return "Exception";
        }finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    //method to clear all fields that has data entered
    private void reset() {
        dropDown.setValue(null);
        txtFirstName.clear();
        txtLastName.clear();
        txtDob.setValue(null);
        txtPhoneNumber.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtCPassword.clear();
        btnCreate.setDisable(true);
    }

    //method to show Appointment message to patient on successful registration
    private void showToast(String message) {
        lblSaved.setText(message);
        lblSaved.setTextFill(Color.BLUE);
        lblSaved.setStyle("-fx-background-color: #fff; -fx-background-radius: 50px;");

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), lblSaved);
        fadeIn.setToValue(1);
        fadeIn.setFromValue(0);
        fadeIn.play();

        fadeIn.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(8));
            pause.play();
            pause.setOnFinished(event2 -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), lblSaved);
                fadeOut.setToValue(0);
                fadeOut.setFromValue(1);
                fadeOut.play();
            });
        });
    }

    //method to show message on pressing "About Us"
    private void showAbout(String message) {
        lblAbout.setText(message);
        lblAbout.setTextFill(Color.TOMATO);
        lblAbout.setStyle("-fx-background-color: #dee1e2; -fx-background-radius: 50px;");

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), lblAbout);
        fadeIn.setToValue(1);
        fadeIn.setFromValue(0);
        fadeIn.play();

        fadeIn.setOnFinished(event -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.play();
                        pause.setOnFinished(event2 -> {
                        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), lblAbout);
                        fadeOut.setToValue(0);
                        fadeOut.setFromValue(1);
                        fadeOut.play();
                        });
                });
    }

    public void initialize(URL location, ResourceBundle resources) {
        ///Add Complains to dropdown...
        dropDown.getItems().clear();
        dropDown.getItems().addAll(DeptSelector.getCOMPLAINS());

        lblSaved.setText("");
        lblAbout.setText("");

        if (con == null) {
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Server Error : Check");  //let user know error in establishing connection to database
        } else {
            lblErrors.setTextFill(Color.GREEN);
            lblErrors.setText("Server is up : Good to go");  //let the user know connection to database is established
        }

        ///Register on pressing enter on Keyboard
        txtCPassword.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                try {
                    createPressed();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        //
    }
}

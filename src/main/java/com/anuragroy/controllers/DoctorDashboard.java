package com.anuragroy.controllers;

import com.anuragroy.entities.DBConnection;
import com.anuragroy.model.FxmlLoader;
import com.anuragroy.model.ModelTable;
import com.anuragroy.model.SessionSaver;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DoctorDashboard implements Initializable {

    private Connection con = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private ObservableList<ModelTable> list;

    @FXML
    private Label lblPname, lblErrors, lblEdit, lblName;
    @FXML
    private JFXButton btnEdit, btnCancel, btnMain, btnDone, btnDel;
    @FXML
    private TreeTableView<ModelTable> treeTableView;
    @FXML
    private TreeTableColumn<ModelTable, String> medCol, disCol, nameCol, reportCol, cureCol;
    @FXML
    private JFXTextField txtDisease;
    @FXML
    private JFXTextArea txtReport, txtMed;


    ///Constructor call on opening page
    public DoctorDashboard(){
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
        if (ae.getSource() == btnMain) {     //Button to log out and go to main page
            if (con != null) {
                con.close();
            }
            FxmlLoader.loadStage("/views/mainPage.fxml");
            ((Node) (ae.getSource())).getScene().getWindow().hide();
        } else if(ae.getSource() == btnEdit){  //Button to edit fields of the selected patient
            showButtons(true);
            editableFields(true);
            txtReport.setText("");
            txtDisease.setText("");
            txtMed.setText("");

            btnEdit.setDisable(true);
            lblEdit.setText("");
        } else if(ae.getSource() == btnCancel){   //Button to cancel editing
            showButtons(false);
            btnEdit.setDisable(false);
            TreeItem<ModelTable> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedItem.getValue();
            }
            showDetails(selectedItem);
            editableFields(false);
        } else if(ae.getSource() == btnDone){     //Button to update the database and show it in table of the selected user
            updatePressed();
        } else if(ae.getSource() == btnDel){      //Button to delete the patient (Only available if patient says he is cured)
            deleteEntry();
            //delete the same entry from table
            int index=treeTableView.getSelectionModel().getSelectedIndex();
            list.remove(index);
            //
            fieldClear();
            btnDel.setDisable(true);
            btnEdit.setDisable(true);
            showButtons(false);
            editableFields(false);
            lblEdit.setTextFill(Color.GREEN);
            lblEdit.setText("Patient Data Deleted");
        }
    }

    //Method to delete patient from database on pressing Del button, when patient says he is cured
    private void deleteEntry() throws SQLException {
        //String to get the first_name from label - Patient Name
        String pName = lblPname.getText();
        int spacePos = pName.indexOf(" ");
        if(spacePos>0){
            pName = pName.substring(0, spacePos);
        }
        //
        String sql = "DELETE FROM patients where first_name=?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, pName);
            preparedStatement.executeUpdate();
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

    //Method if update button is pressed
    private void updatePressed() throws SQLException {
        TreeItem<ModelTable> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
        String report = selectedItem.getValue().getReport();
        String disease = selectedItem.getValue().getDisease();
        String medicine = selectedItem.getValue().getMedicine();
        //To check if the same text as before is found in the fields
        if(report.equals(txtReport.getText()) || disease.equals(txtDisease.getText()) || medicine.equals(txtMed.getText())){
            lblEdit.setTextFill(Color.TOMATO);
            lblEdit.setText("Please Edit All Fields!");
        }
        //To check if none of the fields is clear
        else if(txtReport.getText().equals("") || txtDisease.getText().equals("") || txtMed.getText().equals("")){
            lblEdit.setTextFill(Color.TOMATO);
            lblEdit.setText("Fields can't be empty");
        }
        ///Continue updating if above conditions are fulfilled
        else {
            updateFields();
            //update the same fields in the selected table entry
            ModelTable m = new ModelTable(selectedItem.getValue().getName(),txtReport.getText(),txtDisease.getText(),txtMed.getText(), "Report Sent");
            selectedItem.setValue(m);
            //
            btnDel.setDisable(true);
            btnEdit.setDisable(false);
            showButtons(false);
            editableFields(false);
            lblEdit.setTextFill(Color.GREEN);
            lblEdit.setText("All Fields Updated!");
        }
    }

    //Method to update the report in database of the selected patient, only possible when patient cured field says - "Not Set" or "Report Sent" or  "No"
    private void updateFields() throws SQLException {
        //String to get the first_name from label - Patient Name
        String pName = lblPname.getText();
        int spacePos = pName.indexOf(" ");
        if(spacePos>0){
            pName = pName.substring(0, spacePos);
        }
        //
        String report = txtReport.getText();
        String disease = txtDisease.getText();
        String medicine = txtMed.getText();
        String sql = "UPDATE patients SET report=?, disease=?, medicine=?, cured=? where first_name=?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, report);
            preparedStatement.setString(2, disease);
            preparedStatement.setString(3, medicine);
            preparedStatement.setString(4, "Report Sent");
            preparedStatement.setString(5, pName);
            preparedStatement.executeUpdate();
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


    //Method to show or not-show the Update & Cancel button
    private void showButtons(boolean b) {
        btnDone.setVisible(b);
        btnCancel.setVisible(b);
    }

    //Method to disable the update-able fields
    private void editableFields(boolean b) {
        txtReport.setEditable(b);
        txtDisease.setEditable(b);
        txtMed.setEditable(b);
    }


    //Method to clear all fields when nothing is selected in table
    private void fieldClear() {
        lblPname.setText("");
        txtReport.setText("");
        txtDisease.setText("");
        txtMed.setText("");
        lblEdit.setText("");
    }

    //Method to show the fields by taking data from selected item in table
    private void showDetails(TreeItem<ModelTable> treeItem) {
        lblPname.setText(treeItem.getValue().getName());
        txtReport.setText(treeItem.getValue().getReport());
        txtDisease.setText(treeItem.getValue().getDisease());
        txtMed.setText(treeItem.getValue().getMedicine());
        String cured = treeItem.getValue().getCured();
        if (cured.equals("Yes")){
            btnDel.setDisable(false);
            btnEdit.setDisable(true);
        }else{
            btnDel.setDisable(true);
            btnEdit.setDisable(false);
        }
    }

    //Method to populate the table using the ModelTable after login
    private void populateTable() throws SQLException {
        //Retreive the data from ModelTable and set the data into table cells
        nameCol.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        reportCol.setCellValueFactory(param -> param.getValue().getValue().reportProperty());
        disCol.setCellValueFactory(param -> param.getValue().getValue().diseaseProperty());
        medCol.setCellValueFactory(param -> param.getValue().getValue().medicineProperty());
        cureCol.setCellValueFactory(param -> param.getValue().getValue().curedProperty());

        //Instantiate the Observable list
        list= FXCollections.observableArrayList();

        //Fill the table with data
        TreeItem<ModelTable> root=new RecursiveTreeItem<ModelTable>(list, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);

        //Take data from database and feed to list
        String username = SessionSaver.getUsername();
        String department = SessionSaver.getDepartment();
        String sql = "SELECT * FROM patients Where department = ?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, department);
            resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
                    String pName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
                    list.add(new ModelTable(pName, resultSet.getString("report"), resultSet.getString("disease"),  ///Push the data collected from database to ModelTable
                            resultSet.getString("medicine"), resultSet.getString("cured")));                       //and store in list
                }
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

        //call to database to retrieve the name of Doctor who logged in
        sql = "SELECT * FROM doctors Where username = ?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String Name = resultSet.getString("full_name");
                lblName.setText(Name);
            } else {
                lblErrors.setTextFill(Color.TOMATO);
                lblErrors.setText("Cannot Retreive Data. Server Error");
            }
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

        btnDel.setDisable(true);
        btnEdit.setDisable(true);

        fieldClear();

        showButtons(false);

        try {
            populateTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        editableFields(false);

        ////Event Handler to see if any table is selected
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showDetails(newValue);
            btnEdit.setDisable(false);
            editableFields(false);
            showButtons(false);
            lblEdit.setText("");
            TreeItem<ModelTable> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
            if(selectedItem.getValue().getCured().equals("Yes")){
                btnDel.setDisable(false);
                btnEdit.setDisable(true);
            }
        });


        ///SignIn on pressing enter on Keyboard
        txtMed.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                try {
                    updatePressed();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        //
    }
}

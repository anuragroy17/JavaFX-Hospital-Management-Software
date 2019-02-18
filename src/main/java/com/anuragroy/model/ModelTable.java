package com.anuragroy.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


//Model Class to Store Patient Data and can be retrieved into a treeTableView
public class ModelTable extends RecursiveTreeObject<ModelTable> {
    private StringProperty name, report, disease, medicine, cured;

    public ModelTable(String name, String report, String disease, String medicine, String cured) {
        this.name = new SimpleStringProperty(name) ;
        this.report = new SimpleStringProperty(report) ;
        this.disease = new SimpleStringProperty(disease) ;
        this.medicine = new SimpleStringProperty(medicine) ;
        this.cured = new SimpleStringProperty(cured) ;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getReport() {
        return report.get();
    }

    public StringProperty reportProperty() {
        return report;
    }

    public String getDisease() {
        return disease.get();
    }

    public StringProperty diseaseProperty() {
        return disease;
    }

    public String getMedicine() {
        return medicine.get();
    }

    public StringProperty medicineProperty() {
        return medicine;
    }

    public String getCured() {
        return cured.get();
    }

    public StringProperty curedProperty() {
        return cured;
    }
}

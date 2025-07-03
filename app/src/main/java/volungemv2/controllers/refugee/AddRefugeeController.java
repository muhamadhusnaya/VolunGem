package volungemv2.controllers.refugee;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import volungemv2.models.Refugee;

public class AddRefugeeController {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private Label label_namaPengungsi;

    @FXML
    private TextField textField_namaPengungsi;

    @FXML
    private Label label_keluhan;

    @FXML
    private Label label_lokasi;

    @FXML
    private Label label_umur;

    @FXML
    private TextField textField_keluhan;

    @FXML
    private TextField textField_umur;

    @FXML
    private Label label_jenisKelamin;

    @FXML
    private ComboBox<String> comboBox_jenisKelamin;

    @FXML
    private TextField textField_lokasi;

    public void initialize() {
        setupComboBoxes();
        setupValidation();
    }

    private void setupComboBoxes() {
        // Setup ComboBox untuk Jenis Kelamin
        comboBox_jenisKelamin.setItems(FXCollections.observableArrayList(
            "Laki-laki", "Perempuan"
        ));
    }

    private void setupValidation() {
        // Add listeners for real-time validation
        textField_namaPengungsi.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 50) {
                textField_namaPengungsi.setText(oldValue);
            }
        });

        textField_umur.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                textField_umur.setText(oldValue);
            }
        });

        textField_keluhan.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 200) {
                textField_keluhan.setText(oldValue);
            }
        });

        textField_lokasi.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                textField_lokasi.setText(oldValue);
            }
        });
    }

    public void setRefugeeData(Refugee refugee) {
        if (refugee != null) {
            textField_namaPengungsi.setText(refugee.getName());
            textField_umur.setText(String.valueOf(refugee.getAge()));
            comboBox_jenisKelamin.setValue(refugee.getGender());
            textField_keluhan.setText(refugee.getComplain());
            textField_lokasi.setText(refugee.getAddress());
        }
    }

    public Refugee getRefugeeData() {
        try {
            String name = textField_namaPengungsi.getText().trim();
            String ageText = textField_umur.getText().trim();
            String gender = comboBox_jenisKelamin.getValue();
            String complain = textField_keluhan.getText().trim();
            String address = textField_lokasi.getText().trim();

            // Validation
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Nama pengungsi tidak boleh kosong");
            }

            if (ageText.isEmpty()) {
                throw new IllegalArgumentException("Umur tidak boleh kosong");
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Umur harus berupa angka");
            }

            if (age < 0 || age > 120) {
                throw new IllegalArgumentException("Umur harus antara 0-120 tahun");
            }

            if (gender == null || gender.isEmpty()) {
                throw new IllegalArgumentException("Jenis kelamin harus dipilih");
            }

            if (complain.isEmpty()) {
                throw new IllegalArgumentException("Keluhan tidak boleh kosong");
            }

            if (address.isEmpty()) {
                throw new IllegalArgumentException("Alamat tidak boleh kosong");
            }

            return new Refugee(0, name, age, gender, complain, address);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validasi gagal: " + e.getMessage());
        }
    }

    public boolean validateForm() {
        try {
            getRefugeeData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to clear all fields
    public void clearForm() {
        textField_namaPengungsi.clear();
        textField_umur.clear();
        textField_keluhan.clear();
        textField_lokasi.clear();
        comboBox_jenisKelamin.getSelectionModel().clearSelection();
    }

    // Getters for individual fields if needed
    public String getNamaPengungsi() {
        return textField_namaPengungsi.getText().trim();
    }

    public String getUmur() {
        return textField_umur.getText().trim();
    }

    public String getJenisKelamin() {
        return comboBox_jenisKelamin.getValue();
    }

    public String getKeluhan() {
        return textField_keluhan.getText().trim();
    }

    public String getLokasi() {
        return textField_lokasi.getText().trim();
    }

    // Setters for individual fields if needed
    public void setNamaPengungsi(String nama) {
        textField_namaPengungsi.setText(nama);
    }

    public void setUmur(String umur) {
        textField_umur.setText(umur);
    }

    public void setJenisKelamin(String jenisKelamin) {
        comboBox_jenisKelamin.setValue(jenisKelamin);
    }

    public void setKeluhan(String keluhan) {
        textField_keluhan.setText(keluhan);
    }

    public void setLokasi(String lokasi) {
        textField_lokasi.setText(lokasi);
    }
}
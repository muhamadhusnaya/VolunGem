package volungemv2.controllers.volunteer;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import volungemv2.models.Volunteer;

public class AddVolunteerController {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private Label label_namaVolunteer;

    @FXML
    private TextField textField_namaVolunteer;

    @FXML
    private Label label_kontak;

    @FXML
    private Label label_kelompok;

    @FXML
    private Label label_umur;

    @FXML
    private TextField textField_kontak;

    @FXML
    private ComboBox<String> comboBox_kelompok;

    @FXML
    private Label label_assignedTask;

    @FXML
    private TextField textField_umur;

    @FXML
    private Label label_jenisKelamin;

    @FXML
    private ComboBox<String> comboBox_jenisKelamin;

    @FXML
    private TextField textField_penugasan;

    public void initialize() {
        setupComboBoxes();
        setupValidation();
    }

    private void setupComboBoxes() {
        // Setup ComboBox untuk Jenis Kelamin
        comboBox_jenisKelamin.setItems(FXCollections.observableArrayList(
            "Laki-laki", "Perempuan"
        ));

        // Setup ComboBox untuk Kelompok
        comboBox_kelompok.setItems(FXCollections.observableArrayList(
            "Medis", "Logistik", "Distribusi"
        ));
    }

    private void setupValidation() {
        // Add listeners for real-time validation if needed
        textField_namaVolunteer.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 50) {
                textField_namaVolunteer.setText(oldValue);
            }
        });

        textField_umur.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                textField_umur.setText(oldValue);
            }
        });

        textField_kontak.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 15) {
                textField_kontak.setText(oldValue);
            }
        });

        textField_penugasan.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                textField_penugasan.setText(oldValue);
            }
        });
    }

    public void setVolunteerData(Volunteer volunteer) {
        if (volunteer != null) {
            textField_namaVolunteer.setText(volunteer.getName());
            textField_umur.setText(String.valueOf(volunteer.getAge()));
            comboBox_jenisKelamin.setValue(volunteer.getGender());
            textField_kontak.setText(volunteer.getContact());
            comboBox_kelompok.setValue(volunteer.getTeam());
            textField_penugasan.setText(volunteer.getTask());
        }
    }

    public Volunteer getVolunteerData() {
    try {
        String name = textField_namaVolunteer.getText().trim();
        String ageText = textField_umur.getText().trim();
        String gender = comboBox_jenisKelamin.getValue();
        String contact = textField_kontak.getText().trim();
        String team = comboBox_kelompok.getValue();
        String task = textField_penugasan.getText().trim();

        // Validation
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Nama volunteer tidak boleh kosong");
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

        if (age < 16 || age > 80) {
            throw new IllegalArgumentException("Umur harus antara 16-80 tahun");
        }

        if (gender == null || gender.isEmpty()) {
            throw new IllegalArgumentException("Jenis kelamin harus dipilih");
        }

        if (contact.isEmpty()) {
            throw new IllegalArgumentException("Kontak tidak boleh kosong");
        }

        if (!contact.matches("^[+]?[0-9\\-\\s()]{10,15}$")) {
            throw new IllegalArgumentException("Format kontak tidak valid");
        }

        if (team == null || team.isEmpty()) {
            throw new IllegalArgumentException("Kelompok harus dipilih");
        }

        if (task.isEmpty()) {
            throw new IllegalArgumentException("Penugasan tidak boleh kosong");
        }

        return new Volunteer(0, name, age, gender, contact, team, task);

    } catch (IllegalArgumentException e) {
        // Bisa ganti ini jadi alert jika mau ditampilkan ke UI
        throw new RuntimeException("Validasi gagal: " + e.getMessage());
    }
}

    public boolean validateForm() {
        try {
            getVolunteerData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to clear all fields
    public void clearForm() {
        textField_namaVolunteer.clear();
        textField_umur.clear();
        textField_kontak.clear();
        textField_penugasan.clear();
        comboBox_jenisKelamin.getSelectionModel().selectFirst();
        comboBox_kelompok.getSelectionModel().selectFirst();
    }

    // Getters for individual fields if needed
    public String getNamaVolunteer() {
        return textField_namaVolunteer.getText().trim();
    }

    public String getUmur() {
        return textField_umur.getText().trim();
    }

    public String getJenisKelamin() {
        return comboBox_jenisKelamin.getValue();
    }

    public String getKontak() {
        return textField_kontak.getText().trim();
    }

    public String getKelompok() {
        return comboBox_kelompok.getValue();
    }

    public String getPenugasan() {
        return textField_penugasan.getText().trim();
    }

    // Setters for individual fields if needed
    public void setNamaVolunteer(String nama) {
        textField_namaVolunteer.setText(nama);
    }

    public void setUmur(String umur) {
        textField_umur.setText(umur);
    }

    public void setJenisKelamin(String jenisKelamin) {
        comboBox_jenisKelamin.setValue(jenisKelamin);
    }

    public void setKontak(String kontak) {
        textField_kontak.setText(kontak);
    }

    public void setKelompok(String kelompok) {
        comboBox_kelompok.setValue(kelompok);
    }

    public void setPenugasan(String penugasan) {
        textField_penugasan.setText(penugasan);
    }
}
package volungemv2.controllers.report;

import java.sql.Date;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import volungemv2.models.Report;

public class AddReportController {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private Label label_jenisLaporan;

    @FXML
    private ComboBox<String> comboBox_jenisLaporan;

    @FXML
    private Label label_deskripsi;

    @FXML
    private TextField textField_deskripsi;

    @FXML
    private Label label_tanggal;

    @FXML
    private DatePicker datePicker_tanggal;

    public void initialize() {
        setupComboBoxes();
        setupValidation();
        setDefaultValues();
    }

    private void setupComboBoxes() {
        // Setup ComboBox untuk Jenis Laporan
        comboBox_jenisLaporan.setItems(FXCollections.observableArrayList(
            "Laporan Stok Bantuan",
            "Laporan Distribusi",
            "Laporan Kondisi Pengungsi",
            "Laporan Kesehatan",
            "Laporan Keamanan",
            "Laporan Logistik",
            "Laporan Keuangan",
            "Laporan Volunteer",
            "Laporan Infrastruktur",
            "Laporan Lainnya"
        ));
    }

    private void setupValidation() {
        // Add listeners for real-time validation
        textField_deskripsi.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 500) {
                textField_deskripsi.setText(oldValue);
            }
        });
    }

    private void setDefaultValues() {
        // Set tanggal default ke hari ini
        datePicker_tanggal.setValue(LocalDate.now());
    }

    public void setReportData(Report report) {
        if (report != null) {
            comboBox_jenisLaporan.setValue(report.getType());
            textField_deskripsi.setText(report.getDescription());
            if (report.getDate() != null) {
                datePicker_tanggal.setValue(report.getDate().toLocalDate());
            }
        }
    }

    public Report getReportData() {
        try {
            String type = comboBox_jenisLaporan.getValue();
            String description = textField_deskripsi.getText().trim();
            LocalDate localDate = datePicker_tanggal.getValue();

            // Validation
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("Jenis laporan harus dipilih");
            }

            if (description.isEmpty()) {
                throw new IllegalArgumentException("Deskripsi tidak boleh kosong");
            }

            if (description.length() < 10) {
                throw new IllegalArgumentException("Deskripsi minimal 10 karakter");
            }

            if (localDate == null) {
                throw new IllegalArgumentException("Tanggal harus dipilih");
            }

            // Validasi tanggal tidak boleh di masa depan
            if (localDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Tanggal tidak boleh di masa depan");
            }

            // Convert LocalDate to SQL Date
            Date sqlDate = Date.valueOf(localDate);

            return new Report(0, type, description, sqlDate);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validasi gagal: " + e.getMessage());
        }
    }

    public boolean validateForm() {
        try {
            getReportData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to clear all fields
    public void clearForm() {
        comboBox_jenisLaporan.getSelectionModel().clearSelection();
        textField_deskripsi.clear();
        datePicker_tanggal.setValue(LocalDate.now());
    }

    // Getters for individual fields if needed
    public String getJenisLaporan() {
        return comboBox_jenisLaporan.getValue();
    }

    public String getDeskripsi() {
        return textField_deskripsi.getText().trim();
    }

    public LocalDate getTanggal() {
        return datePicker_tanggal.getValue();
    }

    // Setters for individual fields if needed
    public void setJenisLaporan(String jenisLaporan) {
        comboBox_jenisLaporan.setValue(jenisLaporan);
    }

    public void setDeskripsi(String deskripsi) {
        textField_deskripsi.setText(deskripsi);
    }

    public void setTanggal(LocalDate tanggal) {
        datePicker_tanggal.setValue(tanggal);
    }

    // Method untuk mendapatkan data report tanpa validasi (untuk keperluan internal)
    public Report getReportDataWithoutValidation() {
        String type = comboBox_jenisLaporan.getValue();
        String description = textField_deskripsi.getText().trim();
        LocalDate localDate = datePicker_tanggal.getValue();
        
        Date sqlDate = null;
        if (localDate != null) {
            sqlDate = Date.valueOf(localDate);
        }
        
        return new Report(0, type, description, sqlDate);
    }
}
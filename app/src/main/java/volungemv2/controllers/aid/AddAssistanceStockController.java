package volungemv2.controllers.aid;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import volungemv2.models.Aids;

public class AddAssistanceStockController {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private Label label_kategori;

    @FXML
    private ComboBox<String> comboBox_kategori;

    @FXML
    private Label label_namaBarang; // This is actually for "Nama Barang" based on FXML

    @FXML
    private TextField textField_namaBarang;

    @FXML
    private Label label_stok; // This is actually for "Stok" based on FXML

    @FXML
    private TextField textField_stok;

    @FXML
    private Label label_lokasi; // This is actually for "Lokasi" based on FXML

    @FXML
    private TextField textField_lokasi;

    public void initialize() {
        setupComboBoxes();
        setupValidation();
    }

    private void setupComboBoxes() {
        // Setup ComboBox untuk Kategori
        comboBox_kategori.setItems(FXCollections.observableArrayList(
            "Makanan", "Minuman", "Obat-obatan", "Pakaian", "Peralatan Medis", 
            "Perlengkapan Bayi", "Furnitur", "Alat Kebersihan", "Lainnya"
        ));
    }

    private void setupValidation() {
        // Add listeners for real-time validation
        textField_namaBarang.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                textField_namaBarang.setText(oldValue);
            }
        });

        textField_stok.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                textField_stok.setText(oldValue);
            }
        });

        textField_lokasi.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 200) {
                textField_lokasi.setText(oldValue);
            }
        });
    }

    public void setAidData(Aids aid) {
        if (aid != null) {
            comboBox_kategori.setValue(aid.getCategory());
            textField_namaBarang.setText(aid.getItemName());
            textField_stok.setText(String.valueOf(aid.getQuantity()));
            textField_lokasi.setText(aid.getLocation());
        }
    }

    public Aids getAidData() {
        try {
            String category = comboBox_kategori.getValue();
            String itemName = textField_namaBarang.getText().trim();
            String quantityText = textField_stok.getText().trim();
            String location = textField_lokasi.getText().trim();

            // Validation
            if (category == null || category.isEmpty()) {
                throw new IllegalArgumentException("Kategori harus dipilih");
            }

            if (itemName.isEmpty()) {
                throw new IllegalArgumentException("Nama barang tidak boleh kosong");
            }

            if (quantityText.isEmpty()) {
                throw new IllegalArgumentException("Stok tidak boleh kosong");
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Stok harus berupa angka");
            }

            if (quantity < 0) {
                throw new IllegalArgumentException("Stok tidak boleh negatif");
            }

            if (quantity > 999999) {
                throw new IllegalArgumentException("Stok tidak boleh lebih dari 999999");
            }

            if (location.isEmpty()) {
                throw new IllegalArgumentException("Lokasi tidak boleh kosong");
            }

            return new Aids(0, category, itemName, quantity, location);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validasi gagal: " + e.getMessage());
        }
    }

    public boolean validateForm() {
        try {
            getAidData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to clear all fields
    public void clearForm() {
        comboBox_kategori.getSelectionModel().clearSelection();
        textField_namaBarang.clear();
        textField_stok.clear();
        textField_lokasi.clear();
    }

    // Getters for individual fields if needed
    public String getKategori() {
        return comboBox_kategori.getValue();
    }

    public String getNamaBarang() {
        return textField_namaBarang.getText().trim();
    }

    public String getStok() {
        return textField_stok.getText().trim();
    }

    public String getLokasi() {
        return textField_lokasi.getText().trim();
    }

    // Setters for individual fields if needed
    public void setKategori(String kategori) {
        comboBox_kategori.setValue(kategori);
    }

    public void setNamaBarang(String namaBarang) {
        textField_namaBarang.setText(namaBarang);
    }

    public void setStok(String stok) {
        textField_stok.setText(stok);
    }

    public void setLokasi(String lokasi) {
        textField_lokasi.setText(lokasi);
    }
}
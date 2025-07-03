package volungemv2.controllers.aid;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import volungemv2.models.Aids;
import volungemv2.repository.AidRepository;

public class DataStokBantuanController {

    @FXML
    private VBox vBox_content;

    @FXML
    private AnchorPane anchorPane_navbar;

    @FXML
    private Label label_breadcrumb;

    @FXML
    private ImageView image_profileIcon;

    @FXML
    private Label label_user;

    @FXML
    private AnchorPane anchorPane_content;

    @FXML
    private Label label_contentTitle;

    @FXML
    private TableView<Aids> tableView_dataStokBantuan;

    @FXML
    private TableColumn<Aids, String> tableColumn_kategori;

    @FXML
    private TableColumn<Aids, String> tableColumn_namaBarang;

    @FXML
    private TableColumn<Aids, Integer> tableColumn_stok;

    @FXML
    private TableColumn<Aids, String> tableColumn_lokasi;

    @FXML
    private Button button_add;

    @FXML
    private Button button_delete;

    @FXML
    private Button button_update;

    private AidRepository aidRepository;
    private ObservableList<Aids> aidList;

    public void initialize() {
        try {
            aidRepository = new AidRepository();
            setupTableColumns();
            loadAids();
            setupButtonHandlers();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        tableColumn_kategori.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableColumn_namaBarang.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        tableColumn_stok.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tableColumn_lokasi.setCellValueFactory(new PropertyValueFactory<>("location"));
    }

    private void loadAids() {
        try {
            List<Aids> aids = aidRepository.getAllAids();
            aidList = FXCollections.observableArrayList(aids);
            tableView_dataStokBantuan.setItems(aidList);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load assistance stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonHandlers() {
        button_add.setOnAction(event -> handleAddAid());
        button_delete.setOnAction(event -> handleDeleteAid());
        button_update.setOnAction(event -> handleUpdateAid());
    }

    @FXML
    public void handleAddAid() {
        try {
            Dialog<Aids> dialog = createAidDialog("Tambah Stok Bantuan", null);
            Optional<Aids> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Aids newAid = result.get();
                aidRepository.addAid(newAid);
                loadAids(); // Refresh table
                showSuccessAlert("Berhasil", "Stok bantuan berhasil ditambahkan!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to add assistance stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateAid() {
        Aids selectedAid = tableView_dataStokBantuan.getSelectionModel().getSelectedItem();
        
        if (selectedAid == null) {
            showWarningAlert("Tidak Ada Pilihan", "Silakan pilih stok bantuan yang akan diupdate.");
            return;
        }

        try {
            Dialog<Aids> dialog = createAidDialog("Update Stok Bantuan", selectedAid);
            Optional<Aids> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Aids updatedAid = result.get();
                updatedAid.setIdAid(selectedAid.getIdAid()); // Preserve the ID
                aidRepository.updateAid(updatedAid);
                loadAids(); // Refresh table
                showSuccessAlert("Berhasil", "Stok bantuan berhasil diupdate!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to update assistance stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteAid() {
        Aids selectedAid = tableView_dataStokBantuan.getSelectionModel().getSelectedItem();
        
        if (selectedAid == null) {
            showWarningAlert("Tidak Ada Pilihan", "Silakan pilih stok bantuan yang akan dihapus.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Stok Bantuan");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus " + selectedAid.getItemName() + "?");

        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                aidRepository.deleteAid(selectedAid.getIdAid());
                loadAids(); // Refresh table
                showSuccessAlert("Berhasil", "Stok bantuan berhasil dihapus!");
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Failed to delete assistance stock: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Dialog<Aids> createAidDialog(String title, Aids aid) throws IOException {
        // Load the FXML for the dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/aid/AddAssistanceStock.fxml"));
        DialogPane dialogPane = loader.load();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // Get the controller and set the aid data if updating
        AddAssistanceStockController controller = loader.getController();
        ButtonType okButtonType = ButtonType.OK;
        dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Dialog<Aids> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setDialogPane(dialogPane);

        if (aid != null) {
            controller.setAidData(aid);
        }

        // Convert the result to an Aids object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    return controller.getAidData();
                } catch (RuntimeException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Validasi Gagal");
                    alert.setHeaderText(null);
                    alert.setContentText(e.getMessage().replace("Validation Error: ", "")); // Hapus prefix jika perlu
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
                    alert.showAndWait();
                }
            }
            return null;
        });

        return dialog;
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        alert.showAndWait();
    }

    // Getter untuk akses dari luar jika diperlukan
    public TableView<Aids> getTableView() {
        return tableView_dataStokBantuan;
    }

    public void refreshTable() {
        loadAids();
    }
}
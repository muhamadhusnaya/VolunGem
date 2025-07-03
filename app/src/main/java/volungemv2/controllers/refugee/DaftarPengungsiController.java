package volungemv2.controllers.refugee;

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
import volungemv2.models.Refugee;
import volungemv2.repository.RefugeeRepository;

public class DaftarPengungsiController {

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
    private TableView<Refugee> tableView_daftarPengungsi;

    @FXML
    private TableColumn<Refugee, String> tableColumn_namaPengungsi;

    @FXML
    private TableColumn<Refugee, Integer> tableColumn_umur;

    @FXML
    private TableColumn<Refugee, String> tableColumn_jenisKelamin;

    @FXML
    private TableColumn<Refugee, String> tableColumn_keluhan;

    @FXML
    private TableColumn<Refugee, String> tableColumn_alamat;

    @FXML
    private Button button_add;

    @FXML
    private Button button_delete;

    @FXML
    private Button button_update;

    private RefugeeRepository refugeeRepository;
    private ObservableList<Refugee> refugeeList;

    public void initialize() {
        try {
            refugeeRepository = new RefugeeRepository();
            setupTableColumns();
            loadRefugees();
            setupButtonHandlers();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        tableColumn_namaPengungsi.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumn_umur.setCellValueFactory(new PropertyValueFactory<>("age"));
        tableColumn_jenisKelamin.setCellValueFactory(new PropertyValueFactory<>("gender"));
        tableColumn_keluhan.setCellValueFactory(new PropertyValueFactory<>("complain"));
        tableColumn_alamat.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void loadRefugees() {
        try {
            List<Refugee> refugees = refugeeRepository.getAllRefugees();
            refugeeList = FXCollections.observableArrayList(refugees);
            tableView_daftarPengungsi.setItems(refugeeList);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load refugees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonHandlers() {
        button_add.setOnAction(event -> handleAddRefugee());
        button_delete.setOnAction(event -> handleDeleteRefugee());
        button_update.setOnAction(event -> handleUpdateRefugee());
    }

    @FXML
    public void handleAddRefugee() {
        try {
            Dialog<Refugee> dialog = createRefugeeDialog("Tambah Pengungsi", null);
            Optional<Refugee> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Refugee newRefugee = result.get();
                refugeeRepository.addRefugee(newRefugee);
                loadRefugees(); // Refresh table
                showSuccessAlert("Berhasil", "Data pengungsi berhasil ditambahkan!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Gagal menambahkan pengungsi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateRefugee() {
        Refugee selectedRefugee = tableView_daftarPengungsi.getSelectionModel().getSelectedItem();
        
        if (selectedRefugee == null) {
            showWarningAlert("Tidak Ada Pilihan", "Silakan pilih pengungsi untuk diupdate.");
            return;
        }

        try {
            Dialog<Refugee> dialog = createRefugeeDialog("Update Pengungsi", selectedRefugee);
            Optional<Refugee> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Refugee updatedRefugee = result.get();
                updatedRefugee.setIdRefugee(selectedRefugee.getIdRefugee()); // Preserve the ID
                refugeeRepository.updateRefugee(updatedRefugee);
                loadRefugees(); // Refresh table
                showSuccessAlert("Berhasil", "Data pengungsi berhasil diupdate!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Gagal mengupdate pengungsi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteRefugee() {
        Refugee selectedRefugee = tableView_daftarPengungsi.getSelectionModel().getSelectedItem();
        
        if (selectedRefugee == null) {
            showWarningAlert("Tidak Ada Pilihan", "Silakan pilih pengungsi untuk dihapus.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Pengungsi");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus " + selectedRefugee.getName() + "?");

        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                refugeeRepository.deleteRefugee(selectedRefugee.getIdRefugee());
                loadRefugees(); // Refresh table
                showSuccessAlert("Berhasil", "Data pengungsi berhasil dihapus!");
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Gagal menghapus pengungsi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Dialog<Refugee> createRefugeeDialog(String title, Refugee refugee) throws IOException {
        // Load the FXML for the dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/refugee/AddRefugee.fxml"));
        DialogPane dialogPane = loader.load();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // Get the controller and set the refugee data if updating
        AddRefugeeController controller = loader.getController();
        ButtonType okButtonType = ButtonType.OK;
        dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Dialog<Refugee> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setDialogPane(dialogPane);

        if (refugee != null) {
            controller.setRefugeeData(refugee);
        }

        // Convert the result to a Refugee object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    return controller.getRefugeeData();
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
    public TableView<Refugee> getTableView() {
        return tableView_daftarPengungsi;
    }

    public void refreshTable() {
        loadRefugees();
    }
}
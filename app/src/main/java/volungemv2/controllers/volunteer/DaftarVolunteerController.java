package volungemv2.controllers.volunteer;

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
import volungemv2.models.Volunteer;
import volungemv2.repository.VolunteerRepository;

public class DaftarVolunteerController {

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
    private TableView<Volunteer> tableView_daftarVolunteer;

    @FXML
    private TableColumn<Volunteer, String> tableColumn_namaVolunteer;

    @FXML
    private TableColumn<Volunteer, Integer> tableColumn_umur;

    @FXML
    private TableColumn<Volunteer, String> tableColumn_jenisKelamin;

    @FXML
    private TableColumn<Volunteer, String> tableColumn_kontak;

    @FXML
    private TableColumn<Volunteer, String> tableColumn_kelompok;

    @FXML
    private TableColumn<Volunteer, String> tableColumn_penugasan;

    @FXML
    private Button button_add;

    @FXML
    private Button button_delete;

    @FXML
    private Button button_update;

    private VolunteerRepository volunteerRepository;
    private ObservableList<Volunteer> volunteerList;

    public void initialize() {
        try {
            volunteerRepository = new VolunteerRepository();
            setupTableColumns();
            loadVolunteers();
            setupButtonHandlers();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        tableColumn_namaVolunteer.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumn_umur.setCellValueFactory(new PropertyValueFactory<>("age"));
        tableColumn_jenisKelamin.setCellValueFactory(new PropertyValueFactory<>("gender"));
        tableColumn_kontak.setCellValueFactory(new PropertyValueFactory<>("contact"));
        tableColumn_kelompok.setCellValueFactory(new PropertyValueFactory<>("team"));
        tableColumn_penugasan.setCellValueFactory(new PropertyValueFactory<>("task"));
    }

    private void loadVolunteers() {
        try {
            List<Volunteer> volunteers = volunteerRepository.getAllVolunteers();
            volunteerList = FXCollections.observableArrayList(volunteers);
            tableView_daftarVolunteer.setItems(volunteerList);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load volunteers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonHandlers() {
        button_add.setOnAction(event -> handleAddVolunteer());
        button_delete.setOnAction(event -> handleDeleteVolunteer());
        button_update.setOnAction(event -> handleUpdateVolunteer());
    }

    @FXML
    public void handleAddVolunteer() {
        try {
            Dialog<Volunteer> dialog = createVolunteerDialog("Add Volunteer", null);
            Optional<Volunteer> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Volunteer newVolunteer = result.get();
                volunteerRepository.addVolunteer(newVolunteer);
                loadVolunteers(); // Refresh table
                showSuccessAlert("Success", "Volunteer added successfully!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to add volunteer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateVolunteer() {
        Volunteer selectedVolunteer = tableView_daftarVolunteer.getSelectionModel().getSelectedItem();
        
        if (selectedVolunteer == null) {
            showWarningAlert("No Selection", "Please select a volunteer to update.");
            return;
        }

        try {
            Dialog<Volunteer> dialog = createVolunteerDialog("Update Volunteer", selectedVolunteer);
            Optional<Volunteer> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Volunteer updatedVolunteer = result.get();
                updatedVolunteer.setId(selectedVolunteer.getId()); // Preserve the ID
                volunteerRepository.updateVolunteer(updatedVolunteer);
                loadVolunteers(); // Refresh table
                showSuccessAlert("Success", "Volunteer updated successfully!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to update volunteer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteVolunteer() {
        Volunteer selectedVolunteer = tableView_daftarVolunteer.getSelectionModel().getSelectedItem();
        
        if (selectedVolunteer == null) {
            showWarningAlert("No Selection", "Please select a volunteer to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Volunteer");
        confirmAlert.setContentText("Are you sure you want to delete " + selectedVolunteer.getName() + "?");

        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                volunteerRepository.deleteVolunteer(selectedVolunteer.getId());
                loadVolunteers(); // Refresh table
                showSuccessAlert("Success", "Volunteer deleted successfully!");
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Failed to delete volunteer: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Dialog<Volunteer> createVolunteerDialog(String title, Volunteer volunteer) throws IOException {
        // Load the FXML for the dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/volunteer/AddVolunteer.fxml"));
        DialogPane dialogPane = loader.load();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());


        // Get the controller and set the volunteer data if updating
        AddVolunteerController controller = loader.getController();
        ButtonType okButtonType = ButtonType.OK;
        dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Dialog<Volunteer> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setDialogPane(dialogPane);

        if (volunteer != null) {
            controller.setVolunteerData(volunteer);
    }

        // Convert the result to a Volunteer object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    return controller.getVolunteerData();
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
    public TableView<Volunteer> getTableView() {
        return tableView_daftarVolunteer;
    }

    public void refreshTable() {
        loadVolunteers();
    }
}
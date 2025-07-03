package volungemv2.controllers.report;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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
import volungemv2.models.Report;
import volungemv2.repository.ReportRepository;

public class LaporanMonitoringController {

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
    private TableView<Report> tableView_laporanMonitoring;

    @FXML
    private TableColumn<Report, String> tableColumn_jenisLaporan;

    @FXML
    private TableColumn<Report, String> tableColumn_deskripsi;

    @FXML
    private TableColumn<Report, String> tableColumn_tanggal;

    @FXML
    private Button button_add;

    @FXML
    private Button button_delete;

    @FXML
    private Button button_update;

    private ReportRepository reportRepository;
    private ObservableList<Report> reportList;

    public void initialize() {
        try {
            reportRepository = new ReportRepository();
            setupTableColumns();
            loadReports();
            setupButtonHandlers();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        tableColumn_jenisLaporan.setCellValueFactory(new PropertyValueFactory<>("type"));
        tableColumn_deskripsi.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Format tanggal untuk ditampilkan
        tableColumn_tanggal.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
    }

    private void loadReports() {
        try {
            List<Report> reports = reportRepository.getAllReports();
            reportList = FXCollections.observableArrayList(reports);
            tableView_laporanMonitoring.setItems(reportList);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load reports: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonHandlers() {
        button_add.setOnAction(event -> handleAddReport());
        button_delete.setOnAction(event -> handleDeleteReport());
        button_update.setOnAction(event -> handleUpdateReport());
    }

    @FXML
    public void handleAddReport() {
        try {
            Dialog<Report> dialog = createReportDialog("Tambah Laporan", null);
            Optional<Report> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Report newReport = result.get();
                reportRepository.addReport(newReport);
                loadReports(); // Refresh table
                showSuccessAlert("Berhasil", "Data laporan berhasil ditambahkan!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Gagal menambahkan laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateReport() {
        Report selectedReport = tableView_laporanMonitoring.getSelectionModel().getSelectedItem();
        
        if (selectedReport == null) {
            showWarningAlert("Tidak Ada Pilihan", "Silakan pilih laporan untuk diupdate.");
            return;
        }

        try {
            Dialog<Report> dialog = createReportDialog("Update Laporan", selectedReport);
            Optional<Report> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Report updatedReport = result.get();
                updatedReport.setId(selectedReport.getId()); // Preserve the ID
                reportRepository.updateReport(updatedReport);
                loadReports(); // Refresh table
                showSuccessAlert("Berhasil", "Data laporan berhasil diupdate!");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "Gagal mengupdate laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteReport() {
        Report selectedReport = tableView_laporanMonitoring.getSelectionModel().getSelectedItem();
        
        if (selectedReport == null) {
            showWarningAlert("Tidak Ada Pilihan", "Silakan pilih laporan untuk dihapus.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Laporan");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus laporan " + selectedReport.getType() + "?");

        DialogPane dialogPane = confirmAlert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            // Fallback jika CSS tidak ditemukan
            System.out.println("CSS file not found, using default styling");
        }

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reportRepository.deleteReport(selectedReport.getId());
                loadReports(); // Refresh table
                showSuccessAlert("Berhasil", "Data laporan berhasil dihapus!");
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Gagal menghapus laporan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Dialog<Report> createReportDialog(String title, Report report) throws IOException {
        // Load the FXML for the dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/report/AddReport.fxml"));
        DialogPane dialogPane = loader.load();
        
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            // Fallback jika CSS tidak ditemukan
            System.out.println("CSS file not found, using default styling");
        }

        // Get the controller and set the report data if updating
        AddReportController controller = loader.getController();
        ButtonType okButtonType = ButtonType.OK;
        dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Dialog<Report> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setDialogPane(dialogPane);

        if (report != null) {
            controller.setReportData(report);
        }

        // Convert the result to a Report object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    return controller.getReportData();
                } catch (RuntimeException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Validasi Gagal");
                    alert.setHeaderText(null);
                    alert.setContentText(e.getMessage().replace("Validation Error: ", "")); // Hapus prefix jika perlu
                    try {
                        alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
                    } catch (Exception ex) {
                        // Fallback jika CSS tidak ditemukan
                        System.out.println("CSS file not found, using default styling");
                    }
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
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            // Fallback jika CSS tidak ditemukan
            System.out.println("CSS file not found, using default styling");
        }

        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            // Fallback jika CSS tidak ditemukan
            System.out.println("CSS file not found, using default styling");
        }
        
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            // Fallback jika CSS tidak ditemukan
            System.out.println("CSS file not found, using default styling");
        }

        alert.showAndWait();
    }

    // Getter untuk akses dari luar jika diperlukan
    public TableView<Report> getTableView() {
        return tableView_laporanMonitoring;
    }

    public void refreshTable() {
        loadReports();
    }
}
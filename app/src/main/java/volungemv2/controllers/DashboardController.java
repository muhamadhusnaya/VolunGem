package volungemv2.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import volungemv2.repository.DashboardRepository;

public class DashboardController implements Initializable {

    // FXML Components - Main Layout
    @FXML
    private VBox vBox_content;
    
    @FXML
    private AnchorPane anchorPane_navbar;
    
    @FXML
    private AnchorPane anchorPane_content;

    // FXML Components - Navigation Bar
    @FXML
    private Label label_breadcrumb;
    
    @FXML
    private Label label_user;
    
    @FXML
    private ImageView image_profileIcon;

    // FXML Components - Content Title
    @FXML
    private Label label_contentTitle;
    
    @FXML
    private Label label_titleDetail;

    // FXML Components - Widget 1 (Volunteers)
    @FXML
    private AnchorPane anchorPane_widget1;
    
    @FXML
    private ImageView imageView_volunteer;
    
    @FXML
    private Label label_totalVolunteer;
    
    @FXML
    private Label label_totalVolunteerDetail;

    // FXML Components - Widget 2 (Stock)
    @FXML
    private AnchorPane anchorPane_widget2;
    
    @FXML
    private ImageView imageView_stok;
    
    @FXML
    private Label label_totalStok;
    
    @FXML
    private Label label_totalStokDetail;

    // FXML Components - Widget 3 (Reports)
    @FXML
    private AnchorPane anchorPane_widget3;
    
    @FXML
    private ImageView imageView_report;
    
    @FXML
    private Label label_totalReport;
    
    @FXML
    private Label label_totalReportDetail;

    // FXML Components - Volunteer Table
    @FXML
    private TableView<VolunteerData> tableView_daftarVolunteer;
    
    @FXML
    private TableColumn<VolunteerData, String> tableColumn_namaVolunteer;
    
    @FXML
    private TableColumn<VolunteerData, String> tableColumn_kontak;
    
    @FXML
    private TableColumn<VolunteerData, String> tableColumn_kelompok;

    // FXML Components - Report Table
    @FXML
    private TableView<ReportData> tableView_laporanMonitoring;
    
    @FXML
    private TableColumn<ReportData, String> tableColumn_jenisLaporan;
    
    @FXML
    private TableColumn<ReportData, String> tableColumn_tanggal;
    
    @FXML
    private TableColumn<ReportData, String> tableColumn_deskripsi;

    // Repository
    private DashboardRepository dashboardRepository;

    // Observable Lists for Tables
    private ObservableList<VolunteerData> volunteerList;
    private ObservableList<ReportData> reportList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize repository
            dashboardRepository = new DashboardRepository();
            
            // Initialize table data
            volunteerList = FXCollections.observableArrayList();
            reportList = FXCollections.observableArrayList();
            
            // Setup tables
            setupVolunteerTable();
            setupReportTable();
            
            // Load data
            loadDashboardData();
            
        } catch (SQLException e) {
            System.err.println("Error initializing dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Set default values in case of database error
            setDefaultValues();
        }
    }

    private void setupVolunteerTable() {
        // Setup volunteer table columns
        tableColumn_namaVolunteer.setCellValueFactory(new PropertyValueFactory<>("nama"));
        tableColumn_kontak.setCellValueFactory(new PropertyValueFactory<>("kontak"));
        tableColumn_kelompok.setCellValueFactory(new PropertyValueFactory<>("kelompok"));
        
        // Set the data to table
        tableView_daftarVolunteer.setItems(volunteerList);
    }

    private void setupReportTable() {
        // Setup report table columns
        tableColumn_jenisLaporan.setCellValueFactory(new PropertyValueFactory<>("jenisLaporan"));
        tableColumn_tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        tableColumn_deskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        
        // Set the data to table
        tableView_laporanMonitoring.setItems(reportList);
    }

    private void loadDashboardData() {
        try {
            // Load widget data
            loadWidgetData();
            
            // Load table data
            loadVolunteerData();
            loadReportData();
            
        } catch (SQLException e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadWidgetData() throws SQLException {
        // Load total volunteers
        int totalVolunteers = dashboardRepository.getTotalVolunteers();
        label_totalVolunteer.setText(String.valueOf(totalVolunteers));
        
        // Load total stock
        int totalStock = dashboardRepository.getTotalStock();
        label_totalStok.setText(String.valueOf(totalStock));
        
        // Load total reports
        int totalReports = dashboardRepository.getTotalReports();
        label_totalReport.setText(String.valueOf(totalReports));
    }

    private void loadVolunteerData() throws SQLException {
        List<String[]> volunteers = dashboardRepository.getVolunteers();
        
        // Clear existing data
        volunteerList.clear();
        
        // Add new data
        for (String[] volunteer : volunteers) {
            VolunteerData volunteerData = new VolunteerData(
                volunteer[0], // name
                volunteer[1], // contact
                volunteer[2]  // team
            );
            volunteerList.add(volunteerData);
        }
    }

    private void loadReportData() throws SQLException {
        List<String[]> reports = dashboardRepository.getReports();
        
        // Clear existing data
        reportList.clear();
        
        // Add new data
        for (String[] report : reports) {
            ReportData reportData = new ReportData(
                report[0], // type
                report[1], // date
                report[2]  // description
            );
            reportList.add(reportData);
        }
    }

    private void setDefaultValues() {
        // Set default values if database connection fails
        label_totalVolunteer.setText("0");
        label_totalStok.setText("0");
        label_totalReport.setText("0");
    }

    // Method to refresh dashboard data
    public void refreshDashboard() {
        loadDashboardData();
    }

    // Inner class for Volunteer data model
    public static class VolunteerData {
        private String nama;
        private String kontak;
        private String kelompok;

        public VolunteerData(String nama, String kontak, String kelompok) {
            this.nama = nama;
            this.kontak = kontak;
            this.kelompok = kelompok;
        }

        // Getters
        public String getNama() { return nama; }
        public String getKontak() { return kontak; }
        public String getKelompok() { return kelompok; }

        // Setters
        public void setNama(String nama) { this.nama = nama; }
        public void setKontak(String kontak) { this.kontak = kontak; }
        public void setKelompok(String kelompok) { this.kelompok = kelompok; }
    }

    // Inner class for Report data model
    public static class ReportData {
        private String jenisLaporan;
        private String tanggal;
        private String deskripsi;

        public ReportData(String jenisLaporan, String tanggal, String deskripsi) {
            this.jenisLaporan = jenisLaporan;
            this.tanggal = tanggal;
            this.deskripsi = deskripsi;
        }

        // Getters
        public String getJenisLaporan() { return jenisLaporan; }
        public String getTanggal() { return tanggal; }
        public String getDeskripsi() { return deskripsi; }

        // Setters
        public void setJenisLaporan(String jenisLaporan) { this.jenisLaporan = jenisLaporan; }
        public void setTanggal(String tanggal) { this.tanggal = tanggal; }
        public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    }
}
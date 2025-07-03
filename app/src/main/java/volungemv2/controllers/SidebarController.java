package volungemv2.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarController implements Initializable {

    @FXML
    private HBox daftarVolunteer;
    @FXML
    private VBox vBox_sidebar;
    @FXML
    private AnchorPane anchorPane_sidebar;
    @FXML
    private ImageView image_volungem;
    @FXML
    private Label label_volungem;
    @FXML
    private Label label_manajemenAplikasi;
    @FXML
    private Button button_dashboard;
    @FXML
    private MenuButton menuButton_manajemenVolunteer;
    @FXML
    private Button button_daftarVolunteer;
    @FXML
    private Button button_daftarPenugasan;
    @FXML
    private MenuButton menuButton_distribusiBantuan;
    @FXML
    private Button button_dataStokBantuan;
    @FXML
    private Button button_riwayatDistribusi;
    @FXML
    private MenuButton menuButton_manajemenPengungsi;
    @FXML
    private Button button_daftarPengungsi;
    @FXML
    private Button button_lokasiAtauKesehatan;
    @FXML
    private Button button_alokasiPengungsi;
    @FXML
    private Button button_dashboard1;
    @FXML
    private Label label_laporan;
    @FXML
    private Label label_lainnya;
    @FXML
    private Button button_manajemenUser;
    @FXML
    private Button button_logOut;
    @FXML
    private VBox vBox_content;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Muat konten awal saat start
        loadContent("/fxml/Dashboard.fxml");
    }
    
    private void loadContent(String fxmlFile) {
        try {
            // First check if the resource exists
            URL resourceUrl = getClass().getResource(fxmlFile);
            if (resourceUrl == null) {
                System.err.println("FXML file not found: " + fxmlFile);
                System.err.println("Make sure the file exists in " + fxmlFile);
                return;
            }
            
            System.out.println("Loading FXML from: " + resourceUrl);
            
            // Load the FXML file
            Node content = FXMLLoader.load(resourceUrl);
            vBox_content.getChildren().setAll(content);
            
            System.out.println("Successfully loaded: " + fxmlFile);
            
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void onClickedLoadSceneDashboard() {
        loadContent("/fxml/Dashboard.fxml");
    }

    public void onClickedLoadSceneDaftarVolunteer() {
        loadContent("/fxml/volunteer/DaftarVolunteer.fxml");
    }
    
    public void onClickedLoadSceneDataStokBantuan() {
        loadContent("/fxml/aid/DataStokBantuan.fxml");
    }

    public void onClickedLoadSceneDaftarPengungsi() {
        loadContent("/fxml/refugee/DaftarPengungsi.fxml");
    }

    public void onClickedLoadSceneLaporanMonitoring() {
        loadContent("/fxml/report/LaporanMonitoring.fxml");
    }

    public void onClickedLoadSceneLogin(javafx.event.ActionEvent event) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            // Get stage dari event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Set scene baru
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
            // Center stage
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
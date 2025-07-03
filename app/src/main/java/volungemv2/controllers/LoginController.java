package volungemv2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import volungemv2.models.Users;
import volungemv2.repository.UsersRepository;
import volungemv2.utils.SessionManager;
import volungemv2.utils.PasswordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // FXML Components
    @FXML
    private HBox loginMain;
    
    @FXML
    private ImageView image_volungem;
    
    @FXML
    private Label label_volungem;
    
    @FXML
    private Label label_loginHeader;
    
    @FXML
    private Label label_loginContent;
    
    @FXML
    private Label label_username;
    
    @FXML
    private TextField textField_username;
    
    @FXML
    private Label label_password;
    
    @FXML
    private PasswordField passwordField_password;
    
    @FXML
    private Button button_login;

    // Repository
    private UsersRepository usersRepository;

    public LoginController() {
        this.usersRepository = new UsersRepository();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup event handler untuk tombol login
        setupEventHandlers();
        
        // Setup validation
        setupValidation();
        
        // Focus pada username field
        if (textField_username != null) {
            textField_username.requestFocus();
        }
    }

    private void setupEventHandlers() {
        // Event handler untuk tombol login (jika belum di-set di FXML)
        if (button_login != null) {
            button_login.setOnAction(this::handleLogin);
        }
        
        // Event handler untuk Enter key pada password field
        if (passwordField_password != null) {
            passwordField_password.setOnAction(this::handleLogin);
        }
        
        // Event handler untuk Enter key pada username field
        if (textField_username != null) {
            textField_username.setOnAction(event -> passwordField_password.requestFocus());
        }
    }

    private void setupValidation() {
        // Validasi real-time untuk username field
        if (textField_username != null) {
            textField_username.textProperty().addListener((observable, oldValue, newValue) -> {
                validateInputs();
            });
        }
        
        // Validasi real-time untuk password field
        if (passwordField_password != null) {
            passwordField_password.textProperty().addListener((observable, oldValue, newValue) -> {
                validateInputs();
            });
        }
    }

    private void validateInputs() {
        boolean isValid = true;
        
        // Reset style classes
        if (textField_username != null) {
            textField_username.getStyleClass().remove("error");
        }
        if (passwordField_password != null) {
            passwordField_password.getStyleClass().remove("error");
        }
        
        // Validate username
        if (textField_username != null && textField_username.getText().trim().isEmpty()) {
            isValid = false;
        }
        
        // Validate password
        if (passwordField_password != null && passwordField_password.getText().isEmpty()) {
            isValid = false;
        }
        
        // Enable/disable login button
        if (button_login != null) {
            button_login.setDisable(!isValid);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = textField_username.getText().trim();
        String password = passwordField_password.getText();

        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Username dan password harus diisi!");
            return;
        }

        try {
            // Tampilkan loading
            button_login.setDisable(true);
            button_login.setText("Logging in...");

            // Cari user berdasarkan username atau email
            Users user = usersRepository.findByUsernameOrEmail(username);

            if (user != null) {
                // DEBUG: Print data untuk debugging
                System.out.println("=== LOGIN DEBUG ===");
                System.out.println("Input username: '" + username + "'");
                System.out.println("Input password: '" + password + "'");
                System.out.println("Found user: '" + user.getUsername() + "'");
                System.out.println("User email: '" + user.getEmail() + "'");
                System.out.println("User role: '" + user.getRole() + "'");
                System.out.println("Stored password hash: '" + user.getPassword() + "'");
                
                // Verifikasi password menggunakan PasswordUtils dengan auto-detect format
                if (PasswordUtils.verifyPassword(password, user.getPassword())) {
                    System.out.println("=== LOGIN SUCCESS ===");
                    // Login berhasil
                    handleSuccessfulLogin(user, event);
                } else {
                    System.out.println("=== LOGIN FAILED - WRONG PASSWORD ===");
                    // Password salah
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Username atau password salah!");
                    clearPasswordField();
                }
            } else {
                // User tidak ditemukan
                System.out.println("=== LOGIN FAILED - USER NOT FOUND ===");
                System.out.println("Searched identifier: '" + username + "'");
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Username atau password salah!");
                clearPasswordField();
            }
        } catch (Exception e) {
            // Error saat login
            System.err.println("=== LOGIN ERROR ===");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Terjadi kesalahan sistem. Silakan coba lagi.");
        } finally {
            // Reset button
            button_login.setDisable(false);
            button_login.setText("Login");
        }
    }

    private void handleSuccessfulLogin(Users user, ActionEvent event) {
        try {
            // Simpan session user
            SessionManager.setCurrentUser(user);
            
            // Log login activity
            System.out.println("=== LOGIN ACTIVITY ===");
            System.out.println("User: " + user.getUsername() + " (" + user.getEmail() + ")");
            System.out.println("Role: " + user.getRole());
            System.out.println("Login time: " + java.time.LocalDateTime.now());
            System.out.println("=====================");
            
            // Redirect berdasarkan role
            redirectBasedOnRole(user, event);
            
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Gagal membuka halaman utama.");
            e.printStackTrace();
        }
    }

    private void redirectBasedOnRole(Users user, ActionEvent event) throws IOException {
        String fxmlPath;
        String title;
        
        // Tentukan halaman tujuan berdasarkan role
        String role = user.getRole().toLowerCase().trim();
        
        switch (role) {
            case "super admin":
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
            case "admin gudang":
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
            case "admin medis":
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
            case "petugas":
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
            case "admin":
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
            case "organizer":
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
            case "volunteer":
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
            default:
                fxmlPath = "/fxml/Sidebar.fxml";
                title = "VolunGem";
                break;
        }
        
        System.out.println("Redirecting to: " + fxmlPath + " with title: " + title);
        
        // Load dan tampilkan scene baru
        loadNewScene(fxmlPath, title, event);
    }

    private void loadNewScene(String fxmlPath, String title, ActionEvent event) throws IOException {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Get stage dari event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Set scene baru
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
            
            // Center stage
            stage.centerOnScreen();
            
            System.out.println("Successfully loaded scene: " + fxmlPath);
            
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            throw e;
        }
    }

    private void clearPasswordField() {
        if (passwordField_password != null) {
            passwordField_password.clear();
            passwordField_password.requestFocus();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        
        // Set owner untuk modal behavior
        if (loginMain != null && loginMain.getScene() != null) {
            alert.initOwner(loginMain.getScene().getWindow());
        }
        
        alert.showAndWait();
    }

    // Method untuk clear form (jika diperlukan)
    public void clearForm() {
        if (textField_username != null) {
            textField_username.clear();
        }
        if (passwordField_password != null) {
            passwordField_password.clear();
        }
        if (textField_username != null) {
            textField_username.requestFocus();
        }
    }

    // Method untuk set focus ke username field
    public void focusUsernameField() {
        if (textField_username != null) {
            textField_username.requestFocus();
        }
    }
}
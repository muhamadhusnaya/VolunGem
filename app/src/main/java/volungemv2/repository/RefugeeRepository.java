package volungemv2.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import volungemv2.models.Refugee;
import volungemv2.utils.DBUtil;

public class RefugeeRepository {
    private Connection connection;

    public RefugeeRepository() throws SQLException {
        try {
            this.connection = DBUtil.getConnection();
            System.out.println("Database connection established successfully in RefugeeRepository");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database in RefugeeRepository: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to indicate connection failure
        }
    }

    // CREATE: Menambahkan refugee
    public void addRefugee(Refugee refugee) throws SQLException {
        String query = "INSERT INTO refugees (name, age, gender, complain, address) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, refugee.getName());
            stmt.setInt(2, refugee.getAge());
            stmt.setString(3, refugee.getGender());
            stmt.setString(4, refugee.getComplain());
            stmt.setString(5, refugee.getAddress());
            stmt.executeUpdate();

            // Mendapatkan ID yang dihasilkan oleh database
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    refugee.setIdRefugee(generatedKeys.getInt(1));  // Menyimpan ID yang dihasilkan oleh database
                }
            }
        }
    }

    // READ: Mendapatkan semua refugee
    public List<Refugee> getAllRefugees() throws SQLException {
        String query = "SELECT * FROM refugees";
        List<Refugee> refugees = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Refugee refugee = new Refugee(
                        rs.getInt("idRefugee"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("complain"),
                        rs.getString("address")
                );
                refugees.add(refugee);
            }
        }
        return refugees;
    }

    // UPDATE: Mengupdate data refugee
    public void updateRefugee(Refugee refugee) throws SQLException {
        String query = "UPDATE refugees SET name = ?, age = ?, gender = ?, complain = ?, address = ? WHERE idRefugee = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, refugee.getName());
            stmt.setInt(2, refugee.getAge());
            stmt.setString(3, refugee.getGender());
            stmt.setString(4, refugee.getComplain());
            stmt.setString(5, refugee.getAddress());
            stmt.setInt(6, refugee.getIdRefugee());
            stmt.executeUpdate();
        }
    }

    // DELETE: Menghapus refugee berdasarkan ID
    public void deleteRefugee(int idRefugee) throws SQLException {
        String query = "DELETE FROM refugees WHERE idRefugee = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idRefugee);
            stmt.executeUpdate();
        }
    }
}

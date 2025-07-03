package volungemv2.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import volungemv2.models.Volunteer;
import volungemv2.utils.DBUtil;

public class VolunteerRepository {
    private Connection connection;

    public VolunteerRepository() throws SQLException {
        try {
            this.connection = DBUtil.getConnection();
            System.out.println("Database connection established successfully");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
            this.connection = null;
        }
    }

    // CREATE: Menambahkan volunteer baru
    public void addVolunteer(Volunteer volunteer) throws SQLException {
        String query = "INSERT INTO volunteers (name, age, gender, contact, team, task) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, volunteer.getName());
            stmt.setInt(2, volunteer.getAge());
            stmt.setString(3, volunteer.getGender());
            stmt.setString(4, volunteer.getContact());
            stmt.setString(5, volunteer.getTeam());
            stmt.setString(6, volunteer.getTask());
            stmt.executeUpdate();
            
            // Mendapatkan ID auto-generated dari database
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    volunteer.setId(generatedKeys.getInt(1));  // Menyimpan ID yang dihasilkan oleh database
                }
            }
        }
    }

    // READ: Mendapatkan semua volunteer
    public List<Volunteer> getAllVolunteers() throws SQLException {
        String query = "SELECT * FROM volunteers";
        List<Volunteer> volunteers = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Volunteer volunteer = new Volunteer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("contact"),
                        rs.getString("team"),
                        rs.getString("task")
                );
                volunteers.add(volunteer);
            }
        }
        return volunteers;
    }

    // UPDATE: Mengupdate data volunteer
    public void updateVolunteer(Volunteer volunteer) throws SQLException {
        String query = "UPDATE volunteers SET name = ?, age = ?, gender = ?, contact = ?, team = ?, task = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, volunteer.getName());
            stmt.setInt(2, volunteer.getAge());
            stmt.setString(3, volunteer.getGender());
            stmt.setString(4, volunteer.getContact());
            stmt.setString(5, volunteer.getTeam());
            stmt.setString(6, volunteer.getTask());
            stmt.setInt(7, volunteer.getId());  // Menggunakan ID untuk menentukan yang akan diupdate
            stmt.executeUpdate();
        }
    }

    // DELETE: Menghapus volunteer berdasarkan ID
    public void deleteVolunteer(int id) throws SQLException {
        String query = "DELETE FROM volunteers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

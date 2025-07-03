package volungemv2.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import volungemv2.models.Report;
import volungemv2.utils.DBUtil;

public class ReportRepository {
    private Connection connection;

    public ReportRepository() throws SQLException {
        try {
            this.connection = DBUtil.getConnection();
            System.out.println("Database connection established successfully in RefugeeRepository");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database in RefugeeRepository: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to indicate connection failure
        }
    }

    // CREATE: Menambahkan report
    public void addReport(Report report) throws SQLException {
        String query = "INSERT INTO reports (type, description, date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, report.getType());
            stmt.setString(2, report.getDescription());
            stmt.setDate(3, report.getDate());
            stmt.executeUpdate();

            // Mendapatkan ID yang dihasilkan oleh database
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    report.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // READ: Mendapatkan semua report
    public List<Report> getAllReports() throws SQLException {
        String query = "SELECT * FROM reports";
        List<Report> reports = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Report report = new Report(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getDate("date")
                );
                reports.add(report);
            }
        }
        return reports;
    }

    // UPDATE: Mengupdate report
    public void updateReport(Report report) throws SQLException {
        String query = "UPDATE reports SET type = ?, description = ?, date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, report.getType());
            stmt.setString(2, report.getDescription());
            stmt.setDate(3, report.getDate());
            stmt.setInt(4, report.getId());
            stmt.executeUpdate();
        }
    }

    // DELETE: Menghapus report berdasarkan ID
    public void deleteReport(int id) throws SQLException {
        String query = "DELETE FROM reports WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

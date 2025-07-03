package volungemv2.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import volungemv2.utils.DBUtil;

public class DashboardRepository {
    
    private Connection connection;

    public DashboardRepository() throws SQLException {
        try {
            this.connection = DBUtil.getConnection();
            System.out.println("Database connection established successfully");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
            this.connection = null;
        }
    }

    // Query untuk mendapatkan total jumlah volunteers
    public int getTotalVolunteers() throws SQLException {
        String query = "SELECT COUNT(*) FROM volunteers";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    // Query untuk mendapatkan total jumlah stok
    public int getTotalStock() throws SQLException {
        String query = "SELECT SUM(quantity) FROM aid";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    // Query untuk mendapatkan total jumlah laporan
    public int getTotalReports() throws SQLException {
        String query = "SELECT COUNT(*) FROM reports";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    // Query untuk mendapatkan daftar volunteer
    public List<String[]> getVolunteers() throws SQLException {
        List<String[]> volunteers = new ArrayList<>();
        String query = "SELECT name, contact, team FROM volunteers";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String[] volunteer = new String[3];
                volunteer[0] = resultSet.getString("name");
                volunteer[1] = resultSet.getString("contact");
                volunteer[2] = resultSet.getString("team");
                volunteers.add(volunteer);
            }
        }
        return volunteers;
    }

    // Query untuk mendapatkan daftar laporan
    public List<String[]> getReports() throws SQLException {
        List<String[]> reports = new ArrayList<>();
        String query = "SELECT type, date, description FROM reports ORDER BY date DESC";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String[] report = new String[3];
                report[0] = resultSet.getString("type");
                report[1] = resultSet.getString("date");
                report[2] = resultSet.getString("description");
                reports.add(report);
            }
        }
        return reports;
    }
}

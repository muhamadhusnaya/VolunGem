package volungemv2.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import volungemv2.models.Aids;
import volungemv2.utils.DBUtil;

public class AidRepository {
    private Connection connection;

    public AidRepository() throws SQLException {
        this.connection = DBUtil.getConnection();
        if (this.connection == null) {
            throw new SQLException("Failed to establish database connection");
        }
        System.out.println("Database connection established successfully in AidRepository");
    }

    // CREATE: Menambahkan assistance stock
    public void addAid(Aids stock) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is null");
        }
        
        String query = "INSERT INTO aid (category, item_name, quantity, location) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, stock.getCategory());
            stmt.setString(2, stock.getItemName());
            stmt.setInt(3, stock.getQuantity());
            stmt.setString(4, stock.getLocation());
            stmt.executeUpdate();

            // Mendapatkan ID yang dihasilkan oleh database
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stock.setIdAid(generatedKeys.getInt(1));
                }
            }
        }
    }

    // READ: Mendapatkan semua assistance stock
    public List<Aids> getAllAids() throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is null");
        }
        
        String query = "SELECT * FROM aid";
        List<Aids> stocks = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Aids stock = new Aids(
                        rs.getInt("idAid"),
                        rs.getString("category"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("location")
                );
                stocks.add(stock);
            }
        }
        return stocks;
    }

    // UPDATE: Mengupdate assistance stock
    public void updateAid(Aids stock) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is null");
        }
        
        // Fixed: Changed 'id' to 'idAid' to match the column name used in getAllAids()
        String query = "UPDATE aid SET category = ?, item_name = ?, quantity = ?, location = ? WHERE idAid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, stock.getCategory());
            stmt.setString(2, stock.getItemName());
            stmt.setInt(3, stock.getQuantity());
            stmt.setString(4, stock.getLocation());
            stmt.setInt(5, stock.getIdAid());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected. Aid with ID " + stock.getIdAid() + " not found.");
            }
        }
    }

    // DELETE: Menghapus assistance stock berdasarkan ID
    public void deleteAid(int idAid) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is null");
        }
        
        // Fixed: Changed 'id' to 'idAid' to match the column name used in getAllAids()
        String query = "DELETE FROM aid WHERE idAid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idAid);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Delete failed, no rows affected. Aid with ID " + idAid + " not found.");
            }
        }
    }

    // Method to close connection if needed
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed in AidRepository");
        }
    }

    // Method to check if connection is valid
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}
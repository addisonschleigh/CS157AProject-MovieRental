package dao;

import db.DatabaseConnection;
import model.Rental;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    public List<Rental> getAllRentals() throws SQLException {
        String sql = "SELECT * FROM Rentals ORDER BY RentalID";
        List<Rental> rentals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) rentals.add(buildRental(rs));
        }
        return rentals;
    }

    public List<Rental> getActiveRentals() throws SQLException {
        String sql = "SELECT * FROM Rentals WHERE Status = 'Active' ORDER BY DueDate";
        List<Rental> rentals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) rentals.add(buildRental(rs));
        }
        return rentals;
    }

    public Rental getRentalById(int rentalId) throws SQLException {
        String sql = "SELECT * FROM Rentals WHERE RentalID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return buildRental(rs);
            }
        }
        return null;
    }

    public boolean insertRental(Rental r) throws SQLException {
        String sql = "INSERT INTO Rentals (CustomerID, MovieID, RentalDate, " +
                     "DueDate, Status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, r.getCustomerId());
            pstmt.setInt(2, r.getMovieId());
            pstmt.setDate(3, r.getRentalDate());
            pstmt.setDate(4, r.getDueDate());
            pstmt.setString(5, r.getStatus());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateRental(Rental r) throws SQLException {
        String sql = "UPDATE Rentals SET CustomerID = ?, MovieID = ?, " +
                     "RentalDate = ?, DueDate = ?, Status = ? WHERE RentalID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, r.getCustomerId());
            pstmt.setInt(2, r.getMovieId());
            pstmt.setDate(3, r.getRentalDate());
            pstmt.setDate(4, r.getDueDate());
            pstmt.setString(5, r.getStatus());
            pstmt.setInt(6, r.getRentalId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int rentalId, String status) throws SQLException {
        String sql = "UPDATE Rentals SET Status = ? WHERE RentalID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, rentalId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteRental(int rentalId) throws SQLException {
        String sql = "DELETE FROM Rentals WHERE RentalID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Rental buildRental(ResultSet rs) throws SQLException {
        return new Rental(
            rs.getInt("RentalID"),
            rs.getInt("CustomerID"),
            rs.getInt("MovieID"),
            rs.getDate("RentalDate"),
            rs.getDate("DueDate"),
            rs.getString("Status")
        );
    }
}

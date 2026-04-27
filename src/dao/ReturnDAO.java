package dao;

import db.DatabaseConnection;
import model.Return;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnDAO {

    public List<Return> getAllReturns() throws SQLException {
        String sql = "SELECT * FROM Returns ORDER BY ReturnID";
        List<Return> returns = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) returns.add(buildReturn(rs));
        }
        return returns;
    }

    public Return getReturnById(int returnId) throws SQLException {
        String sql = "SELECT * FROM Returns WHERE ReturnID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, returnId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return buildReturn(rs);
            }
        }
        return null;
    }

    public boolean insertReturn(Return r) throws SQLException {
        String sql = "INSERT INTO Returns (RentalID, ReturnDate, LateFee) " +
                     "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, r.getRentalId());
            pstmt.setDate(2, r.getReturnDate());
            pstmt.setBigDecimal(3, r.getLateFee());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateReturn(Return r) throws SQLException {
        String sql = "UPDATE Returns SET RentalID = ?, ReturnDate = ?, " +
                     "LateFee = ? WHERE ReturnID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, r.getRentalId());
            pstmt.setDate(2, r.getReturnDate());
            pstmt.setBigDecimal(3, r.getLateFee());
            pstmt.setInt(4, r.getReturnId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteReturn(int returnId) throws SQLException {
        String sql = "DELETE FROM Returns WHERE ReturnID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, returnId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Return buildReturn(ResultSet rs) throws SQLException {
        return new Return(
            rs.getInt("ReturnID"),
            rs.getInt("RentalID"),
            rs.getDate("ReturnDate"),
            rs.getBigDecimal("LateFee")
        );
    }
}

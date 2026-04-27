package dao;

import db.DatabaseConnection;
import model.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    public List<Rating> getAllRatings() throws SQLException {
        String sql = "SELECT * FROM Ratings ORDER BY RatingID";
        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) ratings.add(buildRating(rs));
        }
        return ratings;
    }

    public List<Rating> getRatingsByMovie(int movieId) throws SQLException {
        String sql = "SELECT * FROM Ratings WHERE MovieID = ? ORDER BY ReviewDate DESC";
        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) ratings.add(buildRating(rs));
            }
        }
        return ratings;
    }

    public double getAverageScore(int movieId) throws SQLException {
        String sql = "SELECT AVG(Score) AS AvgScore FROM Ratings WHERE MovieID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("AvgScore");
            }
        }
        return 0.0;
    }

    public boolean insertRating(Rating r) throws SQLException {
        String sql = "INSERT INTO Ratings (CustomerID, MovieID, Score, ReviewDate) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, r.getCustomerId());
            pstmt.setInt(2, r.getMovieId());
            pstmt.setInt(3, r.getScore());
            pstmt.setDate(4, r.getReviewDate());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateRating(Rating r) throws SQLException {
        String sql = "UPDATE Ratings SET CustomerID = ?, MovieID = ?, Score = ?, " +
                     "ReviewDate = ? WHERE RatingID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, r.getCustomerId());
            pstmt.setInt(2, r.getMovieId());
            pstmt.setInt(3, r.getScore());
            pstmt.setDate(4, r.getReviewDate());
            pstmt.setInt(5, r.getRatingId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteRating(int ratingId) throws SQLException {
        String sql = "DELETE FROM Ratings WHERE RatingID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ratingId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Rating buildRating(ResultSet rs) throws SQLException {
        return new Rating(
            rs.getInt("RatingID"),
            rs.getInt("CustomerID"),
            rs.getInt("MovieID"),
            rs.getInt("Score"),
            rs.getDate("ReviewDate")
        );
    }
}

package dao;

import db.DatabaseConnection;
import model.Movie;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public List<Movie> getAllMovies() throws SQLException {
        String sql = "SELECT * FROM Movies ORDER BY MovieID";
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(buildMovie(rs));
            }
        }
        return movies;
    }

    public List<Movie> searchByTitle(String keyword) throws SQLException {
        String sql = "SELECT * FROM Movies WHERE Title LIKE ? ORDER BY Title";
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(buildMovie(rs));
                }
            }
        }
        return movies;
    }

    public Movie getMovieById(int movieId) throws SQLException {
        String sql = "SELECT * FROM Movies WHERE MovieID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return buildMovie(rs);
            }
        }
        return null;
    }

    public boolean insertMovie(Movie m) throws SQLException {
        String sql = "INSERT INTO Movies (Title, Genre, Director, ReleaseYear, " +
                     "MPAARating, CopiesAvailable, RentalPrice) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getGenre());
            pstmt.setString(3, m.getDirector());
            pstmt.setInt(4, m.getReleaseYear());
            pstmt.setString(5, m.getMpaaRating());
            pstmt.setInt(6, m.getCopiesAvailable());
            pstmt.setBigDecimal(7, m.getRentalPrice());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateMovie(Movie m) throws SQLException {
        String sql = "UPDATE Movies SET Title = ?, Genre = ?, Director = ?, " +
                     "ReleaseYear = ?, MPAARating = ?, CopiesAvailable = ?, " +
                     "RentalPrice = ? WHERE MovieID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getGenre());
            pstmt.setString(3, m.getDirector());
            pstmt.setInt(4, m.getReleaseYear());
            pstmt.setString(5, m.getMpaaRating());
            pstmt.setInt(6, m.getCopiesAvailable());
            pstmt.setBigDecimal(7, m.getRentalPrice());
            pstmt.setInt(8, m.getMovieId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM Movies WHERE MovieID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Movie buildMovie(ResultSet rs) throws SQLException {
        return new Movie(
            rs.getInt("MovieID"),
            rs.getString("Title"),
            rs.getString("Genre"),
            rs.getString("Director"),
            rs.getInt("ReleaseYear"),
            rs.getString("MPAARating"),
            rs.getInt("CopiesAvailable"),
            rs.getBigDecimal("RentalPrice")
        );
    }
}

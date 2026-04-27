package ui;

import db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MovieRentalApp extends JFrame {

    public MovieRentalApp() {
        setTitle("Movie Rental Database System");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Movies",    new MoviePanel());
        tabs.addTab("Customers", new CustomerPanel());
        tabs.addTab("Rentals",   new RentalPanel());
        tabs.addTab("Returns",   new ReturnPanel());
        tabs.addTab("Ratings",   new RatingPanel());

        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Verify connection on startup
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connected to MySQL: " + conn.getMetaData().getURL());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Cannot connect to database:\n" + ex.getMessage() +
                "\n\nMake sure MySQL is running and credentials in DatabaseConnection.java are correct.",
                "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new MovieRentalApp().setVisible(true));
    }
}

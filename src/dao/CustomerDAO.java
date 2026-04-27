package dao;

import db.DatabaseConnection;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM Customers ORDER BY CustomerID";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) customers.add(buildCustomer(rs));
        }
        return customers;
    }

    public List<Customer> searchByName(String keyword) throws SQLException {
        String sql = "SELECT * FROM Customers " +
                     "WHERE FirstName LIKE ? OR LastName LIKE ? ORDER BY LastName";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) customers.add(buildCustomer(rs));
            }
        }
        return customers;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return buildCustomer(rs);
            }
        }
        return null;
    }

    public boolean insertCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO Customers (FirstName, LastName, Email, Phone, " +
                     "Address, JoinDate) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getFirstName());
            pstmt.setString(2, c.getLastName());
            pstmt.setString(3, c.getEmail());
            pstmt.setString(4, c.getPhone());
            pstmt.setString(5, c.getAddress());
            pstmt.setDate(6, c.getJoinDate());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE Customers SET FirstName = ?, LastName = ?, Email = ?, " +
                     "Phone = ?, Address = ?, JoinDate = ? WHERE CustomerID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getFirstName());
            pstmt.setString(2, c.getLastName());
            pstmt.setString(3, c.getEmail());
            pstmt.setString(4, c.getPhone());
            pstmt.setString(5, c.getAddress());
            pstmt.setDate(6, c.getJoinDate());
            pstmt.setInt(7, c.getCustomerId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM Customers WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Customer buildCustomer(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("CustomerID"),
            rs.getString("FirstName"),
            rs.getString("LastName"),
            rs.getString("Email"),
            rs.getString("Phone"),
            rs.getString("Address"),
            rs.getDate("JoinDate")
        );
    }
}

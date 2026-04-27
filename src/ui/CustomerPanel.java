package ui;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CustomerPanel extends JPanel {

    private final CustomerDAO       dao   = new CustomerDAO();
    private final DefaultTableModel model;
    private final JTable            table;
    private final JTextField        searchField = new JTextField(20);

    private static final String[] COLUMNS = {
        "ID", "First Name", "Last Name", "Email", "Phone", "Address", "Join Date"
    };

    public CustomerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search name:"));
        top.add(searchField);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Show All");
        top.add(searchBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn    = new JButton("Add Customer");
        JButton editBtn   = new JButton("Edit Customer");
        JButton deleteBtn = new JButton("Delete Customer");
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAll());
        searchBtn.addActionListener(e -> doSearch());
        addBtn.addActionListener(e -> showForm(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        loadAll();
    }

    private void loadAll() {
        try { populate(dao.getAllCustomers()); }
        catch (SQLException ex) { error("Failed to load customers", ex); }
    }

    private void doSearch() {
        try { populate(dao.searchByName(searchField.getText().trim())); }
        catch (SQLException ex) { error("Search failed", ex); }
    }

    private void populate(List<Customer> list) {
        model.setRowCount(0);
        for (Customer c : list) {
            model.addRow(new Object[]{
                c.getCustomerId(), c.getFirstName(), c.getLastName(),
                c.getEmail(), c.getPhone(), c.getAddress(), c.getJoinDate()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        try {
            Customer c = dao.getCustomerById(id);
            if (c != null) showForm(c);
        } catch (SQLException ex) { error("Failed to load customer", ex); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete customer ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (dao.deleteCustomer(id)) loadAll();
        } catch (SQLException ex) { error("Failed to delete customer", ex); }
    }

    private void showForm(Customer existing) {
        JTextField firstF   = new JTextField(existing == null ? "" : existing.getFirstName());
        JTextField lastF    = new JTextField(existing == null ? "" : existing.getLastName());
        JTextField emailF   = new JTextField(existing == null ? "" : existing.getEmail());
        JTextField phoneF   = new JTextField(existing == null ? "" : existing.getPhone());
        JTextField addrF    = new JTextField(existing == null ? "" : existing.getAddress());
        JTextField joinF    = new JTextField(existing == null ? "" : existing.getJoinDate().toString());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("First Name:"));     form.add(firstF);
        form.add(new JLabel("Last Name:"));      form.add(lastF);
        form.add(new JLabel("Email:"));          form.add(emailF);
        form.add(new JLabel("Phone:"));          form.add(phoneF);
        form.add(new JLabel("Address:"));        form.add(addrF);
        form.add(new JLabel("Join Date (YYYY-MM-DD):")); form.add(joinF);

        String title = existing == null ? "Add Customer" : "Edit Customer";
        int result = JOptionPane.showConfirmDialog(this, form, title,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Customer c = (existing == null) ? new Customer() : existing;
            c.setFirstName(firstF.getText().trim());
            c.setLastName(lastF.getText().trim());
            c.setEmail(emailF.getText().trim());
            c.setPhone(phoneF.getText().trim());
            c.setAddress(addrF.getText().trim());
            c.setJoinDate(Date.valueOf(joinF.getText().trim()));

            boolean ok = (existing == null) ? dao.insertCustomer(c) : dao.updateCustomer(c);
            if (ok) loadAll();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            error("Failed to save customer", ex);
        }
    }

    private void error(String msg, Exception ex) {
        JOptionPane.showMessageDialog(this, msg + ":\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

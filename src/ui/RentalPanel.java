package ui;

import dao.RentalDAO;
import model.Rental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class RentalPanel extends JPanel {

    private final RentalDAO         dao   = new RentalDAO();
    private final DefaultTableModel model;
    private final JTable            table;

    private static final String[] COLUMNS = {
        "RentalID", "CustomerID", "MovieID", "Rental Date", "Due Date", "Status"
    };

    public RentalPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn  = new JButton("Show All");
        JButton activeBtn   = new JButton("Show Active Only");
        top.add(refreshBtn);
        top.add(activeBtn);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn    = new JButton("Add Rental");
        JButton editBtn   = new JButton("Edit Rental");
        JButton deleteBtn = new JButton("Delete Rental");
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAll());
        activeBtn.addActionListener(e -> loadActive());
        addBtn.addActionListener(e -> showForm(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        loadAll();
    }

    private void loadAll() {
        try { populate(dao.getAllRentals()); }
        catch (SQLException ex) { error("Failed to load rentals", ex); }
    }

    private void loadActive() {
        try { populate(dao.getActiveRentals()); }
        catch (SQLException ex) { error("Failed to load active rentals", ex); }
    }

    private void populate(List<Rental> list) {
        model.setRowCount(0);
        for (Rental r : list) {
            model.addRow(new Object[]{
                r.getRentalId(), r.getCustomerId(), r.getMovieId(),
                r.getRentalDate(), r.getDueDate(), r.getStatus()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        try {
            Rental r = dao.getRentalById(id);
            if (r != null) showForm(r);
        } catch (SQLException ex) { error("Failed to load rental", ex); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete rental ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (dao.deleteRental(id)) loadAll();
        } catch (SQLException ex) { error("Failed to delete rental", ex); }
    }

    private void showForm(Rental existing) {
        JTextField customerF = new JTextField(existing == null ? "" : String.valueOf(existing.getCustomerId()));
        JTextField movieF    = new JTextField(existing == null ? "" : String.valueOf(existing.getMovieId()));
        JTextField rentalF   = new JTextField(existing == null ? "" : existing.getRentalDate().toString());
        JTextField dueF      = new JTextField(existing == null ? "" : existing.getDueDate().toString());
        String[] statuses = {"Active", "Returned", "Overdue"};
        JComboBox<String> statusF = new JComboBox<>(statuses);
        if (existing != null) statusF.setSelectedItem(existing.getStatus());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Customer ID:"));     form.add(customerF);
        form.add(new JLabel("Movie ID:"));        form.add(movieF);
        form.add(new JLabel("Rental Date (YYYY-MM-DD):")); form.add(rentalF);
        form.add(new JLabel("Due Date (YYYY-MM-DD):"));    form.add(dueF);
        form.add(new JLabel("Status:"));          form.add(statusF);

        String title = existing == null ? "Add Rental" : "Edit Rental";
        int result = JOptionPane.showConfirmDialog(this, form, title,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Rental r = (existing == null) ? new Rental() : existing;
            r.setCustomerId(Integer.parseInt(customerF.getText().trim()));
            r.setMovieId(Integer.parseInt(movieF.getText().trim()));
            r.setRentalDate(Date.valueOf(rentalF.getText().trim()));
            r.setDueDate(Date.valueOf(dueF.getText().trim()));
            r.setStatus((String) statusF.getSelectedItem());

            boolean ok = (existing == null) ? dao.insertRental(r) : dao.updateRental(r);
            if (ok) loadAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Customer/Movie ID must be a number.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            error("Failed to save rental", ex);
        }
    }

    private void error(String msg, Exception ex) {
        JOptionPane.showMessageDialog(this, msg + ":\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

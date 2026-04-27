package ui;

import dao.ReturnDAO;
import model.Return;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ReturnPanel extends JPanel {

    private final ReturnDAO         dao   = new ReturnDAO();
    private final DefaultTableModel model;
    private final JTable            table;

    private static final String[] COLUMNS = {
        "ReturnID", "RentalID", "Return Date", "Late Fee"
    };

    public ReturnPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Show All");
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn    = new JButton("Add Return");
        JButton editBtn   = new JButton("Edit Return");
        JButton deleteBtn = new JButton("Delete Return");
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAll());
        addBtn.addActionListener(e -> showForm(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        loadAll();
    }

    private void loadAll() {
        try { populate(dao.getAllReturns()); }
        catch (SQLException ex) { error("Failed to load returns", ex); }
    }

    private void populate(List<Return> list) {
        model.setRowCount(0);
        for (Return r : list) {
            model.addRow(new Object[]{
                r.getReturnId(), r.getRentalId(),
                r.getReturnDate(), r.getLateFee()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        try {
            Return r = dao.getReturnById(id);
            if (r != null) showForm(r);
        } catch (SQLException ex) { error("Failed to load return", ex); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete return ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (dao.deleteReturn(id)) loadAll();
        } catch (SQLException ex) { error("Failed to delete return", ex); }
    }

    private void showForm(Return existing) {
        JTextField rentalF  = new JTextField(existing == null ? "" : String.valueOf(existing.getRentalId()));
        JTextField dateF    = new JTextField(existing == null ? "" : existing.getReturnDate().toString());
        JTextField feeF     = new JTextField(existing == null ? "0.00" : existing.getLateFee().toString());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Rental ID:"));        form.add(rentalF);
        form.add(new JLabel("Return Date (YYYY-MM-DD):")); form.add(dateF);
        form.add(new JLabel("Late Fee:"));         form.add(feeF);

        String title = existing == null ? "Add Return" : "Edit Return";
        int result = JOptionPane.showConfirmDialog(this, form, title,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Return r = (existing == null) ? new Return() : existing;
            r.setRentalId(Integer.parseInt(rentalF.getText().trim()));
            r.setReturnDate(Date.valueOf(dateF.getText().trim()));
            r.setLateFee(new BigDecimal(feeF.getText().trim()));

            boolean ok = (existing == null) ? dao.insertReturn(r) : dao.updateReturn(r);
            if (ok) loadAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number in form.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            error("Failed to save return", ex);
        }
    }

    private void error(String msg, Exception ex) {
        JOptionPane.showMessageDialog(this, msg + ":\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

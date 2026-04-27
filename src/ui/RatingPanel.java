package ui;

import dao.RatingDAO;
import model.Rating;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class RatingPanel extends JPanel {

    private final RatingDAO         dao   = new RatingDAO();
    private final DefaultTableModel model;
    private final JTable            table;
    private final JTextField        movieIdField = new JTextField(6);

    private static final String[] COLUMNS = {
        "RatingID", "CustomerID", "MovieID", "Score", "Review Date"
    };

    public RatingPanel() {
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
        top.add(new JLabel("  Filter by MovieID:"));
        top.add(movieIdField);
        JButton filterBtn = new JButton("Filter");
        JButton avgBtn    = new JButton("Show Average");
        top.add(filterBtn);
        top.add(avgBtn);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn    = new JButton("Add Rating");
        JButton editBtn   = new JButton("Edit Rating");
        JButton deleteBtn = new JButton("Delete Rating");
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAll());
        filterBtn.addActionListener(e -> filterByMovie());
        avgBtn.addActionListener(e -> showAverage());
        addBtn.addActionListener(e -> showForm(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        loadAll();
    }

    private void loadAll() {
        try { populate(dao.getAllRatings()); }
        catch (SQLException ex) { error("Failed to load ratings", ex); }
    }

    private void filterByMovie() {
        try {
            int movieId = Integer.parseInt(movieIdField.getText().trim());
            populate(dao.getRatingsByMovie(movieId));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid MovieID.");
        } catch (SQLException ex) {
            error("Filter failed", ex);
        }
    }

    private void showAverage() {
        try {
            int movieId = Integer.parseInt(movieIdField.getText().trim());
            double avg = dao.getAverageScore(movieId);
            JOptionPane.showMessageDialog(this,
                String.format("Average score for Movie %d: %.2f", movieId, avg));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid MovieID.");
        } catch (SQLException ex) {
            error("Average lookup failed", ex);
        }
    }

    private void populate(List<Rating> list) {
        model.setRowCount(0);
        for (Rating r : list) {
            model.addRow(new Object[]{
                r.getRatingId(), r.getCustomerId(), r.getMovieId(),
                r.getScore(), r.getReviewDate()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        try {
            List<Rating> all = dao.getAllRatings();
            for (Rating r : all) {
                if (r.getRatingId() == id) { showForm(r); return; }
            }
        } catch (SQLException ex) { error("Failed to load rating", ex); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete rating ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (dao.deleteRating(id)) loadAll();
        } catch (SQLException ex) { error("Failed to delete rating", ex); }
    }

    private void showForm(Rating existing) {
        JTextField customerF = new JTextField(existing == null ? "" : String.valueOf(existing.getCustomerId()));
        JTextField movieF    = new JTextField(existing == null ? "" : String.valueOf(existing.getMovieId()));
        JTextField scoreF    = new JTextField(existing == null ? "" : String.valueOf(existing.getScore()));
        JTextField dateF     = new JTextField(existing == null ? "" : existing.getReviewDate().toString());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Customer ID:"));     form.add(customerF);
        form.add(new JLabel("Movie ID:"));        form.add(movieF);
        form.add(new JLabel("Score (1-5):"));     form.add(scoreF);
        form.add(new JLabel("Review Date (YYYY-MM-DD):")); form.add(dateF);

        String title = existing == null ? "Add Rating" : "Edit Rating";
        int result = JOptionPane.showConfirmDialog(this, form, title,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Rating r = (existing == null) ? new Rating() : existing;
            r.setCustomerId(Integer.parseInt(customerF.getText().trim()));
            r.setMovieId(Integer.parseInt(movieF.getText().trim()));
            int score = Integer.parseInt(scoreF.getText().trim());
            if (score < 1 || score > 5) {
                JOptionPane.showMessageDialog(this, "Score must be between 1 and 5.");
                return;
            }
            r.setScore(score);
            r.setReviewDate(Date.valueOf(dateF.getText().trim()));

            boolean ok = (existing == null) ? dao.insertRating(r) : dao.updateRating(r);
            if (ok) loadAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number in form.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            error("Failed to save rating", ex);
        }
    }

    private void error(String msg, Exception ex) {
        JOptionPane.showMessageDialog(this, msg + ":\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

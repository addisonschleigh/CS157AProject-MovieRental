package ui;

import dao.MovieDAO;
import dao.RentalDAO;
import dao.ReturnDAO;
import model.Movie;
import model.Rental;
import model.Return;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalPanel extends JPanel {

    private final RentalDAO         dao        = new RentalDAO();
    private final ReturnDAO         returnDao  = new ReturnDAO();
    private final MovieDAO          movieDao   = new MovieDAO();
    private final DefaultTableModel model;
    private final JTable            table;

    private static final BigDecimal LATE_FEE_PER_DAY = new BigDecimal("1.50");

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
        JButton returnBtn = new JButton("Process Return");
        JButton addBtn    = new JButton("Add Rental");
        JButton editBtn   = new JButton("Edit Rental");
        JButton deleteBtn = new JButton("Delete Rental");
        bottom.add(returnBtn);
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAll());
        activeBtn.addActionListener(e -> loadActive());
        returnBtn.addActionListener(e -> processReturn());
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

    private void processReturn() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a rental to return first.");
            return;
        }

        int    rentalId = (int)    model.getValueAt(row, 0);
        int    movieId  = (int)    model.getValueAt(row, 2);
        Date   dueDate  = (Date)   model.getValueAt(row, 4);
        String status   = (String) model.getValueAt(row, 5);

        if (!"Active".equals(status) && !"Overdue".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "This rental is already \"" + status + "\". Cannot process a return.",
                "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Compute suggested late fee based on today vs due date
        LocalDate today  = LocalDate.now();
        LocalDate dueLd  = dueDate.toLocalDate();
        long daysLate    = ChronoUnit.DAYS.between(dueLd, today);
        BigDecimal suggestedFee = (daysLate > 0)
            ? LATE_FEE_PER_DAY.multiply(BigDecimal.valueOf(daysLate))
            : BigDecimal.ZERO;

        JTextField returnDateF = new JTextField(today.toString());
        JTextField lateFeeF    = new JTextField(suggestedFee.toString());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Rental ID:"));     form.add(new JLabel(String.valueOf(rentalId)));
        form.add(new JLabel("Due Date:"));      form.add(new JLabel(dueDate.toString()));
        form.add(new JLabel("Days Late:"));     form.add(new JLabel(String.valueOf(Math.max(0, daysLate))));
        form.add(new JLabel("Return Date:"));   form.add(returnDateF);
        form.add(new JLabel("Late Fee:"));      form.add(lateFeeF);

        int result = JOptionPane.showConfirmDialog(this, form, "Process Return for Rental " + rentalId,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Return ret = new Return();
            ret.setRentalId(rentalId);
            ret.setReturnDate(Date.valueOf(returnDateF.getText().trim()));
            ret.setLateFee(new BigDecimal(lateFeeF.getText().trim()));

            // Insert the Return record
            boolean returnOk = returnDao.insertReturn(ret);
            if (!returnOk) {
                JOptionPane.showMessageDialog(this, "Failed to record return.");
                return;
            }

            // Update Rental status
            dao.updateStatus(rentalId, "Returned");

            // Increment movie copies available
            Movie m = movieDao.getMovieById(movieId);
            if (m != null) {
                m.setCopiesAvailable(m.getCopiesAvailable() + 1);
                movieDao.updateMovie(m);
            }

            JOptionPane.showMessageDialog(this,
                "Return processed for Rental " + rentalId + ".\nLate fee: $" + lateFeeF.getText().trim());

            loadAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid late fee amount.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            error("Failed to process return", ex);
        }
    }

    private void error(String msg, Exception ex) {
        JOptionPane.showMessageDialog(this, msg + ":\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

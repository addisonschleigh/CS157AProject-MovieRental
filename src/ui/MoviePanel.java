package ui;

import dao.MovieDAO;
import model.Movie;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MoviePanel extends JPanel {

    private final MovieDAO         dao   = new MovieDAO();
    private final DefaultTableModel model;
    private final JTable            table;
    private final JTextField        searchField = new JTextField(20);

    private static final String[] COLUMNS = {
        "ID", "Title", "Genre", "Director", "Year", "MPAA", "Copies", "Price"
    };

    public MoviePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Top bar: search
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search title:"));
        top.add(searchField);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Show All");
        top.add(searchBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        // Bottom bar: action buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn    = new JButton("Add Movie");
        JButton editBtn   = new JButton("Edit Movie");
        JButton deleteBtn = new JButton("Delete Movie");
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        // Listeners
        refreshBtn.addActionListener(e -> loadAll());
        searchBtn.addActionListener(e -> doSearch());
        addBtn.addActionListener(e -> showForm(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        loadAll();
    }

    private void loadAll() {
        try {
            populate(dao.getAllMovies());
        } catch (SQLException ex) {
            error("Failed to load movies", ex);
        }
    }

    private void doSearch() {
        try {
            populate(dao.searchByTitle(searchField.getText().trim()));
        } catch (SQLException ex) {
            error("Search failed", ex);
        }
    }

    private void populate(List<Movie> list) {
        model.setRowCount(0);
        for (Movie m : list) {
            model.addRow(new Object[]{
                m.getMovieId(), m.getTitle(), m.getGenre(), m.getDirector(),
                m.getReleaseYear(), m.getMpaaRating(),
                m.getCopiesAvailable(), m.getRentalPrice()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        try {
            Movie m = dao.getMovieById(id);
            if (m != null) showForm(m);
        } catch (SQLException ex) {
            error("Failed to load movie", ex);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete movie ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (dao.deleteMovie(id)) loadAll();
        } catch (SQLException ex) {
            error("Failed to delete movie", ex);
        }
    }

    private void showForm(Movie existing) {
        JTextField titleF    = new JTextField(existing == null ? "" : existing.getTitle());
        JTextField genreF    = new JTextField(existing == null ? "" : existing.getGenre());
        JTextField directorF = new JTextField(existing == null ? "" : existing.getDirector());
        JTextField yearF     = new JTextField(existing == null ? "" : String.valueOf(existing.getReleaseYear()));
        JTextField mpaaF     = new JTextField(existing == null ? "" : existing.getMpaaRating());
        JTextField copiesF   = new JTextField(existing == null ? "" : String.valueOf(existing.getCopiesAvailable()));
        JTextField priceF    = new JTextField(existing == null ? "" : existing.getRentalPrice().toString());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Title:"));    form.add(titleF);
        form.add(new JLabel("Genre:"));    form.add(genreF);
        form.add(new JLabel("Director:")); form.add(directorF);
        form.add(new JLabel("Year:"));     form.add(yearF);
        form.add(new JLabel("MPAA:"));     form.add(mpaaF);
        form.add(new JLabel("Copies:"));   form.add(copiesF);
        form.add(new JLabel("Price:"));    form.add(priceF);

        String title = existing == null ? "Add Movie" : "Edit Movie";
        int result = JOptionPane.showConfirmDialog(this, form, title,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Movie m = (existing == null) ? new Movie() : existing;
            m.setTitle(titleF.getText().trim());
            m.setGenre(genreF.getText().trim());
            m.setDirector(directorF.getText().trim());
            m.setReleaseYear(Integer.parseInt(yearF.getText().trim()));
            m.setMpaaRating(mpaaF.getText().trim());
            m.setCopiesAvailable(Integer.parseInt(copiesF.getText().trim()));
            m.setRentalPrice(new BigDecimal(priceF.getText().trim()));

            boolean ok = (existing == null) ? dao.insertMovie(m) : dao.updateMovie(m);
            if (ok) loadAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number in form.");
        } catch (SQLException ex) {
            error("Failed to save movie", ex);
        }
    }

    private void error(String msg, Exception ex) {
        JOptionPane.showMessageDialog(this, msg + ":\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

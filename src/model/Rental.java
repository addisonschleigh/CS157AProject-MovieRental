package model;

import java.sql.Date;

public class Rental {
    private int    rentalId;
    private int    customerId;
    private int    movieId;
    private Date   rentalDate;
    private Date   dueDate;
    private String status;

    public Rental() {}

    public Rental(int rentalId, int customerId, int movieId,
                  Date rentalDate, Date dueDate, String status) {
        this.rentalId   = rentalId;
        this.customerId = customerId;
        this.movieId    = movieId;
        this.rentalDate = rentalDate;
        this.dueDate    = dueDate;
        this.status     = status;
    }

    public int    getRentalId()   { return rentalId; }
    public int    getCustomerId() { return customerId; }
    public int    getMovieId()    { return movieId; }
    public Date   getRentalDate() { return rentalDate; }
    public Date   getDueDate()    { return dueDate; }
    public String getStatus()     { return status; }

    public void setRentalId(int v)    { this.rentalId = v; }
    public void setCustomerId(int v)  { this.customerId = v; }
    public void setMovieId(int v)     { this.movieId = v; }
    public void setRentalDate(Date v) { this.rentalDate = v; }
    public void setDueDate(Date v)    { this.dueDate = v; }
    public void setStatus(String v)   { this.status = v; }
}

package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Return {
    private int        returnId;
    private int        rentalId;
    private Date       returnDate;
    private BigDecimal lateFee;

    public Return() {}

    public Return(int returnId, int rentalId, Date returnDate, BigDecimal lateFee) {
        this.returnId   = returnId;
        this.rentalId   = rentalId;
        this.returnDate = returnDate;
        this.lateFee    = lateFee;
    }

    public int        getReturnId()   { return returnId; }
    public int        getRentalId()   { return rentalId; }
    public Date       getReturnDate() { return returnDate; }
    public BigDecimal getLateFee()    { return lateFee; }

    public void setReturnId(int v)        { this.returnId = v; }
    public void setRentalId(int v)        { this.rentalId = v; }
    public void setReturnDate(Date v)     { this.returnDate = v; }
    public void setLateFee(BigDecimal v)  { this.lateFee = v; }
}

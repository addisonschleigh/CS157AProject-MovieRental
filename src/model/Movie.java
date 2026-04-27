package model;

import java.math.BigDecimal;

public class Movie {
    private int        movieId;
    private String     title;
    private String     genre;
    private String     director;
    private int        releaseYear;
    private String     mpaaRating;
    private int        copiesAvailable;
    private BigDecimal rentalPrice;

    public Movie() {}

    public Movie(int movieId, String title, String genre, String director,
                 int releaseYear, String mpaaRating, int copiesAvailable,
                 BigDecimal rentalPrice) {
        this.movieId         = movieId;
        this.title           = title;
        this.genre           = genre;
        this.director        = director;
        this.releaseYear     = releaseYear;
        this.mpaaRating      = mpaaRating;
        this.copiesAvailable = copiesAvailable;
        this.rentalPrice     = rentalPrice;
    }

    public int        getMovieId()         { return movieId; }
    public String     getTitle()           { return title; }
    public String     getGenre()           { return genre; }
    public String     getDirector()        { return director; }
    public int        getReleaseYear()     { return releaseYear; }
    public String     getMpaaRating()      { return mpaaRating; }
    public int        getCopiesAvailable() { return copiesAvailable; }
    public BigDecimal getRentalPrice()     { return rentalPrice; }

    public void setMovieId(int v)              { this.movieId = v; }
    public void setTitle(String v)             { this.title = v; }
    public void setGenre(String v)             { this.genre = v; }
    public void setDirector(String v)          { this.director = v; }
    public void setReleaseYear(int v)          { this.releaseYear = v; }
    public void setMpaaRating(String v)        { this.mpaaRating = v; }
    public void setCopiesAvailable(int v)      { this.copiesAvailable = v; }
    public void setRentalPrice(BigDecimal v)   { this.rentalPrice = v; }
}

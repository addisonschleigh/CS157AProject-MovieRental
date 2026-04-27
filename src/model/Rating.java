package model;

import java.sql.Date;

public class Rating {
    private int  ratingId;
    private int  customerId;
    private int  movieId;
    private int  score;
    private Date reviewDate;

    public Rating() {}

    public Rating(int ratingId, int customerId, int movieId, int score, Date reviewDate) {
        this.ratingId   = ratingId;
        this.customerId = customerId;
        this.movieId    = movieId;
        this.score      = score;
        this.reviewDate = reviewDate;
    }

    public int  getRatingId()   { return ratingId; }
    public int  getCustomerId() { return customerId; }
    public int  getMovieId()    { return movieId; }
    public int  getScore()      { return score; }
    public Date getReviewDate() { return reviewDate; }

    public void setRatingId(int v)    { this.ratingId = v; }
    public void setCustomerId(int v)  { this.customerId = v; }
    public void setMovieId(int v)     { this.movieId = v; }
    public void setScore(int v)       { this.score = v; }
    public void setReviewDate(Date v) { this.reviewDate = v; }
}

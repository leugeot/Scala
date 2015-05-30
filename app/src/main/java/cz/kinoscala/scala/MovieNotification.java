package cz.kinoscala.scala;

/**
 * Created by petr on 29. 5. 2015.
 */
public class MovieNotification {
    private int ID;
    private Movie movie;

    public int getID() {
        return ID;
    }

    public Movie getMovie() {
        return movie;
    }

    public MovieNotification(int ID, Movie movie) {
        this.ID = ID;
        this.movie = movie;
    }
}

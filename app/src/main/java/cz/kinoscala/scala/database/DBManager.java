package cz.kinoscala.scala.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import cz.kinoscala.scala.Movie;
import cz.kinoscala.scala.MovieNotification;

/**
 * Created by petr on 10.5.2015.
 */
public class DBManager {
    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;

    public DBManager(Context context){
        this.context = context;
    }

    public void open() throws SQLException {
        dbHelper = DBHelper.getInstance(context);
        writableDatabase = dbHelper.getWritableDatabase();
        readableDatabase = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertMovie(Movie movie) {
        if (!MoviesTable.containsMovieId(readableDatabase ,movie.getId())) {
            MoviesTable.insert(writableDatabase, movie);
        }
    }

    public List<Movie> getMoviesSince(Date date) {
        return MoviesTable.getMoviesSince(readableDatabase, date);
    }

    public boolean containsMovieId(long id){
        return MoviesTable.containsMovieId(readableDatabase, id);
    }

    public boolean hasNotification(long movieId){
        return NotificationsTable.getNotificationForMovie(readableDatabase, movieId) != -1;
    }

    public List<MovieNotification> getNotifications(){
        return NotificationsTable.getNotifications(readableDatabase);
    }

    public int insertNotification(long movieId) {
        return (int)NotificationsTable.insert(writableDatabase, movieId);
    }

    public int removeNotification(long movieId) {
        return NotificationsTable.remove(writableDatabase, movieId);
    }
}

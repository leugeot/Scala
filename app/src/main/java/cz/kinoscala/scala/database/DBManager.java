package cz.kinoscala.scala.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import cz.kinoscala.scala.Movie;

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
        MoviesDBTable.insert(writableDatabase, movie);
    }

    public List<Movie> getMoviesSince(Date date) {
        return MoviesDBTable.getMoviesSince(readableDatabase, date);
    }
}

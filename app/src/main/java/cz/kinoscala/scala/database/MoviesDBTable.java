package cz.kinoscala.scala.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cz.kinoscala.scala.Movie;

/**
 * Created by petr on 9.5.2015.
 */
public class MoviesDBTable {
    private static final String TABLE_NAME = "movies";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String PRICE = "price";

    public static String getTableName() {
        return TABLE_NAME;
    }

    private static final String CREATE_TABLE_STRING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT NOT NULL, " +
                    DATE + " DATETIME NOT NULL, " +
                    PRICE + " INT NOT NULL)";

    public static void create(SQLiteDatabase database) {
        if (database == null)
            throw new NullPointerException("Database is NULL");

        database.execSQL(CREATE_TABLE_STRING);
    }

    public static void upgrade(SQLiteDatabase database) {
        if (database == null)
            throw new NullPointerException("Database is NULL");

        database.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
        create(database);
    }

    public static long insert(SQLiteDatabase database, Movie movie) {
        if (database == null)
            throw new NullPointerException("Database is NULL");

        if (movie == null)
            throw new NullPointerException("Movie is NULL");

        ContentValues contentValues = new ContentValues();

        contentValues.put(NAME, movie.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        contentValues.put(DATE, dateFormat.format(movie.getDate()));
        contentValues.put(PRICE, movie.getPrice());

        return database.insert(TABLE_NAME, null, contentValues);
    }

    public static List<Movie> getMoviesSince(SQLiteDatabase database, Date date){
        String selection = DATE + " > ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String[] selectionArgs = {
                dateFormat.format(date)
        };

        return getMovies(database, selection, selectionArgs);
    }

    private static List<Movie> getMovies(SQLiteDatabase database, String selection,
                                         String[] selectionArgs) {

        String[] projection = {
            ID,
            NAME,
            DATE,
            PRICE
        };

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Cursor c = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        List<Movie> movies = new LinkedList<>();

        boolean notExit = c.moveToFirst();
        while (notExit) {
            int id = c.getInt(0);
            String name = c.getString(1);
            Date date;
            try {
                date = dateFormat.parse(c.getString(2));

                int price = c.getInt(3);

                Movie movie = new Movie(id, name, date, price);
                movies.add(movie);
            } catch (ParseException e) {
                Log.e("MoviesDBTable", "Incorrect date format. Ignoring line.");
            }

            notExit = c.moveToNext();
        }
        c.close();
        return movies;
    }
}

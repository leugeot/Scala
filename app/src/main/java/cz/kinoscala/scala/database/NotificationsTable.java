package cz.kinoscala.scala.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import cz.kinoscala.scala.Movie;
import cz.kinoscala.scala.MovieNotification;

/**
 * Created by petr on 15.5.2015.
 */
public class NotificationsTable {
    private static final String TABLE_NAME = "notifications";

    private static final String ID = "id";
    private static final String MOVIE_ID = "movie_id";
    public static String getTableName() {
        return TABLE_NAME;
    }

    private static final String CREATE_TABLE_STRING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MOVIE_ID + " INTEGER NOT NULL REFERENCES "+ MoviesTable.getTableName()+"(" + MOVIE_ID + "))";

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

    public static long insert(SQLiteDatabase database, long movieID) {
        if (database == null)
            throw new NullPointerException("Database is NULL");

        ContentValues contentValues = new ContentValues();

        contentValues.put(MOVIE_ID, movieID);

        return database.insert(TABLE_NAME, null, contentValues);
    }

    public static List<MovieNotification> getNotifications(SQLiteDatabase database) {
        int id;
        int movie_id;
        Movie movie;

        String[] projection = {
                ID,
                MOVIE_ID
        };

        Cursor cursor = database.query(TABLE_NAME, projection, null, null, null, null, null);
        List<MovieNotification> notifications = new LinkedList<>();

        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
            movie_id = cursor.getInt(1);

            movie = MoviesTable.getMovie(database, movie_id);
            if (movie != null)
                notifications.add(new MovieNotification(id, movie));
        }

        cursor.close();

        return notifications;
    }
}

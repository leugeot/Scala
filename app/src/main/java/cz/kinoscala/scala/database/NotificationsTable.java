package cz.kinoscala.scala.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by petr on 15.5.2015.
 */
public class NotificationsTable {
    private static final String TABLE_NAME = "notifications";

    private static final String ID = "id";
    private static final String MOVIE_ID = "name";

    public static String getTableName() {
        return TABLE_NAME;
    }

    private static final String CREATE_TABLE_STRING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MOVIE_ID + " FOREIGN KEY NOT NULL)";

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

    public static long insert(SQLiteDatabase database, int movieID) {
        if (database == null)
            throw new NullPointerException("Database is NULL");

        ContentValues contentValues = new ContentValues();

        contentValues.put(MOVIE_ID, movieID);

        return database.insert(TABLE_NAME, null, contentValues);
    }

    public static List<Integer> getNotifications(SQLiteDatabase database) {
        int id;
        String name;

        database.execSQL("SELECT id, name, date, price FROM movies");

        return null;
    }
}

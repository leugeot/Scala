package cz.kinoscala.scala.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by petr on 9.5.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper dbInstance;

    private static final String DATABASE_NAME = "scala_db";
    private static final int DATABASE_VERSION = 1;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DBHelper(context.getApplicationContext());
        }

        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MoviesDBTable.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MoviesDBTable.upgrade(db);
    }
}

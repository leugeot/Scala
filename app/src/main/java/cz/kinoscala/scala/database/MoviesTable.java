package cz.kinoscala.scala.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cz.kinoscala.scala.Movie;

/**
 * Created by petr on 9.5.2015.
 */
public class MoviesTable {
    private static final String TABLE_NAME = "movies";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String PRICE = "price";
    private static final String ORIGINAL_NAME = "original_name";
    private static final String YEAR = "year";
    private static final String RUNTIME = "runtime";
    private static final String COUNTRIES = "countries";
    private static final String LANGUAGES = "languages";
    private static final String PLOT = "plot";
    private static final String CURRENCY = "currency";
    private static final String CSFD_RATING = "csfd_rating";
    private static final String CSFD_ID = "csfd_id";
    private static final String IMDB_RATING = "imdb_rating";
    private static final String IMDB_ID = "imdb_id";
    private static final String IMAGE = "image";
    private static final String YOUTUBE = "youtube";
    private static final String WEBSITE = "website";
    private static final String URL = "url";
    private static final String RESERVATION_URL = "reservation_url";

    public static String getTableName() {
        return TABLE_NAME;
    }

    private static final String CREATE_TABLE_STRING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    ID + " INTEGER PRIMARY KEY NOT NULL, " +
                    NAME + " TEXT NOT NULL, " +
                    DATE + " DATETIME NOT NULL, " +
                    PRICE + " INT NOT NULL, " +
                    ORIGINAL_NAME + " TEXT, " +
                    YEAR + " TEXT, " +
                    RUNTIME + " INTEGER, " +
                    COUNTRIES + " TEXT, " +
                    LANGUAGES + " TEXT, " +
                    PLOT + " TEXT, " +
                    CURRENCY + " TEXT," +
                    CSFD_RATING + " INTEGER, " +
                    CSFD_ID + " TEXT, " +
                    IMDB_RATING + " FLOAT, " +
                    IMDB_ID + " TEXT, " +
                    IMAGE + " TEXT, " +
                    YOUTUBE + " TEXT, " +
                    WEBSITE + " TEXT, " +
                    URL + " TEXT," +
                    RESERVATION_URL + " TEXT)";

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

        contentValues.put(ID, movie.getId());
        contentValues.put(NAME, movie.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        contentValues.put(DATE, dateFormat.format(movie.getDate()));
        contentValues.put(PRICE, movie.getPrice());
        contentValues.put(ORIGINAL_NAME, movie.getOriginalName());
        contentValues.put(YEAR, movie.getYear());
        contentValues.put(RUNTIME, movie.getRuntime());
        contentValues.put(COUNTRIES, movie.getCountries());
        contentValues.put(LANGUAGES, movie.getLanguages());
        contentValues.put(PLOT, movie.getPlot());
        contentValues.put(CURRENCY, movie.getCurrency());
        contentValues.put(CSFD_RATING, movie.getCsfdRating());
        contentValues.put(CSFD_ID, movie.getCsfdID());
        contentValues.put(IMDB_RATING, movie.getImdbRating());
        contentValues.put(IMDB_ID, movie.getImdbID());
        contentValues.put(IMAGE, movie.getImageUrl());
        contentValues.put(YOUTUBE, movie.getYoutubeUrl());
        contentValues.put(WEBSITE, movie.getWebsite());
        contentValues.put(URL, movie.getUrl());
        contentValues.put(RESERVATION_URL, movie.getReservationUrl());

        return database.insert(TABLE_NAME, null, contentValues);
    }

    public static List<Movie> getMoviesSince(SQLiteDatabase database, Date date){
        String selection = DATE + " > ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 2);
        date = c.getTime();

        String[] selectionArgs = {
                dateFormat.format(date)
        };

        List<Movie> movies = getMovies(database, selection, selectionArgs);
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                return m1.getDate().compareTo(m2.getDate());
            }
        });

        return movies;
    }

    public static Movie getMovie(SQLiteDatabase database, int id) {
        String selection = ID + " = ?";

        String[] selectionArgs = {
                Integer.toString(id)
        };

        List<Movie> movies = getMovies(database, selection, selectionArgs);
        if (movies.size() == 0) {
            return null;
        }
        return movies.get(0);
    }

    private static List<Movie> getMovies(SQLiteDatabase database, String selection,
                                         String[] selectionArgs) {

        String[] projection = {
            ID,
            NAME,
            DATE,
            PRICE,
            ORIGINAL_NAME,
            YEAR,
            RUNTIME,
            COUNTRIES,
            LANGUAGES,
            PLOT,
            CURRENCY,
            CSFD_RATING,
            CSFD_ID,
            IMDB_RATING,
            IMDB_ID,
            IMAGE,
            YOUTUBE,
            WEBSITE,
            URL,
            RESERVATION_URL
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

                // first create movie with mandatory data
                Movie movie = new Movie(id, name, date, price);
                movie.setOriginalName(c.getString(4));
                movie.setYear(c.getInt(5));
                movie.setRuntime(c.getInt(6));
                movie.setCountries(c.getString(7));
                movie.setLanguages(c.getString(8));
                movie.setPlot(c.getString(9));
                movie.setCurrency(c.getString(10));
                movie.setCsfdRating(c.getInt(11));
                movie.setCsfdID(c.getString(12));
                movie.setImdbRating(c.getDouble(13));
                movie.setImdbID(c.getString(14));
                movie.setImageUrl(c.getString(15));
                movie.setYoutubeUrl(c.getString(16));
                movie.setWebsite(c.getString(17));
                movie.setUrl(c.getString(18));
                movie.setReservationUrl(c.getString(19));

                movies.add(movie);
            } catch (ParseException e) {
                Log.e("MoviesTable", "Incorrect date format. Ignoring line.");
            }

            notExit = c.moveToNext();
        }
        c.close();
        return movies;
    }

    public static boolean containsMovieId(SQLiteDatabase database, long id){
        String[] selectionArgs = {
                Long.toString(id)
        };
        Cursor cursor = database.rawQuery("SELECT id from " + TABLE_NAME + " where id = ?", selectionArgs);
        boolean hasId = cursor.moveToNext();

        cursor.close();
        return hasId;
    }
}

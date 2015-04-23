package cz.kinoscala.scala;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by petr on 20.4.2015.
 */
public final class MovieParser {
    public static List<Movie> parse(JSONObject jsonObject) {
        if (jsonObject == null) {
            return new LinkedList<>();
        }

        List<Movie> movies = new LinkedList<>();

        try {
            JSONArray moviesJSON = jsonObject.getJSONArray("film");

            for (int i = 0; i < moviesJSON.length(); i++) {
                JSONObject movie = moviesJSON.getJSONObject(i);
                String name = movie.getString("name");
                String dateString = movie.getString("date");
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
                Date date = null;
                try {
                    date = format.parse(dateString);
                } catch (ParseException e) {
                    Log.e("Movie parser", e.toString());
                }

                Long id = movie.getLong("id");
                int price = movie.getInt("price");

                movies.add(new Movie(id, name, date, price));
            }
        } catch (JSONException e) {
            Log.e("MovieParser", "Incorrect JSON file: " + e.toString());
        }


        return movies;
    }
}

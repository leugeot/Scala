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
    public static List<Movie> parseMoviesList(JSONObject jsonObject) {
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

                JSONLoader jsonLoader = new JSONLoader();
                String url = "http://www.kinoscala.cz/1.0/export/description/" + id;
                JSONObject jsonObjectDetail = jsonLoader.getJsonFromUrl(url);

                String imageUrl;
                imageUrl = MovieParser.parseMovieImageUrl(jsonObjectDetail);
                Log.i("parseMovieImageUrl", imageUrl);

                movies.add(new Movie(id, name, date, price, imageUrl));
            }
        } catch (JSONException e) {
            Log.e("MovieParser", "Incorrect JSON file: " + e.toString());
        }

        return movies;
    }

    public static String parseMovieImageUrl(JSONObject movieToParse) {
        if (movieToParse == null) {
            return null;
        }
        try {
            movieToParse = movieToParse.getJSONObject("film");
            return movieToParse.getString("image");
        } catch (JSONException e) {
            Log.e("MovieParser", "Incorrect JSON file: " + e.toString());
        }
        return null;
    }
    public static Movie parseMovieDetail(JSONObject movieToParse) {
        if (movieToParse == null) {
            return null;
        }


        Movie movie = new Movie();

        try {
            movieToParse = movieToParse.getJSONObject("film");

            movie.setName(movieToParse.getString("name"));
            String dateString = movieToParse.getString("date");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
            Date date = null;
            try {
                date = format.parse(dateString);
            } catch (ParseException e) {
                Log.e("Movie parser", e.toString());
                return null;
            }
            movie.setDate(date);
            movie.setPrice(movieToParse.getInt("price"));
            if (movieToParse.has("original"))
                movie.setOriginalName(movieToParse.getString("original"));
            if (movieToParse.has("year"))
                movie.setYear(movieToParse.getInt("year"));
            if (movieToParse.has("runtime"))
                movie.setRuntime(movieToParse.getInt("runtime"));
            if (movieToParse.has("countries"))
                movie.setCountries(movieToParse.getString("countries"));
            if (movieToParse.has("languages"))
                movie.setLanguages(movieToParse.getString("languages"));
            if (movieToParse.has("plot"))
                movie.setPlot(movieToParse.getString("plot"));
            if (movieToParse.has("currency"))
                movie.setCurrency(movieToParse.getString("currency"));
            if (movieToParse.has("csfdRating"))
                movie.setCsfdRating(movieToParse.getInt("csfdRating"));
            if (movieToParse.has("csfdId"))
                movie.setCsfdID(movieToParse.getString("csfdId"));
            if (movieToParse.has("imdbRating"))
                movie.setImdbRating(movieToParse.getDouble("imdbRating"));
            if (movieToParse.has("imdbId"))
                movie.setImdbID(movieToParse.getString("imdbId"));
            if (movieToParse.has("image"))
                movie.setImageUrl(movieToParse.getString("image"));
            if (movieToParse.has("youtube"))
                movie.setYoutubeUrl(movieToParse.getString("youtube"));
            if (movieToParse.has("website"))
                movie.setWebsite(movieToParse.getString("website"));
            if (movieToParse.has("url"))
                movie.setUrl(movieToParse.getString("url"));
            if (movieToParse.has("reservationUrl"))
                movie.setReservationUrl(movieToParse.getString("reservationUrl"));


        } catch (JSONException e) {
            Log.e("MovieParser", "Incorrect JSON file: " + e.toString());
        }

        return movie;
    }
}

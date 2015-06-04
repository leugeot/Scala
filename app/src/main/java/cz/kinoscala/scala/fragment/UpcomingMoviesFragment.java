package cz.kinoscala.scala.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cz.kinoscala.scala.JSONLoader;
import cz.kinoscala.scala.Movie;
import cz.kinoscala.scala.MovieAdapter;
import cz.kinoscala.scala.MovieParser;
import cz.kinoscala.scala.R;
import cz.kinoscala.scala.database.DBManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpcomingMoviesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UpcomingMoviesFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private ListView movieListView;
    ProgressDialog progressDialog;

    // Scala state settings which will be persisted.
    private int moviesLoadDays = 7;
    private Date lastUpdateDate = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()){
            Log.e("wi-fi", "not available");
            Toast.makeText(getActivity(), R.string.noWifi1, Toast.LENGTH_LONG).show();
        }

//        moviesLoadDays = savedInstanceState.getInt("moviesLoadDays");
        moviesLoadDays = 11;

        SharedPreferences settigs = getActivity().getApplicationContext().getSharedPreferences("app_settings", 0);
        String lastUpdateDateString = settigs.getString("lastUpdateDate", null);

        if (lastUpdateDateString != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                lastUpdateDate = dateFormat.parse(lastUpdateDateString);
            } catch (ParseException e) {
                Log.e("mainActivity", "Error when parsing old last update date.");
                lastUpdateDate = null;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upcoming_movies, container, false);
        movieListView = (ListView) v.findViewById(R.id.movie_list);

        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()) {
                    Log.e("wi-fi", "available");
                    Movie movie = (Movie) movieListView.getItemAtPosition(position);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    String fragmentTag = getString(R.string.movie_detail);
                    Fragment selectedFragment = fragmentManager.findFragmentByTag(fragmentTag);

                    if (selectedFragment == null) {
                        selectedFragment = MovieDetailFragment.newInstance(movie);
                        fragmentManager.beginTransaction().addToBackStack(fragmentTag)
                                .replace(R.id.container, selectedFragment, fragmentTag)
                                .commit();
                    }
                } else {
                    Log.e("wi-fi", "not available");
                    Toast.makeText(getActivity(), R.string.noWifi2, Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    private long minuteDifference(Date from, Date to) {
        long difference = to.getTime() - from.getTime();
        return TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onStart() {
        super.onStart();

        Date currentDate = new Date();
        if (lastUpdateDate == null || minuteDifference(lastUpdateDate, currentDate) > 15) {
            new MoviesDownloader().execute();
            Log.i("upcomingmovies", "empty db");
        } else {
            DBManager db = new DBManager(getActivity().getApplicationContext());
            try {
                db.open();

                List<Movie> movies = db.getMoviesSince(new Date());
                db.close();

                updateMovieListView(movies);
                Log.i("upcomingmovies", "set from db");
            } catch (SQLException e) {
                Log.e("upcomingmovies", "ERROR WHILE LOADING MOVIES FROM DB");
                // SHOW SOME KIND OF ERROR
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class MoviesDownloader extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.downloading_movies));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            JSONLoader jsonLoader = new JSONLoader();

            JSONObject jsonObject = jsonLoader.getJsonFromUrl("http://www.kinoscala.cz/1.0/export/showtimes");
            return MovieParser.parseMoviesList(jsonObject);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            DBManager db = new DBManager(getActivity().getApplicationContext());
            try {
                db.open();
                for (Movie movie : movies) {
                    db.insertMovie(movie);
                }
                db.close();
                lastUpdateDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                SharedPreferences settigs = getActivity().getApplicationContext().getSharedPreferences("app_settings", 0);
                SharedPreferences.Editor editor = settigs.edit();
                editor.putString("lastUpdateDate", dateFormat.format(lastUpdateDate));

                editor.apply();

            } catch (SQLException e) {
                Log.e("upcomingmovies", "ERROR WHILE SAVING MOVIES TO DB");
            }

            updateMovieListView(movies);
        }
    }

    private void updateMovieListView(List<Movie> movies) {
        if (movies != null) {
            ListAdapter adapter = new MovieAdapter(getActivity(), 0, movies);
            movieListView.setAdapter(adapter);
        }
    }
}

package cz.kinoscala.scala.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.List;

import cz.kinoscala.scala.JSONLoader;
import cz.kinoscala.scala.Movie;
import cz.kinoscala.scala.MovieAdapter;
import cz.kinoscala.scala.MovieParser;
import cz.kinoscala.scala.R;

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
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        new MoviesDownloader().execute();
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
            progressDialog.setMessage("Downloading movies");
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

            updateMovieListView(movies);
        }
    }

    private void updateMovieListView(List<Movie> movies){
        if (movies != null) {
            ListAdapter adapter = new MovieAdapter(getActivity(), 0, movies);
            movieListView.setAdapter(adapter);
        }
    }
}

package cz.kinoscala.scala.fragment;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.Inflater;

import cz.kinoscala.scala.JSONLoader;
import cz.kinoscala.scala.Movie;
import cz.kinoscala.scala.MovieParser;
import cz.kinoscala.scala.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MOVIE = "movie";

    private Movie movie;
    private ProgressDialog progressDialog;
    private TextView movieDetailName;
    private ImageView movieImage;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie Movie to show.
     * @return A new instance of fragment MovieDetailFragment.
     */
    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable(MOVIE);
        }
    }

    private void downloadAndParseMovieDetail() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Downloading movies");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                JSONLoader jsonLoader = new JSONLoader();

                // JUST TEMPORARY FOR TEST
                long movieID = movie.getId();

                String url = "http://www.kinoscala.cz/1.0/export/description/" + movie.getId();
                JSONObject jsonObject = jsonLoader.getJsonFromUrl(url);
                movie = MovieParser.parseMovieDetail(jsonObject);
                movie.setId(movieID);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                updateMovieDetail();
            }

        }.execute();
    }

    private void updateMovieDetail(){
        if (movie != null) {
//            movieDetailName.setText(movie.getName());

            File imgFile = new File(Environment.getExternalStorageDirectory() +
                    "/KinoScalaImages/" + movie.getId() + ".jpg");
            if (imgFile.exists()) {
                Picasso.with(getActivity().getApplicationContext()).load(imgFile).into(movieImage);
            } else {
                if (movie.getImageUrl() != null || !"".equals(movie.getImageUrl())) {
                    Picasso.with(getActivity().getApplicationContext()).load(movie.getImageUrl()).into(movieImage, new Callback.EmptyCallback());
                    Picasso.with(getActivity().getApplicationContext()).load(movie.getImageUrl())
                            .into(imageTarget);
                } else {
                    movieImage.setImageBitmap(null);
                }
            }

        } else {
            Log.e("Movie detail", "Movie is null");
        }
    }

    private Target imageTarget = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File imageDirectory = new File(Environment.getExternalStorageDirectory() + "/KinoScalaImages");
                    if (!imageDirectory.exists()){
                        imageDirectory.mkdir();
                    }

                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/KinoScalaImages/" + movie.getId() +".jpg");
                    try
                    {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);

//        movieDetailName = (TextView) v.findViewById(R.id.movie_detail_name);
        movieImage = (ImageView) v.findViewById(R.id.movie_detail_image);
        downloadAndParseMovieDetail();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

}
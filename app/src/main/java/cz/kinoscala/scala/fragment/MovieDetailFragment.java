package cz.kinoscala.scala.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import cz.kinoscala.scala.JSONLoader;
import cz.kinoscala.scala.Movie;
import cz.kinoscala.scala.MovieNotificationManager;
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
    private TextView movieDetailYear;
    private TextView movieDetailPlot;
    private TextView movieDetailPrice;
    private TextView movieDetailCurrency;
    private Button movieDetailCsfd;
    private Button movieDetailImdb;
    private ImageView movieImage;

    private OnFragmentInteractionListener mListener;
    private ImageButton ticketReservationUrlButton;
    private ImageButton movieDetailYoutube;
    private ImageButton notifyButton;

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

                progressDialog = new ProgressDialog(getActivity(), R.style.ScalaProgressDialog);
                progressDialog.setMessage(getString(R.string.downloading_movie));
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

    private void updateMovieDetail() {
        if (movie != null) {
            movieDetailName.setText(movie.getName());
            if (movie.getYear() != 0) {
                movieDetailYear.setText(Integer.toString(movie.getYear()));
            }
            movieDetailPlot.setText(movie.getPlot());
            if (movie.getPrice() == 0) {
                movieDetailPrice.setText(getString(R.string.price_not_available));
            } else {
                movieDetailPrice.setText(Integer.toString(movie.getPrice()) + " Kč");
            }
            if (movie.getCsfdRating() == 0) {
                movieDetailCsfd.setText("ČSFD rating: " + Integer.toString(movie.getCsfdRating()) + "%");
            } else {
                movieDetailCsfd.setText("ČSFD rating: " + getString(R.string.no_rating));
            }
            if (movie.getImdbRating() == 0) {
                movieDetailImdb.setText("IMDB rating: " + getString(R.string.no_rating));
            } else {
                movieDetailImdb.setText("IMDB rating: " + Double.toString(movie.getImdbRating()) + "/10");
            }

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
                    if (!imageDirectory.exists()) {
                        imageDirectory.mkdir();
                    }

                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/KinoScalaImages/" + movie.getId() + ".jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();
                    } catch (Exception e) {
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

        movieDetailName = (TextView) v.findViewById(R.id.movie_detail_name);
        movieDetailYear = (TextView) v.findViewById(R.id.movie_detail_year);
        movieImage = (ImageView) v.findViewById(R.id.movie_detail_image);
        movieDetailPlot = (TextView) v.findViewById(R.id.movie_detail_plot);
        movieDetailPrice = (TextView) v.findViewById(R.id.movie_detail_price);
        movieDetailCsfd = (Button) v.findViewById(R.id.movie_detail_csfd_rating);
        movieDetailImdb = (Button) v.findViewById(R.id.movie_detail_imdb_rating);

        ticketReservationUrlButton = (ImageButton) v.findViewById(R.id.movie_detail_reservation_button);
        movieDetailYoutube = (ImageButton) v.findViewById(R.id.movie_detail_trailer_button);
        notifyButton = (ImageButton) v.findViewById(R.id.movie_detail_notify_button);

        addListenerOnButton();

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

    public void addListenerOnButton() {

        if (mListener != null) {
            ticketReservationUrlButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openUrl = new Intent(Intent.ACTION_VIEW);
                    String url = movie.getReservationUrl();
                    openUrl.setData(Uri.parse(url));
                    startActivity(openUrl);
                }
            });
            movieDetailYoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (movie.getYoutubeUrl() != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://youtube.com/watch?v=" + movie.getYoutubeUrl())));
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setMessage(getString(R.string.youtube_not_found));
                        alertDialog.show();
                    }
                }
            });

            notifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieNotificationManager mnm = new MovieNotificationManager(getActivity().getApplicationContext(),
                            (AlarmManager) getActivity().getSystemService(FragmentActivity.ALARM_SERVICE));

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    if (!mnm.hasNotification(movie.getId())) {
                        mnm.addNotification(movie, 2, 8);
                        alertDialog.setMessage(getString(R.string.notification_added));
                    } else {
                        alertDialog.setMessage(getString(R.string.notification_not_added));
                    }
                    alertDialog.show();
                }
            });
            movieDetailImdb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(movie.getImdbID() != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + movie.getImdbID())));
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setMessage(getString(R.string.imdb_not_found));
                        alertDialog.show();
                    }
                }
            });
            movieDetailCsfd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(movie.getCsfdID() != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.csfd.cz/film/" + movie.getCsfdID())));
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setMessage(getString(R.string.csfd_not_found));
                        alertDialog.show();
                    }
                }
            });
        }

    }

}

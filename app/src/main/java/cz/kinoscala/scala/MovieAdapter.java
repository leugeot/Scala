package cz.kinoscala.scala;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by petr on 21.4.2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private Movie movieTarget;

    public MovieAdapter(Activity activity, int resource, List<Movie> movies) {
        super(activity, resource, movies);
    }

    public static class MovieViewHolder {
        public TextView name;
        public TextView date;
        public TextView price;
        public ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MovieViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.movie_list_item,
                    parent, false);

            holder = new MovieViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.image = (ImageView) convertView.findViewById(R.id.movie_item_image);

            convertView.setTag(holder);
        } else {
            holder = (MovieViewHolder) convertView.getTag();
        }
        Movie movie = getItem(position);
        holder.name.setText(movie.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss", Locale.getDefault());
        holder.date.setText(dateFormat.format(movie.getDate()));
        holder.price.setText(Integer.toString(movie.getPrice()) + " Kč");

        File imgFile = new File(Environment.getExternalStorageDirectory() +
                "/KinoScalaImages/" + movie.getId() + ".jpg");
        if (imgFile.exists()) {
            Picasso.with(getContext().getApplicationContext()).load(imgFile).into(holder.image);
        } else {
            if (movie.getImageUrl() != null && !"".equals(movie.getImageUrl())) {
                Picasso.with(getContext().getApplicationContext()).load(movie.getImageUrl()).into(holder.image, new Callback.EmptyCallback());
                movieTarget = movie;
                Picasso.with(getContext().getApplicationContext()).load(movie.getImageUrl())
                        .into(imageTarget);
            } else {
                holder.image.setImageResource(R.drawable.scalalogo);
            }
        }

        return convertView;
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

                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/KinoScalaImages/" + movieTarget.getId() + ".jpg");
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

}

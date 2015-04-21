package cz.kinoscala.scala;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by petr on 21.4.2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Activity activity, int resource, List<Movie> movies) {
        super(activity, resource, movies);
    }

    public static class MovieViewHolder {
        public TextView name;
        public TextView date;
        public TextView price;
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

            convertView.setTag(holder);
        } else {
            holder = (MovieViewHolder) convertView.getTag();
        }
        Movie movie = getItem(position);
        holder.name.setText(movie.getName());
        holder.date.setText(movie.getDate().toString());
        holder.price.setText(Integer.toString(movie.getPrice()));

        return convertView;
    }
}

package cz.kinoscala.scala;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by petr on 29. 5. 2015.
 */
public class NotificationAdapter extends ArrayAdapter<MovieNotification> {
    private List<MovieNotification> notifications;

    public NotificationAdapter(Activity activity, int resource,
                               List<MovieNotification> notifications) {
        super(activity, resource, notifications);
        this.notifications = notifications;
    }

    public static class NotificationViewHolder {
        public MovieNotification movieNotification;
        public TextView name;
        public TextView date;
        public ImageButton removeNotificationButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NotificationViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(
                    R.layout.notification_list_item, parent, false);

            holder = new NotificationViewHolder();

            holder.movieNotification = notifications.get(position);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.removeNotificationButton = (ImageButton) convertView.findViewById(R.id.remove_notification_button);
            holder.removeNotificationButton.setTag(holder.movieNotification);

            convertView.setTag(holder);
        } else {
            holder = (NotificationViewHolder) convertView.getTag();
        }
        MovieNotification notification = getItem(position);
        holder.name.setText(notification.getMovie().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss", Locale.getDefault());
        holder.date.setText(dateFormat.format(notification.getMovie().getDate()));

        return convertView;
    }


}

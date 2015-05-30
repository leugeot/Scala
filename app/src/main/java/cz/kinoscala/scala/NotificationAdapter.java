package cz.kinoscala.scala;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by petr on 29. 5. 2015.
 */
public class NotificationAdapter extends ArrayAdapter<MovieNotification> {

    public NotificationAdapter(Activity activity, int resource,
                               List<MovieNotification> notifications) {
        super(activity, resource, notifications);
    }

    public static class NotificationViewHolder {
        public TextView name;
        public TextView date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NotificationViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(
                    R.layout.notification_list_item, parent, false);

            holder = new NotificationViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);

            convertView.setTag(holder);
        } else {
            holder = (NotificationViewHolder) convertView.getTag();
        }
        MovieNotification notification = getItem(position);
        holder.name.setText(notification.getMovie().getName());
        holder.date.setText(notification.getMovie().getDate().toString());

        return convertView;
    }


}

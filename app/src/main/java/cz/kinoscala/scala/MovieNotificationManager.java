package cz.kinoscala.scala;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import cz.kinoscala.scala.activity.MainActivity;
import cz.kinoscala.scala.database.DBManager;
import cz.kinoscala.scala.notification.MovieNotificationReceiver;

/**
 * Created by petr on 3. 6. 2015.
 */
public class MovieNotificationManager {
    AlarmManager alarmManager;
    Context context;

    public MovieNotificationManager(Context context ,AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
        this.context = context;
    }

    public void addNotification(Movie movie, int shortNotificationHour, int longNotificationHour) {
        Intent intent = new Intent(context, MovieNotificationReceiver.class);
        intent.putExtra("NAME", movie.getName());
        intent.putExtra("DESCRIPTION", movie.getDate().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(new Date());

        Calendar longNotification = Calendar.getInstance();
        longNotification.setTime(movie.getDate());
        longNotification.set(Calendar.HOUR_OF_DAY, longNotificationHour);
        longNotification.set(Calendar.MINUTE, 0);
        longNotification.set(Calendar.SECOND, 0);
        longNotification.set(Calendar.AM_PM, Calendar.AM);

        if (longNotification.compareTo(actualCalendar) > 0) {
            alarmManager.set(AlarmManager.RTC, longNotification.getTimeInMillis(), pendingIntent);
        }

        Calendar shortNotification = Calendar.getInstance();
        shortNotification.setTime(movie.getDate());
        shortNotification.add(Calendar.HOUR_OF_DAY, -shortNotificationHour);
        shortNotification.set(Calendar.MINUTE, 0);
        shortNotification.set(Calendar.SECOND, 0);

        if (shortNotification.compareTo(actualCalendar) > 0) {
            alarmManager.set(AlarmManager.RTC, shortNotification.getTimeInMillis(), pendingIntent);
        }

        DBManager dbManager = new DBManager(context);

        try {
            dbManager.open();
            int id = dbManager.insertNotification(movie.getId());
            dbManager.close();
            intent.putExtra("NOTIFICATION_ID", id);
        } catch (SQLException e) {
            // Better show error somehow
            Log.e("MainActivity", e.toString());
        }
    }

    public void removeNotification(long movieId, long notificationId) {
        DBManager dbManager = new DBManager(context);

        try {
            dbManager.open();
            dbManager.removeNotification(movieId);
            dbManager.close();
            Intent intent = new Intent(context, MovieNotificationReceiver.class);
            intent.putExtra("NOTIFICATION_ID", notificationId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmManager.cancel(pendingIntent);
        } catch (SQLException e) {
            // Better show error somehow
            Log.e("MainActivity", e.toString());
        }
    }

    public boolean hasNotification(long movieId) {
        DBManager dbManager = new DBManager(context);

        boolean hasMovieId = false;
        try {
            dbManager.open();
            hasMovieId = dbManager.hasNotification(movieId);
            dbManager.close();
        } catch (SQLException e) {
            // Better show error somehow
            Log.e("MainActivity", e.toString());
        }
        return hasMovieId;
    }
}

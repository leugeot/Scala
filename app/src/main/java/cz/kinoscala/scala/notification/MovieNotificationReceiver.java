package cz.kinoscala.scala.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import cz.kinoscala.scala.R;
import cz.kinoscala.scala.activity.MainActivity;

/**
 * Created by Vladimira Hezelova on 23. 4. 2015.
 */
public class MovieNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent newIntent = new Intent(context, MainActivity.class);
//        context.startService(service);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.scalalogo)
                        .setContentTitle(intent.getStringExtra("NAME"))
                        .setContentText(intent.getStringExtra("DESCRIPTION"));


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        newIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        int mNotificationId = intent.getIntExtra("NOTIFICATION_ID", -1);

        NotificationManager mManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mManager.notify(mNotificationId, mBuilder.build());
    }
}
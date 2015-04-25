package cz.kinoscala.scala.notification;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cz.kinoscala.scala.R;
import cz.kinoscala.scala.activity.MainActivity;


/**
 * Created by Vladimira Hezelova on 23. 4. 2015.
 */
public class MovieNotificationService extends Service

{
    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public int onStartCommand(Intent intent,int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        Log.d("onStartCommand", String.valueOf(intent));

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.scalalogo)
                        .setContentTitle("Kino Scala")
                        .setContentText("nazov filmu");

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        int mNotificationId = 001;

        NotificationManager mManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mManager.notify(mNotificationId, mBuilder.build());
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
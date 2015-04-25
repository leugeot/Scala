package cz.kinoscala.scala.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Vladimira Hezelova on 23. 4. 2015.
 */
public class MovieNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service = new Intent(context, MovieNotificationService.class);
        context.startService(service);

    }
}
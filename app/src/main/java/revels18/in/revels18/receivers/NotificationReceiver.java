package revels18.in.revels18.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import revels18.in.revels18.R;
import revels18.in.revels18.activities.MainActivity;

/**
 * Created by Saptarshi on 12/6/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private final String NOTIFICATION_TITLE="Upcoming Event";
    private String notificationText="";
    private final String LAUNCH_APPLICATION="Launch Revels'18";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent!=null){
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
            String eventName = intent.getStringExtra("eventName");
            notificationText = eventName+" at "+intent.getStringExtra("startTime")+", "+intent.getStringExtra("eventVenue");
            String catName = intent.getStringExtra("catName");
            Notification notify = new NotificationCompat.Builder(context)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(notificationText)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .addAction(new android.support.v4.app.NotificationCompat.Action(0, LAUNCH_APPLICATION, pendingIntent))
                    .build();

            notificationManager.notify(Integer.parseInt(intent.getStringExtra("eventID")), notify);
        }

    }
}

package com.owlsoft.kotlabtimer

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

/**
 * Created by mac on 29.02.16.
 */

class AlarmReceiver :BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        var theme: String? = p1?.getStringExtra("theme") ?: return
        println("theme:${theme}")
        showNotification(p0,theme)

    }

    private fun showNotification(context:Context?,theme:String?) {
        var contentIntent:PendingIntent = PendingIntent.getActivity(context, 0,
        Intent(context, AlarmReceiver::class.java), 0);

        var mBuilder:NotificationCompat.Builder =
        NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Deadline is coming(hour is left)")
                .setContentText(theme);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        var mNotificationManager:NotificationManager =
        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        mNotificationManager.notify(1, mBuilder.build());
    }
}
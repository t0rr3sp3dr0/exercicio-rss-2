package br.ufpe.cin.if710.rss

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

const val CHANNEL_ID = "channelId"

class MyReceiver : BroadcastReceiver() {

    // On API level 26 or higher, you cannot use the manifest to declare a
    // receiver for implicit broadcasts. So I could not test if this works.
    override fun onReceive(context: Context, intent: Intent) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setContentTitle(intent.getStringExtra("title"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(intent.getIntExtra("id", 0), mBuilder.build())
        }
    }
}

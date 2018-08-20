package com.gainwise.breakthru

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.widget.RemoteViews




class BreakThru : Service() {


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(893, getNotification())
        if(!intent!!.hasExtra("end")) {


         defaultRingtone = RingtoneManager.getRingtone(this,
                Settings.System.DEFAULT_RINGTONE_URI)
        //fetch current Ringtone
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, max, 0)


            defaultRingtone?.play()


        }else{

            defaultRingtone?.stop()
            stopForeground(true)
            stopSelf()
            onDestroy()

        }



        return START_NOT_STICKY
    }

    private fun getNotification(): Notification {
        val contentView = RemoteViews("com.gainwise.breakthru", R.layout.custom_notification)
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
        contentView.setTextViewText(R.id.notificationtext, "Breaking Thru! Tap to Stop!")
        val builder = Notification.Builder(this)
        builder.setSmallIcon(R.drawable.notification_icon_background)
        val intent2 = Intent(this, FinishHim::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 510001, intent2, 0)



        builder.setContentIntent(pendingIntent)

        builder.setContent(contentView)

        builder.setContentTitle("Breaking-Thru! Tap to Stop!")


        builder.setAutoCancel(true)
        builder.setPriority(Notification.PRIORITY_MAX)
        builder.setDefaults(Notification.DEFAULT_VIBRATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "default use"
            val description = "Status Updates"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("ChannelID", name, importance)
            mChannel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = this.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
            builder.setChannelId("ChannelID")
        }

        return builder.build()
    }
companion object {
    var defaultRingtone: Ringtone? = null

}

}

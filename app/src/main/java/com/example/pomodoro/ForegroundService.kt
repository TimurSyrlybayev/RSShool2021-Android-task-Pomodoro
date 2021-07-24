package com.example.pomodoro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ForegroundService : Service() {

    private var isOn = false
    private var notification: NotificationManager? = null
    private var job: Job? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, "Channel_id")
            .setContentTitle("Timer Pomodoro")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_alarm_24)
    }

    override fun onCreate() {
        super.onCreate()
        notification = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private fun handleCommand(intent: Intent?) {
        when (intent?.extras?.getString("COMMAND_ID") ?: "INVALID") {
            "COMMAND_START" -> {
                val startTime = intent?.extras?.getLong("STARTED_TIMER_TIME") ?: return
                commandStart(startTime)
            }
            "COMMAND_STOP" -> commandStop()
            "INVALID" -> return
        }
    }

    private fun commandStart(startTime: Long) {
        if (isOn) {
            return
        }
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer(startTime)
        } finally {
            isOn = true
        }
    }

    private fun continueTimer(startTime: Long) {
        job = GlobalScope.launch(Dispatchers.Main) {
            while(true) {
                notification?.notify(
                    777,
                    getNotification(calculateTime(System.currentTimeMillis() - startTime))
                )
                delay(1000L)
            }
        }
    }

    private fun commandStop() {
        if (!isOn) {
            return
        }
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isOn = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(777, notification)
    }

    private fun getNotification(content: String) = builder.setContentText(content).build()

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                "Channel_id", channelName, importance
            )
            notification?.createNotificationChannel(notificationChannel)
        }
    }

    private fun calculateTime(time: Long): String {
        val hours = time / 3600
        val minutes = time % 3600 / 60
        val seconds = time % 60
        return "${if(hours > 9) hours else "0$hours"}:" +
                "${if(minutes > 9) minutes else "0$minutes"}:" +
                "${if(seconds > 9) seconds else "0$seconds"}"
    }
}
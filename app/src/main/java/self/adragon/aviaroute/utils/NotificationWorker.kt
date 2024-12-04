package self.adragon.aviaroute.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
//import androidx.work.Worker
//import androidx.work.WorkerParameters
import self.adragon.aviaroute.R

//class NotificationWorker(
//    private val context: Context,
//    workerParams: WorkerParameters
//)
//: Worker(context, workerParams) {
//
//    override fun doWork(): Result {
//        val notificationTitle = inputData.getString("title") ?: "Upcoming Event"
//        val notificationText = inputData.getString("text") ?: "An event is about to start."
//        val notificationId = inputData.getInt("notificationId", 0)
//
//        sendNotification(notificationTitle, notificationText, notificationId)
//
//        return Result.success()
//    }
//
//    private fun sendNotification(title: String, text: String, notificationId: Int) {
//        val notificationBuilder = NotificationCompat.Builder(applicationContext, "Channel ID")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle(title)
//            .setContentText(text)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//
//        val channel = NotificationChannel(
//            "Channel ID",
//            "Default Channel",
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "Channel for event notifications"
//        }
//
//        val notificationManager =
//            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) return
//
//        NotificationManagerCompat.from(applicationContext)
//            .notify(notificationId, notificationBuilder.build())
//    }
//}

package pl.akac.android.todo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import java.util.concurrent.ThreadLocalRandom

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "pl.akac.android.todo-channel-v1"

    private val args: MainActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SystemClock.sleep(500)

        createNotificationChannel()
        val notification = createNotification()

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(ThreadLocalRandom.current().nextInt(), notification)
        }
    }

    override fun onNewIntent(intent2: Intent?) {
        super.onNewIntent(intent2)
        val args2 = MainActivityArgs.fromBundle(intent2!!.extras)

            args2.URI?.let {
                val findNavController = findNavController(R.id.nav_host_fragment)
                val uri = it.toUri()
             //   if(findNavController.currentDestination?.hasDeepLink(uri) == true){

                    findNavController.navigate(uri)
            }

//        intent2?.run {
//            findNavController(R.id.nav_host_fragment).handleDeepLink(this)
//        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TODO app channel - v1"
            val descriptionText = "TODO notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
//
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }

        val arg1 = ThreadLocalRandom.current().nextInt()
        val arg2 = ThreadLocalRandom.current().nextInt()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("URI" ,"todoapp://bfragment/$arg1/$arg2")
//            data = "todoapp://bfragment/$arg1/$arg2".toUri()
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            ThreadLocalRandom.current().nextInt(),
            intent,
            0
        ) // or FLAG_UPDATE_CURRENT ?

        val pendingIntentFromNavBuilder = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.BFragment)
            .createPendingIntent()


        val taskStackBuilder = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.BFragment)
            .createTaskStackBuilder()
//
//        taskStackBuilder.editIntentAt(0)?.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//
//        val pendinIntentFromTaskStackBuilder =
//            taskStackBuilder.getPendingIntent( ThreadLocalRandom.current().nextInt(), PendingIntent.FLAG_CANCEL_CURRENT)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("TODO App notification")
            .setContentText("TODO notification content")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Big text - TODO app")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return builder.build()
    }
}

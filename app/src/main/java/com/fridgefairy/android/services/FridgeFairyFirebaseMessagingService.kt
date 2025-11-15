package com.fridgefairy.android.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fridgefairy.android.R
import com.fridgefairy.android.ui.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging Service
 * MANDATORY FEATURE: Push Notifications
 *
 * Handles:
 * - Expiring item notifications (Priority Channel)
 * - General notifications (General Channel)
 * - FCM token updates
 */
class FridgeFairyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"

        // Notification Channels
        private const val CHANNEL_EXPIRY_ID = "expiring_items"
        private const val CHANNEL_EXPIRY_NAME = "Expiring Items"
        private const val CHANNEL_EXPIRY_DESC = "Notifications for items about to expire"

        private const val CHANNEL_GENERAL_ID = "general"
        private const val CHANNEL_GENERAL_NAME = "General Notifications"
        private const val CHANNEL_GENERAL_DESC = "General app notifications"

        // Notification IDs
        private const val NOTIFICATION_ID_EXPIRY = 100
        private const val NOTIFICATION_ID_GENERAL = 200
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    /**
     * Called when a new FCM token is generated
     * Save this token to Firestore to send targeted notifications
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // TODO: Send token to your app server or save to Firestore
        sendTokenToServer(token)
    }

    /**
     * Called when a message is received
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message received from: ${message.from}")

        // Check if message contains data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${message.data}")
            handleDataMessage(message.data)
        }

        // Check if message contains notification payload
        message.notification?.let {
            Log.d(TAG, "Message notification: ${it.body}")
            sendNotification(
                title = it.title ?: "FridgeFairy",
                body = it.body ?: "",
                type = message.data["type"] ?: "general"
            )
        }
    }

    /**
     * Handle data messages (background & foreground)
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: "general"
        val title = data["title"] ?: "FridgeFairy"
        val body = data["body"] ?: ""
        val itemId = data["itemId"]

        when (type) {
            "expiring_item" -> {
                sendNotification(
                    title = title,
                    body = body,
                    type = "expiring_item",
                    itemId = itemId
                )
            }
            "low_stock" -> {
                sendNotification(
                    title = title,
                    body = body,
                    type = "general"
                )
            }
            "recipe_suggestion" -> {
                sendNotification(
                    title = title,
                    body = body,
                    type = "general"
                )
            }
            else -> {
                sendNotification(
                    title = title,
                    body = body,
                    type = "general"
                )
            }
        }
    }

    /**
     * Create notification channels for Android O and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Priority channel for expiring items
            val expiryChannel = NotificationChannel(
                CHANNEL_EXPIRY_ID,
                CHANNEL_EXPIRY_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_EXPIRY_DESC
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // General channel for other notifications
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL_ID,
                CHANNEL_GENERAL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_GENERAL_DESC
                enableLights(true)
                enableVibration(false)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(expiryChannel)
            notificationManager.createNotificationChannel(generalChannel)

            Log.d(TAG, "Notification channels created")
        }
    }

    /**
     * Show notification to user
     */
    private fun sendNotification(
        title: String,
        body: String,
        type: String,
        itemId: String? = null
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (itemId != null) {
                putExtra("itemId", itemId)
                putExtra("notificationType", type)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Choose channel based on notification type
        val channelId = if (type == "expiring_item") {
            CHANNEL_EXPIRY_ID
        } else {
            CHANNEL_GENERAL_ID
        }

        val notificationId = if (type == "expiring_item") {
            NOTIFICATION_ID_EXPIRY
        } else {
            NOTIFICATION_ID_GENERAL
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_fridge) // Use your app icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(
                if (type == "expiring_item")
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )

        // Add action buttons for expiring items
        if (type == "expiring_item" && itemId != null) {
            val consumeIntent = Intent(this, MainActivity::class.java).apply {
                action = "ACTION_CONSUME_ITEM"
                putExtra("itemId", itemId)
            }
            val consumePendingIntent = PendingIntent.getActivity(
                this,
                1,
                consumeIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder.addAction(
                R.drawable.ic_fridge,
                "Mark as Used",
                consumePendingIntent
            )
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())

        Log.d(TAG, "Notification sent: $title")
    }

    /**
     * Send FCM token to server/Firestore
     */
    private fun sendTokenToServer(token: String) {
        try {
            // Save token to SharedPreferences
            val prefs = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("fcm_token", token).apply()

            // TODO: Upload token to Firestore for the current user
            // Example:
            // val userId = FirebaseAuth.getInstance().currentUser?.uid
            // if (userId != null) {
            //     FirebaseFirestore.getInstance()
            //         .collection("users")
            //         .document(userId)
            //         .update("fcmToken", token)
            // }

            Log.d(TAG, "FCM token saved locally")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving FCM token", e)
        }
    }

    /**
     * Get current FCM token
     */
    fun getCurrentToken(context: Context): String? {
        val prefs = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        return prefs.getString("fcm_token", null)
    }
}
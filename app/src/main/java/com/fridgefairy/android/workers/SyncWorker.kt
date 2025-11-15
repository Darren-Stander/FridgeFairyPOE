package com.fridgefairy.android.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker for periodic data synchronization
 * MANDATORY FEATURE: Offline Mode with Sync
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                Log.w(TAG, "User not logged in, skipping sync")
                return Result.success()
            }

            Log.d(TAG, "Starting sync for user: $userId")

            // TODO: Implement actual sync logic here
            // This is where you would sync Room DB with Firestore
            // Example:
            // - Get all local food items
            // - Upload new/modified items to Firestore
            // - Download items from Firestore
            // - Merge changes

            Log.d(TAG, "Sync completed successfully")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        private const val SYNC_WORK_NAME = "FridgeFairySyncWork"
        private const val ONE_TIME_SYNC_NAME = "FridgeFairyOneTimeSync"

        /**
         * Schedule periodic sync work (every 15 minutes)
         */
        fun scheduleSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    // *** FIX IS HERE: Changed from PeriodicWorkRequest to WorkRequest ***
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )

            Log.d(TAG, "Sync worker scheduled successfully")
        }

        /**
         * Trigger immediate one-time sync
         */
        fun syncNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_SYNC_NAME,
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )

            Log.d(TAG, "One-time sync triggered")
        }

        /**
         * Cancel all sync work
         */
        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_SYNC_NAME)
            Log.d(TAG, "Sync worker cancelled")
        }
    }
}
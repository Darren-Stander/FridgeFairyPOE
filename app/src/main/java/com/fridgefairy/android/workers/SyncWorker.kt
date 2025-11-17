//  defines a CoroutineWorker for handling background data synchronization.
// contains the logic for the periodic sync (e.g., syncing Room with Firestore)
// provides companion object methods to schedule, trigger, and cancel the sync.


package com.fridgefairy.android.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit
import com.fridgefairy.android.data.FridgeFairyDatabase
import com.fridgefairy.android.data.entities.FoodItem
import com.fridgefairy.android.data.repository.FoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Log.w(TAG, "User not logged in, skipping sync")
            return Result.success()
        }

        Log.d(TAG, "Starting sync for user: $userId")

        return try {

            val db = FirebaseFirestore.getInstance()
            val dao = FridgeFairyDatabase.getDatabase(applicationContext).foodDao()
            val repository = FoodRepository(dao)




            val localItems = repository.getAllFoodItemsList()
            Log.d(TAG, "Found ${localItems.size} items locally.")


            val batch = db.batch()
            val collectionRef = db.collection("users").document(userId).collection("food_items")

            for (item in localItems) {
                val docRef = collectionRef.document(item.id)
                batch.set(docRef, item)
            }


            batch.commit().await()
            Log.d(TAG, "Successfully uploaded ${localItems.size} items to Firestore.")


            val snapshot = collectionRef.get().await()
            val remoteItems = snapshot.toObjects(FoodItem::class.java)
            Log.d(TAG, "Downloaded ${remoteItems.size} items from Firestore.")


            for (item in remoteItems) {
                dao.insert(item)
            }
            Log.d(TAG, "Local database synced with remote data.")



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


        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_SYNC_NAME)
            Log.d(TAG, "Sync worker cancelled")
        }
    }
}
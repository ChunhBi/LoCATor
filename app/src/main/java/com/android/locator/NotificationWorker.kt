package com.android.locator

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    var catIds:List<String> = mutableListOf()

    @SuppressLint("SuspiciousIndentation")
    override fun doWork(): Result {
        sendTestNotification()
        val tmp = inputData.getStringArray("catIds") ?: return Result.success()
        if (tmp != null) {
            catIds = tmp.filterNotNull()
        }
        Log.d("NOTIF","the first id "+catIds[0])
        // Use catIds to query Firestore for new witnesses

        val lastCheckedTimestamp = TimestampManager.getLastCheckedTimestamp(applicationContext)
        Log.d("NOTIF","last timestamp: ${lastCheckedTimestamp}")


        // Example: Query Firestore for new witness documents related to catIds
        for (catId in catIds) {
            val witnessRef = FirebaseFirestore.getInstance().collection("witness")
                .whereEqualTo("cat", catId)
                //.whereGreaterThan("time", longToTimestamp(lastCheckedTimestamp))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("NOTIF", "Checking witness...")
                        val witData = document.data
                        val name = witData["cat"] as? String ?: "Unknown"
                        val time = witData["time"] as Timestamp
                        // Handle new witness document
                        // Send notification if necessary
                        if(compareTimestamps(time, longToTimestamp(lastCheckedTimestamp))==1)
                        sendNotification(name, time)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("NOTIF", "Failed to load witnesses.")
                }
        }


        val currentTimestamp = System.currentTimeMillis()
        Log.d("NOTIF","new timestamp: ${currentTimestamp}")
        TimestampManager.saveLastCheckedTimestamp(applicationContext, currentTimestamp)


        return Result.success()
    }
    private fun sendTestNotification() {
        Log.d("NOTIF","test")
    }
    private fun sendNotification(catName:String, time:Timestamp) {
        Log.d("NOTIF","${catName} witnessed at ${timestampToTimeString(time)}.")
    }

    fun compareTimestamps(timestamp1: Timestamp, timestamp2: Timestamp): Int {
        val millis1 = timestamp1.seconds * 1000 + timestamp1.nanoseconds / 1000000
        val millis2 = timestamp2.seconds * 1000 + timestamp2.nanoseconds / 1000000

        return when {
            millis1 > millis2 -> 1
            millis1 < millis2 -> -1
            else -> 0
        }
    }
    fun longToTimestamp(milliseconds: Long): Timestamp {
        // Divide milliseconds by 1000 to get seconds and extract the remainder for nanoseconds
        val seconds = milliseconds / 1000
        val nanoseconds = (milliseconds % 1000) * 1000000

        return Timestamp(seconds, nanoseconds.toInt())
    }

    fun timestampToTimeString(timestamp: Timestamp): String {
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(milliseconds))
    }
}

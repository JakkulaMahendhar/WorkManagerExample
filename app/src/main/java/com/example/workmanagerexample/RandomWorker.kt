package com.example.workmanagerexample

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RandomWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://www.randomnumberapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            val response = apiService.getRandomNumbers(10, 100, 1)

            if (response.isNotEmpty()) {
                Log.d("MyWorker", "Random Number: ${response[0]}")
                enqueueRandomWork(applicationContext, true)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

fun enqueueRandomWork(appContext: Context, shouldStartPolling: Boolean = false) {
    val workRequest = OneTimeWorkRequestBuilder<RandomWorker>()
        .addTag("RANDOMWORKER")
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10000L, TimeUnit.MILLISECONDS)

        if(shouldStartPolling) {
            workRequest.setInitialDelay(
                10000L,
                TimeUnit.MILLISECONDS
            ) // Initial delay of 10 seconds
        }
    cancelWorker(appContext)
    WorkManager.getInstance(appContext).enqueue(workRequest.build())
}

fun cancelWorker(appContext: Context) {
    WorkManager.getInstance(appContext).cancelAllWorkByTag("RANDOMWORKER")
}
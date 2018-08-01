package com.android.nataraj.mytodo.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(private val diskIO: Executor, private val networkIO: Executor, private val mainThread: Executor) {

    // To run disk read & write operations off the UI thread
    fun diskIO(): Executor = diskIO

    // To runOnUiThread
    fun mainThread(): Executor = mainThread

    // To make network calls off the UI thread
    fun networkIO(): Executor = networkIO

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        // For Singleton instantiation
        private val mInstance: AppExecutors =
                AppExecutors(Executors.newSingleThreadExecutor(),
                    Executors.newFixedThreadPool(3),
                    MainThreadExecutor())

        @Synchronized
        fun getInstance(): AppExecutors {
            return mInstance
        }
    }
}
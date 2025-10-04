package com.raylabs.doggie.utils

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class AppExecutors @VisibleForTesting constructor(
    val diskIO: Executor,
    val networkIO: Executor,
    val mainThread: Executor
) {

    fun diskIO(): Executor = diskIO
    fun networkIO(): Executor = networkIO
    fun mainThread(): Executor = mainThread

    constructor() : this(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(THREAD_COUNT),
        MainThreadExecutor()
    )

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        private const val THREAD_COUNT = 3
    }
}

package org.nullgroup.lados

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LadosApplication: Application(), Configuration.Provider {

    // For WorkerManager: https://developer.android.com/training/dependency-injection/hilt-jetpack?source=post_page-----b60046ff7f02--------------------------------#workmanager
    @Inject lateinit var workerFactory: HiltWorkerFactory
//    override fun getWorkManagerConfiguration() =
//        Configuration.Builder()
//            .setWorkerFactory(workerFactory)
//            .build()

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
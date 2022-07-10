package com.doops.themealkotlin

import android.app.Application
import com.doops.themealkotlin.data.remote.MealApi
import com.doops.themealkotlin.data.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@HiltAndroidApp
class MealApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMealApi(
        remoteDataSource: RemoteDataSource
    ): MealApi {
        return remoteDataSource.buildApi(MealApi::class.java)
    }

}
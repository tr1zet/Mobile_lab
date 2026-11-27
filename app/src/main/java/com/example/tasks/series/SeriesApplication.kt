package com.example.tasks.series

import android.app.Application
import androidx.room.Room
import com.example.tasks.series.data.OfflineFirstSeriesRepository
import com.example.tasks.series.data.SeriesRepository
import com.example.tasks.series.data.local.SeriesDatabase
import com.example.tasks.series.data.remote.TvmazeApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SeriesApplication : Application() {
    val repository: SeriesRepository by lazy {
        val database = Room.databaseBuilder(this, SeriesDatabase::class.java, "series.db").build()
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().header("User-Agent", "Serialoscope-Android/1.0 (semester project)").build())
            }.build()
        val api = Retrofit.Builder().baseUrl("https://api.tvmaze.com/")
            .client(client).addConverterFactory(GsonConverterFactory.create()).build()
            .create(TvmazeApi::class.java)
        OfflineFirstSeriesRepository(database, api)
    }
}

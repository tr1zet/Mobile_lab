package com.example.rickandmorty

import android.app.Application
import com.example.rickandmorty.data.CharacterRepository
import com.example.rickandmorty.data.NetworkCharacterRepository
import com.example.rickandmorty.network.RickAndMortyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RickAndMortyApplication : Application() {
    val appContainer: AppContainer by lazy { DefaultAppContainer() }
}

interface AppContainer {
    val characterRepository: CharacterRepository
}

class DefaultAppContainer : AppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://rickandmortyapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: RickAndMortyApi = retrofit.create(RickAndMortyApi::class.java)

    override val characterRepository: CharacterRepository by lazy {
        NetworkCharacterRepository(api)
    }
}

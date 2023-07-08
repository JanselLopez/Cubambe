package com.jansellopez.cubambe.core.di

import com.jansellopez.cubambe.data.network.SongsApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit():Retrofit = Retrofit.Builder()
        .baseUrl("https://script.google.com/macros/s/AKfycbxJpWaGdgh88QMM9zwi9pNVKs1TbRJRIx3OPAfm7PvDWJQF9x0LKjvGhtDwqtDvm7E8CQ/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideJokesDao(retrofit: Retrofit):SongsApiClient= retrofit.create(SongsApiClient::class.java)
}
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
        .baseUrl("https://script.google.com/macros/s/AKfycbzl8zSgSuoiAd8Wr99QXnMMsDsO4MlgWq66sKnie7AAWrXMR8ceTa-ptm6cuGxzawNKLA/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideJokesDao(retrofit: Retrofit):SongsApiClient= retrofit.create(SongsApiClient::class.java)
}
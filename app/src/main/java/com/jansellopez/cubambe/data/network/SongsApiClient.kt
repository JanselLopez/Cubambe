package com.jansellopez.cubambe.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface SongsApiClient {
    @GET("exec")
    suspend fun getSongs(
        @Query("spreadsheetId") spreadsheetId:String = "1vC1UUTxuKOql35v6OeWyWjWRhmRtNJGunEVyvNwqc1g",
        @Query("sheet") sheet:String = "songs"
    ):Response<ResponseNetwork>
}
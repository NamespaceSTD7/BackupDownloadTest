package ru.softc.evotor.backupdownloadtest

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ServerAPI {

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

    companion object {
        val BASE_URL = "http://debt.evotor.tech/api/device/"

        val GSON = GsonBuilder().create()

        private val okHttpClient = OkHttpClient.Builder()
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(200, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build();

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .build()

        val API = retrofit.create(ServerAPI::class.java)
    }
}
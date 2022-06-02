package com.immotor.albert.mvinet.net.download

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * retrofit的service
 */
interface DownloadService {
    @GET
    @Streaming
    suspend fun download(@Url url: String, @HeaderMap headers: Map<String, String>) : Response<ResponseBody>
}
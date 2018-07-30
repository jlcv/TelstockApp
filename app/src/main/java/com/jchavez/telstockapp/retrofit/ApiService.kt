package com.jchavez.telstockapp.retrofit

import com.jchavez.telstockapp.models.PhotoItem
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("photos")
    fun photos(): Observable<List<PhotoItem>>

    @POST("photos")
    fun create(@Body photoItem: PhotoItem): Observable<PhotoItem>

    @PUT("photos/{photoId}")
    fun update(@Path("photoId") photoId: Int, @Body photoItem: PhotoItem): Observable<PhotoItem>

    @DELETE("photos/{photoId}")
    fun destroy(@Path("photoId") photoId: Int): Observable<PhotoItem>

    companion object Factory {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://jsonplaceholder.typicode.com/")
                    .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
package com.example.yandexmobiletest.PolygonAPIWorker.common

import com.example.yandexmobiletest.PolygonAPIWorker.RetrofitServices
import com.example.yandexmobiletest.PolygonAPIWorker.RetrofitClient

object Common {
    private val BASE_URL = "https://api.polygon.io/"

    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}
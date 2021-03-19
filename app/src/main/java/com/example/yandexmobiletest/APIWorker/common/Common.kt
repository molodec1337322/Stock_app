package com.example.yandexmobiletest.APIWorker.common

import com.example.yandexmobiletest.APIWorker.RetrofitServices
import com.example.yandexmobiletest.APIWorker.RetrofitClient

object Common {
    private val BASE_URL = "https://finnhub.io/api/"

    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}
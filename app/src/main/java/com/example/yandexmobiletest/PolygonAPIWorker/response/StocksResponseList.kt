package com.example.yandexmobiletest.PolygonAPIWorker.response

import com.google.gson.annotations.SerializedName

data class StocksResponseList(
    @SerializedName("count") val count: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("perPage") val perPage: Int,
    @SerializedName("status") val status: String,
    @SerializedName("tickers") val tickers: List<Ticker>
)
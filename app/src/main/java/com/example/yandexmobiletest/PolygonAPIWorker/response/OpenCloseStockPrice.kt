package com.example.yandexmobiletest.PolygonAPIWorker.response

import com.google.gson.annotations.SerializedName

data class OpenCloseStockPrice(
    @SerializedName("afterHours") val afterHours: Double,
    @SerializedName("close") val close: Double,
    @SerializedName("from") val from: String,
    @SerializedName("high") val high: Double,
    @SerializedName("low") val low: Double,
    @SerializedName("`open`") val open: Double,
    @SerializedName("preMarket") val preMarket: Double,
    @SerializedName("status") val status: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("volume") val volume: Int
)
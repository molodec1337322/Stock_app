package com.example.yandexmobiletest.PolygonAPIWorker

import com.google.gson.annotations.SerializedName

data class Ticker(
    @SerializedName("active") val active: Boolean,
    @SerializedName("codes") val codes: Codes,
    @SerializedName("currency") val currency: String,
    @SerializedName("locale") val locale: String,
    @SerializedName("market") val market: String,
    @SerializedName("name") val name: String,
    @SerializedName("primaryExch") val primaryExch: String,
    @SerializedName("ticker") val ticker: String,
    @SerializedName("type") val type: String,
    @SerializedName("updated") val updated: String,
    @SerializedName("url") val url: String
)
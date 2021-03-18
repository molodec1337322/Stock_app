package com.example.yandexmobiletest.PolygonAPIWorker.response

import com.google.gson.annotations.SerializedName

data class Codes(
    @SerializedName("cfigi") val cfigi: String,
    @SerializedName("cik") val cik: String,
    @SerializedName("figi") val figi: String,
    @SerializedName("figiuid") val figiuid: String,
    @SerializedName("scfigi") val scfigi: String
)
package com.example.yandexmobiletest.APIWorker.responseFinHub

data class StocksListItem(
    val currency: String,
    val description: String,
    val displaySymbol: String,
    val figi: String,
    val mic: String,
    val symbol: String,
    val type: String
)
package com.example.yandexmobiletest.stocks.models

data class StockTickerAndDesc(
    var ticker: String,
    var description: String,
    var isFavourite: Boolean
) {
}
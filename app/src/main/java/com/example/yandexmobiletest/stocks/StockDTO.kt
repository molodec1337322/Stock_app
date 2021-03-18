package com.example.yandexmobiletest.stocks

data class StockDTO(
    var ticker: String,
    var company: String,
    var price: String,
    var priceChange: String,
    var isFavourite: Boolean = false
) {
}
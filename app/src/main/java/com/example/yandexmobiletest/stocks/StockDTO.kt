package com.example.yandexmobiletest.stocks

class StockDTO(
    var ticker: String,
    var company: String,
    var price: String,
    var priceChange: String,
    var isFavourite: Boolean = false
) {
}
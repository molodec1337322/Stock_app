package com.example.yandexmobiletest.stocks

import com.example.yandexmobiletest.stocks.models.StockDTO

interface StockAdapterListener {
    fun onMarkedAsFavorite(stock: StockDTO)

    fun onRemovedFromFavorite(stock: StockDTO)
}
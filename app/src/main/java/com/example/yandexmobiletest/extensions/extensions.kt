package com.example.yandexmobiletest.extensions

import com.example.yandexmobiletest.stocks.models.StockDTO
import com.example.yandexmobiletest.stocks.models.StockTickerAndDesc

fun MutableList<StockTickerAndDesc>.containsStock(stockTickerAndDesc: StockTickerAndDesc): Boolean{
    for(i in 0 until this.size){
        if(stockTickerAndDesc.ticker == this[i].ticker){
            return true
        }
    }
    return false
}

fun MutableList<StockDTO>.containsStock(stockDTO: StockDTO): Boolean{
    for(i in 0 until this.size){
        if(stockDTO.ticker == this[i].ticker){
            return true
        }
    }
    return false
}
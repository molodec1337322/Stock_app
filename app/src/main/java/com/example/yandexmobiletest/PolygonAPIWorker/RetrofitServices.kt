package com.example.yandexmobiletest.PolygonAPIWorker

import com.example.yandexmobiletest.PolygonAPIWorker.response.OpenCloseStockPrice
import com.example.yandexmobiletest.PolygonAPIWorker.response.StocksResponseList
import retrofit2.Call
import retrofit2.http.GET

//https://api.polygon.io/v2/reference/tickers?sort=ticker&market=STOCKS&search=US&perpage=50&page=1&apiKey=ePx00uLEVpETBH5DHSTqSO4KhKqrdpRF для получения списка всех акций
//https://api.polygon.io/v1/open-close/AAPL/2020-10-14?unadjusted=false&apiKey=ePx00uLEVpETBH5DHSTqSO4KhKqrdpRF для получения цены акций по укзанному тикеру

interface RetrofitServices {
    @GET("v2/reference/tickers?sort=ticker&market=STOCKS&search=US&perpage=50&page=1&apiKey=ePx00uLEVpETBH5DHSTqSO4KhKqrdpRF")
    fun getStocksResponseList(): Call<StocksResponseList>

    @GET("v1/open-close/AAPL/2020-10-14?unadjusted=false&apiKey=ePx00uLEVpETBH5DHSTqSO4KhKqrdpRF")
    fun getOpenCloseStockPrice(): Call<OpenCloseStockPrice>
}
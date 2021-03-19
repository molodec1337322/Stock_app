package com.example.yandexmobiletest.APIWorker

import com.example.yandexmobiletest.APIWorker.responseFinHub.BestMatchingStockList
import com.example.yandexmobiletest.APIWorker.responseFinHub.StockPrice
import com.example.yandexmobiletest.APIWorker.responseFinHub.StocksList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

//https://api.polygon.io/v2/reference/tickers?sort=ticker&market=STOCKS&search=US&perpage=50&page=1&apiKey=ePx00uLEVpETBH5DHSTqSO4KhKqrdpRF для получения списка всех акций
//https://api.polygon.io/v1/open-close/AAPL/2020-10-14?unadjusted=false&apiKey=ePx00uLEVpETBH5DHSTqSO4KhKqrdpRF для получения цены акций по укзанному тикеру

//https://finnhub.io/api/v1/search?q=apple&token=c19j8rf48v6prmim2iog поиск более подходящего тикера
//https://finnhub.io/api/v1/stock/symbol?exchange=US&token=c19j8rf48v6prmim2iog полчуние всех акций
//https://finnhub.io/api/v1/quote?symbol=AAPL&token=c19j8rf48v6prmim2iog получение цены на акцию

interface RetrofitServices {
    @GET("https://finnhub.io/api/v1/stock/symbol?exchange=US&token=c19j8rf48v6prmim2iog")
    fun getStocksResponseList(): Call<StocksList>

    @GET//(https://finnhub.io/api/v1/search?q=apple&token=c19j8rf48v6prmim2iog)
    fun getBestMatchingTickerOrName(@Url tickerOrName: String): Call<BestMatchingStockList>

    @GET//(https://finnhub.io/api/v1/quote?symbol=AAPL&token=c19j8rf48v6prmim2iog)
    fun getOpenCloseStockPrice(@Url ticker: String): Call<StockPrice>
}
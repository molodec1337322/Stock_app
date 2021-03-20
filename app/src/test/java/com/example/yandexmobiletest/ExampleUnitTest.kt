package com.example.yandexmobiletest

import com.example.yandexmobiletest.APIWorker.common.Common
import com.example.yandexmobiletest.APIWorker.responseFinHub.StocksList
import com.example.yandexmobiletest.stocks.StockDTO
import com.google.gson.internal.LinkedHashTreeMap
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getAPIInfo(){
        val retrofitService = Common.retrofitService
        val res = retrofitService.getStocksResponseList().execute().body()
        File("textjson.txt").writeText(res.toString())
    }
}
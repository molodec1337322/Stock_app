package com.example.yandexmobiletest

import android.widget.Toast
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.yandexmobiletest.APIWorker.common.Common
import com.example.yandexmobiletest.APIWorker.responseFinHub.StocksList

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.yandexmobiletest", appContext.packageName)
    }

    @Test
    fun test_1(){
        var retrofitService = Common.retrofitService
        retrofitService.getStocksResponseList().enqueue(object : Callback<StocksList> {
            override fun onFailure(call: Call<StocksList>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<StocksList>,
                response: Response<StocksList>
            ) {
                File("jsontest.txt").writeText(response.toString())
            }
        })
    }
}
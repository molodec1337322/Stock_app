package com.example.yandexmobiletest.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.yandexmobiletest.APIWorker.common.Common
import com.example.yandexmobiletest.APIWorker.responseFinHub.StockPrice
import com.example.yandexmobiletest.APIWorker.responseFinHub.StocksList
import com.example.yandexmobiletest.APIWorker.responseFinHub.StocksListItem
import com.example.yandexmobiletest.R
import com.example.yandexmobiletest.stocks.StockDTO
import com.example.yandexmobiletest.stocks.StockAdapter
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.btn_favourite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ListActivity : AppCompatActivity() {

    lateinit var search: EditText
    lateinit var stock: Button
    lateinit var favourite: Button
    lateinit var stock_recycler: RecyclerView

    private val context: Context = this

    private var retrofitService = Common.retrofitService

    private var adapterStocks: RecyclerView.Adapter<StockAdapter.StockHolder>? = null
    private var adapterFavourites: RecyclerView.Adapter<StockAdapter.StockHolder>? = null
    private var stockDTOS: MutableList<StockDTO> = mutableListOf(
        /*
        StockDTO("AAPL", "Apple inc.", "$5000.00","+$12.03(0.1%)", true),
        StockDTO("YYNDX", "Yandex LLC", "$45200.00","-$12.03(0.01%)", true),
        StockDTO("GOOGL", "Alphabet Class P", "$5000.00","+$12.03(0.15%)", false),
        StockDTO("AMZN", "Amazon", "$5540.00","+$12.03(0.15%)", false),
        StockDTO("BAC", "Bank of America", "$300.00","-$125.03(33.1%)", true),
        StockDTO("MSFT", "Microsoft Inc.", "$54540.00","+$12.03(0.15%)", false),
        StockDTO("TSLA", "Tesla motors", "$540.00","+$12.03(0.15%)", false)
         */
    )
    private var stocksListResponse: StocksList? = null
    private var currentShowingStocks = 0
    private val stocksPerPage = 20

    private var favourites: MutableList<StockDTO> = mutableListOf()

    //надо спрятать
    private val API_KEY = "c19j8rf48v6prmim2iog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        search = etd_search
        stock = btn_stocks
        favourite = btn_favourite

        search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString()
                val stocksTmp = stockDTOS.filter {
                    it.company.toLowerCase(Locale.getDefault()).startsWith(userInput.toLowerCase(Locale.getDefault())) ||
                            it.ticker.toLowerCase(Locale.getDefault()).startsWith(userInput.toLowerCase(Locale.getDefault()))
                }.toMutableList()
                val tmpAdapter = StockAdapter(stocksTmp, context)
                stock_recycler.swapAdapter(tmpAdapter, false)
            }
        })

        stock.setOnClickListener(View.OnClickListener {
            showStocks()
        })
        favourite.setOnClickListener(View.OnClickListener {
            showFavourites()
        })

        currentShowingStocks = 0
        getStocksList()
        stock_recycler = recycle_list_stock
        stock_recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        stock_recycler.layoutManager = layoutManager
        adapterStocks = StockAdapter(stockDTOS, context)
        stock_recycler.adapter = adapterStocks
        stock_recycler.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy > 0){
                    if(layoutManager.childCount + layoutManager.findFirstVisibleItemPosition() >= layoutManager.itemCount){
                        getPriceForStocks()
                    }
                }
            }
        })
        showStocks()
    }

    fun setButtonActive(button: Button){
        button.setTextSize(32f)
        button.setTextColor(this.getColor(R.color.colorBlack))
        button.isEnabled = false
    }

    fun setButtonInactive(button: Button){
        button.setTextSize(24f)
        button.setTextColor(this.getColor(R.color.colorInactiveButton))
        button.isEnabled = true
    }

    fun showStocks(){
        setButtonActive(stock)
        setButtonInactive(favourite)
        adapterStocks = StockAdapter(stockDTOS, context)
        stock_recycler.swapAdapter(adapterStocks, false)

    }

    fun showFavourites(){
        setButtonActive(favourite)
        setButtonInactive(stock)
        updateFavourites()
        adapterFavourites = StockAdapter(favourites, context)
        stock_recycler.swapAdapter(adapterFavourites, false)
    }

    fun updateFavourites(){
        favourites = stockDTOS.filter { it.isFavourite }.toMutableList()
    }

    fun getStocksList(){
        retrofitService.getStocksResponseList().enqueue(object : Callback<StocksList>{
            override fun onFailure(call: Call<StocksList>, t: Throwable) {
                Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<StocksList>,
                response: Response<StocksList>
            ) {
                stocksListResponse = response.body()
                getPriceForStocks()
            }
        })
    }

    fun getPriceForStocks(){
        for(i in currentShowingStocks until currentShowingStocks + stocksPerPage){

            var currTicker: StocksListItem?
            if(stocksListResponse != null && i < stocksListResponse!!.size){
                currTicker = stocksListResponse?.get(i)
            }
            else{
                break
                //currTicker = null
            }

            if(currTicker != null){
                retrofitService.getOpenCloseStockPrice("https://finnhub.io/api/v1/quote?symbol=${currTicker.symbol}&token=c19j8rf48v6prmim2iog")
                    .enqueue(object : Callback<StockPrice>{
                        override fun onFailure(call: Call<StockPrice>, t: Throwable) {

                        }

                        override fun onResponse(
                            call: Call<StockPrice>,
                            response: Response<StockPrice>
                        ) {
                            if(response.body() != null){
                                val priceChange: String
                                if(String.format("%.2f", response.body()!!.o - response.body()!!.c).toDouble() > 0){
                                    priceChange = String.format("+%.2f", response.body()!!.o - response.body()!!.c)
                                }
                                else if(String.format("%.2f", response.body()!!.o - response.body()!!.c).toDouble() < 0){
                                    priceChange = String.format("%.2f", response.body()!!.o - response.body()!!.c)
                                }
                                else{
                                    priceChange = String.format("%.2f", response.body()!!.o - response.body()!!.c)
                                }

                                stockDTOS.add(
                                    StockDTO(
                                        currTicker.symbol,
                                        currTicker.description,
                                        response.body()!!.c.toString(),
                                        priceChange
                                    )
                                )
                                showStocks()
                            }
                        }
                    })
            }
        }
        currentShowingStocks += stocksPerPage
    }
}
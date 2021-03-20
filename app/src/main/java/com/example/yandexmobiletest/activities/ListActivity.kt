package com.example.yandexmobiletest.activities

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.yandexmobiletest.APIWorker.common.Common
import com.example.yandexmobiletest.APIWorker.responseFinHub.BestMatchingStockList
import com.example.yandexmobiletest.APIWorker.responseFinHub.Result
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
    lateinit var progress_Bar: ProgressBar

    private val context: Context = this

    private var retrofitService = Common.retrofitService

    private var adapterStocks: RecyclerView.Adapter<StockAdapter.StockHolder>? = null
    private var adapterFavourites: RecyclerView.Adapter<StockAdapter.StockHolder>? = null

    private var stockDTOS: MutableList<StockDTO> = mutableListOf()

    private var stocksListResponse: StocksList? = null

    private var currentShowingStocks = 0
    private var currentShowingSearchedStocks = 0
    private val stocksPerPage = 10

    private var favourites: MutableList<StockDTO> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolBar))
        setContentView(R.layout.activity_list)

        search = etd_search
        stock = btn_stocks
        favourite = btn_favourite
        progress_Bar = progressBar

        search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(s.toString() != ""){
                    currentShowingSearchedStocks = 0


                    retrofitService.getBestMatchingTickerOrName("https://finnhub.io/api/v1/search?q=${s.toString()}&token=c19j8rf48v6prmim2iog")
                        .enqueue(object : Callback<BestMatchingStockList>{
                            override fun onFailure(call: Call<BestMatchingStockList>, t: Throwable) {
                                Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(
                                call: Call<BestMatchingStockList>,
                                response: Response<BestMatchingStockList>
                            ) {
                                val responseList = response.body()
                                getPriceForSearchedStocks(responseList, currentShowingSearchedStocks, s.toString())

                                /*
                                val stocksTmp = stockDTOS.filter {
                                    it.company.toLowerCase(Locale.getDefault()).startsWith(s.toString().toLowerCase(Locale.getDefault())) ||
                                            it.ticker.toLowerCase(Locale.getDefault()).startsWith(s.toString().toLowerCase(Locale.getDefault()))
                                }.toMutableList()
                                val tmpAdapter = StockAdapter(stocksTmp, context)
                                stock_recycler.swapAdapter(tmpAdapter, false)

                                 */
                            }
                        })
                }
                else{
                    stock_recycler.swapAdapter(adapterStocks, false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

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
                if(dy >= 5){
                    //hideProgressBar()
                }
                if(dy > 0){
                    if(layoutManager.childCount + layoutManager.findFirstVisibleItemPosition() >= layoutManager.itemCount){
                        getPriceForStocks(stocksListResponse, currentShowingStocks)
                        currentShowingStocks += stocksPerPage
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

    fun showProgressBar(){
        progress_Bar.visibility = View.VISIBLE
    }

    fun hideProgressBar(){
        progress_Bar.visibility = View.INVISIBLE
    }

    fun updateFavourites(){
        favourites = stockDTOS.filter { it.isFavourite }.toMutableList()
    }

    fun getStocksList(){
        showProgressBar()
        retrofitService.getStocksResponseList().enqueue(object : Callback<StocksList>{
            override fun onFailure(call: Call<StocksList>, t: Throwable) {
                Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<StocksList>,
                response: Response<StocksList>
            ) {
                stocksListResponse = response.body()
                getPriceForStocks(stocksListResponse, currentShowingStocks)
                currentShowingStocks += stocksPerPage
            }
        })
    }

    fun getPriceForStocks(stocksListResponse: StocksList?, currentShowingStocks: Int){
        for(i in currentShowingStocks until currentShowingStocks + stocksPerPage){

            var currTicker: StocksListItem?
            if(stocksListResponse != null && i < stocksListResponse.size){
                currTicker = stocksListResponse[i]
            }
            else{
                break
            }

            showProgressBar()
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
                            adapterStocks!!.notifyDataSetChanged()
                            //hideProgressBar()
                        }
                    }
                })
        }
    }

    fun getPriceForSearchedStocks(stocksListResponse: BestMatchingStockList?, currentShowingStocks: Int, s: String) {
        for (i in currentShowingStocks until currentShowingStocks + stocksPerPage) {

            var currTicker: Result?
            if (stocksListResponse != null && i < stocksListResponse.result.size) {
                currTicker = stocksListResponse.result[i]
            } else {
                break
            }

            showProgressBar()
            retrofitService.getOpenCloseStockPrice("https://finnhub.io/api/v1/quote?symbol=${currTicker.symbol}&token=c19j8rf48v6prmim2iog")
                .enqueue(object : Callback<StockPrice> {
                    override fun onFailure(call: Call<StockPrice>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<StockPrice>,
                        response: Response<StockPrice>
                    ) {
                        if (response.body() != null) {
                            val priceChange: String
                            if (String.format("%.2f", response.body()!!.c - response.body()!!.pc).toDouble() > 0) {
                                priceChange = String.format("+%.2f", response.body()!!.c - response.body()!!.pc)
                            }
                            else if (String.format("%.2f", response.body()!!.c - response.body()!!.pc).toDouble() < 0) {
                                priceChange = String.format("%.2f", response.body()!!.c - response.body()!!.pc)
                            }
                            else {
                                priceChange = String.format("%.2f", response.body()!!.c - response.body()!!.pc)
                            }

                            stockDTOS.add(
                                StockDTO(
                                    currTicker.symbol,
                                    currTicker.description,
                                    response.body()!!.c.toString(),
                                    priceChange
                                )
                            )
                            adapterStocks!!.notifyDataSetChanged()
                            //hideProgressBar()
                        }
                    }
                })
        }
        val stocksTmp = stockDTOS.filter {
            it.company.toLowerCase(Locale.getDefault()).startsWith(s.toLowerCase(Locale.getDefault())) ||
                    it.ticker.toLowerCase(Locale.getDefault()).startsWith(s.toLowerCase(Locale.getDefault()))
        }.toMutableList()
        val tmpAdapter = StockAdapter(stocksTmp, context)
        stock_recycler.swapAdapter(tmpAdapter, false)
    }
}
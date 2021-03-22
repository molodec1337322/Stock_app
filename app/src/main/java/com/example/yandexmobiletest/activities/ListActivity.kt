package com.example.yandexmobiletest.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexmobiletest.APIWorker.common.Common
import com.example.yandexmobiletest.APIWorker.responseFinHub.BestMatchingStockList
import com.example.yandexmobiletest.APIWorker.responseFinHub.StockPrice
import com.example.yandexmobiletest.R
import com.example.yandexmobiletest.TinyDB.TinyDB
import com.example.yandexmobiletest.extensions.containsStock
import com.example.yandexmobiletest.stocks.models.StockDTO
import com.example.yandexmobiletest.stocks.StockAdapter
import com.example.yandexmobiletest.stocks.StockAdapterListener
import com.example.yandexmobiletest.stocks.StockTickerAndDescListWrapper
import com.example.yandexmobiletest.stocks.StocksStartList
import com.example.yandexmobiletest.stocks.models.StockTickerAndDesc
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.btn_favourite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ListActivity : AppCompatActivity() {

    lateinit var search: EditText
    lateinit var stock: Button
    lateinit var favourite: Button
    lateinit var stock_recycler: RecyclerView
    lateinit var progress_Bar: ProgressBar

    private val context: Context = this

    private var retrofitService = Common.retrofitService
    private var requestsCount = 0

    private var adapterStocks: StockAdapter? = null
    private var adapterFavourites: StockAdapter? = null
    private var adapterSearch: StockAdapter? = null

    private var stockDTOS: MutableList<StockDTO> = mutableListOf()
    private var stockDTOSSearch: MutableList<StockDTO> = mutableListOf()
    private var favouritesDTOS: MutableList<StockDTO> = mutableListOf()

    private var isLoading = false
    private var isSearching = false
    private var isInFavorite = false

    private var stockTickerAndDescList: MutableList<StockTickerAndDesc> = mutableListOf()
    private var stockTickerAndDescListSearch: MutableList<StockTickerAndDesc> = mutableListOf()

    private val stocksPerPage = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolBar))
        setContentView(R.layout.activity_list)

        loadStartStockListLocal()

        search = etd_search
        stock = btn_stocks
        favourite = btn_favourite
        progress_Bar = progressBar

        search.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    val searchString = search.text.toString().toLowerCase(Locale.getDefault())
                    if(searchString != ""){
                        if(!isInFavorite){
                            isSearching = true
                            showProgressBar()

                            stockDTOSSearch.clear()
                            stockTickerAndDescListSearch.clear()

                            stock_recycler.swapAdapter(adapterSearch, true)

                            retrofitService.getBestMatchingTickerOrName("https://finnhub.io/api/v1/search?q=${searchString}&token=c19j8rf48v6prmim2iog")
                                .enqueue(object : Callback<BestMatchingStockList>{
                                    override fun onFailure(call: Call<BestMatchingStockList>, t: Throwable) {
                                        Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onResponse(
                                        call: Call<BestMatchingStockList>,
                                        response: Response<BestMatchingStockList>
                                    ) {
                                        if(response.errorBody() != null){
                                            Toast.makeText(context, "err", Toast.LENGTH_SHORT).show()
                                        }

                                        for(i in 0 until stockTickerAndDescList.size){
                                            val currentTickerAndDesc = stockTickerAndDescList[i]
                                            if(currentTickerAndDesc.ticker.toLowerCase(Locale.getDefault()).startsWith(searchString) ||
                                                currentTickerAndDesc.description.toLowerCase(Locale.getDefault()).startsWith(searchString)){
                                                stockTickerAndDescListSearch.add(currentTickerAndDesc)
                                            }
                                        }

                                        if(response.body() != null){
                                            val responseList = response.body()!!.result
                                            val currentLoadedStocks = response.body()!!.count

                                            for(i in 0 until currentLoadedStocks){
                                                val currentTickerAndDesc =
                                                    StockTickerAndDesc(
                                                        responseList[i].symbol,
                                                        responseList[i].description,
                                                        false
                                                    )
                                                if (!stockTickerAndDescListSearch.containsStock(currentTickerAndDesc)){
                                                    stockTickerAndDescListSearch.add(currentTickerAndDesc)
                                                }
                                            }
                                        }

                                        getPriceForStocks(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
                                    }
                                })
                        }
                        else{
                            stockDTOSSearch.clear()
                            stockTickerAndDescListSearch.clear()

                            for(i in 0 until stockTickerAndDescList.size){
                                val currentTickerAndDesc = stockTickerAndDescList[i]
                                if((currentTickerAndDesc.ticker.toLowerCase(Locale.getDefault()).startsWith(searchString) ||
                                    currentTickerAndDesc.description.toLowerCase(Locale.getDefault()).startsWith(searchString)) &&
                                    currentTickerAndDesc.isFavourite){
                                    stockTickerAndDescListSearch.add(currentTickerAndDesc)
                                }
                            }
                            getPriceForStocks(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
                        }
                    }
                    else{
                        isSearching = false
                        requestsCount = 0
                        if(!isInFavorite){
                            stock_recycler.swapAdapter(adapterStocks, true)
                        }
                        else{
                            stock_recycler.swapAdapter(adapterFavourites, true)
                        }
                    }
                }
                return true
            }
        })


        stock.setOnClickListener(View.OnClickListener {
            showStocks()
        })
        favourite.setOnClickListener(View.OnClickListener {
            showFavourites()
        })

        adapterSearch = StockAdapter(stockDTOSSearch, context)
        adapterSearch!!.stockAdapterListener = object : StockAdapterListener{
            override fun onMarkedAsFavorite(stock: StockDTO) {
                addedToFavouritesSync(stock)
            }

            override fun onRemovedFromFavorite(stock: StockDTO) {
                removedFromFavouritesSync(stock)
            }

        }

        adapterStocks = StockAdapter(stockDTOS, context)
        adapterStocks!!.stockAdapterListener = object : StockAdapterListener{
            override fun onMarkedAsFavorite(stock: StockDTO) {
                addedToFavouritesSync(stock)
            }

            override fun onRemovedFromFavorite(stock: StockDTO) {
                removedFromFavouritesSync(stock)
            }

        }

        adapterFavourites = StockAdapter(favouritesDTOS, context)
        adapterFavourites!!.stockAdapterListener = object : StockAdapterListener{
            override fun onMarkedAsFavorite(stock: StockDTO) {
                addedToFavouritesSync(stock)
            }

            override fun onRemovedFromFavorite(stock: StockDTO) {
                removedFromFavouritesSync(stock)
            }

        }

        stock_recycler = recycle_list_stock
        stock_recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        stock_recycler.layoutManager = layoutManager
        stock_recycler.adapter = adapterStocks
        getStocksList()

        stock_recycler.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy > 0){
                    if(layoutManager.childCount + layoutManager.findFirstVisibleItemPosition() >= layoutManager.itemCount && !isLoading){
                        if(isInFavorite){
                            getPriceForStocks(stockTickerAndDescList.filter { it.isFavourite }.toMutableList(), adapterFavourites!!, stockDTOS.filter { it.isFavourite }.toMutableList())
                        }
                        else if(isSearching){
                            getPriceForStocks(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
                        }
                        else if(!isSearching){
                            getPriceForStocks(stockTickerAndDescList, adapterStocks!!, stockDTOS)
                        }

                        showProgressBar()
                    }
                }
            }
        })

        showStocks()
    }

    fun addedToFavouritesSync(stock: StockDTO){
        favouritesDTOS.add(stock)
        for(i in 0 until stockTickerAndDescList.size){
            if(stockTickerAndDescList[i].ticker == stock.ticker){
                stockTickerAndDescList[i].isFavourite = true
            }
        }
        saveStartStockListLocal()
    }

    fun removedFromFavouritesSync(stock: StockDTO){
        for(i in 0 until favouritesDTOS.size){
            if(stock.ticker == favouritesDTOS[i].ticker){
                favouritesDTOS.removeAt(i)
                break
            }
        }

        for(i in 0 until stockTickerAndDescList.size){
            if(stockTickerAndDescList[i].ticker == stock.ticker){
                stockTickerAndDescList[i].isFavourite = false
            }
        }

        for(i in 0 until stockDTOS.size){
            if(stockDTOS[i].ticker == stock.ticker){
                stockDTOS[i] = stock
                break
            }
        }

        if(!stockDTOSSearch.none()){
            for(i in 0 until stockDTOSSearch.size){
                if(stockDTOSSearch[i].ticker == stock.ticker){
                    stockDTOSSearch[i] = stock
                }
            }
        }

        saveStartStockListLocal()
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
        isInFavorite = false

        setButtonActive(stock)
        setButtonInactive(favourite)
        if(!isSearching){
            stock_recycler.swapAdapter(adapterStocks, true)
        }
        else{
            stock_recycler.swapAdapter(adapterSearch, true)
        }

    }

    fun showFavourites(){
        isInFavorite = true

        setButtonActive(favourite)
        setButtonInactive(stock)

        getPriceForStocks(stockTickerAndDescList.filter { it.isFavourite }.toMutableList(), adapterFavourites!!, favouritesDTOS)

        stock_recycler.swapAdapter(adapterFavourites, true)
    }

    fun getStocksList(){
        showProgressBar()

        getPriceForStocks(stockTickerAndDescList, adapterStocks!!, stockDTOS)
    }

    fun showProgressBar(){
        isLoading = true
        progress_Bar.visibility = View.VISIBLE
    }

    fun hideProgressBar(){
        isLoading = false
        progress_Bar.visibility = View.INVISIBLE
    }

    fun getPriceForStocks(
        tickerAndDescList: MutableList<StockTickerAndDesc>,
        adapter: RecyclerView.Adapter<StockAdapter.StockHolder>,
        stocksDTOList: MutableList<StockDTO>
    ){
        var currentTicker: StockTickerAndDesc
        val currentLoadedStocks = stocksDTOList.size

        if(currentLoadedStocks == 0){
            hideProgressBar()
            stock_recycler.swapAdapter(adapter, false)
        }

        for(i in currentLoadedStocks until currentLoadedStocks + stocksPerPage){
            if(i < tickerAndDescList.size){
                currentTicker = tickerAndDescList[i]

                showProgressBar()
                requestsCount++
                getPrice(currentTicker, adapter, stocksDTOList)
            }
            else{
                break
            }
        }
    }

    fun getPrice(stock: StockTickerAndDesc, adapter: RecyclerView.Adapter<StockAdapter.StockHolder>, stocksDTOList: MutableList<StockDTO>){
        retrofitService.getOpenCloseStockPrice("https://finnhub.io/api/v1/quote?symbol=${stock.ticker}&token=c19j8rf48v6prmim2iog")
            .enqueue(object : Callback<StockPrice>{
                override fun onFailure(call: Call<StockPrice>, t: Throwable) {
                    Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<StockPrice>,
                    response: Response<StockPrice>
                ) {
                    if(requestsCount > 0){
                        requestsCount--
                        if(response.errorBody() != null){
                            if(requestsCount == 0){
                                hideProgressBar()
                            }
                        }

                        if(response.body() != null){
                            val priceChange: String
                            if(String.format(Locale.US, "%.2f", response.body()!!.o - response.body()!!.c).toDouble() > 0){
                                priceChange = String.format(Locale.US, "+%.2f", response.body()!!.o - response.body()!!.c) + "(" +
                                        String.format(Locale.US, "+%.2f",  100 * (response.body()!!.o - response.body()!!.c) / response.body()!!.o) + "%)"
                            }
                            else if(String.format(Locale.US, "%.2f", response.body()!!.o - response.body()!!.c).toDouble() < 0){
                                priceChange = String.format(Locale.US, "%.2f", response.body()!!.o - response.body()!!.c) + " (" +
                                        String.format(Locale.US, "%.2f",  100 * (response.body()!!.o - response.body()!!.c) / response.body()!!.o) + "%)"
                            }
                            else{
                                priceChange = String.format(Locale.US, "%.2f", response.body()!!.o - response.body()!!.c) + " (" +
                                        String.format(Locale.US, "%.2f",  100 * (response.body()!!.o - response.body()!!.c) / response.body()!!.o) + "%)"
                            }

                            val loadedStock =
                                StockDTO(
                                    stock.ticker,
                                    stock.description,
                                    response.body()!!.c.toString(),
                                    priceChange,
                                    stock.isFavourite
                                )

                            if(!stocksDTOList.containsStock(loadedStock)){
                                stocksDTOList.add(loadedStock)
                            }
                            if(!stockTickerAndDescList.containsStock(stock)){
                                stockTickerAndDescList.add(stock)
                            }

                        }
                    }
                    if(requestsCount == 0){
                        hideProgressBar()
                        stock_recycler.swapAdapter(adapter, false)
                    }

                    else if(requestsCount < 0){
                        requestsCount = 0
                    }
                }
            })
    }

    fun saveStartStockListLocal(){
        val tinyDB = TinyDB(context)
        StocksStartList.startList = stockTickerAndDescList
        tinyDB.putObject(StocksStartList.startStockListName, StockTickerAndDescListWrapper(stockTickerAndDescList))
    }

    fun loadStartStockListLocal(){
        val tinyDB = TinyDB(context)
        val loaded = tinyDB.getObject(StocksStartList.startStockListName, StockTickerAndDescListWrapper()::class.java)//.stockTickerAndDesc

        if(loaded == null){
            stockTickerAndDescList = StocksStartList.startList
        }
        else{
            stockTickerAndDescList = loaded.stockTickerAndDesc!!

        }
    }
}
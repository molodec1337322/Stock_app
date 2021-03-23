package com.example.yandexmobiletest.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    lateinit var clear: ImageButton
    lateinit var stock_recycler: RecyclerView
    lateinit var swipe_layoyt: SwipeRefreshLayout

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
        clear = btn_clear_search
        favourite = btn_favourite
        swipe_layoyt = swipe_refresh_layout

        swipe_layoyt.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                if(isSearching){
                    getPriceForStocksFromApi(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
                }
                else if(isInFavorite){
                    getPriceForStocksFromApi(stockTickerAndDescList.filter { it.isFavourite }.toMutableList(), adapterFavourites!!, favouritesDTOS)
                }
                else{
                    getPriceForStocksFromApi(stockTickerAndDescList, adapterStocks!!, stockDTOS)
                }

                showProgressBar()
            }
        } )

        search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length > 0){
                    clear.visibility = View.VISIBLE
                }
                else{
                    clear.visibility = View.INVISIBLE
                }
            }
        })

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

                            getBestMatchingTickerFromApi(searchString)
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
                            getPriceForStocksFromApi(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
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
        clear.setOnClickListener(View.OnClickListener {
            isSearching = false
            requestsCount = 0
            search.text.clear()
            hideProgressBar()
            if(!isInFavorite){
                stock_recycler.swapAdapter(adapterStocks, true)
            }
            else{
                stock_recycler.swapAdapter(adapterFavourites, true)
            }
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
                        if(isSearching){
                            getPriceForStocksFromApi(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
                        }
                        else if(isInFavorite){
                            getPriceForStocksFromApi(stockTickerAndDescList.filter { it.isFavourite }.toMutableList(), adapterFavourites!!, favouritesDTOS)
                        }
                        else{
                            getPriceForStocksFromApi(stockTickerAndDescList, adapterStocks!!, stockDTOS)
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
        for(i in 0 until stockDTOS.size){
            if(stockDTOS[i].ticker == stock.ticker){
                stockDTOS[i].isFavourite = true
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

        getPriceForStocksFromApi(stockTickerAndDescList.filter { it.isFavourite }.toMutableList(), adapterFavourites!!, favouritesDTOS)

        stock_recycler.swapAdapter(adapterFavourites, true)
    }

    fun getStocksList(){
        showProgressBar()

        getPriceForStocksFromApi(stockTickerAndDescList, adapterStocks!!, stockDTOS)
    }

    fun showProgressBar(){
        isLoading = true

        adapterStocks!!.showLoading()
        adapterFavourites!!.showLoading()
        adapterSearch!!.showLoading()
    }

    fun hideProgressBar(){
        isLoading = false

        adapterSearch!!.hideLoading()
        adapterFavourites!!.hideLoading()
        adapterStocks!!.hideLoading()

        swipe_layoyt.isRefreshing = isLoading
    }

    fun getBestMatchingTickerFromApi(searchString: String){
        retrofitService.getBestMatchingTickerOrName("https://finnhub.io/api/v1/search?q=${searchString}&token=c19j8rf48v6prmim2iog")
            .enqueue(object : Callback<BestMatchingStockList>{
                override fun onFailure(call: Call<BestMatchingStockList>, t: Throwable) {
                    Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }

                override fun onResponse(
                    call: Call<BestMatchingStockList>,
                    response: Response<BestMatchingStockList>
                ) {
                    if(search.text.toString() != ""){
                        if(response.errorBody() != null){
                            Toast.makeText(context, "Failed connect to server\nTry again later", Toast.LENGTH_SHORT).show()
                            if(response.code() == 429){
                                hideProgressBar()
                            }
                        }
                        else{
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

                            getPriceForStocksFromApi(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
                        }
                    }
                }
            })
    }

    fun getPriceForStocksFromApi(
        tickerAndDescList: MutableList<StockTickerAndDesc>,
        adapter: StockAdapter,
        stocksDTOList: MutableList<StockDTO>
    ){
        var currentTicker: StockTickerAndDesc
        val currentLoadedStocks = stocksDTOList.size

        if(currentLoadedStocks == 0){
            hideProgressBar()
            stock_recycler.swapAdapter(adapter, false)
        }

        for(i in currentLoadedStocks until currentLoadedStocks + stocksPerPage){
            if(tickerAndDescList.size == stocksDTOList.size){
                hideProgressBar()
                break
            }
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

    fun getPrice(stock: StockTickerAndDesc, adapter: StockAdapter, stocksDTOList: MutableList<StockDTO>){
        retrofitService.getOpenCloseStockPrice("https://finnhub.io/api/v1/quote?symbol=${stock.ticker}&token=c19j8rf48v6prmim2iog")
            .enqueue(object : Callback<StockPrice>{
                override fun onFailure(call: Call<StockPrice>, t: Throwable) {
                    Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }

                override fun onResponse(
                    call: Call<StockPrice>,
                    response: Response<StockPrice>
                ) {
                    if(requestsCount > 0){
                        requestsCount--
                        if(response.errorBody() != null){
                            if(response.code() == 429){
                                requestsCount = 0

                                Toast.makeText(context, "Failed connect to server\nTry again later", Toast.LENGTH_SHORT).show()
                            }
                            if(requestsCount == 0){
                                hideProgressBar()
                            }
                        }

                        if(response.body() != null){
                            val priceChange = getPriceChange(response)

                            val loadedStock =
                                StockDTO(
                                    stock.ticker,
                                    stock.description,
                                    String.format(Locale.US, "$%.2f", response.body()!!.c),
                                    priceChange,
                                    stock.isFavourite
                                )

                            if(!stocksDTOList.containsStock(loadedStock)){
                                stocksDTOList.add(loadedStock)
                            }
                            if(!stockTickerAndDescList.containsStock(stock)){
                                stockTickerAndDescList.add(stock)
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                    if(requestsCount == 0){
                        hideProgressBar()
                    }

                    else if(requestsCount < 0){
                        requestsCount = 0
                    }
                }
            })
    }

    fun getPriceChange(response: Response<StockPrice>): String{
        if(String.format(Locale.US, "%.2f", response.body()!!.c - response.body()!!.pc).toDouble() > 0){
            return String.format(Locale.US, "+%.2f", response.body()!!.c - response.body()!!.pc) + "(" +
                    String.format(Locale.US, "+%.2f",  100 * (response.body()!!.c - response.body()!!.pc) / response.body()!!.c) + "%)"
        }
        else if(String.format(Locale.US, "%.2f", response.body()!!.c - response.body()!!.pc).toDouble() < 0){
            return String.format(Locale.US, "%.2f", response.body()!!.c - response.body()!!.pc) + " (" +
                    String.format(Locale.US, "%.2f",  100 * (response.body()!!.c - response.body()!!.pc) / response.body()!!.c) + "%)"
        }
        else{
            return String.format(Locale.US, "%.2f", response.body()!!.c - response.body()!!.pc) + " (" +
                    String.format(Locale.US, "%.2f",  100 * (response.body()!!.c - response.body()!!.pc) / response.body()!!.c) + "%)"
        }
    }

    fun saveStartStockListLocal(){
        val tinyDB = TinyDB(context)
        StocksStartList.startList = stockTickerAndDescList
        tinyDB.putObject(StocksStartList.startStockListName, StockTickerAndDescListWrapper(stockTickerAndDescList))
    }

    fun loadStartStockListLocal(){
        val tinyDB = TinyDB(context)
        val loaded = tinyDB.getObject(StocksStartList.startStockListName, StockTickerAndDescListWrapper()::class.java)

        if(loaded == null){
            stockTickerAndDescList = StocksStartList.startList
        }
        else{
            stockTickerAndDescList = loaded.stockTickerAndDesc!!
        }
    }
}
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
import com.example.yandexmobiletest.extensions.containsStock
import com.example.yandexmobiletest.stocks.models.StockDTO
import com.example.yandexmobiletest.stocks.StockAdapter
import com.example.yandexmobiletest.stocks.StockAdapterListener
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
    private var favourites: MutableList<StockDTO> = mutableListOf()

    private var isLoading = false
    private var isSearching = false
    private var isInFavorite = false

    private var stockTickerAndDescList: MutableList<StockTickerAndDesc> = mutableListOf(
        StockTickerAndDesc(
            "AAPL",
            "Apple Inc.",
            false
        ),
        StockTickerAndDesc(
            "BKNG",
            "Booking Holdings Inc.",
            false
        ),
        StockTickerAndDesc(
            "MSFT",
            "Microsoft Corporation",
            false
        ),
        StockTickerAndDesc(
            "AVGO",
            "Broadcom Limited",
            false
        ),
        StockTickerAndDesc(
            "AMZN",
            "Amazon.com Inc.",
            false
        ),
        StockTickerAndDesc(
            "ACN",
            "Accenture Plc Class A",
            false
        ),
        StockTickerAndDesc(
            "FB",
            "Facebook Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "UTX",
            "United Technologies Corporation",
            false
        ),
        StockTickerAndDesc(
            "JPM",
            "JPMorgan Chase & Co.",
            false
        ),
        StockTickerAndDesc(
            "GS",
            "Goldman Sachs Group Inc.",
            false
        ),
        StockTickerAndDesc(
            "BRK.B",
            "Berkshire Hathaway Inc. Class B",
            false
        ),
        StockTickerAndDesc(
            "SLB",
            "Schlumberger NV",
            false
        ),
        StockTickerAndDesc(
            "JNJ",
            "Johnson & Johnson",
            false
        ),
        StockTickerAndDesc(
            "CAT",
            "Caterpillar Inc.",
            false
        ),
        StockTickerAndDesc(
            "GOOG",
            "Alphabet Inc. Class C",
            false
        ),
        StockTickerAndDesc(
            "PYPL",
            "PayPal Holdings Inc",
            false
        ),
        StockTickerAndDesc(
            "GOOGL",
            "Alphabet Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "QCOM",
            "QUALCOMM Incorporated",
            false
        ),
        StockTickerAndDesc(
            "XOM",
            "Exxon Mobil Corporation",
            false
        ),
        StockTickerAndDesc(
            "CRM",
            "salesforce.com inc.",
            false
        ),
        StockTickerAndDesc(
            "BAC",
            "Bank of America Corporation",
            false
        ),
        StockTickerAndDesc(
            "NKE",
            "NIKE Inc. Class B",
            false
        ),
        StockTickerAndDesc(
            "WFC",
            "Wells Fargo & Company",
            false
        ),
        StockTickerAndDesc(
            "TMO",
            "Thermo Fisher Scientific Inc.",
            false
        ),
        StockTickerAndDesc(
            "INTC",
            "Intel Corporation",
            false
        ),
        StockTickerAndDesc(
            "USB",
            "U.S. Bancorp",
            false
        ),
        StockTickerAndDesc(
            "T",
            "AT&T Inc.",
            false
        ),
        StockTickerAndDesc(
            "SBUX",
            "Starbucks Corporation",
            false
        ),
        StockTickerAndDesc(
            "V",
            "Visa Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "LMT",
            "Lockheed Martin Corporation",
            false
        ),
        StockTickerAndDesc(
            "CSCO",
            "Cisco Systems Inc.",
            false
        ),
        StockTickerAndDesc(
            "COST",
            "Costco Wholesale Corporation",
            false
        ),
        StockTickerAndDesc(
            "CVX",
            "Chevron Corporation",
            false
        ),
        StockTickerAndDesc(
            "MS",
            "Morgan Stanley",
            false
        ),
        StockTickerAndDesc(
            "UNH",
            "UnitedHealth Group Incorporated",
            false
        ),
        StockTickerAndDesc(
            "PNC",
            "PNC Financial Services Group Inc.",
            false
        ),
        StockTickerAndDesc(
            "PFE",
            "Pfizer Inc.",
            false
        ),
        StockTickerAndDesc(
            "LLY",
            "Eli Lilly and Company",
            false
        ),
        StockTickerAndDesc(
            "HD",
            "Home Depot Inc.",
            false
        ),
        StockTickerAndDesc(
            "UPS",
            "United Parcel Service Inc. Class B",
            false
        ),
        StockTickerAndDesc(
            "PG",
            "Procter & Gamble Company",
            false
        ),
        StockTickerAndDesc(
            "TWX",
            "Time Warner Inc.",
            false
        ),
        StockTickerAndDesc(
            "VZ",
            "Verizon Communications Inc.",
            false
        ),
        StockTickerAndDesc(
            "NEE",
            "NextEra Energy Inc.",
            false
        ),
        StockTickerAndDesc(
            "C",
            "Citigroup Inc.",
            false
        ),
        StockTickerAndDesc(
            "CELG",
            "Celgene Corporation",
            false
        ),
        StockTickerAndDesc(
            "ABBV",
            "AbbVie Inc.",
            false
        ),
        StockTickerAndDesc(
            "LOW",
            "Lowe’s Companies Inc.",
            false
        ),
        StockTickerAndDesc(
            "BA",
            "Boeing Company",
            false
        ),
        StockTickerAndDesc(
            "BLK",
            "BlackRock Inc.",
            false
        ),
        StockTickerAndDesc(
            "KO",
            "Coca-Cola Company",
            false
        ),
        StockTickerAndDesc(
            "CVS",
            "CVS Health Corporation",
            false
        ),
        StockTickerAndDesc(
            "CMCSA",
            "Comcast Corporation Class A",
            false
        ),
        StockTickerAndDesc(
            "AXP",
            "American Express Company",
            false
        ),
        StockTickerAndDesc(
            "MA",
            "Mastercard Incorporated Class A",
            false
        ),
        StockTickerAndDesc(
            "MU",
            "Micron Technology Inc.",
            false
        ),
        StockTickerAndDesc(
            "PM",
            "Philip Morris International Inc.",
            false
        ),
        StockTickerAndDesc(
            "CHTR",
            "Charter Communications Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "PEP",
            "PepsiCo Inc.",
            false
        ),
        StockTickerAndDesc(
            "SCHW",
            "Charles Schwab Corporation",
            false
        ),
        StockTickerAndDesc(
            "ORCL",
            "Oracle Corporation",
            false
        ),
        StockTickerAndDesc(
            "MDLZ",
            "Mondelez International Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "DIS",
            "Walt Disney Company",
            false
        ),
        StockTickerAndDesc(
            "CB",
            "Chubb Limited",
            false
        ),
        StockTickerAndDesc(
            "MRK",
            "Merck & Co. Inc.",
            false
        ),
        StockTickerAndDesc(
            "COP",
            "ConocoPhillips",
            false
        ),
        StockTickerAndDesc(
            "NVDA",
            "NVIDIA Corporation",
            false
        ),
        StockTickerAndDesc(
            "AMAT",
            "Applied Materials Inc.",
            false
        ),
        StockTickerAndDesc(
            "MMM",
            "3M Company",
            false
        ),
        StockTickerAndDesc(
            "DHR",
            "Danaher Corporation",
            false
        ),
        StockTickerAndDesc(
            "AMGN",
            "Amgen Inc.",
            false
        ),
        StockTickerAndDesc(
            "HLT",
            "Hilton Worldwide Holdings Inc",
            false
        ),
        StockTickerAndDesc(
            "IBM",
            "International Business Machines Corporation",
            false
        ),
        StockTickerAndDesc(
            "AMT",
            "American Tower Corporation",
            false
        ),
        StockTickerAndDesc(
            "NFLX",
            "Netflix Inc.",
            false
        ),
        StockTickerAndDesc(
            "CL",
            "Colgate-Palmolive Company",
            false
        ),
        StockTickerAndDesc(
            "WMT",
            "Walmart Inc.",
            false
        ),
        StockTickerAndDesc(
            "GD",
            "General Dynamics Corporation",
            false
        ),
        StockTickerAndDesc(
            "MO",
            "Altria Group Inc",
            false
        ),
        StockTickerAndDesc(
            "FDX",
            "FedEx Corporation",
            false
        ),
        StockTickerAndDesc(
            "MCD",
            "McDonald’s Corporation",
            false
        ),
        StockTickerAndDesc(
            "RTN",
            "Raytheon Company",
            false
        ),
        StockTickerAndDesc(
            "GE",
            "General Electric Company",
            false
        ),
        StockTickerAndDesc(
            "WBA",
            "Walgreens Boots Alliance Inc",
            false
        ),
        StockTickerAndDesc(
            "HON",
            "Honeywell International Inc.",
            false
        ),
        StockTickerAndDesc(
            "NOC",
            "Northrop Grumman Corporation",
            false
        ),
        StockTickerAndDesc(
            "MDT",
            "Medtronic plc",
            false
        ),
        StockTickerAndDesc(
            "BIIB",
            "Biogen Inc.",
            false
        ),
        StockTickerAndDesc(
            "ABT",
            "Abbott Laboratories",
            false
        ),
        StockTickerAndDesc(
            "BDX",
            "Becton Dickinson and Company",
            false
        ),
        StockTickerAndDesc(
            "TXN",
            "Texas Instruments Incorporated",
            false
        ),
        StockTickerAndDesc(
            "ANTM",
            "Anthem Inc.",
            false
        ),
        StockTickerAndDesc(
            "BMY",
            "Bristol-Myers Squibb Company",
            false
        ),
        StockTickerAndDesc(
            "AET",
            "Aetna Inc.",
            false
        ),
        StockTickerAndDesc(
            "ADBE",
            "Adobe Systems Incorporated",
            false
        ),
        StockTickerAndDesc(
            "EOG",
            "EOG Resources Inc.",
            false
        ),
        StockTickerAndDesc(
            "UNP",
            "Union Pacific Corporation",
            false
        ),
        StockTickerAndDesc(
            "BK",
            "Bank of New York Mellon Corporation",
            false
        ),
        StockTickerAndDesc(
            "GILD",
            "Gilead Sciences Inc.",
            false
        ),
        StockTickerAndDesc(
            "ATVI",
            "Activision Blizzard Inc.",
            false
        )
    )
    private var stockTickerAndDescListSearch: MutableList<StockTickerAndDesc> = mutableListOf()

    private val stocksPerPage = 10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolBar))
        setContentView(R.layout.activity_list)

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

                            stock_recycler.swapAdapter(adapterSearch, false)

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
                            for(i in 0 until stockTickerAndDescList.size){
                                val currentTickerAndDesc = stockTickerAndDescList[i]
                                if(currentTickerAndDesc.ticker.toLowerCase(Locale.getDefault()).startsWith(searchString) ||
                                    currentTickerAndDesc.description.toLowerCase(Locale.getDefault()).startsWith(searchString)){
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
                favourites.add(stock)
                for(i in 0 until stockTickerAndDescList.size){
                    if(stockTickerAndDescList[i].ticker == stock.ticker){
                        stockTickerAndDescList[i].isFavourite = true
                    }
                }
            }

            override fun onRemovedFromFavorite(stock: StockDTO) {
                for(i in 0 until favourites.size){
                    if(stock.ticker == favourites[i].ticker){
                        favourites.removeAt(i)
                        break
                    }
                }

                for(i in 0 until stockTickerAndDescList.size){
                    if(stockTickerAndDescList[i].ticker == stock.ticker){
                        stockTickerAndDescList[i].isFavourite = false
                    }
                }
            }

        }

        adapterStocks = StockAdapter(stockDTOS, context)
        adapterStocks!!.stockAdapterListener = object : StockAdapterListener{
            override fun onMarkedAsFavorite(stock: StockDTO) {
                favourites.add(stock)
                for(i in 0 until stockTickerAndDescList.size){
                    if(stockTickerAndDescList[i].ticker == stock.ticker){
                        stockTickerAndDescList[i].isFavourite = true
                    }
                }
            }

            override fun onRemovedFromFavorite(stock: StockDTO) {
                for(i in 0 until favourites.size){
                    if(stock.ticker == favourites[i].ticker){
                        favourites.removeAt(i)
                        break
                    }
                }

                for(i in 0 until stockTickerAndDescList.size){
                    if(stockTickerAndDescList[i].ticker == stock.ticker){
                        stockTickerAndDescList[i].isFavourite = false
                    }
                }
            }

        }

        adapterFavourites = StockAdapter(favourites, context)
        adapterFavourites!!.stockAdapterListener = object : StockAdapterListener{
            override fun onMarkedAsFavorite(stock: StockDTO) {
                favourites.add(stock)
                for(i in 0 until stockTickerAndDescList.size){
                    if(stockTickerAndDescList[i].ticker == stock.ticker){
                        stockTickerAndDescList[i].isFavourite = true
                    }
                }
            }

            override fun onRemovedFromFavorite(stock: StockDTO) {
                for(i in 0 until favourites.size){
                    if(stock.ticker == favourites[i].ticker){
                        favourites.removeAt(i)
                        break
                    }
                }

                for(i in 0 until stockTickerAndDescList.size){
                    if(stockTickerAndDescList[i].ticker == stock.ticker){
                        stockTickerAndDescList[i].isFavourite = false
                    }
                }
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
                if(dy >= 5){
                    //hideProgressBar()
                }
                if(dy > 0){
                    if(layoutManager.childCount + layoutManager.findFirstVisibleItemPosition() >= layoutManager.itemCount && !isLoading){
                        if(isInFavorite){
                            getPriceForStocks(stockTickerAndDescList.filter { it.isFavourite }.toMutableList(), adapterFavourites!!, stockDTOS)
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
            stock_recycler.swapAdapter(adapterStocks, false)
        }
        else{
            stock_recycler.swapAdapter(adapterSearch, false)
        }

    }

    fun showFavourites(){
        isInFavorite = true

        setButtonActive(favourite)
        setButtonInactive(stock)

        stock_recycler.swapAdapter(adapterFavourites, false)
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
        for(i in currentLoadedStocks until currentLoadedStocks + stocksPerPage){
            if(i < tickerAndDescList.size){
                currentTicker = tickerAndDescList[i]
            }
            else{
                break
            }

            showProgressBar()
            requestsCount++
            getPrice(currentTicker, adapter, stocksDTOList)
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
                        if(requestsCount == 0){
                            hideProgressBar()
                            stock_recycler.swapAdapter(adapter, false)
                        }
                    }

                    else if(requestsCount < 0){
                        requestsCount = 0
                    }
                }
            })
    }
}
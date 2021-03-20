package com.example.yandexmobiletest.activities

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
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
import com.example.yandexmobiletest.stocks.StockTickerAndDesc
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
    private var requestsCount = 0
    private var searchRequestCount = 0

    private var adapterStocks: RecyclerView.Adapter<StockAdapter.StockHolder>? = null
    private var adapterFavourites: RecyclerView.Adapter<StockAdapter.StockHolder>? = null
    private var adapterSearch: RecyclerView.Adapter<StockAdapter.StockHolder>? = null

    private var stockDTOS: MutableList<StockDTO> = mutableListOf()
    private var stockDTOSSearch: MutableList<StockDTO> = mutableListOf()

    private var timer: Timer = Timer()

    private var isLoading = false
    private var isSearching = false

    private var stockTickerAndDescList: MutableList<StockTickerAndDesc> = mutableListOf(
        StockTickerAndDesc("AAPL", "Apple Inc."), StockTickerAndDesc("BKNG", "Booking Holdings Inc."),
        StockTickerAndDesc("MSFT", "Microsoft Corporation"), StockTickerAndDesc("AVGO", "Broadcom Limited"),
        StockTickerAndDesc("AMZN", "Amazon.com Inc."), StockTickerAndDesc("ACN", "Accenture Plc Class A"),
        StockTickerAndDesc("FB", "Facebook Inc. Class A"), StockTickerAndDesc("UTX", "United Technologies Corporation"),
        StockTickerAndDesc("JPM", "JPMorgan Chase & Co."), StockTickerAndDesc("GS", "Goldman Sachs Group Inc."),
        StockTickerAndDesc("BRK.B", "Berkshire Hathaway Inc. Class B"), StockTickerAndDesc("SLB", "Schlumberger NV"),
        StockTickerAndDesc("JNJ", "Johnson & Johnson"), StockTickerAndDesc("CAT", "Caterpillar Inc."),
        StockTickerAndDesc("GOOG", "Alphabet Inc. Class C"), StockTickerAndDesc("PYPL", "PayPal Holdings Inc"),
        StockTickerAndDesc("GOOGL", "Alphabet Inc. Class A"), StockTickerAndDesc("QCOM", "QUALCOMM Incorporated"),
        StockTickerAndDesc("XOM", "Exxon Mobil Corporation"), StockTickerAndDesc("CRM", "salesforce.com inc."),
        StockTickerAndDesc("BAC", "Bank of America Corporation"), StockTickerAndDesc("NKE", "NIKE Inc. Class B"),
        StockTickerAndDesc("WFC", "Wells Fargo & Company"), StockTickerAndDesc("TMO", "Thermo Fisher Scientific Inc."),
        StockTickerAndDesc("INTC", "Intel Corporation"), StockTickerAndDesc("USB", "U.S. Bancorp"),
        StockTickerAndDesc("T", "AT&T Inc."), StockTickerAndDesc("SBUX", "Starbucks Corporation"),
        StockTickerAndDesc("V", "Visa Inc. Class A"), StockTickerAndDesc("LMT", "Lockheed Martin Corporation"),
        StockTickerAndDesc("CSCO", "Cisco Systems Inc."), StockTickerAndDesc("COST", "Costco Wholesale Corporation"),
        StockTickerAndDesc("CVX", "Chevron Corporation"), StockTickerAndDesc("MS", "Morgan Stanley"),
        StockTickerAndDesc("UNH", "UnitedHealth Group Incorporated"), StockTickerAndDesc("PNC", "PNC Financial Services Group Inc."),
        StockTickerAndDesc("PFE", "Pfizer Inc."), StockTickerAndDesc("LLY", "Eli Lilly and Company"),
        StockTickerAndDesc("HD", "Home Depot Inc."), StockTickerAndDesc("UPS", "United Parcel Service Inc. Class B"),
        StockTickerAndDesc("PG", "Procter & Gamble Company"), StockTickerAndDesc("TWX", "Time Warner Inc."),
        StockTickerAndDesc("VZ", "Verizon Communications Inc."), StockTickerAndDesc("NEE", "NextEra Energy Inc."),
        StockTickerAndDesc("C", "Citigroup Inc."), StockTickerAndDesc("CELG", "Celgene Corporation"),
        StockTickerAndDesc("ABBV", "AbbVie Inc."), StockTickerAndDesc("LOW", "Lowe’s Companies Inc."),
        StockTickerAndDesc("BA", "Boeing Company"), StockTickerAndDesc("BLK", "BlackRock Inc."),
        StockTickerAndDesc("KO", "Coca-Cola Company"), StockTickerAndDesc("CVS", "CVS Health Corporation"),
        StockTickerAndDesc("CMCSA", "Comcast Corporation Class A"), StockTickerAndDesc("AXP", "American Express Company"),
        StockTickerAndDesc("MA", "Mastercard Incorporated Class A"), StockTickerAndDesc("MU", "Micron Technology Inc."),
        StockTickerAndDesc("PM", "Philip Morris International Inc."), StockTickerAndDesc("CHTR", "Charter Communications Inc. Class A"),
        StockTickerAndDesc("PEP", "PepsiCo Inc."), StockTickerAndDesc("SCHW", "Charles Schwab Corporation"),
        StockTickerAndDesc("ORCL", "Oracle Corporation"), StockTickerAndDesc("MDLZ", "Mondelez International Inc. Class A"),
        StockTickerAndDesc("DIS", "Walt Disney Company"), StockTickerAndDesc("CB", "Chubb Limited"),
        StockTickerAndDesc("MRK", "Merck & Co. Inc."), StockTickerAndDesc("COP", "ConocoPhillips"),
        StockTickerAndDesc("NVDA", "NVIDIA Corporation"), StockTickerAndDesc("AMAT", "Applied Materials Inc."),
        StockTickerAndDesc("MMM", "3M Company"), StockTickerAndDesc("DHR", "Danaher Corporation"),
        StockTickerAndDesc("AMGN", "Amgen Inc."), StockTickerAndDesc("HLT", "Hilton Worldwide Holdings Inc"),
        StockTickerAndDesc("IBM", "International Business Machines Corporation"), StockTickerAndDesc("AMT", "American Tower Corporation"),
        StockTickerAndDesc("NFLX", "Netflix Inc."), StockTickerAndDesc("CL", "Colgate-Palmolive Company"),
        StockTickerAndDesc("WMT", "Walmart Inc."), StockTickerAndDesc("GD", "General Dynamics Corporation"),
        StockTickerAndDesc("MO", "Altria Group Inc"), StockTickerAndDesc("FDX", "FedEx Corporation"),
        StockTickerAndDesc("MCD", "McDonald’s Corporation"), StockTickerAndDesc("RTN", "Raytheon Company"),
        StockTickerAndDesc("GE", "General Electric Company"), StockTickerAndDesc("WBA", "Walgreens Boots Alliance Inc"),
        StockTickerAndDesc("HON", "Honeywell International Inc."), StockTickerAndDesc("NOC", "Northrop Grumman Corporation"),
        StockTickerAndDesc("MDT", "Medtronic plc"), StockTickerAndDesc("BIIB", "Biogen Inc."),
        StockTickerAndDesc("ABT", "Abbott Laboratories"), StockTickerAndDesc("BDX", "Becton Dickinson and Company"),
        StockTickerAndDesc("TXN", "Texas Instruments Incorporated"), StockTickerAndDesc("ANTM", "Anthem Inc."),
        StockTickerAndDesc("BMY", "Bristol-Myers Squibb Company"), StockTickerAndDesc("AET", "Aetna Inc."),
        StockTickerAndDesc("ADBE", "Adobe Systems Incorporated"), StockTickerAndDesc("EOG", "EOG Resources Inc."),
        StockTickerAndDesc("UNP", "Union Pacific Corporation"), StockTickerAndDesc("BK", "Bank of New York Mellon Corporation"),
        StockTickerAndDesc("GILD", "Gilead Sciences Inc."), StockTickerAndDesc("ATVI", "Activision Blizzard Inc.")
    )
    private var stockTickerAndDescListSearch: MutableList<StockTickerAndDesc> = mutableListOf()

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

                timer.schedule(object : TimerTask(){
                    override fun run() {
                        runOnUiThread(object : Runnable{
                            override fun run() {
                                if(s.toString() != ""){
                                    isSearching = true
                                    searchRequestCount++
                                    showProgressBar()

                                    stockDTOSSearch.clear()
                                    stockTickerAndDescListSearch.clear()
                                    stock_recycler.swapAdapter(adapterSearch, true)

                                    retrofitService.getBestMatchingTickerOrName("https://finnhub.io/api/v1/search?q=${s.toString()}&token=c19j8rf48v6prmim2iog")
                                        .enqueue(object : Callback<BestMatchingStockList>{
                                            override fun onFailure(call: Call<BestMatchingStockList>, t: Throwable) {
                                                Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
                                            }

                                            override fun onResponse(
                                                call: Call<BestMatchingStockList>,
                                                response: Response<BestMatchingStockList>
                                            ) {
                                                searchRequestCount--
                                                if(searchRequestCount == 0){
                                                    if(response.errorBody() != null){
                                                        Toast.makeText(context, "err", Toast.LENGTH_SHORT).show()
                                                    }

                                                    if(response.body() != null){
                                                        val responseList = response.body()!!.result
                                                        val currentLoadedStocks = response.body()!!.count
                                                        for(i in 0 until currentLoadedStocks){
                                                            stockTickerAndDescListSearch.add(StockTickerAndDesc(responseList[i].symbol, responseList[i].description))
                                                        }
                                                        getPriceForStocks(stockTickerAndDescListSearch, adapterSearch!!, stockDTOSSearch)
                                                    }

                                                    if(requestsCount == 0){
                                                        hideProgressBar()
                                                    }
                                                }
                                            }
                                        })
                                }
                                else{
                                    isSearching = false
                                    stock_recycler.swapAdapter(adapterStocks, true)
                                }
                            }
                        })
                    }
                }, 1000)
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

        adapterSearch = StockAdapter(stockDTOSSearch, context)
        adapterStocks = StockAdapter(stockDTOS, context)
        adapterFavourites = StockAdapter(favourites, context)

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
                        if(isSearching){
                            getPriceForStocks(stockTickerAndDescList, adapterStocks!!, stockDTOSSearch)
                        }
                        else{
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
        setButtonActive(stock)
        setButtonInactive(favourite)
        //adapterStocks = StockAdapter(stockDTOS, context)
        stock_recycler.swapAdapter(adapterStocks, false)

    }

    fun showFavourites(){
        setButtonActive(favourite)
        setButtonInactive(stock)
        updateFavourites()
        stock_recycler.swapAdapter(adapterFavourites, false)
    }

    fun showProgressBar(){
        isLoading = true
        progress_Bar.visibility = View.VISIBLE
    }

    fun hideProgressBar(){
        isLoading = false
        progress_Bar.visibility = View.INVISIBLE
    }

    fun updateFavourites(){
        favourites = stockDTOS.filter { it.isFavourite }.toMutableList()
    }

    fun getStocksList(){
        showProgressBar()
        getPriceForStocks(stockTickerAndDescList, adapterStocks!!, stockDTOS)
        /*
        retrofitService.getStocksResponseList().enqueue(object : Callback<StocksList>{
            override fun onFailure(call: Call<StocksList>, t: Throwable) {
                Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<StocksList>,
                response: Response<StocksList>
            ) {
                if(response.body() != null){

                    for(i in response.body()!!.indices){
                        stockTickerAndDescList.add(StockTickerAndDesc(response.body()!![i].symbol, response.body()!![i].description))
                    }
                }

                getPriceForStocks(stockTickerAndDescList, adapterStocks!!, currentShowingStocks)
                currentShowingStocks += stocksPerPage
            }
        })
         */
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
                    requestsCount--
                    if(response.errorBody() != null){
                        if(requestsCount == 0){
                            hideProgressBar()
                            Toast.makeText(context, "Failed connect to server", Toast.LENGTH_SHORT).show()
                        }
                    }

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

                        stocksDTOList.add(
                            StockDTO(
                                stock.ticker,
                                stock.description,
                                response.body()!!.c.toString(),
                                priceChange
                            )
                        )

                        if(requestsCount == 0){
                            hideProgressBar()
                            stock_recycler.swapAdapter(adapter, false)
                        }
                    }
                }
            })
    }
}
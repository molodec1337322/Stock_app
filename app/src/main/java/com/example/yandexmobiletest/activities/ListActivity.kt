package com.example.yandexmobiletest.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.yandexmobiletest.R
import com.example.yandexmobiletest.stocks.Stock
import com.example.yandexmobiletest.stocks.StockAdapter
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.btn_favourite
import kotlinx.android.synthetic.main.stocks.*

class ListActivity : AppCompatActivity() {

    lateinit var search: EditText
    lateinit var stock: Button
    lateinit var favourite: Button
    lateinit var stock_recycler: RecyclerView

    private var adapterStocks: RecyclerView.Adapter<StockAdapter.StockHolder>? = null
    private var adapterFavourites: RecyclerView.Adapter<StockAdapter.StockHolder>? = null
    private var stocks: MutableList<Stock> = mutableListOf(
        Stock("AAPL", "Apple inc.", "$5000.00","+$12.03(0.1%)", true),
        Stock("YYNDX", "Yandex LLC", "$45200.00","-$12.03(0.01%)", true),
        Stock("GOOGL", "Alphabet Class P", "$5000.00","+$12.03(0.15%)", false),
        Stock("AMZN", "Amazon", "$5540.00","+$12.03(0.15%)", false),
        Stock("BAC", "Bank of America", "$300.00","-$125.03(33.1%)", true)
    )

    private var favourites: MutableList<Stock> = mutableListOf(
        //Stock("AAPL", "Apple inc.", "$5000.00","+$12.03(0.1%)", true),
        //Stock("YYNDX", "Yandex LLC", "$45200.00","-$12.03(0.01%)", true),
        //Stock("BAC", "Bank of America", "$300.00","-$125.03(33.1%)", true)
    )

    private val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)




        search = etd_search
        stock = btn_stocks
        favourite = btn_favourite

        stock.setOnClickListener(View.OnClickListener {
            showStocks()
        })
        favourite.setOnClickListener(View.OnClickListener {
            showFavourites()
        })

        stock_recycler = recycle_list_stock
        stock_recycler.setHasFixedSize(true)
        stock_recycler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        adapterStocks = StockAdapter(stocks, context)
        stock_recycler.adapter = adapterStocks
        updateFavourites()
        showStocks()
        //stock_recycler.adapter = adapterStocks
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
        adapterStocks = StockAdapter(stocks, context)
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
        favourites = stocks.filter { it.isFavourite }.toMutableList()
    }
}
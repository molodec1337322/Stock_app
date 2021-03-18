package com.example.yandexmobiletest.stocks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexmobiletest.R

class StockAdapter(val stocks: MutableList<Stock>, val context: Context):
    RecyclerView.Adapter<StockAdapter.StockHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockHolder {
        val stockView = LayoutInflater.from(parent.context).inflate(R.layout.stocks, parent, false)
        return StockHolder(stockView)
    }

    override fun getItemCount(): Int {
        return stocks.size;
    }

    override fun onBindViewHolder(holder: StockHolder, position: Int) {
        holder.ticker.text = stocks[position].ticker

        holder.company.text = stocks[position].company

        if(!stocks[position].isFavourite){
            holder.favourite.setBackgroundResource(R.drawable.ic_baseline_star_not_favourite_40)
        }
        else{
            holder.favourite.setBackgroundResource(R.drawable.ic_baseline_star_favourite_40)
        }
        holder.favourite.setOnClickListener(View.OnClickListener {
            if(!stocks[position].isFavourite){
                stocks[position].isFavourite = true
                holder.favourite.setBackgroundResource(R.drawable.ic_baseline_star_favourite_40)
            }
            else{
                stocks[position].isFavourite = false
                holder.favourite.setBackgroundResource(R.drawable.ic_baseline_star_not_favourite_40)
            }
        })

        holder.price.text = stocks[position].price

        holder.priceChange.text = stocks[position].priceChange
        if(stocks[position].priceChange[0] == '+'){
            holder.priceChange.setTextColor(context.getColor(R.color.colorIncreasePriceGreen))
        }
        else if(stocks[position].priceChange[0] == '-'){
            holder.priceChange.setTextColor(context.getColor(R.color.colorDecreasePriceRed))
        }
        else{
            holder.priceChange.setTextColor(context.getColor(R.color.colorNotFavourite))
        }

        if(position % 2 == 1){
            holder.layout.setBackgroundResource(R.drawable.roundrectangle)
        }
        else{
            holder.layout.setBackgroundResource(R.drawable.roundrectangle_dark)
        }
    }

    class StockHolder(stockView: View): RecyclerView.ViewHolder(stockView){
        val ticker = stockView.findViewById<TextView>(R.id.tv_ticker)
        val company = stockView.findViewById<TextView>(R.id.tv_company_name)
        val favourite = stockView.findViewById<ImageButton>(R.id.btn_favourite)
        val price = stockView.findViewById<TextView>(R.id.tv_price)
        val priceChange = stockView.findViewById<TextView>(R.id.tv_price_change)

        val layout = stockView.findViewById<RelativeLayout>(R.id.lay_stock)
    }
}
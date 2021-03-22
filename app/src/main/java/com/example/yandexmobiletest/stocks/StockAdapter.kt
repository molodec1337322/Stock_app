package com.example.yandexmobiletest.stocks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexmobiletest.R
import com.example.yandexmobiletest.stocks.models.StockDTO

class StockAdapter(val stockDTOS: MutableList<StockDTO>, val context: Context):
    RecyclerView.Adapter<StockAdapter.BaseHolder>() {

    companion object {
        private const val VIEW_TYPE_LOADING = 1
        private const val VIEW_TYPE_NORMAL = 2
    }

    private var isShowingLoading = false


    var stockAdapterListener: StockAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        if(viewType == VIEW_TYPE_NORMAL){
            val stockView = LayoutInflater.from(parent.context).inflate(R.layout.stocks, parent, false)
            return StockHolder(stockView)
        }
        else if(viewType == VIEW_TYPE_LOADING){
            val loadingView = LayoutInflater.from(parent.context).inflate(R.layout.loading, parent, false)
            return LoadingHolder(loadingView)
        }
        return BaseHolder(parent)
    }

    override fun getItemCount(): Int {
        return stockDTOS.size + if(isShowingLoading){1} else {0}
    }

    override fun getItemViewType(position: Int): Int {
        if(position < stockDTOS.size){
            return VIEW_TYPE_NORMAL
        }
        return VIEW_TYPE_LOADING
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if(holder is StockHolder) {
            holder.bind(stockDTOS[position], context, stockAdapterListener)
        }
        else{

        }
    }

    fun hideLoading(){
        isShowingLoading = false
        notifyItemRemoved(itemCount)
    }

    fun showLoading(){
        isShowingLoading = true
        notifyItemInserted(itemCount + 1)
    }

    open class BaseHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    class StockHolder(stockView: View): BaseHolder(stockView){

        val ticker = stockView.findViewById<TextView>(R.id.tv_ticker)
        val company = stockView.findViewById<TextView>(R.id.tv_company_name)
        val favourite = stockView.findViewById<ImageButton>(R.id.btn_favourite)
        val price = stockView.findViewById<TextView>(R.id.tv_price)
        val priceChange = stockView.findViewById<TextView>(R.id.tv_price_change)

        val layout = stockView.findViewById<RelativeLayout>(R.id.lay_stock)

        var stockAdapterListener: StockAdapterListener? = null

        fun bind(stockDTO : StockDTO, context: Context, newStockAdapterListener: StockAdapterListener?){
            stockAdapterListener = newStockAdapterListener

            this.ticker.text = stockDTO.ticker

            this.company.text = stockDTO.company

            if(!stockDTO.isFavourite){
                this.favourite.setBackgroundResource(R.drawable.ic_baseline_star_not_favourite_40)
            }
            else{
                this.favourite.setBackgroundResource(R.drawable.ic_baseline_star_favourite_40)
            }
            this.favourite.setOnClickListener(View.OnClickListener {
                if(!stockDTO.isFavourite){
                    stockDTO.isFavourite = true
                    this.favourite.setBackgroundResource(R.drawable.ic_baseline_star_favourite_40)
                    stockAdapterListener?.onMarkedAsFavorite(stockDTO)
                }
                else{
                    stockDTO.isFavourite = false
                    this.favourite.setBackgroundResource(R.drawable.ic_baseline_star_not_favourite_40)
                    stockAdapterListener?.onRemovedFromFavorite(stockDTO)
                }
            })

            this.price.text = stockDTO.price

            this.priceChange.text = stockDTO.priceChange
            if(stockDTO.priceChange[0] == '+'){
                this.priceChange.setTextColor(context.getColor(R.color.colorIncreasePriceGreen))
            }
            else if(stockDTO.priceChange[0] == '-'){
                this.priceChange.setTextColor(context.getColor(R.color.colorDecreasePriceRed))
            }
            else{
                this.priceChange.setTextColor(context.getColor(R.color.colorNotFavourite))
            }

            if(position % 2 == 1){
                this.layout.setBackgroundResource(R.drawable.roundrectangle)
            }
            else{
                this.layout.setBackgroundResource(R.drawable.roundrectangle_dark)
            }
        }
    }


    class LoadingHolder(loadingView: View): BaseHolder(loadingView){
        val loading = loadingView.findViewById<ProgressBar>(R.id.progressBar)
    }
}
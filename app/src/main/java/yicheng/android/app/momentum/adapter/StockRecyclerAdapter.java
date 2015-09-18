package yicheng.android.app.momentum.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;
import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.activity.StockDetailActivity;

/**
 * Created by ZhangY on 8/24/2015.
 */
public class StockRecyclerAdapter extends RecyclerView.Adapter<StockRecyclerAdapter.ViewHolder> {

    List<Stock> stockList;

    Context context;

    public StockRecyclerAdapter(List<Stock> stockList) {
        this.stockList = stockList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_stock, parent, false);

        context = parent.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("ACTIVITY_STOCK_DETAIL");
                v.getContext().startActivity(intent);
            }
        });


        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stock stock = stockList.get(position);

        String name = stock.getName();
        StockQuote quote = stock.getQuote();
        String price = quote.getPrice().toString();
        String priceChange = quote.getChange().toString();
        String priceChangePercent = quote.getChangeInPercent().toString();


        holder.recycler_item_stock_name_textView.setText(name);
        holder.recycler_item_stock_price_textView.setText(price);
        holder.recycler_item_stock_price_change_textView.setText(priceChange + " " + priceChangePercent);

        if (priceChangePercent.substring(0, 1).equals("-")){
            holder.recycler_item_stock_price_change_textView.setTextColor(context.getResources().getColor(R.color.theme_accent));
        }
        else{
            holder.recycler_item_stock_price_change_textView.setTextColor(context.getResources().getColor(R.color.theme_primary));
        }

    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recycler_item_stock_name_textView;
        TextView recycler_item_stock_price_textView;
        TextView recycler_item_stock_price_change_textView;
        CheckBox recycler_item_stock_favorite_checkBox;
        CheckBox recycler_item_stock_portfolio_checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_item_stock_name_textView = (TextView) itemView.findViewById(R.id.recycler_item_stock_name_textView);
            recycler_item_stock_price_textView = (TextView) itemView.findViewById(R.id.recycler_item_stock_price_textView);
            recycler_item_stock_price_change_textView = (TextView) itemView.findViewById(R.id.recycler_item_stock_price_change_textView);
            recycler_item_stock_favorite_checkBox = (CheckBox) itemView.findViewById(R.id.recycler_item_stock_favorite_checkBox);
            recycler_item_stock_portfolio_checkBox = (CheckBox) itemView.findViewById(R.id.recycler_item_stock_portfolio_checkBox);
        }
    }

}

package yicheng.android.app.momentum.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snappydb.DB;

import java.util.List;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;
import yicheng.android.app.momentum.R;

/**
 * Created by ZhangY on 9/21/2015.
 */
public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.ViewHolder> {

    List<Stock> stockList;


    Context context;

    View itemView;

    Stock stock;

    DB favoriteDB;

    public PortfolioRecyclerAdapter(List<Stock> stockList) {
        this.stockList = stockList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_favorite, parent, false);

        context = parent.getContext();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        stock = stockList.get(position);

        final String name = stock.getName();
        StockQuote quote = stock.getQuote();
        String price = quote.getPrice().toString();
        String priceChange = quote.getChange().toString();
        String priceChangePercent = quote.getChangeInPercent().toString();

        holder.recycler_item_portfolio_name_textView.setText(name);
        holder.recycler_item_portfolio_price_textView.setText(price);
        holder.recycler_item_portfolio_price_change_textView.setText(priceChange + " " + priceChangePercent + "%");


        if (priceChangePercent.substring(0, 1).equals("-")) {
            holder.recycler_item_portfolio_price_change_textView.setTextColor(context.getResources().getColor(R.color.theme_accent));
        } else {
            holder.recycler_item_portfolio_price_change_textView.setTextColor(context.getResources().getColor(R.color.theme_primary));
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("ACTIVITY_STOCK_DETAIL");
                intent.putExtra("Stock Name", name);
                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recycler_item_portfolio_name_textView;
        TextView recycler_item_portfolio_price_textView;
        TextView recycler_item_portfolio_price_change_textView;


        public ViewHolder(View itemView) {
            super(itemView);

            recycler_item_portfolio_name_textView = (TextView) itemView.findViewById(R.id.recycler_item_portfolio_name_textView);
            recycler_item_portfolio_price_textView = (TextView) itemView.findViewById(R.id.recycler_item_portfolio_price_textView);
            recycler_item_portfolio_price_change_textView = (TextView) itemView.findViewById(R.id.recycler_item_portfolio_price_change_textView);


        }
    }

}

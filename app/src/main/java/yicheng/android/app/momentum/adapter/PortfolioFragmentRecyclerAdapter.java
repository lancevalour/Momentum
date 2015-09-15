package yicheng.android.app.momentum.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yahoofinance.Stock;
import yicheng.android.app.momentum.R;

/**
 * Created by ZhangY on 8/24/2015.
 */
public class PortfolioFragmentRecyclerAdapter extends RecyclerView.Adapter<PortfolioFragmentRecyclerAdapter.ViewHolder> {

    List<Stock> stockList;


    public PortfolioFragmentRecyclerAdapter(List<Stock> stockList) {
        this.stockList = stockList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_portfolio_item, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stock stock = stockList.get(position);

        holder.title.setText(stock.getName());
        holder.content.setText(stock.getQuote().getPrice().toString());

    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.fragment_portfolio_item_title_textView);
            content = (TextView) itemView.findViewById(R.id.fragment_portfolio_item_content_textView);

        }
    }

}

package yicheng.android.app.momentum.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.adapter.PortfolioFragmentRecyclerAdapter;

/**
 * Created by ZhangY on 8/24/2015.
 */
public class PortfolioFragment extends Fragment {
    ViewGroup rootView;

    RecyclerView fragment_portfolio_recyclerView;

    List<Stock> stockList;

    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_portfolio, container, false);


        return rootView;

    }

    private void setHandlerControl() {

    }


    private void loadStockData() {

    }

    private void initiateComponents() {

    }


}

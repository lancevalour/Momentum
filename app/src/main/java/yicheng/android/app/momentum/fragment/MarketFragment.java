package yicheng.android.app.momentum.fragment;

import android.app.Fragment;
import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
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
import yicheng.android.app.momentum.adapter.StockRecyclerAdapter;

/**
 * Created by ZhangY on 9/16/2015.
 */
public class MarketFragment extends Fragment {
    ViewGroup rootView;

    SwipeRefreshLayout fragment_market_refreshLayout;

    RecyclerView fragment_market_recyclerView;

    List<Stock> stockList;

    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_market, container, false);

        initiateComponents();

        setHandlerControl();

        loadStockData();

        setComponentControl();


        return rootView;

    }

    private void setHandlerControl() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        fragment_market_recyclerView.setAdapter(new StockRecyclerAdapter(stockList));
                        if (fragment_market_refreshLayout.isRefreshing()) {
                            fragment_market_refreshLayout.setRefreshing(false);
                        }
                    }
                    break;


                }
            }
        };

    }


    private void loadStockData() {
        stockList = new ArrayList<Stock>();

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            stockList.add(YahooFinance.get("^QNET"));
                            stockList.add(YahooFinance.get("^IXCO"));
                            stockList.add(YahooFinance.get("^IXIC"));
                            stockList.add(YahooFinance.get("^NBI"));
                            stockList.add(YahooFinance.get("^CHXN"));
                            stockList.add(YahooFinance.get("^IXHC"));


                            Message msg = Message.obtain();
                            msg.what = 1;
                            handler.sendMessage(msg);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    private void initiateComponents() {
        fragment_market_recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_market_recyclerView);


        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        fragment_market_recyclerView.setLayoutManager(layoutManager);


        fragment_market_refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_market_refreshLayout);

        fragment_market_refreshLayout.setColorSchemeColors(Color
                .parseColor("#4b994f"));

        fragment_market_refreshLayout.setRefreshing(true);


    }

    private void setComponentControl() {
        setRefreshLayoutControl();
    }

    private void setRefreshLayoutControl() {
        fragment_market_refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadStockData();
            }
        });
    }


}

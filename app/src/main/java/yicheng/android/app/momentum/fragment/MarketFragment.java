package yicheng.android.app.momentum.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.snappydb.DB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.adapter.MarketRecyclerAdapter;

/**
 * Created by ZhangY on 9/16/2015.
 */
public class MarketFragment extends Fragment {
    ViewGroup rootView;

    ProgressBar fragment_market_progressBar;

    SwipeRefreshLayout fragment_market_refreshLayout;

    RecyclerView fragment_market_recyclerView;

    StaggeredGridLayoutManager layoutManager;


    List<Stock> stockList;

    DB favoriteDB;

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
                        fragment_market_progressBar.setVisibility(View.INVISIBLE);

                        if (fragment_market_refreshLayout.isRefreshing()) {
                            fragment_market_refreshLayout.setRefreshing(false);
                        }


                        fragment_market_recyclerView.setAdapter(new MarketRecyclerAdapter(stockList));

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
                            String[] names = new String[]{"CNYMUR=X", "^IXCO",
                                    "^IXIC", "F000002L06.TO", "FSRBX", "FSPHX"
                            };

                            Map<String, Stock> map = YahooFinance.get(names);
                            stockList.addAll(map.values());

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
        fragment_market_progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_market_progressBar);
        fragment_market_progressBar.setVisibility(View.VISIBLE);

        fragment_market_recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_market_recyclerView);


        layoutManager = new StaggeredGridLayoutManager(2, 1);
        fragment_market_recyclerView.setLayoutManager(layoutManager);
        fragment_market_recyclerView.setItemAnimator(new DefaultItemAnimator());


        fragment_market_refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_market_refreshLayout);

        fragment_market_refreshLayout.setColorSchemeColors(Color
                .parseColor("#4b994f"));

        fragment_market_refreshLayout.setRefreshing(true);


    }

    private void setComponentControl() {
        setRefreshLayoutControl();
        setRecyclerViewControl();

    }

    private void setRecyclerViewControl() {
      /*  fragment_market_recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(int newState) {
            }

            @Override
            public void onScrolled(int dx, int dy) {
                LinearLayout.LayoutParams
                fragment_market_refreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });*/
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

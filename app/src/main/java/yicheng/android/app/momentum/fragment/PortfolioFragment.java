package yicheng.android.app.momentum.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snappydb.DB;
import com.snappydb.KeyIterator;
import com.snappydb.SnappydbException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.adapter.FavoriteRecyclerAdapter;
import yicheng.android.app.momentum.model.Snappy;

/**
 * Created by ZhangY on 8/24/2015.
 */
public class PortfolioFragment extends Fragment {
    ViewGroup rootView;

    RecyclerView fragment_portfolio_recyclerView;

    StaggeredGridLayoutManager layoutManager;

    TextView fragment_portfolio_textView;


    List<Stock> stockList;

    DB portfolioDB;

    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_portfolio, container, false);

        initiateComponents();

        setHandlerControl();

        loadStockData();


        return rootView;

    }

    private void setHandlerControl() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        if (stockList.size() == 0) {
                            fragment_portfolio_textView.setVisibility(View.VISIBLE);

                        } else {
                            fragment_portfolio_textView.setVisibility(View.INVISIBLE);
                        }

                        fragment_portfolio_recyclerView.setAdapter(new FavoriteRecyclerAdapter(stockList));

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

                            String[] keys = null;

                            try {

                                portfolioDB = Snappy.open(rootView.getContext(), Snappy.DB_NAME_PORTFOLIO);
                                KeyIterator keyIt = portfolioDB.allKeysIterator();
                                if (keyIt.hasNext()) {
                                    keys = keyIt.next(10000);
                                    System.out.println(Arrays.toString(keys));
                                }
                                keyIt.close();
                                portfolioDB.close();

                            } catch (SnappydbException e) {
                                e.printStackTrace();
                            }


                            if (keys != null) {
                                Map<String, Stock> map = YahooFinance.get(keys);
                                stockList.addAll(map.values());
                            }

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
        fragment_portfolio_recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_portfolio_recyclerView);


        layoutManager = new StaggeredGridLayoutManager(2, 1);
        fragment_portfolio_recyclerView.setLayoutManager(layoutManager);
        fragment_portfolio_recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(fragment_portfolio_recyclerView);

        fragment_portfolio_textView = (TextView) rootView.findViewById(R.id.fragment_portfolio_textView);
        fragment_portfolio_textView.setVisibility(View.INVISIBLE);
    }


}

package yicheng.android.app.momentum.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.snappydb.DB;
import com.squareup.picasso.Picasso;

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
    FragmentCallback fragmentCallback;

    ViewGroup rootView;

    ProgressBar fragment_market_progressBar;

    SwipeRefreshLayout fragment_market_refreshLayout;

    ObservableRecyclerView fragment_market_recyclerView;

    StaggeredGridLayoutManager layoutManager;

    Toolbar fragment_market_toolbar;

    ImageView fragment_market_imageView;

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

                        if (!fragment_market_refreshLayout.isEnabled()) {
                            fragment_market_refreshLayout.setEnabled(true);
                        }

                        fragment_market_progressBar.setVisibility(View.INVISIBLE);

                        if (fragment_market_refreshLayout.isRefreshing()) {
                            fragment_market_refreshLayout.setRefreshing(false);
                        }

                        layoutManager = new StaggeredGridLayoutManager(2, 1);
                        fragment_market_recyclerView.setLayoutManager(layoutManager);
                        fragment_market_recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                                    "^IXIC", "F000002L06.TO", "FSRBX", "FSPHX",
                                    "BABA", "APPL", "FIT", "FARO", "VTV", "VTI"
                            };

                            Map<String, Stock> map = YahooFinance.get(names);

                            stockList.addAll(map.values());

                            System.out.println("Stock Size: " + stockList.size());

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
        //fragment_market_recyclerView_background = rootView.findViewById(R.id.fragment_market_recyclerView_background);
        fragment_market_imageView = (ImageView) rootView.findViewById(R.id.fragment_market_imageView);
        Picasso.with(rootView.getContext()).load(R.drawable.market).into(fragment_market_imageView);

        fragment_market_toolbar = (Toolbar) rootView.findViewById(R.id.fragment_market_toolbar);
        fragment_market_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.theme_primary)));
        fragment_market_toolbar.setTitle(R.string.drawer_title_market);


        fragment_market_progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_market_progressBar);
        fragment_market_progressBar.setVisibility(View.VISIBLE);

        fragment_market_recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.fragment_market_recyclerView);


        layoutManager = new StaggeredGridLayoutManager(2, 1);
        fragment_market_recyclerView.setLayoutManager(layoutManager);
        fragment_market_recyclerView.setItemAnimator(new DefaultItemAnimator());


        RecyclerViewHeader paddingView = (RecyclerViewHeader) rootView.findViewById(R.id.fragment_market_recyclerView_header);
        paddingView.attachTo(fragment_market_recyclerView, true);


        fragment_market_refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_market_refreshLayout);

        fragment_market_refreshLayout.setColorSchemeColors(Color
                .parseColor("#4b994f"));

        fragment_market_refreshLayout.setRefreshing(true);


    }

    private void setComponentControl() {
        setToolbarControl();
        setRefreshLayoutControl();
        setRecyclerViewScrollControl();

    }

    private void setToolbarControl() {
        fragment_market_toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
        fragment_market_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallback.menuClick();
            }
        });
    }

    int parallaxImageHeight;

    private void setRecyclerViewScrollControl() {
        parallaxImageHeight = getResources().getDimensionPixelSize(
                R.dimen.flexible_space_image_height);

        fragment_market_recyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

                int baseColor = getResources().getColor(R.color.theme_primary);
                float alpha = Math.min(1, (float) (scrollY + 490 - parallaxImageHeight / 2) / parallaxImageHeight);

                fragment_market_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
                ViewHelper.setTranslationY(fragment_market_imageView, -scrollY - 490);

            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {

            }
        });
    }

    private void setRefreshLayoutControl() {
        fragment_market_refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragment_market_refreshLayout.setEnabled(false);
                loadStockData();
            }
        });
    }


    public interface FragmentCallback {
        void menuClick();

        void menuSearch();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentCallback = (FragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement Fragment Two.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentCallback = null;
    }


    @Override
    public void onResume() {
        super.onResume();

        loadStockData();
    }
}

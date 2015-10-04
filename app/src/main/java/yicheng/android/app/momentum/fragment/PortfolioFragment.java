package yicheng.android.app.momentum.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.nineoldandroids.view.ViewHelper;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
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
import yicheng.android.app.momentum.adapter.PortfolioRecyclerAdapter;
import yicheng.android.app.momentum.model.Snappy;

/**
 * Created by ZhangY on 8/24/2015.
 */
public class PortfolioFragment extends Fragment {
    FragmentCallback fragmentCallback;

    ViewGroup rootView;

    ProgressBar fragment_portfolio_progressBar;

    ObservableRecyclerView fragment_portfolio_recyclerView;

    StaggeredGridLayoutManager layoutManager;

    TextView fragment_portfolio_textView;

    Toolbar fragment_portfolio_toolbar;

    ImageView fragment_portfolio_imageView;


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

        setComponentControl();

        return rootView;

    }

    private void setHandlerControl() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        fragment_portfolio_progressBar.setVisibility(View.INVISIBLE);

                        if (stockList.size() == 0) {
                            fragment_portfolio_textView.setVisibility(View.VISIBLE);

                        } else {
                            fragment_portfolio_textView.setVisibility(View.INVISIBLE);
                        }

                        fragment_portfolio_recyclerView.setAdapter(new PortfolioRecyclerAdapter(stockList));

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

        fragment_portfolio_imageView = (ImageView) rootView.findViewById(R.id.fragment_portfolio_imageView);

        fragment_portfolio_toolbar = (Toolbar) rootView.findViewById(R.id.fragment_portfolio_toolbar);
        fragment_portfolio_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.theme_primary)));
        fragment_portfolio_toolbar.setTitle(R.string.drawer_title_portfolio);

        fragment_portfolio_progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_portfolio_progressBar);
        fragment_portfolio_progressBar.setVisibility(View.VISIBLE);

        fragment_portfolio_recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.fragment_portfolio_recyclerView);


        layoutManager = new StaggeredGridLayoutManager(2, 1);
        fragment_portfolio_recyclerView.setLayoutManager(layoutManager);
        fragment_portfolio_recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerViewHeader paddingView = (RecyclerViewHeader) rootView.findViewById(R.id.fragment_portfolio_recyclerView_header);

        paddingView.attachTo(fragment_portfolio_recyclerView, true);


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


    private void setComponentControl() {
        setToolbarControl();
        setRecyclerViewScrollControl();

    }


    private void setToolbarControl() {
        fragment_portfolio_toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
        fragment_portfolio_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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

        fragment_portfolio_recyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                int baseColor = getResources().getColor(R.color.theme_primary);
                float alpha = Math.min(1, (float) (scrollY + 490 - parallaxImageHeight) / parallaxImageHeight);

                fragment_portfolio_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
                ViewHelper.setTranslationY(fragment_portfolio_imageView, -scrollY - 490);
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {

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

}

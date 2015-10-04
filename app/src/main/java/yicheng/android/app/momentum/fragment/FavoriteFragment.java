package yicheng.android.app.momentum.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
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
import yicheng.android.app.momentum.activity.NavigationDrawerActivity;
import yicheng.android.app.momentum.adapter.FavoriteRecyclerAdapter;
import yicheng.android.app.momentum.model.Snappy;

/**
 * Created by ZhangY on 9/16/2015.
 */
public class FavoriteFragment extends Fragment {
    FragmentCallback fragmentCallback;

    ViewGroup rootView;

    ProgressBar fragment_favorite_progressBar;

    ObservableRecyclerView fragment_favorite_recyclerView;

    StaggeredGridLayoutManager layoutManager;

    TextView fragment_favorite_textView;

    Toolbar fragment_favorite_toolbar;

    ImageView fragment_favorite_imageView;


    List<Stock> stockList;

    Stock removedStock;
    int removedStockPosition;

    DB favoriteDB;

    Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favorite, container, false);


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
                        fragment_favorite_progressBar.setVisibility(View.INVISIBLE);

                        if (stockList.size() == 0) {
                            fragment_favorite_textView.setVisibility(View.VISIBLE);

                        } else {
                            fragment_favorite_textView.setVisibility(View.INVISIBLE);
                        }

                        fragment_favorite_recyclerView.setAdapter(new FavoriteRecyclerAdapter(stockList));

                    }
                    break;


                }
            }
        };
    }

    private void initiateComponents() {

        fragment_favorite_imageView = (ImageView) rootView.findViewById(R.id.fragment_favorite_imageView);

        fragment_favorite_toolbar = (Toolbar) rootView.findViewById(R.id.fragment_favorite_toolbar);
        fragment_favorite_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.theme_primary)));
        fragment_favorite_toolbar.setTitle(R.string.drawer_title_favorite);

        fragment_favorite_progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_favorite_progressBar);
        fragment_favorite_progressBar.setVisibility(View.VISIBLE);

        fragment_favorite_recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.fragment_favorite_recyclerView);


        layoutManager = new StaggeredGridLayoutManager(2, 1);
        fragment_favorite_recyclerView.setLayoutManager(layoutManager);
        fragment_favorite_recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerViewHeader paddingView = (RecyclerViewHeader) rootView.findViewById(R.id.fragment_favorite_recyclerView_header);

        paddingView.attachTo(fragment_favorite_recyclerView, true);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        // Toast.makeText(getActivity().getBaseContext(), "" + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        removedStockPosition = viewHolder.getAdapterPosition();

                        removedStock = stockList.remove(removedStockPosition);
                        fragment_favorite_recyclerView.getAdapter().notifyDataSetChanged();

                        try {
                            favoriteDB = Snappy.open(rootView.getContext(), Snappy.DB_NAME_FAVORITE);
                            favoriteDB.del(removedStock.getSymbol());
                            favoriteDB.close();

                        } catch (SnappydbException e) {
                            e.printStackTrace();
                        }

                        Snackbar snackBar = Snackbar.make(NavigationDrawerActivity.activity_navigation_drawer_coordinatorLayout, "Favorite Removed", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                stockList.add(removedStockPosition, removedStock);

                                fragment_favorite_recyclerView.getAdapter().notifyDataSetChanged();

                                try {
                                    favoriteDB = Snappy.open(rootView.getContext(), Snappy.DB_NAME_FAVORITE);
                                    favoriteDB.put(removedStock.getSymbol(), removedStock);
                                    favoriteDB.close();

                                } catch (SnappydbException e) {
                                    e.printStackTrace();
                                }


                                //  fragment_favorite_recyclerView.setAdapter(new FavoriteRecyclerAdapter(stockList));
                                Toast.makeText(getActivity().getBaseContext(), "Undo", Toast.LENGTH_SHORT).show();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.theme_primary));

                        View view = snackBar.getView();
                        TextView textView = (TextView)
                                view.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);

                        view.setMinimumHeight(120);
                        snackBar.show();


                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(fragment_favorite_recyclerView);

        fragment_favorite_textView = (TextView) rootView.findViewById(R.id.fragment_favorite_textView);
        fragment_favorite_textView.setVisibility(View.INVISIBLE);
    }


    String[] keys;

    private void loadStockData() {
        stockList = new ArrayList<Stock>();

        try {

            if (favoriteDB == null) {
                favoriteDB = Snappy.open(rootView.getContext(), Snappy.DB_NAME_FAVORITE);
            }
            KeyIterator keyIt = favoriteDB.allKeysIterator();
            if (keyIt.hasNext()) {
                keys = keyIt.next(10000);
                System.out.println(Arrays.toString(keys));
            }
            keyIt.close();
            favoriteDB.close();

        } catch (SnappydbException e) {
            e.printStackTrace();
        }


        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

/*

                            String[] names = new String[]{"CNYMUR=X", "^IXCO",
                                    "^IXIC", "F000002L06.TO", "FSRBX", "FSPHX",
                                    "BABA", "APPL"
                            };

                            Map<String, Stock> map = YahooFinance.get(names);
                            stockList.addAll(map.values());



*/

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


    private void setComponentControl() {
        setToolbarControl();
        setRecyclerViewScrollControl();
    }


    private void setToolbarControl() {
        fragment_favorite_toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
        fragment_favorite_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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


        fragment_favorite_recyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                int baseColor = getResources().getColor(R.color.theme_primary);
                float alpha = Math.min(1, (float) (scrollY + 490 - parallaxImageHeight) / parallaxImageHeight);

                fragment_favorite_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
                ViewHelper.setTranslationY(fragment_favorite_imageView, -scrollY - 490);
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

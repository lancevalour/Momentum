package yicheng.android.app.momentum.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.snappydb.DB;
import com.snappydb.SnappydbException;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.adapter.StockRecyclerAdapter;
import yicheng.android.app.momentum.model.Snappy;

/**
 * Created by ZhangY on 9/17/2015.
 */
public class StockDetailActivity extends AppCompatActivity {
    Button activity_stock_detail_sell_button, activity_stock_detail_buy_button;

    ProgressBar activity_stock_detail_progressBar, activity_stock_detail_chart_progressBar;

    ScrollView activity_stock_detail_scrollView;

    LineChart activity_stock_detail_chart;

    CheckBox activity_stock_detail_favorite_checkBox;

    TextView activity_stock_detail_symbol_textView, activity_stock_detail_price_textView, activity_stock_detail_price_change_textView;

    Toolbar activity_stock_detail_toolbar;

    List<HistoricalQuote> stockHistory;

    Stock stock;
    String stockName;
    String stockSymbol;

    Handler handler;

    DB favoriteDB;

    DB portfolioDB;

    double buyPrice;
    int buyShares;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_detail);

        //  getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        stockName = getIntent().getStringExtra("Stock Name");
        stockSymbol = getIntent().getStringExtra("Stock Symbol");

        setHandlerControl();
        loadStockInfo();

        initiateComponent();
        setComponentControl();
    }

    private void updateStockUI() {


        activity_stock_detail_symbol_textView.setText(stockSymbol);
        activity_stock_detail_price_textView.setText(stock.getQuote().getPrice().toString());
        activity_stock_detail_price_change_textView.setText(stock.getQuote().getChange().toString() + "   " + stock.getQuote().getChangeInPercent().toString() + "%");


    }

    private void updateStockChart() {
        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();


        for (int i = 0; i < stockHistory.size(); i++) {
            valsComp1.add(new Entry(stockHistory.get(i).getClose().floatValue(), i));
        }


        LineDataSet setComp1 = new LineDataSet(valsComp1, "Price");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < stockHistory.size(); i++) {
            xVals.add("" + i);
        }


        activity_stock_detail_chart.setData(new LineData(xVals, dataSets));
        activity_stock_detail_chart.invalidate();
    }


    private void setHandlerControl() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        updateStockUI();
                        activity_stock_detail_scrollView.setVisibility(View.VISIBLE);
                        activity_stock_detail_progressBar.setVisibility(View.INVISIBLE);
                        activity_stock_detail_chart_progressBar.setVisibility(View.VISIBLE);
                    }
                    break;
                    case 2: {
                        updateStockChart();
                        activity_stock_detail_chart_progressBar.setVisibility(View.INVISIBLE);
                    }
                    break;


                }
            }
        };

    }

    private void loadStockInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            stock = YahooFinance.get(stockSymbol);

                            Message msg = Message.obtain();
                            msg.what = 1;
                            handler.sendMessage(msg);


                            Calendar from = Calendar.getInstance();
                            Calendar to = Calendar.getInstance();
                            from.add(Calendar.YEAR, -2);

                            stockHistory = stock.getHistory(from, to, Interval.MONTHLY);
                            Collections.reverse(stockHistory);

                            msg = Message.obtain();
                            msg.what = 2;
                            handler.sendMessage(msg);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private void setComponentStyle() {

    }

    private void initiateComponent() {
        activity_stock_detail_sell_button = (Button) findViewById(R.id.activity_stock_detail_sell_button);
        activity_stock_detail_buy_button = (Button) findViewById(R.id.activity_stock_detail_buy_button);

        activity_stock_detail_progressBar = (ProgressBar) findViewById(R.id.activity_stock_detail_progressBar);
        activity_stock_detail_progressBar.setVisibility(View.VISIBLE);
        activity_stock_detail_chart_progressBar = (ProgressBar) findViewById(R.id.activity_stock_detail_chart_progressBar);
        activity_stock_detail_chart_progressBar.setVisibility(View.INVISIBLE);

        activity_stock_detail_scrollView = (ScrollView) findViewById(R.id.activity_stock_detail_scrollView);
        activity_stock_detail_scrollView.setVisibility(View.INVISIBLE);

        activity_stock_detail_chart = (LineChart) findViewById(R.id.activity_stock_detail_chart);

        activity_stock_detail_favorite_checkBox = (CheckBox) findViewById(R.id.activity_stock_detail_favorite_checkBox);

        try {
            favoriteDB = Snappy.open(getBaseContext(), Snappy.DB_NAME_FAVORITE);
            if (favoriteDB.exists(stockSymbol)) {
                activity_stock_detail_favorite_checkBox.setChecked(true);
            } else {
                activity_stock_detail_favorite_checkBox.setChecked(false);
            }
            favoriteDB.close();

        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        activity_stock_detail_symbol_textView = (TextView) findViewById(R.id.activity_stock_detail_symbol_textView);
        activity_stock_detail_price_textView = (TextView) findViewById(R.id.activity_stock_detail_price_textView);
        activity_stock_detail_price_change_textView = (TextView) findViewById(R.id.activity_stock_detail_price_change_textView);


        activity_stock_detail_toolbar = (Toolbar) findViewById(R.id.activity_stock_detail_toolbar);
        activity_stock_detail_toolbar
                .setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);


        setSupportActionBar(activity_stock_detail_toolbar);
        getSupportActionBar().setTitle(stockName);

    }

    private void setComponentControl() {
        setToolbarNavigationControl();
        setFavoriteCheckBoxControl();
        setBuyButtonControl();
        setSellButtonControl();
    }

    private void setBuyButtonControl() {
        activity_stock_detail_buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(StockDetailActivity.this);

                dialogbuilder.setTitle("Buy");

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_buy, null);

                dialogbuilder.setView(dialogView);


                final EditText buyPriceEditText = (EditText) dialogView.findViewById(R.id.dialog_buy_price_editText);
                final EditText buySharesEditText = (EditText) dialogView.findViewById(R.id.dialog_buy_shares_editText);


                dialogbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (buyPriceEditText.getText().toString().trim().equals("") || buySharesEditText.getText().toString().trim().equals("")) {
                            Toast.makeText(getBaseContext(), "Price or shares can't be nothing.", Toast.LENGTH_SHORT).show();
                        } else {

                            buyPrice = Double.parseDouble(buyPriceEditText.getText().toString());
                            buyShares = Integer.parseInt(buySharesEditText.getText().toString());


                            try {
                                portfolioDB = Snappy.open(getBaseContext(), Snappy.DB_NAME_PORTFOLIO);
                                favoriteDB.put(stockSymbol, new Object[]{stock, buyPrice, buyShares});
                                favoriteDB.close();

                            } catch (SnappydbException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });

                dialogbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog dialog = dialogbuilder.create();
                dialog.show();

                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.theme_accent));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.theme_primary));


            }
        });
    }

    private void setSellButtonControl() {
        activity_stock_detail_sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setFavoriteCheckBoxControl() {
        activity_stock_detail_favorite_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        favoriteDB = Snappy.open(getBaseContext(), Snappy.DB_NAME_FAVORITE);
                        favoriteDB.put(stockSymbol, stock);
                        favoriteDB.close();

                    } catch (SnappydbException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        favoriteDB = Snappy.open(getBaseContext(), Snappy.DB_NAME_FAVORITE);
                        favoriteDB.del(stockSymbol);
                        favoriteDB.close();

                    } catch (SnappydbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setToolbarNavigationControl() {
        activity_stock_detail_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

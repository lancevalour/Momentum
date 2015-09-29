package yicheng.android.app.momentum.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.adapter.StockRecyclerAdapter;
import yicheng.android.app.momentum.model.Snappy;

/**
 * Created by ZhangY on 9/21/2015.
 */
public class StockSearchActivity extends AppCompatActivity {
    Button activity_stock_search_buy_button, activity_stock_search_sell_button;


    ProgressBar activity_stock_search_progressBar, activity_stock_search_chart_progressBar;


    ScrollView activity_stock_search_scrollView;

    LineChart activity_stock_search_chart;

    CheckBox activity_stock_search_favorite_checkBox;

    TextView activity_stock_search_symbol_textView, activity_stock_search_price_textView, activity_stock_search_price_change_textView;

    Toolbar activity_stock_search_toolbar;

    List<HistoricalQuote> stockHistory;

    SimpleCursorAdapter cursorAdapter;

    MatrixCursor matrixCursor;

    Handler handler;

    Stock stock;
    String stockSymbol;
    String stockName;

    DB favoriteDB;
    DB portfolioDB;

    double buyPrice;
    int buyShares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_search);

        setHandlerControl();
        initiateComponent();
        setComponentControl();
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


        activity_stock_search_chart.setData(new LineData(xVals, dataSets));
        activity_stock_search_chart.invalidate();
    }


    private void setHandlerControl() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        cursorAdapter.changeCursor(matrixCursor);
                    }
                    break;
                    case 2: {
                        updateStockUI();
                        activity_stock_search_scrollView.setVisibility(View.VISIBLE);
                        activity_stock_search_progressBar.setVisibility(View.INVISIBLE);
                        activity_stock_search_chart_progressBar.setVisibility(View.VISIBLE);
                    }
                    break;
                    case 3: {
                        updateStockChart();
                        activity_stock_search_chart_progressBar.setVisibility(View.INVISIBLE);
                    }
                    break;


                }
            }
        };
    }


    private void setComponentStyle() {

    }

    private void initiateComponent() {
        activity_stock_search_buy_button = (Button) findViewById(R.id.activity_stock_search_buy_button);
        activity_stock_search_sell_button = (Button) findViewById(R.id.activity_stock_search_sell_button);

        activity_stock_search_progressBar = (ProgressBar) findViewById(R.id.activity_stock_search_progressBar);
        activity_stock_search_progressBar.setVisibility(View.VISIBLE);
        activity_stock_search_chart_progressBar = (ProgressBar) findViewById(R.id.activity_stock_search_chart_progressBar);
        activity_stock_search_chart_progressBar.setVisibility(View.INVISIBLE);

        activity_stock_search_scrollView = (ScrollView) findViewById(R.id.activity_stock_search_scrollView);
        activity_stock_search_scrollView.setVisibility(View.INVISIBLE);

        activity_stock_search_chart = (LineChart) findViewById(R.id.activity_stock_search_chart);

        activity_stock_search_favorite_checkBox = (CheckBox) findViewById(R.id.activity_stock_search_favorite_checkBox);


        activity_stock_search_symbol_textView = (TextView) findViewById(R.id.activity_stock_search_symbol_textView);
        activity_stock_search_price_textView = (TextView) findViewById(R.id.activity_stock_search_price_textView);
        activity_stock_search_price_change_textView = (TextView) findViewById(R.id.activity_stock_search_price_change_textView);


        activity_stock_search_toolbar = (Toolbar) findViewById(R.id.activity_stock_search_toolbar);
        activity_stock_search_toolbar
                .setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);

        setSupportActionBar(activity_stock_search_toolbar);
        getSupportActionBar().setTitle("Search Stock");

    }

    private void setComponentControl() {
        setToolbarNavigationControl();
        setFavoriteCheckBoxControl();
        setBuyButtonControl();
        setSellButtonControl();
    }

    private void setBuyButtonControl() {
        activity_stock_search_buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(StockSearchActivity.this);

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
        activity_stock_search_sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setFavoriteCheckBoxControl() {
        activity_stock_search_favorite_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        activity_stock_search_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        final MenuItem menuItem = menu.findItem(R.id.actionbar_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        final String[] from = new String[]{"stockSymbol"};
        final int[] to = new int[]{android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(getBaseContext(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchView.setSuggestionsAdapter(cursorAdapter);


        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                matrixCursor.moveToPosition(i);

                stockSymbol = matrixCursor.getString(matrixCursor
                        .getColumnIndex("stockSymbol"));

                loadStockInfo();

                menuItem.collapseActionView();

                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });

        menuItem.expandActionView();

        return true;
    }


    private void updateStockUI() {

        try {
            favoriteDB = Snappy.open(getBaseContext(), Snappy.DB_NAME_FAVORITE);
            if (favoriteDB.exists(stockSymbol)) {
                activity_stock_search_favorite_checkBox.setChecked(true);
            } else {
                activity_stock_search_favorite_checkBox.setChecked(false);
            }
            favoriteDB.close();

        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        activity_stock_search_symbol_textView.setText(stockSymbol);
        activity_stock_search_price_textView.setText(stock.getQuote().getPrice().toString());
        activity_stock_search_price_change_textView.setText(stock.getQuote().getChange().toString() + "   " + stock.getQuote().getChangeInPercent().toString() + "%");

        getSupportActionBar().setTitle(stock.getName());
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
                            msg.what = 2;
                            handler.sendMessage(msg);


                            Calendar from = Calendar.getInstance();
                            Calendar to = Calendar.getInstance();
                            from.add(Calendar.YEAR, -2);

                            stockHistory = stock.getHistory(from, to, Interval.MONTHLY);
                            Collections.reverse(stockHistory);

                            msg = Message.obtain();
                            msg.what = 3;
                            handler.sendMessage(msg);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }


    OkHttpClient client = new OkHttpClient();

    private String httpGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    private void populateAdapter(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            try {
                                String response = httpGet("http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + s + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback");

                                response = response.substring("YAHOO.Finance.SymbolSuggest.ssCallback(".length(), response.length() - 1);


                                JSONObject object = new JSONObject(response);

                                JSONArray array = object.getJSONObject("ResultSet").getJSONArray("Result");

                                matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, "stockSymbol"});

                                for (int i = 0; i < array.length(); i++) {
                                    matrixCursor.addRow(new Object[]{i, array.getJSONObject(i).get("symbol")});
                                    System.out.println(array.getJSONObject(i).get("symbol"));
                                }


                                Message msg = Message.obtain();
                                msg.what = 1;
                                handler.sendMessage(msg);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


    }


/*    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
        for (int i = 0; i < SUGGESTIONS.length; i++) {
            if (SUGGESTIONS[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[]{i, SUGGESTIONS[i]});
        }
        mAdapter.changeCursor(c);
    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}

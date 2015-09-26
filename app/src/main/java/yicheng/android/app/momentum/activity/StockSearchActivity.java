package yicheng.android.app.momentum.activity;

import android.app.SearchManager;
import android.content.Context;
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
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.adapter.StockRecyclerAdapter;

/**
 * Created by ZhangY on 9/21/2015.
 */
public class StockSearchActivity extends AppCompatActivity {

    TextView activity_stock_search_symbol_textView, activity_stock_search_price_textView, activity_stock_search_price_change_textView;


    Toolbar activity_stock_search_toolbar;


    SimpleCursorAdapter cursorAdapter;

    MatrixCursor matrixCursor;

    Handler handler;


    String stockSymbol;

    Stock stock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_search);

        setHandlerControl();
        initiateComponent();
        setComponentControl();
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
                    }
                    break;

                }
            }
        };
    }


    private void setComponentStyle() {

    }

    private void initiateComponent() {
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

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.actionbar_search).getActionView();
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

                //    matrixCursor.move(i);
                //     String s = matrixCursor.getString(matrixCursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                //  Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();

                //   Toast.makeText(getBaseContext(), matrixCursor.getColumnIndex(BaseColumns._ID), Toast.LENGTH_SHORT).show();
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

        return true;
    }


    private void updateStockUI(){

        activity_stock_search_symbol_textView.setText(stockSymbol);
        activity_stock_search_price_textView.setText(stock.getQuote().getPrice().toString());
        activity_stock_search_price_change_textView.setText(stock.getQuote().getChangeInPercent().toString());
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

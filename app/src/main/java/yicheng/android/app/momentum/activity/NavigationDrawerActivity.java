package yicheng.android.app.momentum.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.fragment.FavoriteFragment;
import yicheng.android.app.momentum.fragment.MarketFragment;
import yicheng.android.app.momentum.fragment.PortfolioFragment;
import yicheng.android.app.momentum.fragment.SettingsFragment;
import yicheng.android.app.momentum.fragment.TrendingFragment;

/**
 * Created by ZhangY on 8/17/2015.
 */
public class NavigationDrawerActivity extends AppCompatActivity {
    DrawerLayout activity_navigation_drawer_layout;

    Toolbar activity_navigation_drawer_toolbar;
    FrameLayout activity_navigation_drawer_content_framelayout;

    NavigationView activity_navigation_drawer_navigation_view;


    FloatingActionButton activity_navigation_drawer_search_fab;

    ActionBarDrawerToggle drawerToggle;

    TextView email;


    private final Handler drawerActionHandler = new Handler();

    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    int curDrawerItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        initiateComponents();
        setComponentControl();

    }

    private void initiateComponents() {
        email = (TextView) findViewById(R.id.email);
        email.setText("haha");


        activity_navigation_drawer_layout = (DrawerLayout) findViewById(R.id.activity_navigation_drawer_layout);

        activity_navigation_drawer_toolbar = (Toolbar) findViewById(R.id.activity_navigation_drawer_toolbar);

        setSupportActionBar(activity_navigation_drawer_toolbar);


        activity_navigation_drawer_content_framelayout = (FrameLayout) findViewById(R.id.activity_navigation_drawer_content_framelayout);

        activity_navigation_drawer_navigation_view = (NavigationView) findViewById(R.id.activity_navigation_drawer_navigation_view);


        activity_navigation_drawer_navigation_view.getMenu().findItem(R.id.drawer_market).setChecked(true);


        activity_navigation_drawer_search_fab = (FloatingActionButton) findViewById(R.id.activity_navigation_drawer_search_fab);
        activity_navigation_drawer_search_fab.setIcon(R.drawable.ic_action_search);
        //  activity_navigation_drawer_floatingActionMenu = (FloatingActionsMenu) findViewById(R.id.activity_navigation_drawer_floatingActionMenu);


        drawerToggle = new ActionBarDrawerToggle(this, activity_navigation_drawer_layout, activity_navigation_drawer_toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont
                // want anything happened whe drawer is
                // open I am not going to put anything here)
             /*   if (activity_navigation_drawer_floatingActionMenu.isExpanded()) {
                    activity_navigation_drawer_floatingActionMenu.collapse();
                }*/
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed

            }
        };
        activity_navigation_drawer_layout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();


        curDrawerItemId = R.id.drawer_market;

        navigate(curDrawerItemId);

    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
        Fragment frontFragment = null;
        int titleStringID = R.string.app_name;

        switch (itemId) {
            case R.id.drawer_market: {
                frontFragment = new MarketFragment();
                titleStringID = R.string.drawer_title_market;
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    System.out.println(
                                            get("http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=yahoo&callback=YAHOO.Finance.SymbolSuggest.ssCallback"));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });*/
            }
            break;
            case R.id.drawer_portfolio: {
                frontFragment = new PortfolioFragment();
                titleStringID = R.string.drawer_title_portfolio;
            }
            break;
            case R.id.drawer_favorite: {
                frontFragment = new FavoriteFragment();
                titleStringID = R.string.drawer_title_favorite;
            }
            break;
            case R.id.drawer_trending: {
                frontFragment = new TrendingFragment();
                titleStringID = R.string.drawer_title_trending;
            }
            break;
            case R.id.drawer_settings: {
                frontFragment = new SettingsFragment();
                titleStringID = R.string.drawer_title_settings;
            }
            break;
        }


        getFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.activity_navigation_drawer_content_framelayout,
                        frontFragment).commit();

        getSupportActionBar()
                .setTitle(
                        titleStringID);

    }

    private void setComponentControl() {
        setNavigationViewControl();

        setFloatingActionButtonControl();
    }

    private void setFloatingActionButtonControl() {
        activity_navigation_drawer_search_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationDrawerActivity.this, StockSearchActivity.class);
                v.getContext().startActivity(intent);


             /*   runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    try {
                                        String response = get("http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=yahoo&callback=YAHOO.Finance.SymbolSuggest.ssCallback");

                                        response = response.substring("YAHOO.Finance.SymbolSuggest.ssCallback(".length(), response.length() - 1);


                                        JSONObject object = new JSONObject(response);

                                        JSONArray array = object.getJSONObject("ResultSet").getJSONArray("Result");

                                        for (int i = 0; i < array.length(); i++) {
                                            System.out.println(array.getJSONObject(i).get("symbol"));
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });*/
            }
        });
    }


    OkHttpClient client = new OkHttpClient();

    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void setNavigationViewControl() {
        activity_navigation_drawer_navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                menuItem.setChecked(true);
                curDrawerItemId = menuItem.getItemId();

                // allow some time after closing the drawer before performing real navigation
                // so the user can see what is happening
                activity_navigation_drawer_layout.closeDrawer(GravityCompat.START);


                drawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(curDrawerItemId);
                    }
                }, DRAWER_CLOSE_DELAY_MS);
                return true;
            }
        })
    ;
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }


}

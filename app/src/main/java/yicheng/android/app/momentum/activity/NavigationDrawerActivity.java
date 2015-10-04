package yicheng.android.app.momentum.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
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
public class NavigationDrawerActivity extends AppCompatActivity implements
        PortfolioFragment.FragmentCallback, MarketFragment.FragmentCallback,
        SettingsFragment.FragmentCallback, FavoriteFragment.FragmentCallback,
        TrendingFragment.FragmentCallback {
    DrawerLayout activity_navigation_drawer_layout;

    FrameLayout activity_navigation_drawer_content_framelayout;

    NavigationView activity_navigation_drawer_navigation_view;

    public static CoordinatorLayout activity_navigation_drawer_coordinatorLayout;


    FloatingActionButton activity_navigation_drawer_search_fab;


    TextView email;

    private final Handler drawerActionHandler = new Handler();

    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    int curDrawerItemId;
    String DRAWER_ID = "drawer_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        if (null == savedInstanceState) {
            curDrawerItemId = R.id.drawer_market;
        } else {
            curDrawerItemId = savedInstanceState.getInt(DRAWER_ID);
        }


        initiateComponents();


        setComponentControl();

    }

    private void initiateComponents() {
        email = (TextView) findViewById(R.id.email);
        email.setText("haha");

        activity_navigation_drawer_coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_navigation_drawer_coordinatorLayout);

        activity_navigation_drawer_layout = (DrawerLayout) findViewById(R.id.activity_navigation_drawer_layout);

        activity_navigation_drawer_content_framelayout = (FrameLayout) findViewById(R.id.activity_navigation_drawer_content_framelayout);

        activity_navigation_drawer_navigation_view = (NavigationView) findViewById(R.id.activity_navigation_drawer_navigation_view);

        activity_navigation_drawer_navigation_view.getMenu().findItem(curDrawerItemId).setChecked(true);

        activity_navigation_drawer_search_fab = (FloatingActionButton) findViewById(R.id.activity_navigation_drawer_search_fab);
        activity_navigation_drawer_search_fab.setIcon(R.drawable.ic_action_search);


        navigate(curDrawerItemId);

    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
        Fragment frontFragment = null;

        switch (itemId) {
            case R.id.drawer_market: {
                frontFragment = new MarketFragment();
            }
            break;
            case R.id.drawer_portfolio: {
                frontFragment = new PortfolioFragment();
            }
            break;
            case R.id.drawer_favorite: {
                frontFragment = new FavoriteFragment();
            }
            break;
            case R.id.drawer_trending: {
                frontFragment = new TrendingFragment();
            }
            break;
            case R.id.drawer_settings: {
                frontFragment = new SettingsFragment();
            }
            break;
        }


        getFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.activity_navigation_drawer_content_framelayout,
                        frontFragment).commit();

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

            }
        });
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


    @Override
    public void onBackPressed() {
        if (activity_navigation_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            activity_navigation_drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(this.DRAWER_ID, curDrawerItemId);
    }

    @Override
    public void menuClick() {
        activity_navigation_drawer_layout.openDrawer(GravityCompat.START);
    }

    @Override
    public void menuSearch() {
        //   getSupportFragmentManager().beginTransaction().replace(R.id.containerSearch, new FragmentSearch()).addToBackStack(null).commit();
    }

}

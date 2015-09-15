package yicheng.android.app.momentum.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import yicheng.android.app.momentum.R;
import yicheng.android.app.momentum.fragment.PortfolioFragment;

/**
 * Created by ZhangY on 8/17/2015.
 */
public class NavigationDrawerActivity extends AppCompatActivity {
    DrawerLayout activity_navigation_drawer_layout;

    Toolbar activity_navigation_drawer_toolbar;
    FrameLayout activity_navigation_drawer_content_framelayout;

    NavigationView activity_navigation_drawer_navigation_view;

    FloatingActionsMenu activity_navigation_drawer_floatingActionMenu;

    ActionBarDrawerToggle drawerToggle;


    private final Handler drawerActionHandler = new Handler();

    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    int curDrawerItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateComponents();
        setComponentControl();

    }

    private void initiateComponents() {
        activity_navigation_drawer_layout = (DrawerLayout) findViewById(R.id.activity_navigation_drawer_layout);

        activity_navigation_drawer_toolbar = (Toolbar) findViewById(R.id.activity_navigation_drawer_toolbar);

        setSupportActionBar(activity_navigation_drawer_toolbar);


     /*   final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
        activity_navigation_drawer_content_framelayout = (FrameLayout) findViewById(R.id.activity_navigation_drawer_content_framelayout);

        activity_navigation_drawer_navigation_view = (NavigationView) findViewById(R.id.activity_navigation_drawer_navigation_view);


        activity_navigation_drawer_navigation_view.getMenu().findItem(R.id.drawer_portfolio).setChecked(true);

        activity_navigation_drawer_floatingActionMenu = (FloatingActionsMenu) findViewById(R.id.activity_navigation_drawer_floatingActionMenu);


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


        curDrawerItemId = R.id.drawer_portfolio;

        navigate(curDrawerItemId);

    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
        Fragment frontFragment = null;
        int titleStringID = R.string.app_name;

        switch (itemId) {
            case R.id.drawer_portfolio: {
                frontFragment = new PortfolioFragment();
                titleStringID = R.string.drawer_title_portfolio;
            }
            break;
            case R.id.drawer_trending: {
                frontFragment = new PortfolioFragment();
                titleStringID = R.string.drawer_title_trending;
            }
            break;
            case R.id.drawer_settings: {
                frontFragment = new PortfolioFragment();
                titleStringID = R.string.drawer_title_portfolio;
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
        setFloatingActionMenuControl();
        setFloatingActionButtonControl();
    }

    private void setFloatingActionButtonControl() {

    }


    private void setFloatingActionMenuControl() {
        activity_navigation_drawer_content_framelayout
                .setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        if (activity_navigation_drawer_floatingActionMenu
                                .isExpanded()) {
                            activity_navigation_drawer_floatingActionMenu
                                    .collapse();
                        }
                        return false;
                    }
                });

        activity_navigation_drawer_toolbar
                .setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        if (activity_navigation_drawer_floatingActionMenu
                                .isExpanded()) {
                            activity_navigation_drawer_floatingActionMenu
                                    .collapse();
                        }
                        return false;
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

                Toast.makeText(getBaseContext(), "" + curDrawerItemId, Toast.LENGTH_SHORT).show();
                drawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(curDrawerItemId);
                    }
                }, DRAWER_CLOSE_DELAY_MS);
                return true;
            }
        });
    }
}

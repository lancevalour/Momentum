package yicheng.android.app.momentum.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import yicheng.android.app.momentum.R;

/**
 * Created by ZhangY on 9/17/2015.
 */
public class StockDetailActivity extends AppCompatActivity {


    Toolbar activity_stock_detail_toolbar;

    String stockName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_detail);

      //  getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        stockName = getIntent().getStringExtra("Stock Name");

        initiateComponent();
        setComponentControl();
    }

    private void setComponentStyle() {

    }

    private void initiateComponent() {


        activity_stock_detail_toolbar = (Toolbar) findViewById(R.id.activity_stock_detail_toolbar);
        activity_stock_detail_toolbar
                .setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);

       /* setActionBar(activity_stock_detail_toolbar);

        getActionBar().setTitle(stockName);*/
        setSupportActionBar(activity_stock_detail_toolbar);
        getSupportActionBar().setTitle(stockName);

    }

    private void setComponentControl() {
        setToolbarNavigationControl();
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

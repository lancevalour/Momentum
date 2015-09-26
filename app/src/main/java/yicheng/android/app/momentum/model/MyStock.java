package yicheng.android.app.momentum.model;

import java.io.Serializable;

import yahoofinance.Stock;

/**
 * Created by ZhangY on 9/26/2015.
 */
public class MyStock extends Stock implements Serializable{

    public MyStock(String symbol) {
        super(symbol);
    }
}

package yicheng.android.app.momentum.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yicheng.android.app.momentum.R;

/**
 * Created by ZhangY on 9/16/2015.
 */
public class FavoriteFragment extends Fragment {
    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favorite, container, false);

        return rootView;

    }
}

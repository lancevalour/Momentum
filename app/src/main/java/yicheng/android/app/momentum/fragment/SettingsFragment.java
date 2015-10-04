package yicheng.android.app.momentum.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import yicheng.android.app.momentum.R;

/**
 * Created by ZhangY on 9/16/2015.
 */
public class SettingsFragment extends Fragment {
    FragmentCallback fragmentCallback;

    Toolbar fragment_settings_toolbar;

    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);

        initiateComponents();

        setComponentControl();

        return rootView;

    }

    private void initiateComponents() {
        fragment_settings_toolbar = (Toolbar) rootView.findViewById(R.id.fragment_settings_toolbar);
        //   fragment_settings_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.theme_primary)));
        fragment_settings_toolbar.setTitle(R.string.drawer_title_settings);


    }

    private void setComponentControl() {
        setToolbarControl();
    }

    private void setToolbarControl() {
        fragment_settings_toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
        fragment_settings_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallback.menuClick();
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

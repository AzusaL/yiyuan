package com.team.azusa.yiyuan.recharge_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.azusa.yiyuan.R;

/**
 * Created by Azusa on 2016/4/17.
 */
public class CardRechargeFragment extends Fragment{

    private View view;
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recharge_fg2_car, null);
        initView();
        return view;
    }

    private void initView() {
    }
}

package com.team.azusa.yiyuan.yiyuan_search_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.RecentSearchLvAdapter;
import com.team.azusa.yiyuan.database.SharedPreferenceData;
import com.team.azusa.yiyuan.event.SearchTextEven;
import com.team.azusa.yiyuan.utils.UserUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class RecentSearchFragment extends Fragment {

    @Bind(R.id.btn_clearrecord)
    Button btnClearrecord;
    @Bind(R.id.recent_lv)
    ListView mlistView;
    private View view;
    private ArrayList<String> datas;
    private String user_id;
    private RecentSearchLvAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fg1_recent, null);
        ButterKnife.bind(this, view);
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new SearchTextEven(datas.get(position)));
            }
        });
    }

    private void initData() {
        if (UserUtils.userisLogin) {
            user_id = UserUtils.user.getId();
        } else {
            user_id = "未登录";
        }
        datas = SharedPreferenceData.getInstance().getSearchList(getContext(), user_id);
        adapter = new RecentSearchLvAdapter(getContext(), datas);
        mlistView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_clearrecord)
    public void onClick() {
        SharedPreferenceData.getInstance().clearSearchtext(getContext(), user_id);
        datas.clear();
        adapter.notifyDataSetChanged();
    }
}

package com.team.azusa.yiyuan.yiyuan_search_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.HomePublicRviewAdapter;
import com.team.azusa.yiyuan.adapter.HotSearchRviewAdapter;
import com.team.azusa.yiyuan.event.SearchTextEven;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.widget.DividerGridItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class HotSearchFragment extends Fragment {

    @Bind(R.id.hot_search_rv)
    RecyclerView hotSearchRv;
    private View view;
    private ArrayList<String> datas;
    HotSearchRviewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fg1_hot, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        hotSearchRv.setLayoutManager(gridLayoutManager);
        hotSearchRv.setHasFixedSize(true);
        datas = new ArrayList<String>();
        datas.add("iPhon6s");
        datas.add("小米5");
        datas.add("魅族");
        datas.add("平板电脑");
        datas.add("单反相机");
        datas.add("汽车");
        adapter = new HotSearchRviewAdapter(datas);
        hotSearchRv.setAdapter(adapter);
        adapter.setOnItemClickLitener(new RecyclerViewItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                EventBus.getDefault().post(new SearchTextEven(datas.get(position)));
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}

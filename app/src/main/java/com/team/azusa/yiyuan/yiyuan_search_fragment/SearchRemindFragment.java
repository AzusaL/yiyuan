package com.team.azusa.yiyuan.yiyuan_search_fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.TabFragmentPagerAdapter;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SearchRemindFragment extends Fragment {

    @Bind(R.id.tablayout_search)
    TabLayout tb_search;
    @Bind(R.id.vp_search)
    ViewPager mviewpager;
    private View view;
    ArrayList<Fragment> fragmentsList;
    ArrayList<String> titles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fg1, null);
        ButterKnife.bind(this, view);
        iniView();
        return view;
    }

    private void iniView() {
        titles = new ArrayList<>();
        titles.add("最近搜索");
        titles.add("热门搜索");

        tb_search.addTab(tb_search.newTab().setText(titles.get(0)));
        tb_search.addTab(tb_search.newTab().setText(titles.get(1)));

        tb_search.setTabTextColors(getResources().getColor(R.color.tabcolor), getResources().getColor(R.color.tabcolor_select));

        mviewpager.setOffscreenPageLimit(2);
        fragmentsList = new ArrayList<Fragment>();
        Fragment recentSearchFragment = new RecentSearchFragment();
        Fragment hotSearchFragment = new HotSearchFragment();

        fragmentsList.add(recentSearchFragment);
        fragmentsList.add(hotSearchFragment);
        TabFragmentPagerAdapter tabFragmentPagerAdapter = new TabFragmentPagerAdapter(
                getActivity().getSupportFragmentManager(), fragmentsList, titles);
        mviewpager.setAdapter(new TabFragmentPagerAdapter(
                getActivity().getSupportFragmentManager(), fragmentsList, titles));
        mviewpager.setCurrentItem(0);

        tb_search.setupWithViewPager(mviewpager);
        tb_search.setTabsFromPagerAdapter(tabFragmentPagerAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}

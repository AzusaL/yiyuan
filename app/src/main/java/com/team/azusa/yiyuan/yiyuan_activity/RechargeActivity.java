package com.team.azusa.yiyuan.yiyuan_activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.TabFragmentPagerAdapter;
import com.team.azusa.yiyuan.recharge_fragment.BankRechargeFragment;
import com.team.azusa.yiyuan.recharge_fragment.CardRechargeFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends AppCompatActivity {

    @Bind(R.id.return_recharge)
    ImageView returnRecharge;
    @Bind(R.id.tv_acountdetail)
    TextView tvAcountdetail;
    @Bind(R.id.tablayout_recharge)
    TabLayout mtablayout;
    @Bind(R.id.vp_recharge)
    ViewPager mviewpager;

    private View view;
    ArrayList<Fragment> fragmentsList;
    ArrayList<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        iniView();
    }

    private void iniView() {
        titles = new ArrayList<>();
        titles.add("网银充值");
        titles.add("充值卡充值");

        mtablayout.addTab(mtablayout.newTab().setText(titles.get(0)));
        mtablayout.addTab(mtablayout.newTab().setText(titles.get(1)));

        mtablayout.setTabTextColors(getResources().getColor(R.color.tabcolor), getResources().getColor(R.color.tabcolor_select));

        mviewpager.setOffscreenPageLimit(2);
        fragmentsList = new ArrayList<>();
        Fragment bankRechargeFragment = new BankRechargeFragment();
        Fragment cardRechargeFragment = new CardRechargeFragment();

        fragmentsList.add(bankRechargeFragment);
        fragmentsList.add(cardRechargeFragment);
        TabFragmentPagerAdapter tabFragmentPagerAdapter = new TabFragmentPagerAdapter(
                getSupportFragmentManager(), fragmentsList, titles);
        mviewpager.setAdapter(new TabFragmentPagerAdapter(
                getSupportFragmentManager(), fragmentsList, titles));
        mviewpager.setCurrentItem(0);

        mtablayout.setupWithViewPager(mviewpager);
        mtablayout.setTabsFromPagerAdapter(tabFragmentPagerAdapter);
    }


    @OnClick({R.id.return_recharge, R.id.tv_acountdetail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_recharge:
                finish();
                break;
            case R.id.tv_acountdetail:
                break;
        }
    }
}

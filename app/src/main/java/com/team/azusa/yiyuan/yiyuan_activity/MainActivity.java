package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.event.AddCarsEvent;
import com.team.azusa.yiyuan.event.IntParameterEvent;
import com.team.azusa.yiyuan.event.SortEvent;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DepthPageTransformer;
import com.team.azusa.yiyuan.widget.BadgeView;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.yiyuan_mainfragment.AllGoodsFragment;
import com.team.azusa.yiyuan.yiyuan_mainfragment.CartFragment;
import com.team.azusa.yiyuan.yiyuan_mainfragment.HomeFragment;
import com.team.azusa.yiyuan.yiyuan_mainfragment.MeFragment;
import com.team.azusa.yiyuan.yiyuan_mainfragment.NewestFragment;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity implements OnClickListener {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private ArrayList<Fragment> mFragments;

    private LinearLayout mTabHome;
    private LinearLayout mTabAllGoods;
    private LinearLayout mTabNewest;
    private LinearLayout mTabCart;
    private LinearLayout mTabMe;

    private ImageButton mImgHome;
    private ImageButton mImgAllGoods;
    private ImageButton mImgNewest;
    private ImageButton mImgCart;
    private ImageButton mImgMe;

    private TextView mTestHome;
    private TextView mTestAllGoods;
    private TextView mTestNewest;
    private TextView mTestCart;
    private TextView mTestMe;
    private int[] car_location; //购物车的位置
    public int car_count; //购物车商品个数
    private BadgeView badge; //购物车商品个数小圆点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        setListener();
        setSelect(0);
        badge = new BadgeView(ConstanceUtils.CONTEXT);
        badge.setTargetView(mTabCart);
        badge.setBackgroundResource(R.drawable.cart_count_bg);
        badge.setBadgeMarginRight(16);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 20) {
            int result = data.getIntExtra("result", 0);
            setSelect(result);
        }
        if (resultCode == 111) {
            setSelect(3);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取购物车位置
    public int[] getCar_location() {
        car_location = new int[2];
        mTabCart.getLocationInWindow(car_location);
        return car_location;
    }

    //eventbus接收购物车页面发来的添加个数,修改购物车小红点上显示数
    public void onEventMainThread(AddCarsEvent event) {
        if (0 == event.getCount()) {
            car_count = 0;
        } else {
            car_count = car_count + event.getCount();
        }
        ConstanceUtils.carcount = car_count;
        badge.setBadgeCount(car_count);
    }

    //eventbus接收购物车页面发来的消息,确定要跳转到哪个Fragment
    public void onEventMainThread(IntParameterEvent event) {
        setSelect(event.getI());
    }
    private void setListener() {
        mTabHome.setOnClickListener(this);
        mTabAllGoods.setOnClickListener(this);
        mTabNewest.setOnClickListener(this);
        mTabCart.setOnClickListener(this);
        mTabMe.setOnClickListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        mTabHome = (LinearLayout) findViewById(R.id.id_tab_home);
        mTabAllGoods = (LinearLayout) findViewById(R.id.id_all_goods);
        mTabNewest = (LinearLayout) findViewById(R.id.id_tab_newest);
        mTabCart = (LinearLayout) findViewById(R.id.id_tab_cart);
        mTabMe = (LinearLayout) findViewById(R.id.id_tab_me);

        mImgHome = (ImageButton) findViewById(R.id.id_tab_home_img);
        mImgAllGoods = (ImageButton) findViewById(R.id.id_all_goods_img);
        mImgNewest = (ImageButton) findViewById(R.id.id_tab_newest_img);
        mImgCart = (ImageButton) findViewById(R.id.id_tab_cart_img);
        mImgMe = (ImageButton) findViewById(R.id.id_tab_me_img);

        mTestHome = (TextView) findViewById(R.id.text_home);
        mTestAllGoods = (TextView) findViewById(R.id.text_all_goods);
        mTestNewest = (TextView) findViewById(R.id.text_newest);
        mTestCart = (TextView) findViewById(R.id.text_cart);
        mTestMe = (TextView) findViewById(R.id.text_me);


        mFragments = new ArrayList<Fragment>();
        Fragment mTab01 = new HomeFragment();
        Fragment mTab02 = new AllGoodsFragment();
        Fragment mTab03 = new NewestFragment();
        Fragment mTab04 = new CartFragment();
        Fragment mTab05 = new MeFragment();

        mFragments.add(mTab01);
        mFragments.add(mTab02);
        mFragments.add(mTab03);
        mFragments.add(mTab04);
        mFragments.add(mTab05);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                setTab(currentItem);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        mViewPager.setOffscreenPageLimit(5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tab_home:
                setSelect(0);
                break;
            case R.id.id_all_goods:
                setSelect(1);
                break;
            case R.id.id_tab_newest:
                setSelect(2);
                break;
            case R.id.id_tab_cart:
                setSelect(3);
                break;
            case R.id.id_tab_me:
                setSelect(4);
                break;

            default:
                break;
        }
    }

    private void setSelect(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i, false);
    }

    private void setTab(int i) {
        resetImgs();
        switch (i) {
            case 0:
                mImgHome.setImageResource(R.drawable.home_select);
                mTestHome.setTextColor(Color.parseColor("#FF7F24"));
                break;
            case 1:
                mImgAllGoods.setImageResource(R.drawable.allgoods_select);
                mTestAllGoods.setTextColor(Color.parseColor("#FF7F24"));
                break;
            case 2:
                mImgNewest.setImageResource(R.drawable.newest_select);
                mTestNewest.setTextColor(Color.parseColor("#FF7F24"));
                break;
            case 3:
                mImgCart.setImageResource(R.drawable.cart_select);
                mTestCart.setTextColor(Color.parseColor("#FF7F24"));
                break;
            case 4:
                mImgMe.setImageResource(R.drawable.me_select);
                mTestMe.setTextColor(Color.parseColor("#FF7F24"));
                break;
        }
    }

    private void resetImgs() {
        mImgHome.setImageResource(R.drawable.home);
        mImgAllGoods.setImageResource(R.drawable.allgoods);
        mImgNewest.setImageResource(R.drawable.newest);
        mImgCart.setImageResource(R.drawable.cart);
        mImgMe.setImageResource(R.drawable.me);

        mTestHome.setTextColor(Color.parseColor("#696969"));
        mTestAllGoods.setTextColor(Color.parseColor("#696969"));
        mTestNewest.setTextColor(Color.parseColor("#696969"));
        mTestCart.setTextColor(Color.parseColor("#696969"));
        mTestMe.setTextColor(Color.parseColor("#696969"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

package com.team.azusa.yiyuan.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.database.SharedPreferenceData;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by Azusa on 2016/1/7.
 */
public class RefreshHead extends RelativeLayout implements PtrUIHandler {
    private ProgressBar pbrefresh;
    private TextView tvlode, tvtime;
    private int rotation = 0; //progressbar旋转的角度
    private int mLastY = 0; //下拉时手指滑动的距离
    private int head_high; //头部的高度
    private int currentY = 0;
    private Context context;
    private String where;   //用于标记是哪个页面，不同页面刷新的时间不同

    public RefreshHead(Context context, String where) {
        super(context);
        this.context = context;
        this.where = where;
        initView();
    }

    private void initView() {
        head_high = context.getResources().getDimensionPixelOffset(R.dimen.refresh_head);
        View layout = View.inflate(getContext(), R.layout.pulltorefresh_head, null);
        pbrefresh = (ProgressBar) layout.findViewById(R.id.pull_to_refresh_load_progress);
        tvlode = (TextView) layout.findViewById(R.id.pull_to_refresh_loadmore_text);
        tvtime = (TextView) layout.findViewById(R.id.recenttime);
        addView(layout);
    }

    //当位置回到初始位置 statu1
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        setRefreshTime();
        pbrefresh.clearAnimation();
    }

    //当位置离开初始位置 statu2
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
    }

    //开始刷新动画 statu3
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        tvlode.setText(" 正在加载...");
        tvtime.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.myrotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin); //设置插值器，匀速加载动画
        pbrefresh.startAnimation(animation);
    }

    //刷新动画完成。刷新完成之后，开始回归初始位置 statu4
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);
        SharedPreferenceData.getInstance().saveLastRefreshTime(context, time, where);
    }

    //位置发生变化时此方法通知UI更新
    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        currentY = ptrIndicator.getCurrentPosY();
        //如果是向下拉progressbar正向旋转，往回拉的时候逆时针回转
        if (currentY - mLastY > 0) {
            pbrefresh.setRotation(rotation += 10);
        } else {
            pbrefresh.setRotation(rotation -= 10);
        }
        mLastY = currentY;

        if (status == 2) {
            if (currentY > head_high * 1.2) {
                tvlode.setText("松开后刷新");
            } else {
                tvlode.setText(" 下拉刷新");
            }
        }
    }

    //设置刷新的时间
    public void setRefreshTime() {
        String time = SharedPreferenceData.getInstance().getLastRefreshTime(context, where);
        tvlode.setText("下拉刷新");
        tvtime.setVisibility(View.VISIBLE);
        tvtime.setText("上次更新:" + time);
    }

}

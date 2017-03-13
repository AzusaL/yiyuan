package com.team.azusa.yiyuan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.listener.OnLoadListener;

/**
 * Created by Azusa on 2016/1/15.
 */
public class PulluptoRefreshListview extends ListView implements AbsListView.OnScrollListener {
    // 上拉监听器, 到了最底部的上拉加载操作
    private OnLoadListener mOnLoadListener;
    // ListView的加载中footer
    private View mListViewFooter;
    private ProgressBar mloding_pb;
    private TextView loadmore_tv;
    private Animation animation; //旋转动画
    // 是否在加载中 ( 上拉加载更多 )
    private boolean isLoading = false;
    // 是否到达底部
    private boolean mIsEnd = false;
    // 是否已经全部加载完毕
    private boolean loadmoreComplete = false;
    private TopbarAnimation topbarAnimation;
    private float mLastY = 0;
    private int mfirstitemcount;

    public PulluptoRefreshListview(Context context) {
        super(context);
        initView(context);
    }

    public PulluptoRefreshListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setFloatTopbaranimation(TopbarAnimation topbarAnimation) {
        this.topbarAnimation = topbarAnimation;
    }

    public boolean isLoadmoreComplete() {
        return loadmoreComplete;
    }

    private void initView(Context context) {
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null, false);
        mloding_pb = (ProgressBar) mListViewFooter.findViewById(R.id.pull_to_refresh_load_progress);
        loadmore_tv = (TextView) mListViewFooter.findViewById(R.id.pull_to_refresh_loadmore_text);
        this.setOnScrollListener(this);
        this.addFooterView(mListViewFooter);
        mListViewFooter.setVisibility(GONE);
        this.setFooterDividersEnabled(false);
        initAnimation(context);
    }

    private void initAnimation(Context context) {
        animation = AnimationUtils.loadAnimation(context, R.anim.myrotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin); //设置插值器，匀速加载动画
    }

    // 如果到了最底部,那么执行onLoad方法
    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            mOnLoadListener.onLoad();
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            mListViewFooter.setVisibility(VISIBLE);
            mloding_pb.startAnimation(animation);
        } else {
            mListViewFooter.setVisibility(GONE);
            mloding_pb.clearAnimation();
        }
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    public void setLoadComplete(boolean complete) {
        loadmoreComplete = complete;
        if (loadmoreComplete) {
            mloding_pb.setVisibility(GONE);
            mloding_pb.clearAnimation();
            mListViewFooter.setVisibility(VISIBLE);
            loadmore_tv.setText("没有更多内容了");
        } else {
            mloding_pb.setVisibility(VISIBLE);
            loadmore_tv.setText("正在加载...");
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if (scrollState == SCROLL_STATE_IDLE) {
//            if (mIsEnd && !isLoading && !loadmoreComplete) {
//                loadData();
//            }
//        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mfirstitemcount = firstVisibleItem;
        if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
            mIsEnd = true;
        } else {
            mIsEnd = false;
        }
        if (mIsEnd && !isLoading && !loadmoreComplete && visibleItemCount > 2) {
            loadData();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (topbarAnimation != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    boolean topbarshowing = topbarAnimation.getshowing();
                    boolean topbarhideing = topbarAnimation.gethideing();
                    boolean topbarisShow = topbarAnimation.getisShow();

                    if (mLastY - ev.getRawY() > 0 && !topbarhideing && mfirstitemcount > 0) {
                        if (topbarisShow) {
                            topbarAnimation.hideTopbar();
                        }
                    }
                    if (mLastY - ev.getRawY() < 0 && !topbarshowing) {
                        if (!topbarisShow) {
                            topbarAnimation.showTopbar();
                        }
                    }
                    mLastY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    mLastY = 0;
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}

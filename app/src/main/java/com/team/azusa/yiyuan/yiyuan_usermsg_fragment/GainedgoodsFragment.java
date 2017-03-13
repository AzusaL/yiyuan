package com.team.azusa.yiyuan.yiyuan_usermsg_fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.MygainedGoodsRviewAdapter;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.callback.BuyRecordProductCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.yiyuan_activity.GoodsDetailsActivity;
import com.team.azusa.yiyuan.yiyuan_activity.NewShareOrderActivity;
import com.team.azusa.yiyuan.yiyuan_activity.OrderProgressActivity;
import com.team.azusa.yiyuan.yiyuan_activity.UsermsgActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/1/17.
 */
public class GainedgoodsFragment extends Fragment {
    @Bind(R.id.usermsg_fg2_rv)
    XRecyclerView recyclerview;
    private View view;
    private MygainedGoodsRviewAdapter adapter;
    private ArrayList<BuyRecordInfo> datas;
    private View footer;
    //上拉加载更多的footer里面的pb，tv；
    private ProgressBar loadmore_pb;
    private Animation animation; //pb的旋转动画
    private boolean initViewed = false; //是否初始化过页面
    private String user_id; //用户ID
    private boolean cancelrequest = false; //是否取消网络请求
    private int what; // 区别不同页面使用该fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.usermsg_fgtab2, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView();
        initAnimation();
        what = getArguments().getInt("what", -1);
        if (what != -1) {
            getData(0);
            initViewed = true;
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (!initViewed) {
                getData(0);
                initViewed = true;
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initView() {
        initFooter();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ConstanceUtils.CONTEXT);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerview.setHasFixedSize(true);
        //设置分割线
        recyclerview.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(ConstanceUtils.CONTEXT).color(Color.parseColor("#eeeeee")).build());

        adapter = new MygainedGoodsRviewAdapter(datas, user_id.equals(UserUtils.user.getId()));
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new RecyclerViewItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //如果是用户自己的获奖记录，则跳转到订单详情页面，否则跳转到商品详情页面
                if (user_id.equals(UserUtils.user.getId())) {
                    Intent intent = new Intent(getActivity(), OrderProgressActivity.class);
                    intent.putExtra("buyRecordInfo", JsonUtils.getJsonStringformat(datas.get(position)));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                    intent.putExtra("productId", datas.get(position).getProductId());
                    intent.putExtra("yunNum", datas.get(position).getYunNum()+"");
                    startActivity(intent);
                }
            }
        });
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.addFootView(footer);
        recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                getData(datas.size());
                loadmore_pb.startAnimation(animation);
            }
        });
    }

    private void initFooter() {
        footer = View.inflate(ConstanceUtils.CONTEXT, R.layout.listview_footer, null);
        loadmore_pb = (ProgressBar) footer.findViewById(R.id.pull_to_refresh_load_progress);
    }

    private void initData() {
        user_id = getArguments().getString("user_id");
        datas = new ArrayList<>();
    }

    /**
     * 从服务器获取数据
     *
     * @param firstResult 要加载的第一条数据的position
     */
    private void getData(final int firstResult) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_getUserWinProduct")
                .addParams("userId", user_id).addParams("firstResult", firstResult + "")
                .tag("GainedgoodsFragment")
                .build().execute(new BuyRecordProductCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
                recyclerview.loadMoreComplete();
            }

            @Override
            public void onResponse(ArrayList<BuyRecordInfo> response) {
                datas.addAll(response);
                loadmore_pb.clearAnimation();
                recyclerview.loadMoreComplete();
            }
        });
    }


    private void initAnimation() {
        animation = AnimationUtils.loadAnimation(ConstanceUtils.CONTEXT, R.anim.myrotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin); //设置插值器，匀速加载动画
        loadmore_pb.startAnimation(animation);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("GainedgoodsFragment");
        super.onDestroyView();
    }
}

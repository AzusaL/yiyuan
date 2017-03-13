package com.team.azusa.yiyuan.yiyuan_usermsg_fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.team.azusa.yiyuan.adapter.ShareOrderRviewAdapter;
import com.team.azusa.yiyuan.bean.ShowOrderDto;
import com.team.azusa.yiyuan.callback.ShowOrderCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.yiyuan_activity.ShareOrderDetailActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/1/17.
 */
public class ShareOrderFragment extends Fragment {
    @Bind(R.id.usermsg_fg2_rv)
    XRecyclerView recyclerview;
    private View view;
    private ShareOrderRviewAdapter adapter;
    private ArrayList<ShowOrderDto> datas;
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

        adapter = new ShareOrderRviewAdapter(datas);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new RecyclerViewItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == datas.size()) {
                    return;
                }
                Intent intent = new Intent(getActivity(), ShareOrderDetailActivity.class);
                String data = JsonUtils.getJsonStringformat(datas.get(position));
                intent.putExtra("showOrderDto", data);
                startActivity(intent);
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
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_getUserShowOrders")
                .addParams("userId", user_id).addParams("firstResult", firstResult + "")
                .tag("ShareOrderFragment")
                .build().execute(new ShowOrderCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
                recyclerview.loadMoreComplete();
            }

            @Override
            public void onResponse(ArrayList<ShowOrderDto> response) {
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
        OkHttpUtils.getInstance().cancelTag("ShareOrderFragment");
        super.onDestroyView();
    }
}
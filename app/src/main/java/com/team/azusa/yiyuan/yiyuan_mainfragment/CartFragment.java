package com.team.azusa.yiyuan.yiyuan_mainfragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.GoodsCarRviewAdapter;
import com.team.azusa.yiyuan.bean.GoodsCar;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.database.SharedPreferenceData;
import com.team.azusa.yiyuan.event.AddCarsEvent;
import com.team.azusa.yiyuan.event.LoginEvent;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.yiyuan_activity.GoodsDetailsActivity;
import com.team.azusa.yiyuan.yiyuan_activity.LoginActivity;
import com.team.azusa.yiyuan.yiyuan_activity.PayActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Azusa on 2015/1/25.
 */
public class CartFragment extends Fragment {
    @Bind(R.id.recyclerview_goodscar)
    RecyclerView recyclerview;
    @Bind(R.id.unlogin_goodscar)
    LinearLayout unloginlayout;
    @Bind(R.id.nogoods_layout)
    LinearLayout nogoodsLayout;
    @Bind(R.id.btn_envy)
    Button btn_login;
    @Bind(R.id.tv_cart_count)
    TextView tv_CartCount;
    @Bind(R.id.btn_gotobuy)
    Button btn_Gotobuy;
    @Bind(R.id.goodcar_bottom)
    RelativeLayout bottom_layout;
    private View view;
    private GoodsCarRviewAdapter adapter;
    private ArrayList<GoodsCar> datas;
    private AlertDialog mloding_dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fgtab04, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        iniView();
        return view;
    }

    private void iniView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerview.setHasFixedSize(true);
        //设置分割线
        recyclerview.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(getActivity()).color(0xffeeeeee).build());
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        datas = SharedPreferenceData.getInstance().getGoodsCarData(getContext());
        if (datas.size() == 0) {
            setNulldataView();
        } else {
            setNormalView();
            setBottom_layout();
            EventBus.getDefault().post(new AddCarsEvent(datas.size()));
        }
        adapter = new GoodsCarRviewAdapter(datas, getActivity());
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new RecyclerViewItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String productId = datas.get(position).getProductDto().getProductId();
                int yunNum = datas.get(position).getProductDto().getYunNum();

                Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("yunNum", yunNum + "");
                startActivity(intent);
            }
        });
    }

    //eventbus接收登陆界面发来的消息
    public void onEventMainThread(LoginEvent event) {
        if (event.isLoginSuccess()) {
            unloginlayout.setVisibility(View.GONE);
        } else {
            if (datas.size() == 0) {
                setNulldataView();
            }
        }
    }

    //eventbus接收其他页面添加商品的信息
    public void onEventMainThread(ProductDto event) {
        //删除购物车商品时
        if (StringUtil.isEmpty(event.getProductId())) {
            if (datas.size() == 0) {
                setNulldataView();
            }
            EventBus.getDefault().post(new AddCarsEvent(-1));
            setBottom_layout();
            SharedPreferenceData.getInstance().saveGoodsCarData(getContext(), datas);
            return;
        }
        //购买成功跳回该页面时，将购物车数据清空
        if (event.getProductId().equals("0")) {
            setNulldataView();
            datas.clear();
            return;
        }
        //为购物车商品修改购买次数
        if (event.getProductId().equals("-1")) {
            setBottom_layout();
            SharedPreferenceData.getInstance().saveGoodsCarData(getContext(), datas);
            return;
        }
        //添加商品到购物车
        setNormalView();
        int position = jduge(event);
        if (position == -1) {
            datas.add(0, new GoodsCar(event, 1));
            EventBus.getDefault().post(new AddCarsEvent(1));
        } else {
            datas.get(position).addBuyCount();
        }
        adapter.notifyDataSetChanged();
        setBottom_layout();
        SharedPreferenceData.getInstance().saveGoodsCarData(getContext(), datas);
    }


    //判断加入购物车的商品是否已经存在，是则返回所在位置，否则返回-1;
    // 自定义了ProductDto的equals方法，只判断productId是否相等
    private int jduge(ProductDto productDto) {
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getProductDto().equal(productDto)) {
                return i;
            }
        }
        return -1;
    }

    //设置购物车底部合计布局
    private void setBottom_layout() {
        String product_count = datas.size() + "";
        int yuan = 0;
        for (int i = 0; i < datas.size(); i++) {
            yuan += datas.get(i).getBuyCount();
        }
        String total_yuan = yuan + "";
        SpannableString string = new SpannableString("共 " + product_count + " 件商品，合计 " + total_yuan + ".00 元");
        string.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7700")), 2, product_count.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7700")), product_count.length() + 10, product_count.length() + 13 + total_yuan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_CartCount.setText(string);
    }

    //设置购物车为空时的布局
    private void setNulldataView() {
        nogoodsLayout.setVisibility(View.VISIBLE);
        if (!UserUtils.userisLogin) {
            unloginlayout.setVisibility(View.VISIBLE);
        } else {
            unloginlayout.setVisibility(View.GONE);
        }
        bottom_layout.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
    }

    //设置购物车不为空时的布局
    private void setNormalView() {
        nogoodsLayout.setVisibility(View.GONE);
        unloginlayout.setVisibility(View.GONE);
        bottom_layout.setVisibility(View.VISIBLE);
        recyclerview.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_envy, R.id.btn_gotobuy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_envy:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_gotobuy:
                if (UserUtils.userisLogin) {
                    Intent intent1 = new Intent(getActivity(), PayActivity.class);
                    intent1.putExtra("goodscar", JsonUtils.getJsonStringformat(datas));
                    startActivity(intent1);
                } else {
                    Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent2);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}

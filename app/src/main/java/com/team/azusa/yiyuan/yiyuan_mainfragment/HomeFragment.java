package com.team.azusa.yiyuan.yiyuan_mainfragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.HomePopularityRviewAdapter;
import com.team.azusa.yiyuan.adapter.HomePublicRviewAdapter;
import com.team.azusa.yiyuan.bean.Advert;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.bean.ShowOrderDto;
import com.team.azusa.yiyuan.callback.AdvertCallback;
import com.team.azusa.yiyuan.callback.AllProductCallback;
import com.team.azusa.yiyuan.callback.DaoJiShiCallback;
import com.team.azusa.yiyuan.callback.ShowOrderCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.event.IntParameterEvent;
import com.team.azusa.yiyuan.event.SortEvent;
import com.team.azusa.yiyuan.listener.MyOnClickListener;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.widget.DividerGridItemDecoration;
import com.team.azusa.yiyuan.widget.FlashView;
import com.team.azusa.yiyuan.widget.RefreshHead;
import com.team.azusa.yiyuan.widget.WrapScrollRview;
import com.team.azusa.yiyuan.yiyuan_activity.AllPartakerActivity;
import com.team.azusa.yiyuan.yiyuan_activity.GoodsDetailsActivity;
import com.team.azusa.yiyuan.yiyuan_activity.RechargeActivity;
import com.team.azusa.yiyuan.yiyuan_activity.SearchActivity;
import com.team.azusa.yiyuan.yiyuan_activity.ShowOrderActivity;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class HomeFragment extends Fragment {

    @Bind(R.id.title_search)
    ImageView titleSearch;
    @Bind(R.id.flash_view)
    FlashView flashView;
    @Bind(R.id.new_recommend)
    ImageButton newRecommend;
    @Bind(R.id.nav_shaidan)
    ImageButton navShaidan;
    @Bind(R.id.nav_map)
    ImageButton navMap;
    @Bind(R.id.nav_recharge)
    ImageButton navRecharge;
    @Bind(R.id.ptrpulltorefresh)
    PtrFrameLayout ptrpulltorefresh;
    @Bind(R.id.recyclerview_popularity)
    RecyclerView recyclerview;
    @Bind(R.id.homescrollview)
    ScrollView homescrollview;
    @Bind(R.id.recyclerview_newpublic)
    WrapScrollRview recyclerviewNewpublic;
    @Bind(R.id.img_shaidan1)
    SimpleDraweeView imgShaidan1;
    @Bind(R.id.tv_shaidan_title1)
    TextView tvShaidanTitle1;
    @Bind(R.id.img_shaidan2)
    SimpleDraweeView imgShaidan2;
    @Bind(R.id.tv_shaidan_title2)
    TextView tvShaidanTitle2;
    @Bind(R.id.img_shaidan3)
    SimpleDraweeView imgShaidan3;
    @Bind(R.id.tv_shaidan_title3)
    TextView tvShaidanTitle3;
    @Bind(R.id.click_xiangou)
    ImageView clickXiangou;
    @Bind(R.id.click_jiadian)
    ImageView clickJiadian;
    @Bind(R.id.click_phone)
    ImageView clickPhone;
    @Bind(R.id.click_gold)
    ImageView clickGold;
    @Bind(R.id.click_car)
    ImageView clickCar;
    @Bind(R.id.home_rl_newpublic)
    RelativeLayout homeRlNewpublic;
    @Bind(R.id.home_rl_popularity)
    RelativeLayout homeRlPopularity;
    @Bind(R.id.home_shaidan)
    RelativeLayout homeShaidan;
    private View view;
    private ArrayList<Advert> advertDatas = new ArrayList<>();
    private ArrayList<String> advimageUrls = new ArrayList<>(); //广告为图片url集合
    private RefreshHead refreshHead; //自定义下拉刷新头部
    private ArrayList<ProductDto> populardata; //人气商品的数据集合
    private HomePopularityRviewAdapter popularadapter; //人气商品recyclerView的适配器
    private HomePublicRviewAdapter homePublicRviewAdapter;
    private ArrayList<SimpleDraweeView> shaidanimgs = new ArrayList<>(); //晒单的imageview集合
    private ArrayList<TextView> shaidantvs = new ArrayList<>(); //晒单的imageview集合
    private boolean cancelRequest = false;  //是否取消网络请求
    private boolean advfresh_ok, newpublicerefresh_ok, renqirefresh_ok, shaidanrefresh_ok;

    private ArrayList<ProductDto> publciData = new ArrayList<ProductDto>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updatePublicData();
                    break;
                case 2:
                    if (advfresh_ok && newpublicerefresh_ok && renqirefresh_ok && shaidanrefresh_ok) {
                        ptrpulltorefresh.refreshComplete();
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fgtab01, container, false);
        ButterKnife.bind(this, view);
        iniView();
        initnewpublicRecyclerview();
        initpopularityRecyclerview();
        initShareOrderView();
        setListener();
        return view;
    }

    //初始化晒单分享界面
    private void initShareOrderView() {
        shaidanimgs.add(imgShaidan1);
        shaidanimgs.add(imgShaidan2);
        shaidanimgs.add(imgShaidan3);

        shaidantvs.add(tvShaidanTitle1);
        shaidantvs.add(tvShaidanTitle2);
        shaidantvs.add(tvShaidanTitle3);

    }

    /**
     * 获得晒单分享数据
     */
    public void getShaidanData() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getAllShowOrders")
                .tag("HomeFragment")
                .build().execute(new ShowOrderCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelRequest) {
                    return;
                }
                MyToast.showToast("网络链接出错");
            }

            @Override
            public void onResponse(ArrayList<ShowOrderDto> response) {
                for (int i = 0; i < response.size(); i++) {
                    ArrayList<String> imgs = StringUtil.getList(response.get(i).getImgUrl());
                    String url;
                    //如果没有晒单图片，则显示默认图片
                    if (imgs.size() > 0) {
                        url = imgs.get(0);
                    } else {
                        url = "res:///" + R.drawable.account_pic_default;
                    }
                    ImageLoader.getInstance().displayImage(url, shaidanimgs.get(i));
                    shaidantvs.get(i).setText(response.get(i).getTitle());
                    if (i == 2) {
                        break;
                    }
                }
                shaidanrefresh_ok = true;
                handler.sendEmptyMessage(2);
            }
        });
    }

    //初始化最新揭晓
    private void initnewpublicRecyclerview() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerviewNewpublic.setLayoutManager(gridLayoutManager);
        recyclerviewNewpublic.addItemDecoration(new DividerGridItemDecoration(getActivity(), R.drawable.rv_divider));
        recyclerviewNewpublic.setHasFixedSize(true);
        updatePublicData();
        homePublicRviewAdapter = new HomePublicRviewAdapter(publciData);
        homePublicRviewAdapter.setHandler(handler);
        recyclerviewNewpublic.setAdapter(homePublicRviewAdapter);
        homePublicRviewAdapter.setOnItemClickLitener(new RecyclerViewItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                MyToast.showToast("" + position);
            }
        });
    }

    //更新最新揭晓数据
    private void updatePublicData() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getDaojishi")
                .tag("HomeFragment")
                .build().execute(new DaoJiShiCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelRequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(ArrayList<ProductDto> response) {
                publciData.clear();
                if (null != response && response.size() != 0) {
                    for (int i = 0; i < 4 && i < response.size(); i++) {
                        publciData.add(response.get(i));
                    }
                }
                homePublicRviewAdapter.notifyDataSetChanged();
                newpublicerefresh_ok = true;
                handler.sendEmptyMessage(2);
            }
        });
    }

    //初始化人气推荐
    private void initpopularityRecyclerview() {
        //创建默认的线性LayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerview.setHasFixedSize(true);
        //设置分割线
        recyclerview.addItemDecoration(new VerticalDividerItemDecoration
                .Builder(getActivity()).color(Color.parseColor("#dddddd")).build());

        populardata = new ArrayList<>();
        popularadapter = new HomePopularityRviewAdapter(populardata, getContext());
        recyclerview.setAdapter(popularadapter);
        popularadapter.setOnItemClickLitener(new RecyclerViewItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String productId = populardata.get(position).getProductId();
                int yunNum = populardata.get(position).getYunNum();

                Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("yunNum", yunNum + "");
                startActivity(intent);
            }

        });
    }

    private void setListener() {
        //广告位图片点击监听
        flashView.setOnPageClickListener(new MyOnClickListener() {
            @Override
            public void onClick(int position) {
                clickSearch(advertDatas.get(position).getKey_word());
            }
        });
    }

    //获取广告位信息
    private void getAdvertData() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getAdvert")
                .tag("HomeFragment").build().execute(new AdvertCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelRequest) {
                    return;
                }
                MyToast.showToast("网络链接出错");
            }

            @Override
            public void onResponse(ArrayList<Advert> response) {
                advertDatas = response;
                ArrayList<String> temp = new ArrayList<>();
                for (int i = 0; i < response.size(); i++) {
                    temp.add(response.get(i).getUrl());
                }
                if (!advimageUrls.containsAll(temp)) {
                    advimageUrls = temp;
                    flashView.setImageUris(advimageUrls);
                }
                advfresh_ok = true;
                handler.sendEmptyMessage(2);
            }
        });
    }

    private void iniView() {

        ptrpulltorefresh.disableWhenHorizontalMove(true); //解决跟viewpager的滑动冲突
        refreshHead = new RefreshHead(getActivity(), "mainpager");
        ptrpulltorefresh.setHeaderView(refreshHead);
        ptrpulltorefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getAdvertData();
                getPopularityData();
                updatePublicData();
                getShaidanData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return homescrollview.getScrollY() == 0;
            }
        });

        ptrpulltorefresh.addPtrUIHandler(refreshHead); //添加下拉刷新头部UI变化接口
        homescrollview.scrollTo(0, 0);
        setAutoRefresh();
    }

    //设置自动刷新
    private void setAutoRefresh() {
        ptrpulltorefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrpulltorefresh.autoRefresh();
            }
        }, 100);

    }

    /**
     * 从服务器获取数据
     * <p/>
     * 获得人气数据
     */
    private void getPopularityData() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getCurrentYunNums")
                .addParams("orderBy", "人气")
                .tag("HomeFragment")
                .build().execute(new AllProductCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelRequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(ArrayList<ProductDto> response) {
                populardata.clear();
                for (int i = 0; i < response.size(); i++) {
                    populardata.add(response.get(i));
                    if (i == 5)
                        break;
                }
                popularadapter.notifyDataSetChanged();
                renqirefresh_ok = true;
                handler.sendEmptyMessage(2);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        cancelRequest = true;
        OkHttpUtils.getInstance().cancelTag("HomeFragment");
    }

    @OnClick({R.id.home_rl_newpublic, R.id.home_rl_popularity, R.id.home_shaidan, R.id.click_xiangou, R.id.click_jiadian, R.id.click_phone, R.id.click_gold, R.id.click_car, R.id.title_search, R.id.new_recommend, R.id.nav_shaidan, R.id.nav_map, R.id.nav_recharge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.new_recommend:
                break;
            case R.id.nav_shaidan:
                turntoShareOrderPage();
                break;
            case R.id.nav_map:
                Intent intent1 = new Intent(getActivity(), AllPartakerActivity.class);
                intent1.putExtra("what", 1);
                startActivity(intent1);
                break;
            case R.id.nav_recharge:
                Intent intent2 = new Intent(getActivity(), RechargeActivity.class);
                startActivity(intent2);
                break;
            case R.id.click_xiangou:
                clickSearch("xianGou");
                break;
            case R.id.click_jiadian:
                clickSearch("家电");
                break;
            case R.id.click_phone:
                clickSearch("手机");
                break;
            case R.id.click_gold:
                clickSearch("黄金");
                break;
            case R.id.click_car:
                clickSearch("汽车");
                break;
            case R.id.home_rl_newpublic:
                EventBus.getDefault().post(new IntParameterEvent(2));
                break;
            case R.id.home_rl_popularity:
                EventBus.getDefault().post(new SortEvent("人气", 2));
                EventBus.getDefault().post(new IntParameterEvent(1));
                break;
            case R.id.home_shaidan:
                turntoShareOrderPage();
                break;
        }
    }

    //跳转到所有晒单页面
    private void turntoShareOrderPage() {
        Intent shaidan = new Intent(getActivity(), ShowOrderActivity.class);
        shaidan.putExtra("what", 1);
        startActivity(shaidan);
    }

    //跳转到搜索页面
    private void clickSearch(String search_key) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("what", ConstanceUtils.MESSAGE_OK);
        intent.putExtra("search_key", search_key);
        startActivity(intent);
    }

}

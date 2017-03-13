package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.YunNumRviewAdapter;
import com.team.azusa.yiyuan.bean.YunNumDto;
import com.team.azusa.yiyuan.callback.ProductDetailCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YunNumberActivity extends Activity {

    @Bind(R.id.rv_yunnun)
    XRecyclerView recyclerview;
    private ArrayList<String> data = new ArrayList<String>();
    private YunNumRviewAdapter adapter;
    private View footer;
    //上拉加载更多的footer里面的pb，tv；
    private ProgressBar loadmore_pb;
    private Animation animation; //pb的旋转动画
    private String productId;                //商品ID
    private int yuncount, lastNumber;
    private int x, y;
    private Intent intent;
    private MyDialog myDialog = new MyDialog();
    private AlertDialog loding_dialog;
    private String yunnumber;
    private String yunNum;
    private boolean cancelrequest = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            for (int i = 1; i < 90; i++) {
                x = --yuncount;
                if (x <= 0)
                    break;
                data.add("第" + x + "云");
            }
            loadmore_pb.clearAnimation();
            recyclerview.loadMoreComplete();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_number);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    //获取其他activity传过来的数据
    private void initData() {
        Intent intent = getIntent();
        yuncount = intent.getIntExtra("yuncount", 0);
        lastNumber = yuncount;
        productId = intent.getStringExtra("productId");
        yunNum = intent.getStringExtra("yunNum");
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerview.setLayoutManager(gridLayoutManager);
        recyclerview.setHasFixedSize(true);
        data = new ArrayList<String>();
        adapter = new YunNumRviewAdapter(data, Integer.parseInt(yunNum), lastNumber);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new RecyclerViewItemClickLitener() {

            @Override
            public void onItemClick(View view, int position) {
                loding_dialog = myDialog.showLodingDialog(YunNumberActivity.this);
                loding_dialog.setOnKeyListener(backlistener);
                String URL = Config.IP + "/yiyuan/b_getYunNum";
                yunnumber = lastNumber - position + "";
                OkHttpUtils.get().url(URL)
                        .addParams("productId", productId)
                        .addParams("yunNum", yunnumber)
                        .tag("YunNumberActivity")
                        .build().execute(new ProductDetailCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (cancelrequest) {
                            return;
                        }
                        MyToast.showToast("网络连接出错");
                    }

                    @Override
                    public void onResponse(YunNumDto response) {
                        loding_dialog.dismiss();
                        switch (response.getStatus()) {
                            case "0":
                            case "1":
                                intent = new Intent(YunNumberActivity.this, GoodsDetailsActivity.class);
                                intent.putExtra("productId", productId);
                                intent.putExtra("yunNum", yunnumber);
                                intent.putExtra("yunNumId", response.getYunNumId());
                                startActivity(intent);
                                finish();
                                break;
                            case "2":
                                intent = new Intent(YunNumberActivity.this, ResultActivity.class);
                                String data = JsonUtils.getJsonStringformat(response);
                                intent.putExtra("data", data);
                                startActivity(intent);
                                finish();
                                break;
                        }
                    }
                });

            }
        });
        recyclerview.setPullRefreshEnabled(false);
        footer = View.inflate(getApplicationContext(), R.layout.listview_footer, null);
        loadmore_pb = (ProgressBar) footer.findViewById(R.id.pull_to_refresh_load_progress);
        recyclerview.addFootView(footer);
        initAnimation();
        recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                loadmore_pb.startAnimation(animation);
                handler.sendEmptyMessageDelayed(1, 500);
            }
        });
        getdata();
        recyclerview.loadMoreComplete();
    }

    //初次获取data的数据
    private void getdata() {
        if (yuncount <= 90) {
            y = yuncount;
        } else {
            y = 90;
        }
        data.add("第" + yuncount + "云(进行中)");
        for (int i = 1; i < y; i++) {
            x = --yuncount;
            data.add("第" + x + "云");
        }
    }

    private DialogInterface.OnKeyListener backlistener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                myDialog.dismissDialog();
                return false;    //已处理
            }
            return false;
        }
    };

    private void initAnimation() {
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myrotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin); //设置插值器，匀速加载动画
    }

    @OnClick(R.id.yun_number_go_back)
    public void onClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("YunNumberActivity");
        super.onDestroy();
    }
}

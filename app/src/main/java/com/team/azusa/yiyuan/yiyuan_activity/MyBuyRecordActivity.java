package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.MyBuyRecordLvAdapter;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.callback.BuyRecordProductCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.PulluptoRefreshListview;
import com.team.azusa.yiyuan.widget.RefreshHead;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MyBuyRecordActivity extends AppCompatActivity {


    @Bind(R.id.mybuyrecordlv)
    PulluptoRefreshListview mybuyrecordlv;
    @Bind(R.id.buyrecordptrpulltorefresh)
    PtrFrameLayout buyrecordptrpulltorefresh;
    private MyBuyRecordLvAdapter adapter;
    private ArrayList<BuyRecordInfo> datas;
    private RefreshHead refreshHead;
    private boolean cancelrequest = false; //是否取消网络请求
    private MyDialog myDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (1 == msg.what) {
                Intent intent = new Intent(MyBuyRecordActivity.this, YunRecordDetailActivity.class);
                intent.putExtra("product_data", msg.obj.toString());
                startActivityForResult(intent, 1);
                myDialog.dismissDialog();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_buy_record);
        ButterKnife.bind(this);
        initData();
        initView();
        setListener();
    }

    private void setListener() {
        mybuyrecordlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == datas.size()) {
                    return;
                }
                myDialog = new MyDialog();
                myDialog.showLodingDialog(MyBuyRecordActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = JsonUtils.getJsonStringformat(datas.get(position));
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = data;
                        handler.sendMessageDelayed(message, 500);
                    }
                }).start();
            }
        });
    }

    private void initData() {
        datas = new ArrayList<>();
        adapter = new MyBuyRecordLvAdapter(datas, MyBuyRecordActivity.this);
        mybuyrecordlv.setAdapter(adapter);
    }


    /**
     * 从服务器获取数据
     *
     * @param firstResult 要加载的第一条数据的position
     */
    private void getData(final int firstResult, final int what) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_getUserRecord")
                .addParams("userId", UserUtils.user.getId())
                .addParams("firstResult", firstResult + "")
                .tag("MyBuyRecordActivity")
                .build().execute(new BuyRecordProductCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
                mybuyrecordlv.setLoadComplete(true);
            }

            @Override
            public void onResponse(ArrayList<BuyRecordInfo> response) {
                if (null == response || response.size() == 0) {
                    mybuyrecordlv.setLoadComplete(true);
                    buyrecordptrpulltorefresh.refreshComplete();
                    return;
                } else if (1 == what) {
                    datas.clear();
                }
                if (1 == what) {
                    if (cancelrequest) {
                        return;
                    }
                    buyrecordptrpulltorefresh.refreshComplete();
                }
                mybuyrecordlv.setLoading(false);
                datas.addAll(response);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        refreshHead = new RefreshHead(MyBuyRecordActivity.this, "buyRecordPage");
        buyrecordptrpulltorefresh.setHeaderView(refreshHead);
        buyrecordptrpulltorefresh.addPtrUIHandler(refreshHead); //添加下拉刷新头部UI变化接口

        buyrecordptrpulltorefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mybuyrecordlv.isLoadmoreComplete()) {
                    mybuyrecordlv.setLoadComplete(false);
                }
                getData(0, 1);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return mybuyrecordlv.getChildAt(0).getTop() == 0;
            }
        });

        mybuyrecordlv.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                getData(datas.size(), 2);
            }
        });
        buyrecordptrpulltorefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                buyrecordptrpulltorefresh.autoRefresh();
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelrequest = true;
        ButterKnife.unbind(this);
        OkHttpUtils.getInstance().cancelTag("MyBuyRecordActivity");
    }

    @OnClick(R.id.return_myrecord)
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.return_myrecord:
                finish();
                break;
        }
    }
}
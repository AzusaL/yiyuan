package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.PartakerRecordAdapter;
import com.team.azusa.yiyuan.bean.JoinRecordDto;
import com.team.azusa.yiyuan.callback.JoinRecordCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.widget.PulluptoRefreshListview;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllPartakerActivity extends Activity {

    @Bind(R.id.all_partaker_recordlv)
    PulluptoRefreshListview allPartakerRecordlv;    //Listview

    private PartakerRecordAdapter adapter;         //Listview adapter           //数据
    private String yunNumId = "";                //期数的ID
    private ArrayList<JoinRecordDto> datas = new ArrayList<>();//得到的晒单数据
    private boolean cancelnet = false;

    private int what; //区分是显示全站购买记录还是某商品的所有购买记录 1为全站，0为某商品

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allpartaker);
        ButterKnife.bind(this);
        iniData();
        setListener();
    }

    private void setListener() {
        allPartakerRecordlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == datas.size()) {
                    return;
                }
                Intent intent = new Intent(AllPartakerActivity.this, UsermsgActivity.class);
                intent.putExtra("user_name", datas.get(position).getJoinUserName());
                intent.putExtra("user_id", datas.get(position).getJoinUserId());
                intent.putExtra("userhead_img", datas.get(position).getJoinUserImgUrl());
                startActivity(intent);
            }
        });
    }

    private void iniData() {
        Intent intent = getIntent();
        what = intent.getIntExtra("what", -1);
        if (what == -1) {
            yunNumId = intent.getStringExtra("yunNumId");
        }
        adapter = new PartakerRecordAdapter(datas, what);
        allPartakerRecordlv.setAdapter(adapter);
        allPartakerRecordlv.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                if (what == 1) {
                    getAllData(datas.size());
                } else {
                    getData(datas.size());
                }
            }
        });

        if (what == 1) {
            getAllData(0);
        } else {
            getData(0);
        }
    }

    private void getData(int firstResult) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getRecordByYunNum")
                .addParams("yunNumId", yunNumId)
                .addParams("firstResult", firstResult + "")
                .tag("AllPartakerActivity")
                .build().execute(new JoinRecordCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelnet) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(ArrayList<JoinRecordDto> response) {
                if (response == null || response.isEmpty()) {
                    allPartakerRecordlv.setLoadComplete(true);
                    return;
                }
                allPartakerRecordlv.setLoading(false);
                datas.addAll(response);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void getAllData(int firstResult) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getJoinRecord")
                .addParams("firstResult", firstResult + "")
                .tag("AllPartakerActivity")
                .build().execute(new JoinRecordCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelnet) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(ArrayList<JoinRecordDto> response) {
                if (response == null || response.isEmpty()) {
                    allPartakerRecordlv.setLoadComplete(true);
                    return;
                }
                allPartakerRecordlv.setLoading(false);
                datas.addAll(response);
                adapter.notifyDataSetChanged();
            }
        });
    }


    @OnClick(R.id.all_pt_go_back)
    public void onClick() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        cancelnet = true;
        OkHttpUtils.getInstance().cancelTag("AllPartakerActivity");
    }
}

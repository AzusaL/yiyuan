package com.team.azusa.yiyuan.yiyuan_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.PhotoDetailRvAdapter;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoTextActivity extends AppCompatActivity {

    @Bind(R.id.tw_go_back)
    ImageView twGoBack;
    @Bind(R.id.rv_photodetail)
    RecyclerView recyclerView;

    private ArrayList<String> imgdatas; //图片URL集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_text);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        imgdatas = StringUtil.getList(getIntent().getStringExtra("imgs"));
    }

    private void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(ConstanceUtils.CONTEXT);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new PhotoDetailRvAdapter(imgdatas));

    }

    @OnClick(R.id.tw_go_back)
    public void onClick() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.CalculateDetailsLvAdapter;
import com.team.azusa.yiyuan.bean.CalculateDto;
import com.team.azusa.yiyuan.bean.User;
import com.team.azusa.yiyuan.callback.CalculateCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalculateDetailsActivity extends AppCompatActivity {

    @Bind(R.id.lv_calculate)
    ListView listview;
    private boolean cancelreq = false;
    private CalculateDetailsLvAdapter adapter;
    private String YunNumId;
    private long jiexiaoTime; //揭晓时间
    private int total_count; //本商品总需要参与人次
    private ArrayList<CalculateDto> datas = new ArrayList<>(); //计算详情集合
    private AlertDialog dialog;
    private View footer, header; //listview的头部和尾部view
    private TextView tv_headtime, tv_headcount; //头部显示的时间和记录个数
    private TextView tv_footer_qiuhe, tv_footer_quyu, tv_footer_result; //尾部显示的求和、取余、结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_details);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        dialog = new MyDialog().showLodingDialog(CalculateDetailsActivity.this);
        // 初始化头部和尾部
        header = View.inflate(ConstanceUtils.CONTEXT, R.layout.headview_calculate, null);
        footer = View.inflate(ConstanceUtils.CONTEXT, R.layout.footerview_calculate, null);
        tv_headtime = (TextView) header.findViewById(R.id.tv_jxtime);
        tv_headcount = (TextView) header.findViewById(R.id.tv_jilucount);
        tv_footer_qiuhe = (TextView) footer.findViewById(R.id.tv_calculatedetail1);
        tv_footer_quyu = (TextView) footer.findViewById(R.id.tv_calculatedetail2);
        tv_footer_result = (TextView) footer.findViewById(R.id.tv_calculatedetail3);

        //初始化listview
        listview.addHeaderView(header);
        listview.addFooterView(footer);
        adapter = new CalculateDetailsLvAdapter(datas);
        listview.setAdapter(adapter);
    }

    private void initData() {
        jiexiaoTime = getIntent().getLongExtra("time", 0);
        total_count = getIntent().getIntExtra("count", 0);
        YunNumId = getIntent().getStringExtra("yunNumId");
        getData();
    }

    /**
     * 获得计算详情数据
     */
    private void getData() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getCalculateDetails")
                .addParams("time", jiexiaoTime + "")
                .addParams("yunNumId", YunNumId)
                .tag("CalculateDetailsActivity")
                .build().execute(new CalculateCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelreq) {
                    return;
                }
                MyToast.showToast("网络链接出错");
            }

            @Override
            public void onBefore(Request request) {
                Log.e("TAG", "onBefore: " + request.urlString());
                super.onBefore(request);
            }

            @Override
            public void onResponse(ArrayList<CalculateDto> response) {
                datas.addAll(response);
                adapter.notifyDataSetChanged();
                initFooterandHead();
            }
        });
    }

    private void initFooterandHead() {
        tv_headtime.setText("截止揭晓时间【" + DateUtil.getStringByFormat(jiexiaoTime, DateUtil.dateFormatYMDHMSSSS) + "】");
        tv_headcount.setText("最后" + datas.size() + "条全站购买记录");

        //求和
        long count = 0;
        for (int i = 0; i < datas.size(); i++) {
            count += datas.get(i).getTime();
        }
        tv_footer_qiuhe.setText("1、求和：" + count + "（上面每条云购记录时间距离1970-1-1 00:00:00.000时刻的毫秒数取值相加之和）");

        //取余
        long yushu = 0;
        yushu = count % total_count;
        tv_footer_quyu.setText("2、取余：" + count + "（" + datas.size() + "条时间记录之和）% " + total_count + "(本商品总需要参与人次) = " + yushu + "(余数)");

        //结果
        tv_footer_result.setText("3、结果：" + yushu + "（余数）+ 10000001 = " + (yushu + 10000001));

        dialog.dismiss();
    }

    @OnClick(R.id.back_calculate)
    public void onClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("CalculateDetailsActivity");
        super.onDestroy();
    }
}

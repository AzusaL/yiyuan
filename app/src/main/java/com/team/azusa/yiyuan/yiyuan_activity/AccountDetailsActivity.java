package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.AccountDetails_Chongzhi_LvAdapter;
import com.team.azusa.yiyuan.adapter.AccountDetails_Xiaofei_LvAdapter;
import com.team.azusa.yiyuan.bean.AccountDetail;
import com.team.azusa.yiyuan.callback.AccountDetialsCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.PulluptoRefreshListview;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Delete_exe on 2016/3/22.
 */
public class AccountDetailsActivity extends Activity {
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.account_money_type)
    TextView accountMoneyType;
    @Bind(R.id.account_money)
    TextView accountMoney;
    @Bind(R.id.type_1)
    TextView type1;
    @Bind(R.id.type_2)
    TextView type2;
    @Bind(R.id.type_3)
    TextView type3;
    @Bind(R.id.account_detials_chongzhi)
    PulluptoRefreshListview accountDetialsChongzhi_lv;
    @Bind(R.id.account_detials_xiaofei)
    PulluptoRefreshListview accountDetialsXiaofei_lv;
    private MyDialog myDialog = new MyDialog();
    private AlertDialog loading_dialog;
    private List<AccountDetail> listDetialsChongzhi = new ArrayList<>();
    private List<AccountDetail> listDetialsXiaofei = new ArrayList<>();
    private AccountDetails_Chongzhi_LvAdapter details_Chongzhi_Adapter;
    private AccountDetails_Xiaofei_LvAdapter details_Xiaofei_Adapter;
    private int page_chongzhi = 0;
    private int page_xiaofei = 0;
    private String sum_chongzhi = "0";
    private String sum_xiaofei = "0";
    private boolean cancelrequest = false; //是否取消网络请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        ButterKnife.bind(this);
        loading_dialog = myDialog.showLodingDialog(this);
        loading_dialog.setOnKeyListener(backlistener);
        initView(1);
        initData(1);
        initView(2);
        initData(2);
    }

    public void initData(int part) {
        if (part == 1) {
            OkHttpUtils.get().tag("AccountDetailsActivity")
                    .url(Config.IP + "/yiyuan/user_getUserAccountDetails")
                    .addParams("userId", UserUtils.user.getId())
                    .addParams("type", "1")                                 //“1”为充值明细
                    .addParams("firstResult", "" + page_chongzhi)
                    .build().execute(new AccountDetialsCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    if (cancelrequest) {
                        return;
                    }
                    MyToast.showToast("网络连接出错");
                    loading_dialog.dismiss();
                    accountDetialsChongzhi_lv.setLoadComplete(true);
                }

                @Override
                public void onResponse(Map<String, Object> map) {
                    List<AccountDetail> response = (List<AccountDetail>) map.get("list");
                    if (null == response || response.size() == 0) {
                        accountDetialsChongzhi_lv.setLoadComplete(true);
                        return;
                    }
                    sum_chongzhi = (String) map.get("sum");
                    accountMoney.setText("￥" + sum_chongzhi);
                    listDetialsChongzhi.addAll(response);
                    page_chongzhi += response.size();
                    details_Chongzhi_Adapter.notifyDataSetChanged();
                    accountDetialsChongzhi_lv.setLoading(false);
                    loading_dialog.dismiss();
                }
            });
        }
        if (part == 2) {
            OkHttpUtils.get()
                    .url(Config.IP + "/yiyuan/user_getUserAccountDetails")
                    .addParams("userId", UserUtils.user.getId())
                    .addParams("type", "2")                                 //“2”消费明细
                    .addParams("firstResult", "" + page_xiaofei)
                    .build().execute(new AccountDetialsCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    MyToast.showToast("网络连接出错");
                    loading_dialog.dismiss();
                    accountDetialsXiaofei_lv.setLoadComplete(true);
                }

                @Override
                public void onResponse(Map<String, Object> map) {
                    List<AccountDetail> response = (List<AccountDetail>) map.get("list");
                    if (null == response || response.size() == 0) {
                        accountDetialsXiaofei_lv.setLoadComplete(true);
                        return;
                    }
                    sum_xiaofei = (String) map.get("sum");
                    accountMoney.setText("￥" + sum_xiaofei);
                    page_xiaofei += response.size();
                    listDetialsXiaofei.addAll(response);
                    details_Xiaofei_Adapter.notifyDataSetChanged();
                    accountDetialsXiaofei_lv.setLoading(false);
                    loading_dialog.dismiss();
                }
            });
        }
    }

    public void initView(int part) {
        if (part == 1) {
            details_Chongzhi_Adapter = new AccountDetails_Chongzhi_LvAdapter(listDetialsChongzhi, this);
            accountDetialsChongzhi_lv.setAdapter(details_Chongzhi_Adapter);
            accountDetialsChongzhi_lv.setOnLoadListener(new OnLoadListener() {
                @Override
                public void onLoad() {
                    initData(1);
                }
            });
        } else if (part == 2) {
            details_Xiaofei_Adapter = new AccountDetails_Xiaofei_LvAdapter(listDetialsXiaofei, this);
            accountDetialsXiaofei_lv.setAdapter(details_Xiaofei_Adapter);
            accountDetialsXiaofei_lv.setOnLoadListener(new OnLoadListener() {
                @Override
                public void onLoad() {
                    initData(2);
                }
            });
        }
    }

    @OnClick({R.id.return_account_detials, R.id.chongzhi, R.id.xiaofei})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_account_detials:
                finish();
                break;
            case R.id.chongzhi:
                togglePart();
                break;
            case R.id.xiaofei:
                togglePart();
                break;
        }
    }

    /**
     * 切换页面的开关
     */
    public void togglePart() {
        if (R.id.xiaofei == radioGroup.getCheckedRadioButtonId()) {            //切换至消费明细页面
            type1.setText("消费时间");
            type2.setText("消费金额");
            type2.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 2f));
            type3.setVisibility(View.GONE);
            accountMoneyType.setText("总消费金额:");
            accountMoney.setText("￥" + sum_xiaofei);
            accountDetialsChongzhi_lv.setVisibility(View.GONE);
            accountDetialsXiaofei_lv.setVisibility(View.VISIBLE);
        } else if (R.id.chongzhi == radioGroup.getCheckedRadioButtonId()) {      //切换至充值明细页面
            type1.setText("充值时间");
            type2.setText("资金渠道");
            type2.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            type3.setVisibility(View.VISIBLE);
            accountMoneyType.setText("总充值金额:");
            accountMoney.setText("￥" + sum_chongzhi);
            accountDetialsChongzhi_lv.setVisibility(View.VISIBLE);
            accountDetialsXiaofei_lv.setVisibility(View.GONE);
        }
    }

    private DialogInterface.OnKeyListener backlistener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                loading_dialog.dismiss();
                return false;    //已处理
            }
            return false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("AccountDetailsActivity");
        ButterKnife.unbind(this);
    }
}

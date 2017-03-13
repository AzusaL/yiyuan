package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.Setting;
import com.team.azusa.yiyuan.callback.SettingCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.database.SharedPreferenceData;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.WiperSwitch;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookiePolicy;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {


    @Bind(R.id.btn_unlogin)
    Button unlogin_btn;
    @Bind(R.id.tv_newversion)
    TextView tvNewversion;
    @Bind(R.id.wiperSwitch)
    WiperSwitch wiperSwitch;
    @Bind(R.id.cache_size)
    TextView cacheSize;
    @Bind(R.id.tv_callcenter)
    TextView callcenter;
    @Bind(R.id.rl_edtmsg)
    RelativeLayout rlCenter;
    @Bind(R.id.rl_safeseting)
    RelativeLayout rlSafeseting;
    private MyDialog myDialog = new MyDialog();
    private Dialog dialog;
    private Setting setting;
    private String callnum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initData();
        //初始化无图模式开关
        tvNewversion.setText("" + SuggestionsActivity.getVersionName(ConstanceUtils.CONTEXT));
        wiperSwitch.setChecked(Config.isNoDownload);
        wiperSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Config.isNoDownload = isChecked;
                SharedPreferenceData.getInstance().saveImageConfig(ConstanceUtils.CONTEXT, isChecked);
                String toast = isChecked ? "开启无图模式" : "切换为正常显示图片模式";
                MyToast.showToast(toast);
            }
        });
        if (!UserUtils.userisLogin) {
            unlogin_btn.setText("登 录");
            rlCenter.setVisibility(View.GONE);
            rlSafeseting.setVisibility(View.GONE);
        }else {
            rlCenter.setVisibility(View.VISIBLE);
            rlSafeseting.setVisibility(View.VISIBLE);
        }
    }

    public void initData() {
        //初始化客服热线和二维码地址
        OkHttpUtils.get().url(Config.IP + "/yiyuan/setting_getSetting")
                .build().execute(new SettingCallback() {
            @Override
            public void onError(Request request, Exception e) {
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(Setting response) {
                if (null == response) {
                    setting = new Setting();
                    setting.setHotLine("");
                    setting.setUrl("");
                    return;
                }
                setting = response;
                callnum = (null == response.getHotLine() || 0 == response.getHotLine().length()) ? "" : response.getHotLine();
                callcenter.setText(callnum);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstanceUtils.SettingActivity_ForResult &&
                resultCode == ConstanceUtils.SafeActivitySetResult) {
            String result = data.getStringExtra("result");
            if (result.equals("finish"))
                finish();
            super.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if (UserUtils.userisLogin) {
            unlogin_btn.setText("退出登录");
        }
        super.onResume();
    }

    //注销
    private void unLogin() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_UserLoginOut")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    String unloginResponses = jsonObject.get("messageInfo").toString();
                    if ("退出成功".equals(unloginResponses)) {
                        UserUtils.userisLogin = false;
                        UserUtils.user = null;
                        OkHttpUtils.getInstance().getOkHttpClient().setCookieHandler(new CookieManager(
                                new PersistentCookieStore(getApplicationContext()),
                                CookiePolicy.ACCEPT_ALL));
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        MyToast.showToast("网络连接出错");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.return_seting, R.id.rl_edtmsg, R.id.rl_safeseting, R.id.rl_helpcenter, R.id.rl_suggess, R.id.rl_callcenter, R.id.rl_noimg, R.id.rl_acceptmsg, R.id.rl_clearcash, R.id.rl_newversion, R.id.rl_aboutus, R.id.rl_serviceagreement, R.id.btn_unlogin})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回按钮
            case R.id.return_seting:
                finish();
                break;
            //编辑个人资料
            case R.id.rl_edtmsg:
                if (UserUtils.userisLogin) {
                    startActivity(new Intent(SettingActivity.this, EditProfileActivity.class));
                } else {
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                }
                break;
            //安全设置
            case R.id.rl_safeseting:
                Intent startSafeActivity = new Intent(SettingActivity.this, SafeActivity.class);
                startActivityForResult(startSafeActivity, ConstanceUtils.SettingActivity_ForResult);
                break;
            //帮助中心
            case R.id.rl_helpcenter:
                startActivity(new Intent(SettingActivity.this, SettingHelpActivity.class));
                break;
            //意见反馈
            case R.id.rl_suggess:
                startActivity(new Intent(SettingActivity.this, SuggestionsActivity.class));
                break;
            //客服热线
            case R.id.rl_callcenter:
                View v = View.inflate(ConstanceUtils.CONTEXT, R.layout.activity_call_kefu_dialog, null);
                ((TextView) v.findViewById(R.id.dianhua)).setText(callnum);
                dialog = myDialog.showDialog(v, this);
                Button cancal = (Button) v.findViewById(R.id.dialog_cancal);
                Button bohao = (Button) v.findViewById(R.id.dialog_bohao);

                View.OnClickListener l = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.dialog_cancal:
                                dialog.dismiss();
                                break;
                            case R.id.dialog_bohao:
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callnum));
                                startActivity(intent);
                                break;
                        }
                    }
                };
                cancal.setOnClickListener(l);
                bohao.setOnClickListener(l);
                break;
            //无图模式
            case R.id.rl_noimg:
                break;
            //消息接受
            case R.id.rl_acceptmsg:
                break;
            //清除缓存
            case R.id.rl_clearcash:
                break;
            //版本更新
            case R.id.rl_newversion:
                break;
            //关于我们
            case R.id.rl_aboutus:
                Intent intent = new Intent(SettingActivity.this, AboutUsActivity.class);
                String url = (null == setting.getUrl() || 0 == setting.getUrl().length()) ? "" : setting.getUrl();
                intent.putExtra("url", url);
                startActivity(intent);
                break;
            //服务协议
            case R.id.rl_serviceagreement:
                startActivity(new Intent(SettingActivity.this, ServiceAgreementActivity.class));
                break;
            //注销或者登陆
            case R.id.btn_unlogin:
                if (UserUtils.userisLogin) {
                    unLogin();
                } else {
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                }
                break;
        }
    }

}
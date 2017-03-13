package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.Advice;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.EditTextShakeHelper;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuggestionsActivity extends Activity {

    @Bind(R.id.spinner)
    Spinner spinner;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_suggestion_detial)
    EditText etSuggestionDetial;
    @Bind(R.id.mobile_text)
    TextView mobileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        ButterKnife.bind(this);

        mobileText.setText("手机型号 : " + Build.MODEL +
                "\n安卓版本 : " + Build.VERSION.RELEASE +
                "\n网络类型 : " + getCurrentNetType(this) +
                "\nip地址 : " + getPhoneIp() +
                "\nAPP版本名 : " + getVersionName(this) +
                "\nAPP版本号 : " + getVersionCode(this)
        );
    }

    /**
     * 得到当前的手机网络类型
     *
     * @param context
     * @return
     */
    public static String getCurrentNetType(Context context) {
        String type = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            type = "null";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "wifi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = "4g";
            }
        }
        return type;
    }

    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取APP版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 获取APP版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    @OnClick({R.id.return_suggestion, R.id.bt_suggestion})
    public void onClick(View view) {
        EditTextShakeHelper helper = new EditTextShakeHelper(ConstanceUtils.CONTEXT);
        switch (view.getId()) {
            case R.id.return_suggestion:
                finish();
                break;
            case R.id.bt_suggestion:
                Advice advice = new Advice();
                String phone = etPhone.getText().toString();
                if (!StringUtil.isMobileNo(phone)) {
                    helper.shake(etPhone);
                    return;
                }

                String comment = etSuggestionDetial.getText().toString();
                if (StringUtil.isEmpty(comment)) {
                    helper.shake(etPhone);
                    return;
                }
                advice.setTelephone(phone);
                advice.setComment(comment);
                advice.setType(spinner.getSelectedItemPosition() + "");//(0:投诉与建议,1:商品配送,2:售后服务)
                advice.setPhoneDetail(mobileText.getText().toString());
                OkHttpUtils.post().tag("SuggestionsActivity")
                        .url(Config.IP + "/yiyuan/setting_addAdvice")
                        .addParams("advice", JsonUtils.getJsonStringformat(advice))
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        MyToast.showToast("网络连接出错");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            MyToast.showToast(new JSONObject(response).getString("messageInfo"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SuggestionsActivity.this.finish();
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag("SuggestionsActivity");
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

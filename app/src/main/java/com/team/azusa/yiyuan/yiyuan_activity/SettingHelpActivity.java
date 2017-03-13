package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.widget.MyDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingHelpActivity extends AppCompatActivity {

    @Bind(R.id.webview)
    WebView webview;
    private MyDialog myDialog = new MyDialog();
    private AlertDialog loding_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_help);
        ButterKnife.bind(this);
        loding_dialog = myDialog.showLodingDialog(this);
        loding_dialog.setOnKeyListener(backlistener);
        try {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl("http://192.168.1.110:8080/yiyuan/setting_help");
        } catch (Exception e) {
            MyToast.showToast("连接出错");
        } finally {
            myDialog.dismissDialog();
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

    @OnClick(R.id.help_go_back)
    public void onClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}

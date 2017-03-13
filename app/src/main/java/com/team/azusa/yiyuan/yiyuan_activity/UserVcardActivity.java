package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.User;
import com.team.azusa.yiyuan.callback.UserCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserVcardActivity extends AppCompatActivity {

    @Bind(R.id.return_vcard)
    ImageView returnVcard;
    @Bind(R.id.img_userhead)
    SimpleDraweeView imgUserhead;
    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.tv_userphone)
    TextView tvUserphone;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_jinyan)
    TextView tvJinyan;
    @Bind(R.id.tv_dengji)
    TextView tvDengji;
    @Bind(R.id.tv_sign)
    TextView tvSign;
    private MyDialog dialog;
    private String userId; //用户id
    private String userheadurl; //用户头像url
    private String username;  //用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vcard);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userheadurl = intent.getStringExtra("headImg");
        username = intent.getStringExtra("userName");
        dialog = new MyDialog();
        dialog.showLodingDialog(UserVcardActivity.this);
        getData();
    }

    /**
     * 通过Userid获取User对象信息
     */
    private void getData() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_getUser")
                .addParams("userId", userId)
                .build().execute(new UserCallback() {
            @Override
            public void onError(Request request, Exception e) {
                MyToast.showToast("网络链接出错");
            }

            @Override
            public void onResponse(User response) {
                initView(response);
            }
        });
    }

    private void initView(User user) {
        dialog.dismissDialog();
        //设置用户头像
        ImageLoader.getInstance().displayImage(userheadurl, imgUserhead);
        //设置用户名
        tvUsername.setText(username);
        //设置用户手机
        tvUserphone.setText(user.getMobile().substring(0, 5) + "****" + user.getMobile().substring(9, 11));
        //设置性别
        String sex;
        if (StringUtil.isEmpty(user.getGender())) {
            sex = "保密";
        } else {
            sex = user.getGender();
        }
        tvSex.setText(sex);
        //设置经验值
        tvJinyan.setText(user.getJingyan() + "");
        //设置等级
        tvDengji.setText(getUserLv(user.getJingyan()));
        //设置个性签名
        tvSign.setText(StringUtil.parseEmpty(user.getSign()));

    }

    //通过经验获得等级
    public String getUserLv(int exprence) {
        if (0 <= exprence && exprence <= 9999) {
            return "云购小将";
        } else if (exprence <= 99999) {
            return "云购少将";
        } else if (exprence <= 999999) {
            return "云购中将";
        } else if (exprence <= 9999999) {
            return "云购大将";
        } else if (exprence <= 99999999) {
            return "云购将军";
        } else {
            return "云购总司";
        }
    }

    @OnClick({R.id.return_vcard, R.id.img_userhead})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_vcard:
                finish();
                break;
            case R.id.img_userhead:
                break;
        }
    }


}

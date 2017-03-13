package com.team.azusa.yiyuan.yiyuan_mainfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.User;
import com.team.azusa.yiyuan.event.AddPhotoEvent;
import com.team.azusa.yiyuan.event.LoginEvent;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.yiyuan_activity.AccountDetailsActivity;
import com.team.azusa.yiyuan.yiyuan_activity.LoginActivity;
import com.team.azusa.yiyuan.yiyuan_activity.MyAddressActivity;
import com.team.azusa.yiyuan.yiyuan_activity.MyBuyRecordActivity;
import com.team.azusa.yiyuan.yiyuan_activity.MyGaingoodsActivity;
import com.team.azusa.yiyuan.yiyuan_activity.MyShareOrderActivity;
import com.team.azusa.yiyuan.yiyuan_activity.SettingActivity;
import com.team.azusa.yiyuan.yiyuan_activity.UsermsgActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class MeFragment extends Fragment {

    @Bind(R.id.setting)
    ImageView btn_setting;  //设置按钮
    @Bind(R.id.login)
    Button btn_login;  //登陆按钮
    @Bind(R.id.layout_unlogin)
    LinearLayout layoutUnlogin;  //未登陆状态下的头部布局
    @Bind(R.id.fg5_top_img)
    SimpleDraweeView img_userhead;  //用户头像
    @Bind(R.id.yungou_lv_img)
    ImageView Img_yungouLv;  //云购等级图标
    @Bind(R.id.yungou_lv_tv)
    TextView tv_yungouLv;  //云购等级
    @Bind(R.id.fg5_jinyan_tv)
    TextView tv_exprence;  //经验值
    @Bind(R.id.charge_money)
    Button chargeMoney;  //登录后的充值按钮
    @Bind(R.id.me1)
    RelativeLayout play_record; //我的云购记录栏
    @Bind(R.id.me2)
    RelativeLayout my_gaintedgoods; //获得的商品栏
    @Bind(R.id.me3)
    RelativeLayout my_shareorder; //我的晒单栏
    @Bind(R.id.me4)
    RelativeLayout my_frient; //我的朋友栏
    @Bind(R.id.me5)
    RelativeLayout message_center; //消息中心栏
    @Bind(R.id.me6)
    RelativeLayout account_detail; //账户明细栏
    @Bind(R.id.me7)
    RelativeLayout address;  //收货地址栏
    @Bind(R.id.fg5_login_toplayout)
    LinearLayout loginToplayout; //登录后显示的头部
    @Bind(R.id.fg5_username)
    TextView userName;
    @Bind(R.id.tv_fufen)
    TextView tvFufen;
    @Bind(R.id.tv_balance)
    TextView tvBalance;
    private View view;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fgtab05, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        iniView();
        return view;
    }

    private void iniView() {
        if (UserUtils.userisLogin) {
            setLoginView();
        } else {
            setUnLoginView();
        }
    }

    //eventbus接收选择图片界面发来的消息
    public void onEventMainThread(AddPhotoEvent event) {
        //更新头像
        ImageLoader.getInstance().displayImage(event.getImgurl().get(0), img_userhead);
    }

    //eventbus接收登陆界面发来的消息
    public void onEventMainThread(LoginEvent event) {
        if (event.isLoginSuccess()) {
            setLoginView();
        } else {
            setUnLoginView();
        }
    }

    //设置为登陆的界面
    private void setUnLoginView() {
        layoutUnlogin.setVisibility(View.VISIBLE);
        if (loginToplayout.getVisibility() == View.VISIBLE) {
            loginToplayout.setVisibility(View.GONE);
        }
    }

    //设置已登陆的界面
    private void setLoginView() {
        if (layoutUnlogin.getVisibility() == View.VISIBLE) {
            layoutUnlogin.setVisibility(View.GONE);
        }
        loginToplayout.setVisibility(View.VISIBLE);
        user = UserUtils.user;
        //设置头像
        if (StringUtil.isEmpty(UserUtils.user.getImgUrl())) {
            ImageLoader.getInstance().displayImage("res:///" + R.drawable.default_head_, img_userhead);
        } else ImageLoader.getInstance().displayImage(UserUtils.user.getImgUrl(), img_userhead);

        //设置用户账号名
        if (StringUtil.isEmpty(user.getName())) {
            userName.setText(user.getMobile());
        } else {
            userName.setText(user.getName() + "(" + user.getMobile() + ")");
        }
        //设置用户等级
        setUserLv(Img_yungouLv, tv_yungouLv, user.getJingyan());
        //设置经验值
        tv_exprence.setText(user.getJingyan() + "");
        //设置可用福分
        tvFufen.setText(user.getJifen() + "");
        //设置可用余额
        tvBalance.setText("￥" + user.getBalance() + ".00");
    }

    public void setUserLv(ImageView img_lv, TextView tv_lv, int exprence) {
        if (0 <= exprence && exprence <= 9999) {
            img_lv.setImageResource(R.drawable.degree1);
            tv_lv.setText("云购小将");
        } else if (exprence <= 99999) {
            img_lv.setImageResource(R.drawable.degree2);
            tv_lv.setText("云购少将");
        } else if (exprence <= 999999) {
            img_lv.setImageResource(R.drawable.degree3);
            tv_lv.setText("云购中将");
        } else if (exprence <= 9999999) {
            img_lv.setImageResource(R.drawable.degree4);
            tv_lv.setText("云购大将");
        } else if (exprence <= 99999999) {
            img_lv.setImageResource(R.drawable.degree5);
            tv_lv.setText("云购将军");
        } else {
            img_lv.setImageResource(R.drawable.degree6);
            tv_lv.setText("云购总司");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.setting, R.id.login, R.id.fg5_top_img, R.id.yungou_lv_img, R.id.yungou_lv_tv, R.id.fg5_jinyan_tv,
            R.id.charge_money, R.id.me1, R.id.me2, R.id.me3, R.id.me4, R.id.me5, R.id.me6, R.id.me7})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting:
                Intent intentsetting = new Intent(getActivity(), SettingActivity.class);
                startActivity(intentsetting);
                break;
            case R.id.login:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.fg5_top_img:
                Intent intent2 = new Intent(getActivity(), UsermsgActivity.class);
                intent2.putExtra("user_id", user.getId());
                if (StringUtil.isEmpty(user.getName())) {
                    intent2.putExtra("user_name",  user.getMobile().substring(0, 5) + "****" + user.getMobile().substring(9, 11));
                } else {
                    intent2.putExtra("user_name", user.getName());
                }
                intent2.putExtra("userhead_img", user.getImgUrl());
                startActivity(intent2);
                break;
            case R.id.yungou_lv_img:
                break;
            case R.id.yungou_lv_tv:
                break;
            case R.id.fg5_jinyan_tv:
                break;
            case R.id.charge_money:
                break;
            case R.id.me1:
                Intent intent3;
                if (UserUtils.userisLogin) {
                    intent3 = new Intent(getActivity(), MyBuyRecordActivity.class);
                } else {
                    intent3 = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intent3);
                break;
            case R.id.me2:
                Intent intentme2;
                if (UserUtils.userisLogin) {
                    intentme2 = new Intent(getActivity(), MyGaingoodsActivity.class);
                } else {
                    intentme2 = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intentme2);
                break;
            case R.id.me3:
                Intent intentme3;
                if (UserUtils.userisLogin) {
                    intentme3 = new Intent(getActivity(), MyShareOrderActivity.class);
                } else {
                    intentme3 = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intentme3);
                break;
            case R.id.me4:
                break;
            case R.id.me5:
                break;
            case R.id.me6:

                Intent intentme6;
                if (UserUtils.userisLogin) {
                    intentme6 = new Intent(getActivity(), AccountDetailsActivity.class);
                } else {
                    intentme6 = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intentme6);

                break;
            case R.id.me7:
                Intent intent1;
                if (UserUtils.userisLogin) {
                    intent1 = new Intent(getActivity(), MyAddressActivity.class);
                } else {
                    intent1 = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intent1);
                break;
        }
    }
}

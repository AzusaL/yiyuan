package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.event.AddPhotoEvent;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.wheelview.OnWheelScrollListener;
import com.team.azusa.yiyuan.wheelview.WheelView;
import com.team.azusa.yiyuan.wheelview.adapter.NumericWheelAdapter;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.SelectPicPopupWindow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class EditProfileActivity extends AppCompatActivity {

    @Bind(R.id.img_userhead)
    SimpleDraweeView img_userhead;
    @Bind(R.id.tv_username)
    TextView tv_username;
    @Bind(R.id.tv_sex)
    TextView tv_sex;
    @Bind(R.id.tv_birthday)
    TextView tv_birthday;
    @Bind(R.id.tv_qq)
    TextView tv_qq;
    @Bind(R.id.rl_qq)
    RelativeLayout rlQq;
    @Bind(R.id.tv_signname)
    TextView tv_signname;
    @Bind(R.id.edit_root)
    LinearLayout edit_root;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private NumericWheelAdapter numericWheelAdapterday;
    private PopupWindow popWindow;
    private View popwindowView;
    private SelectPicPopupWindow menuWindow;//自定义的弹出框类
    private MyDialog myDialog = new MyDialog();
    private AlertDialog loding_dialog;
    private boolean cancelreq = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();

        Log.e("main", "onCreate: "+UserUtils.user.getId());

    }

    //eventbus接收选择图片界面发来的消息
    public void onEventMainThread(AddPhotoEvent event) {
        //更新头像
        ImageLoader.getInstance().displayImage(event.getImgurl().get(0), img_userhead);
        upLoadHeadImg(event.getImgurl().get(0));
    }

    //上传头像
    private void upLoadHeadImg(String imgurl) {
        OkHttpUtils.post().url(Config.IP + "/yiyuan/user_uploadHeadImage")
                .addParams("userId", UserUtils.user.getId())
                .addFile("files", "img.jpg", new File(imgurl.replace("file://", "")))
                .tag("EditProfileActivity")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelreq) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String respon = object.get("messageInfo").toString();
                    if (respon.equals("上传成功")) {
                        MyToast.showToast_center("头像修改成功！");
                    } else {
                        MyToast.showToast_center("头像修改失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        //设置头像
        if (StringUtil.isEmpty(UserUtils.user.getImgUrl())) {
            ImageLoader.getInstance().displayImage("res:///" + R.drawable.default_head_, img_userhead);
        } else {
            ImageLoader.getInstance().displayImage(UserUtils.user.getImgUrl(), img_userhead);
        }
        //设置昵称
        if (StringUtil.isEmpty(UserUtils.user.getName())) {
            tv_username.setText("未设置昵称");
        } else {
            tv_username.setText(UserUtils.user.getName());
        }
        //设置性别
        if (StringUtil.isEmpty(UserUtils.user.getGender())) {
            tv_sex.setText("保密");
        } else {
            tv_sex.setText(UserUtils.user.getGender());
        }
        //设置生日
        tv_birthday.setText(DateUtil.getStringByFormat(UserUtils.user.getBirthday()
                , DateUtil.dateFormatYMD));
        //设置QQ
        if (StringUtil.isEmpty(UserUtils.user.getQQ())) {
            tv_qq.setText("");
        } else {
            tv_qq.setText("" + UserUtils.user.getQQ());
        }
        //设置个性签名
        if (StringUtil.isEmpty(UserUtils.user.getSign())) {
            tv_signname.setText("");
        } else {
            tv_signname.setText(UserUtils.user.getSign());
        }
        //设置日期弹出窗布局
        popwindowView = View.inflate(this, R.layout.wheel_date_picker, null);
        Button ensure = (Button) popwindowView.findViewById(R.id.date_edit_ensure);
        Button cancel = (Button) popwindowView.findViewById(R.id.date_edit_cancel);
        initPopupWindow(popwindowView);
        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (R.id.date_edit_ensure == v.getId()) {
                    String birthday = new StringBuilder().append((year.getCurrentItem() + 1910)).append("-")
                            .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1)
                                    : (month.getCurrentItem() + 1))
                            .append("-").append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1)
                                    : (day.getCurrentItem() + 1))
                            .toString();
                    if (!birthday.equals(tv_birthday.getText().toString())) {
                        tv_birthday.setText(birthday);
                        save_information("birthday", birthday);
                        UserUtils.user.setBirthday(DateUtil.getDateByFormat(birthday, "yyyy-MM-dd"));
                    }
                    popWindow.dismiss();
                } else if (R.id.date_edit_cancel == v.getId()) {
                    popWindow.dismiss();
                }
            }
        };
        ensure.setOnClickListener(clickListener);
        cancel.setOnClickListener(clickListener);
    }

    @OnClick({R.id.btn_go_back, R.id.rl_headimg, R.id.rl_name, R.id.rl_sex, R.id.rl_birthday, R.id.rl_qq,
            R.id.rl_signname})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go_back:
                finish();
                break;
            case R.id.rl_headimg:
                Intent intent = new Intent(EditProfileActivity.this, PhotoSelectActivity.class);
                intent.putExtra("pic_count", 1);
                intent.putExtra("isneedcutimg", true);
                startActivity(intent);
                break;
            case R.id.rl_name:
                Intent starUserNameActivity = new Intent(EditProfileActivity.this, AlterUserNameActivity.class);
                startActivityForResult(starUserNameActivity, ConstanceUtils.EditProfileActivity_ForResult);
                break;
            case R.id.rl_sex:
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(EditProfileActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(EditProfileActivity.this.findViewById(R.id.edit_root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

                break;
            case R.id.rl_birthday:
                popWindow.showAtLocation(edit_root, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.rl_qq:
                Intent starAlterQQ = new Intent(EditProfileActivity.this, AlterQQActivity.class);
                startActivityForResult(starAlterQQ, ConstanceUtils.EditProfileActivity_ForResult);
                break;
            case R.id.rl_signname:
                Intent starAlterSingature = new Intent(EditProfileActivity.this, AlterSignatureActivity.class);
                startActivityForResult(starAlterSingature, 100);
                break;
        }
    }

    //为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.tv_girl:
                    if (!UserUtils.user.getGender().equals("女")) {
                        save_information("gender", "女");
                        UserUtils.user.setGender("女");
                        tv_sex.setText("女");
                    }
                    break;
                case R.id.tv_boy:
                    if (!tv_sex.getText().toString().equals("男")) {
                        save_information("gender", "男");
                        UserUtils.user.setGender("男");
                        tv_sex.setText("男");
                    }
                    break;
                case R.id.tv_secret:
                    if (!tv_sex.getText().toString().equals("保密")) {
                        save_information("gender", "保密");
                        UserUtils.user.setGender("保密");
                        tv_sex.setText("保密");
                    }
                    break;
                default:
                    break;
            }


        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getStringExtra("result");
        switch (result) {
            case "name":
                tv_username.setText(UserUtils.user.getName());
                save_information("name", UserUtils.user.getName());
                break;
            case "qq":
                tv_qq.setText(UserUtils.user.getQQ());
                save_information("qq", UserUtils.user.getQQ());
                break;
            case "sign":
                tv_signname.setText(UserUtils.user.getSign());
                save_information("sign", UserUtils.user.getSign());
                break;
            case "Not_change":
                break;
            default:
                break;
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

    private void save_information(String name, String value) {
        loding_dialog = myDialog.showLodingDialog(this);
        loding_dialog.setOnKeyListener(backlistener);
        OkHttpUtils.post()
                .url(Config.IP + "/yiyuan/user_modifyUser")
                .addParams("userId", UserUtils.user.getId())
                .addParams(name, value)
                .tag("EditProfileActivity")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelreq) {
                    return;
                }
                MyToast.showToast("修改出错");
            }

            @Override
            public void onResponse(String response) {
                myDialog.dismissDialog();
                MyToast.showToast("修改成功");
            }
        });
    }

    private void initPopupWindow(View contentView) {
        // 一个自定义的布局，作为显示的内容
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);

//        int curYear = mYear;
//		int curMonth = mMonth + 1;
//		int curDate = mDay;

        year = (WheelView) contentView.findViewById(R.id.year);
        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(this, 1910, curYear);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(false);// 是否可循环滑动
        year.addScrollingListener(scrollListener);

        month = (WheelView) contentView.findViewById(R.id.month);
        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 1, 12, "%02d");
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(false);
        month.addScrollingListener(scrollListener);

        day = (WheelView) contentView.findViewById(R.id.day);
        numericWheelAdapterday = new NumericWheelAdapter(this, 1, 31, "%02d");
        numericWheelAdapterday.setLabel("日");
        day.setViewAdapter(numericWheelAdapterday);
        day.setCyclic(false);

        year.setVisibleItems(7);// 设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        year.setCurrentItem(curYear - 1910);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);

        if (null == popWindow) {
            popWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);

            popWindow.setTouchable(true);

//            popWindow.setTouchInterceptor(new View.OnTouchListener() {
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    return false;
//                    // 这里如果返回true的话，touch事件将被拦截
//                    // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//                }
//            });

            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            popWindow.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.bg11));
        }
        popWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
    }


    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }


        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1910;// 年
            int n_month = month.getCurrentItem() + 1;// 月
            initDay(n_year, n_month);
        }
    };

    private void initDay(int arg1, int arg2) {
        int Max_day = getDay(arg1, arg2);
        numericWheelAdapterday = new NumericWheelAdapter(this, 1, Max_day, "%02d");
        numericWheelAdapterday.setLabel("日");
        day.setViewAdapter(numericWheelAdapterday);
        if (day.getCurrentItem() + 1 > Max_day) {
            day.setCurrentItem(Max_day - 1, true);
        }
    }

    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("EditProfileActivity");
        super.onDestroy();
    }


}

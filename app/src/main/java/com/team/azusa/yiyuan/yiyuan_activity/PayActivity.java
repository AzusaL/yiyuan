package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.PayLvAdapter;
import com.team.azusa.yiyuan.bean.GoodsCar;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.bean.User;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.database.SharedPreferenceData;
import com.team.azusa.yiyuan.event.AddCarsEvent;
import com.team.azusa.yiyuan.event.LoginEvent;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.PayPwdEnterView;
import com.team.azusa.yiyuan.widget.WrapHeightListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class PayActivity extends AppCompatActivity {

    @Bind(R.id.return_pay)
    ImageView returnPay;
    @Bind(R.id.tv_totalmoney)
    TextView tv_totalmoney;
    @Bind(R.id.cb_fufen)
    CheckBox cbFufen;
    @Bind(R.id.rl_fufen)
    RelativeLayout rl_fufen;
    @Bind(R.id.cb_yuer)
    CheckBox cbYuer;
    @Bind(R.id.rl_yuer)
    RelativeLayout rl_yuer;
    @Bind(R.id.cb_weichatpay)
    CheckBox cbWeichatpay;
    @Bind(R.id.rl_weichat_pay)
    RelativeLayout rl_weichatPay;
    @Bind(R.id.cb_alipay)
    CheckBox cbAlipay;
    @Bind(R.id.rl_ali_pay)
    RelativeLayout rl_aliPay;
    @Bind(R.id.btn_gotopay)
    Button btnGotopay;
    @Bind(R.id.paylv)
    WrapHeightListView listview;
    @Bind(R.id.tv_fufen)
    TextView tv_fufen;
    @Bind(R.id.tv_yuer)
    TextView tv_yuer;
    @Bind(R.id.rl_moreraw)
    RelativeLayout rl_moreraw;

    private PayLvAdapter adapter;  //显示商品信息的adapter
    private ArrayList<GoodsCar> datas; //所有商品信息
    private ArrayList<GoodsCar> temdatas;  //临时存储所有商品信息的前三个
    private AlertDialog mloding_dialog;
    private int totalyuan = 0; //总需支付金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        datas = JsonUtils.getcarlistfromString(getIntent().getStringExtra("goodscar"));
        temdatas = new ArrayList<>();
    }

    private void initView() {
        //设置显示商品信息的listview,item大于3则隐藏大于3的部分，显示加载更多的布局
        if (datas.size() > 3) {
            rl_moreraw.setVisibility(View.VISIBLE);
            temdatas.add(datas.get(0));
            temdatas.add(datas.get(1));
            temdatas.add(datas.get(2));
        } else {
            temdatas.addAll(datas);
            rl_moreraw.setVisibility(View.GONE);
        }
        adapter = new PayLvAdapter(temdatas);
        listview.setAdapter(adapter);
        //设置需支付金额
        for (int i = 0; i < datas.size(); i++) {
            totalyuan += datas.get(i).getBuyCount();
        }
        tv_totalmoney.setText(totalyuan + ".00");

        //设置福分和余额（福分和余额可合并付款，但两者都不能与网银支付合并付款）
        if ((UserUtils.user.getJifen() / 1000 + UserUtils.user.getBalance()) >= totalyuan) {
            if (UserUtils.user.getJifen() / 1000 < 1) {
                tv_fufen.setText("您的福分不足（您的福分：" + UserUtils.user.getJifen() + "）");
                rl_fufen.setClickable(false);
            } else {
                tv_fufen.setText("您的福分：" + UserUtils.user.getJifen());
            }

            if (UserUtils.user.getBalance() < 1) {
                tv_yuer.setText("您的余额不足（您的余额：￥" + UserUtils.user.getBalance() + ".00）");
                rl_yuer.setClickable(false);
            } else {
                tv_yuer.setText("您的余额：￥" + UserUtils.user.getBalance() + ".00");
            }

        } else {
            tv_fufen.setText("您的福分不足（您的福分：" + UserUtils.user.getJifen() + "）");
            tv_yuer.setText("您的余额不足（您的余额：￥" + UserUtils.user.getBalance() + ".00）");
            rl_fufen.setClickable(false);
            rl_yuer.setClickable(false);
        }

    }

    /**
     * 支付
     *
     * @param payType  payType为支付方式，0为余额，1为网银支付
     * @param useJifen 是否使用福分，1表示使用，0表示不使用
     */
    private void pay(final int payType, final int useJifen, String pwd) {
        mloding_dialog = new MyDialog().showLodingDialog(PayActivity.this);
        StringBuilder url = new StringBuilder();
        url.append(Config.IP + "/yiyuan/p_pay?userId=")
                .append(UserUtils.user.getId())
                .append("&payType=" + payType)
                .append("&useJifen=" + useJifen)
                .append("&payPwd=" + pwd);
        for (int i = 0; i < datas.size(); i++) {
            url.append("&productId=" + datas.get(i).getProductDto().getProductId());
            url.append("&num=" + datas.get(i).getBuyCount());
        }
        OkHttpUtils.get().url(url.toString())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                MyToast.showToast("网络连接出错");
                mloding_dialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String payResponses = jsonObject.get("message").toString();
                    if ("success".equals(payResponses)) {
                        MyToast.showToast("购买成功");
                        //如果支付方式是余额或者福分，后台会返回一个User对象，更新User对象的余额福分等信息
                        if (0 == payType) {
                            UserUtils.user = JsonUtils.getObjectfromString(jsonObject.get("user").toString(), User.class);
                            EventBus.getDefault().post(new LoginEvent(true)); //通知MeFragment更新显示用户信息
                        }
                        //清空主页购物车显示的商品个数
                        EventBus.getDefault().post(new AddCarsEvent(0));
                        //清空购物车
                        ProductDto dto = new ProductDto();
                        dto.setProductId("0");
                        EventBus.getDefault().post(dto);
                        //清空SharedPreference中存储的购物车信息
                        datas.clear();
                        SharedPreferenceData.getInstance().saveGoodsCarData(ConstanceUtils.CONTEXT, datas);
                        Intent intent = new Intent(PayActivity.this, MyBuyRecordActivity.class);
                        mloding_dialog.dismiss();
                        startActivity(intent);
                        finish();
                    } else {
                        MyToast.showToast("密码错误！");
                        mloding_dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //1表示把福分余额取消，其他表示把网银支付的取消
    private void setUncheck(int what) {
        if (1 == what) {
            cbYuer.setChecked(false);
            cbFufen.setChecked(false);
        } else {
            cbWeichatpay.setChecked(false);
            cbAlipay.setChecked(false);
        }
    }

    private void showPayPwdView() {
        View dialogview = View.inflate(ConstanceUtils.CONTEXT, R.layout.dialog_paypwd, null);
        final MyDialog dialog = new MyDialog();
        dialog.showAddgoodsCarDialog(PayActivity.this,dialogview);
        PayPwdEnterView editpwd = (PayPwdEnterView) dialogview.findViewById(R.id.edit_pwd);
        RelativeLayout cancel = (RelativeLayout) dialogview.findViewById(R.id.cancel_pay_pwd);
        //输入完成的回调接口
        editpwd.setSecurityEditCompleListener(new PayPwdEnterView.SecurityEditCompleListener() {
            @Override
            public void onNumCompleted(String num) {
                //判断是否使用福分
                if (cbFufen.isChecked()) {
                    pay(0, 1, num);
                } else {
                    pay(0, 0, num);
                }
                dialog.dismissDialog();
            }
        });
        //取消按钮点击监听
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismissDialog();
            }
        });
    }


    @OnClick({R.id.return_pay, R.id.rl_fufen, R.id.rl_yuer, R.id.rl_weichat_pay, R.id.rl_ali_pay, R.id.btn_gotopay, R.id.rl_moreraw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_pay:
                finish();
                break;
            case R.id.rl_moreraw:
                temdatas.clear();
                temdatas.addAll(datas);
                adapter.notifyDataSetChanged();
                rl_moreraw.setVisibility(View.GONE);
                break;
            case R.id.rl_fufen:
                cbFufen.setChecked(!cbFufen.isChecked());
                setUncheck(2);
                break;
            case R.id.rl_yuer:
                cbYuer.setChecked(!cbYuer.isChecked());
                setUncheck(2);
                break;
            case R.id.rl_weichat_pay:
                cbWeichatpay.setChecked(true);
                cbAlipay.setChecked(false);
                setUncheck(1);
                break;
            case R.id.rl_ali_pay:
                cbWeichatpay.setChecked(false);
                cbAlipay.setChecked(true);
                setUncheck(1);
                break;
            case R.id.btn_gotopay:
                //判断支付方式
                if (cbYuer.isChecked() || cbFufen.isChecked()) {
                    //余额或者福分支付时弹出输入支付密码dialog
                    showPayPwdView();
                } else if (cbWeichatpay.isChecked()) {

                } else if (cbAlipay.isChecked()) {

                } else {
                    MyToast.showToast_center("请先选择支付方式！");
                }
                break;
        }
    }
}

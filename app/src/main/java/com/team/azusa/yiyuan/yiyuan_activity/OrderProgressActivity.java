package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.OrderProgressLvAdapter;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.bean.OrderProgress;
import com.team.azusa.yiyuan.bean.OrderProgressDto;
import com.team.azusa.yiyuan.callback.OrderProgressCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.WrapHeightListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 订单进度页面
 */
public class OrderProgressActivity extends AppCompatActivity {

    @Bind(R.id.return_orderprogress)
    ImageView btn_back;
    @Bind(R.id.btn_shaidanorshouhuo)
    Button btn_shaidanorshouhuo;
    @Bind(R.id.tv_adressmsg)
    TextView tv_adressmsg;
    @Bind(R.id.lv_orderprogress)
    WrapHeightListView listview;
    @Bind(R.id.img_product)
    SimpleDraweeView img_product;
    @Bind(R.id.tv_productname)
    TextView tv_productname;
    @Bind(R.id.tv_luckyunnum)
    TextView tv_luckyunnum;
    @Bind(R.id.tv_time)
    TextView tv_time;
    @Bind(R.id.rl_productmsg)
    RelativeLayout rlProductmsg;
    @Bind(R.id.rl_setaddress)
    RelativeLayout rlSetaddress;

    private OrderProgressLvAdapter adapter; //进度lv适配器
    private ArrayList<OrderProgress> datas = new ArrayList<>(); //进度信息
    private BuyRecordInfo buyRecordInfo; //商品购买信息

    private OrderProgressDto orderProgressDto; //订单进度详情
    private MyDialog dialog;
    private boolean cancelrequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_progress);
        ButterKnife.bind(this);
        initData();
        initView();
        getData();
    }

    private void initData() {
        buyRecordInfo = JsonUtils.getObjectfromString(
                getIntent().getStringExtra("buyRecordInfo"), BuyRecordInfo.class);
    }

    //从服务器获取订单进度信息
    private void getData() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/order_getProgressOrder")
                .addParams("yunNumId", buyRecordInfo.getYunNumId())
                .addParams("bId", buyRecordInfo.getBuyRecordId())
                .tag("OrderProgressActivity")
                .build().execute(new OrderProgressCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("服务器出错！");
            }

            @Override
            public void onResponse(OrderProgressDto response) {
                orderProgressDto = response;
                datas.clear();
                datas.add(new OrderProgress("恭喜您云购成功，请尽快填写收货地址，以便我们为您配送", new Date(buyRecordInfo.getJieXiaoTime())));
                //首先判断用户是否已确认收货地址
                if (response != null && 1 == response.getFlag()) {
                    datas.add(new OrderProgress("会员已填写配送地址信息，等待商城发货。", response.getDistributionAddrDate()));
                    datas.add(new OrderProgress("您的配送信息已确认。", response.getDistributionAddrDate()));
                    rlSetaddress.setClickable(false); //确认地址后，更改地址布局不能点击
                    tv_adressmsg.setText("配送地址：" + response.getName() + " "
                            + response.getMobile() + " " + response.getRough() + response.getDatail());
                    //判断是否发货,快递信息不为空则为已发货状态
                    if (!StringUtil.isEmpty(response.getPostCompany())) {
                        datas.add(new OrderProgress("您的商品[" + buyRecordInfo.getTitle() + "]由" + response.getPostCompany()
                                + "[单号：" + response.getPostId() + "]配送，请注意收货。", response.getPostDate()));
                    }
                    //判断是否确认收货，0为未确认收货,1为确认收货
                    if (1 == response.getConfirm()) {
                        datas.add(new OrderProgress("已成功提交确认收货！", response.getConfirmDate()));
                        btn_shaidanorshouhuo.setText("去晒单");
                    } else {
                        btn_shaidanorshouhuo.setText("确认收货");
                    }
                } else {
                    //如果地址为空，则显示提醒添加地址
                    if (response == null || StringUtil.isEmpty(response.getRough())) {
                        tv_adressmsg.setText("您还没有添加过地址，点击此处去添加。");
                    } else {
                        tv_adressmsg.setText("配送地址：" + response.getName() + " "
                                + response.getMobile() + " " + response.getRough() + response.getDatail());
                    }
                    btn_shaidanorshouhuo.setText("确认收货地址");
                }

                Collections.reverse(datas);
                adapter.notifyDataSetChanged();
                dialog.dismissDialog();

            }
        });
    }

    private void initView() {

        adapter = new OrderProgressLvAdapter(datas);
        listview.setAdapter(adapter);

        dialog = new MyDialog();
        dialog.showLodingDialog(OrderProgressActivity.this);
        //设置获得的商品信息
        ImageLoader.getInstance().displayImage(buyRecordInfo.getProImgUrl(), img_product);
        tv_productname.setText("(第" + buyRecordInfo.getYunNum() + "云)" + buyRecordInfo.getTitle());
        tv_luckyunnum.setText("幸运云购码：" + buyRecordInfo.getWinCode());
        tv_time.setText("揭晓时间：" + DateUtil.getStringbylong(buyRecordInfo.getJieXiaoTime(), DateUtil.dateFormatYMDHMS));

    }

    /**
     * 确认收货地址或确认收货
     *
     * @param url
     */
    private void confirmOrder(final String url) {
        OkHttpUtils.get().url(Config.IP + url).addParams("id", orderProgressDto.getId())
                .tag("OrderProgressActivity")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络链接出错");
            }

            @Override
            public void onResponse(String response) {
                String result = "";
                try {
                    JSONObject object = new JSONObject(response);
                    result = object.get("messageInfo").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ("确认成功".equals(result)) {
                    if (url.equals("/yiyuan/order_confirmOrder")) {
                        MyToast.showToast_center("订单已提交");
                    } else {
                        MyToast.showToast_center("确认收货成功");
                    }
                    getData();
                } else {
                    MyToast.showToast_center("确认失败");
                }
            }
        });
    }

    @OnClick({R.id.return_orderprogress, R.id.btn_shaidanorshouhuo, R.id.rl_productmsg, R.id.rl_setaddress})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_orderprogress:
                finish();
                break;
            case R.id.btn_shaidanorshouhuo:
                switch (btn_shaidanorshouhuo.getText().toString()) {
                    case "确认收货地址":
                        if (tv_adressmsg.getText().equals("您还没有添加过地址，点击此处去添加。")) {
                            MyToast.showToast_center("您还没选择收货地址");
                            return;
                        }
                        dialog.dialogShow();
                        confirmOrder("/yiyuan/order_confirmOrder");
                        break;
                    case "确认收货":
                        dialog.dialogShow();
                        confirmOrder("/yiyuan/order_confirmPostOrder");
                        break;
                    case "去晒单":
                        Intent intent = new Intent(OrderProgressActivity.this, NewShareOrderActivity.class);
                        intent.putExtra("productimgurl", buyRecordInfo.getProImgUrl());
                        intent.putExtra("productId", buyRecordInfo.getProductId());
                        startActivity(intent);
                        break;
                }
                break;
            case R.id.rl_productmsg:
                //点击商品，跳转至商品详情页面
                Intent intent = new Intent(this, GoodsDetailsActivity.class);
                intent.putExtra("productId", buyRecordInfo.getProductId());
                intent.putExtra("yunNum", buyRecordInfo.getYunNum() + "");
                startActivity(intent);
                break;
            case R.id.rl_setaddress:
                //添加地址
                Intent addressintent = new Intent(this, MyAddressActivity.class);
                addressintent.putExtra("what", 0x111);
                startActivityForResult(addressintent, 100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //从添加地址页面返回时刷新页面
        if (requestCode == 100 && resultCode == RESULT_OK) {
            dialog.dialogShow();
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("OrderProgressActivity");
        super.onDestroy();
    }
}

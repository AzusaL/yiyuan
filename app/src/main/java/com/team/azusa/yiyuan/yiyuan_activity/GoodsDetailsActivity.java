package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.WinnerDto;
import com.team.azusa.yiyuan.bean.YunNumDto;
import com.team.azusa.yiyuan.callback.ProductDetailCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.event.AddCarsEvent;
import com.team.azusa.yiyuan.event.IntParameterEvent;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.widget.AddCarAnimation;
import com.team.azusa.yiyuan.widget.BadgeView;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.RefreshHead;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class GoodsDetailsActivity extends Activity implements CountdownView.OnCountdownEndListener {

    @Bind(R.id.number_yun)
    TextView numberYun;
    @Bind(R.id.goods_details_tv)
    TextView goods_details;
    @Bind(R.id.goods_details_rl)
    RelativeLayout goodsDetailsRl;
    @Bind(R.id.cv_countdownView2)
    CountdownView cvCountdownView2;         //倒计时
    @Bind(R.id.goods_details_scrollview)
    ScrollView goods_details_scrollview;     //商品详情scrollview
    @Bind(R.id.new_ptrpulltorefresh)
    PtrFrameLayout newPtrpulltorefresh;     //下拉刷新
    @Bind(R.id.details)
    TextView details;                       //商品介绍
    @Bind(R.id.Price)
    TextView Price;                       //商品价格
    @Bind(R.id.number_partake)
    TextView number_partake;                //已购买次数
    @Bind(R.id.need_number)
    TextView need_number;                //总次数
    @Bind(R.id.number_remainder)
    TextView number_remainder;                //剩余
    @Bind(R.id.id_progressbar)
    ProgressBar idProgressbar;//进度条
    @Bind(R.id.sriv_avatar)
    SimpleDraweeView srivAvatar;                   //用户头像
    @Bind(R.id.img_fm_allgoods)
    SimpleDraweeView img_fm_allgoods;                   //商品图片
    @Bind(R.id.Countdown)
    LinearLayout Countdown;                 //倒计时LinearLayout
    @Bind(R.id.Calculating)
    LinearLayout Calculating;               //水平缓冲ProgressBar  LinearLayout
    @Bind(R.id.btn_go_shopping)
    Button btnGoShopping;                   //立即一元云购
    @Bind(R.id.btn_add_to_cart)
    Button btnAddToCart;                    //加入购物车
    @Bind(R.id.btn_shopping_Calculation)
    Button btnShoppingCalculation;          //正在进行
    @Bind(R.id.goods_details_layout_TV)     //无数据时文字
            TextView goods_details_layout_TV;
    @Bind(R.id.goods_details_layout)
    LinearLayout goods_details_layout;      //详情LinearLayout
    @Bind(R.id.shaidan_number)
    TextView shaidan_number;                //晒单数
    @Bind(R.id.tv_gainer_nickname)
    TextView tv_gainer_nickname;            //用户名
    @Bind(R.id.tv_gainer_addr)
    TextView tv_gainer_addr;                //用户地址
    @Bind(R.id.tv_shopping_time)
    TextView tv_shopping_time;                //购物时间
    @Bind(R.id.tv_announced_time)
    TextView tv_announced_time;                //揭晓时间
    @Bind(R.id.lucky_number)
    TextView lucky_number;                //得奖云购码
    @Bind(R.id.last_getter)
    LinearLayout last_getter;      //上一获奖者
    @Bind(R.id.layout_cart)
    RelativeLayout layout_cart;      //购物车

    private long time1 = (long) 10 * 1000;  //倒计时时间
    private String strDetails1;             //商品介绍
    private String strRedDetails;           //红色部分商品介绍
    private RefreshHead refreshHead; //自定义下拉刷新头部

    private float buyNum;                        //已购买次数
    private int totalNum;                        //已购买次数

    private long showOrderCount;            //晒单数

    private YunNumDto yunNumDto;
    private String strNumber;
    private String strNumberPartake;                //已购买次数
    private String strNeedNumber;                   //总次数
    private String strNumberRemainder;              //剩余次数
    private String yunNumId = "";                //期数的ID

    private SpannableStringBuilder builder;
    private ForegroundColorSpan redSpan;
    private String strPrice;
    private String gainer_nickname;
    private String gainer_addr;
    private String shopping_time;
    private String announced_time;
    private String strlucky_number;
    private WinnerDto winnerDto;
    private String winnerImgUrl;            //获奖者头像
    private BadgeView badge; //购物车商品个数小圆点
    private int[] car_location; //购物车的位置
    private int car_count;

    private MyDialog myDialog = new MyDialog();
    private AlertDialog loding_dialog;
    private boolean cancelreq = false;  //是否取消网络请求
    private String yunNum;                        //商品期数
    private String productId;
    private int latestyun;
    private boolean obtain_orNot = false;
    private static final int CHANGE_MESSAGE = 1;
    private static final int REFRESH_COMPLETE = 2;
    private Intent OpenYunNumberActivity;
    private Handler handler_lgy = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_MESSAGE:
                    Calculating.setVisibility(View.GONE);//水平缓冲ProgressBar  LinearLayout隐藏
                    break;
                case REFRESH_COMPLETE:
                    newPtrpulltorefresh.refreshComplete();
                    MyToast.showToast("刷新完成");
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
//        Countdown.setVisibility(View.VISIBLE);
        cvCountdownView2.setOnCountdownEndListener(this);
        car_count = ConstanceUtils.carcount;
        initHead();
        iniDate();
        badge = new BadgeView(GoodsDetailsActivity.this);
        badge.setTargetView(layout_cart);
        badge.setBackgroundResource(R.drawable.cart_count_bg);
        badge.setBadgeCount(car_count);
        badge.setBadgeMargin(0, 5, 3, 0);
    }


    private void iniDate() {
        loding_dialog = myDialog.showLodingDialog(this);
        loding_dialog.setOnKeyListener(backlistener);
        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        yunNum = intent.getStringExtra("yunNum");
        yunNumId = intent.getStringExtra("yunNumId");
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getYunNum")
                .addParams("productId", productId)
                .addParams("yunNum", yunNum)
                .tag("GoodsDetailsActivity")
                .build().execute(new ProductDetailCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelreq) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(YunNumDto response) {
                myDialog.dismissDialog();
                yunNumDto = response;
                yunNumId = response.getYunNumId();
                latestyun = response.getCount();
                OpenYunNumberActivity = new Intent(GoodsDetailsActivity.this, YunNumberActivity.class);
                OpenYunNumberActivity.putExtra("yuncount", latestyun);
                OpenYunNumberActivity.putExtra("yunNum", yunNum);
                OpenYunNumberActivity.putExtra("productId", productId);
                switch (response.getStatus()) {
                    case "0":
                        Log.e("状态", "0");
                        Calculating.setVisibility(View.GONE);
                        Countdown.setVisibility(View.GONE);

                        btnGoShopping.setVisibility(View.VISIBLE);
                        btnAddToCart.setVisibility(View.VISIBLE);
                        btnShoppingCalculation.setVisibility(View.GONE);
                        break;
                    case "1":
                        Log.e("状态", "1-------------");
                        Countdown.setVisibility(View.VISIBLE);
                        btnGoShopping.setVisibility(View.GONE);
                        btnAddToCart.setVisibility(View.GONE);
                        btnShoppingCalculation.setVisibility(View.VISIBLE);
                        btnShoppingCalculation.setText("第" + response.getCount() + "云(正在进行中...)");
                        time1 = (long) response.getDaojishi() * 1000;
                        Log.e("lgy+++++++", time1 + "------------------");
                        cvCountdownView2.start(time1);
                        break;
                    case "2":
                        Intent OpenResultActivity = new Intent(GoodsDetailsActivity.this, ResultActivity.class);
                        String data = JsonUtils.getJsonStringformat(response);
                        OpenResultActivity.putExtra("data", data);
                        startActivity(OpenResultActivity);
                        finish();
                        break;
                }

                strNumber = "(第" + yunNumDto.getYunNum() + "云) ";
                strDetails1 = yunNumDto.getTitle();
                strRedDetails = yunNumDto.getContent();
                Log.e("红色部分", strRedDetails);
                builder = new SpannableStringBuilder(strRedDetails);
                redSpan = new ForegroundColorSpan(Color.RED);
                builder.setSpan(redSpan, 0, strRedDetails.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                //        builder.setSpan(redSpan,strNumber.length() + strDetails1  .length() - 1 , strRedDetails.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                details.setText(strNumber + strDetails1);
                details.append(builder);

                strNumberPartake = String.valueOf(yunNumDto.getBuyNum());
                strNeedNumber = String.valueOf(yunNumDto.getTotalNum());
                strNumberRemainder = String.valueOf(yunNumDto.getTotalNum() - yunNumDto.getBuyNum());

                showOrderCount = yunNumDto.getShowOrderCount();

                winnerDto = yunNumDto.getWinner();
                if (winnerDto != null) {
                    gainer_nickname = winnerDto.getWinnerName();
                    gainer_addr = winnerDto.getAddress();
                    shopping_time = DateUtil.getStringByFormat(yunNumDto.getWinner().getYunGouTime(), "yyyy-MM-dd HH:mm:ss:sss");
                    announced_time = DateUtil.getStringByFormat(yunNumDto.getWinner().getJieXiaoTime(), "yyyy-MM-dd HH:mm:ss:sss");
                    strlucky_number = yunNumDto.getWinner().getWinCode();
                    winnerImgUrl = winnerDto.getWinnerImgUrl();
                }
                iniView();
            }
        });

    }

    //eventbus接收购物车页面发来的添加个数
    public void onEventMainThread(AddCarsEvent event) {
        if (0 == event.getCount()) {
            car_count = 0;
        } else {
            car_count = car_count + event.getCount();
        }
        ConstanceUtils.carcount = car_count;
        badge.setBadgeCount(car_count);
    }

    //倒计时结束
    @Override
    public void onEnd(CountdownView cv) {
        MyToast.showToast("时间到");
        Calculating.setVisibility(View.VISIBLE);
        Countdown.setVisibility(View.GONE);
        do {
            try {
                get_result();
                Thread.sleep(30000);
            } catch (Exception E) {
                MyToast.showToast("计算过程出问题");
            }
        } while (obtain_orNot);

    }

    private boolean get_result() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getYunNum")
                .addParams("productId", productId)
                .addParams("yunNum", yunNum)
                .tag("GoodsDetailsActivity")
                .build().execute(new ProductDetailCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelreq) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(YunNumDto response) {

                switch (response.getStatus()) {
                    case "0":
                    case "1":
                        obtain_orNot = false;
                        break;
                    case "2":
                        handler_lgy.sendEmptyMessage(CHANGE_MESSAGE);
                        obtain_orNot = true;
                        Intent OpenResultActivity = new Intent(GoodsDetailsActivity.this, ResultActivity.class);
                        String data = JsonUtils.getJsonStringformat(response);
                        OpenResultActivity.putExtra("data", data);
                        startActivity(OpenResultActivity);
                        finish();
                        break;
                }
            }
        });
        return obtain_orNot;
    }

    /**
     * 界面
     */
    private void iniView() {
        goods_details.setText("商品詳情");
        numberYun.setText("第" + yunNumDto.getYunNum() + "云");
        strPrice = "价值 ￥" + yunNumDto.getValue();
        Price.setText(strPrice);
        buyNum = yunNumDto.getBuyNum();
        totalNum = yunNumDto.getTotalNum();
        Log.e("GoodsDetailsFragment2", "iniView2");
        /**
         *refreshHead  yunNumDto
         */
        idProgressbar.setProgress((int) ((buyNum / totalNum) * 100));//进度条30%   img_lv_allgoods
        ImageLoader.getInstance().displayImage(yunNumDto.getProImgUrl(), img_fm_allgoods);//商品图片
        number_partake.setText(strNumberPartake);
        need_number.setText(strNeedNumber);
        number_remainder.setText(strNumberRemainder);

        shaidan_number.setText(String.valueOf(showOrderCount));

        if (winnerDto == null) {
            last_getter.setVisibility(View.GONE);
        } else {
            tv_gainer_nickname.setText(gainer_nickname);
            tv_gainer_addr.setText(gainer_addr);
            tv_shopping_time.setText("云购时间: " + shopping_time);
            tv_announced_time.setText("揭晓时间: " + announced_time);
            lucky_number.setText(strlucky_number);
            ImageLoader.getInstance().displayImage(winnerImgUrl, srivAvatar);//下载图片显示圆形头像
        }
    }

    private void initHead() {
        refreshHead = new RefreshHead(this, "productDetailsPager");
        newPtrpulltorefresh.setHeaderView(refreshHead);

        newPtrpulltorefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler_lgy.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000); //在这里进行刷新操作
                iniDate();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return goods_details_scrollview.getScrollY() == 0;
            }
        });

        newPtrpulltorefresh.addPtrUIHandler(refreshHead); //添加下拉刷新头部UI变化接口
        goods_details_scrollview.scrollTo(0, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 20) {
            setResult(111);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * 隐藏上下控件
     */
//    private void if_gone() {
//        Calculating.setVisibility(View.GONE);
//        Countdown.setVisibility(View.VISIBLE);
//
//        btnGoShopping.setVisibility(View.VISIBLE);
//        btnAddToCart.setVisibility(View.VISIBLE);
//        btnShoppingCalculation.setVisibility(View.GONE);
//    }

    //获取购物车位置
    public int[] getCar_location() {
        car_location = new int[2];
        layout_cart.getLocationInWindow(car_location);
        return car_location;
    }

    //跳转到MainActivity的时候确定到哪个Fragment
    private void mainActivity_setSelect(int i) {
        IntParameterEvent Event = new IntParameterEvent(i);
        EventBus.getDefault().post(Event);
        finish();//关闭Activity(关闭窗口)
    }

    @OnClick(R.id.tupian_detaile)
    public void onClick() {
        MyToast.showToast("更多图片");
    }

    @OnClick({R.id.btn_go_shopping, R.id.btn_add_to_cart, R.id.layout_cart,
            R.id.photo_text_detail, R.id.Participation_record, R.id.show_order, R.id.sriv_avatar, R.id.last_getter,
            R.id.btn_shopping_Calculation, R.id.go_back, R.id.number_yun
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go_shopping:
                EventBus.getDefault().post(yunNumDto.getProductDto());
                mainActivity_setSelect(3);
                break;
            case R.id.btn_add_to_cart:
                int[] start_location = new int[2];
                img_fm_allgoods.getLocationInWindow(start_location);//获取点击商品图片的位置
                img_fm_allgoods.setDrawingCacheEnabled(false); //清除复制的bitmap
                img_fm_allgoods.setDrawingCacheEnabled(true); //设置为true才能复制bitmap
                Bitmap bitmap = img_fm_allgoods.getDrawingCache(); //复制一个bitmap
                new AddCarAnimation(GoodsDetailsActivity.this).doAnim(bitmap, start_location, getCar_location());
                EventBus.getDefault().post(yunNumDto.getProductDto());
                break;
            case R.id.layout_cart:
                mainActivity_setSelect(3);
                break;
            case R.id.photo_text_detail:
                Intent OpenPhotoTextActivity = new Intent(this, PhotoTextActivity.class);
                OpenPhotoTextActivity.putExtra("imgs", yunNumDto.getContentImgUrl());
                startActivity(OpenPhotoTextActivity);
                break;
            case R.id.Participation_record:
                Intent OpenAllPartakerActivity = new Intent(this, AllPartakerActivity.class);
                OpenAllPartakerActivity.putExtra("yunNumId", yunNumId);
                startActivity(OpenAllPartakerActivity);
                break;
            case R.id.show_order:
                MyToast.showToast("晒单");
                Intent OpenShowOrderActivity = new Intent(this, ShowOrderActivity.class);
                OpenShowOrderActivity.putExtra("productId", productId);
                startActivity(OpenShowOrderActivity);
                break;
            case R.id.sriv_avatar:
                MyToast.showToast("上一云获得者头像");
                break;
            case R.id.last_getter:
                MyToast.showToast("上一云获得者");

                loding_dialog = myDialog.showLodingDialog(GoodsDetailsActivity.this);
                loding_dialog.setOnKeyListener(backlistener);
                OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getYunNum")
                        .addParams("productId", productId)
                        .addParams("yunNum", Integer.parseInt(yunNum) - 1 + "")
                        .build().execute(new ProductDetailCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        MyToast.showToast("网络连接出错");
                    }

                    @Override
                    public void onResponse(YunNumDto response) {
                        Intent OpenResultActivity = new Intent(GoodsDetailsActivity.this, ResultActivity.class);
                        String data = JsonUtils.getJsonStringformat(response);
                        OpenResultActivity.putExtra("data", data);
                        startActivity(OpenResultActivity);
                    }
                });
                myDialog.dismissDialog();
                break;
            case R.id.btn_shopping_Calculation:
                Intent OpenGoodsDetailsActivity = new Intent(this, GoodsDetailsActivity.class);
                OpenGoodsDetailsActivity.putExtra("productId", productId);
                OpenGoodsDetailsActivity.putExtra("yunNum", latestyun + "");
                startActivity(OpenGoodsDetailsActivity);
                break;

            case R.id.go_back:
                finish();
                break;
            case R.id.number_yun:
                startActivity(OpenYunNumberActivity);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("GoodsDetailsActivity");
        super.onDestroy();
    }
}

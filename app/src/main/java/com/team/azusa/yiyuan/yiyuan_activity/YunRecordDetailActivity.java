package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.YunDetailLvAdapter;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.bean.RecordDto;
import com.team.azusa.yiyuan.callback.RecordDtoCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.event.IntParameterEvent;
import com.team.azusa.yiyuan.listener.MyOnClickListener;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Azusa on 2016/2/5.
 */
public class YunRecordDetailActivity extends AppCompatActivity {

    @Bind(R.id.return_yundetail)
    ImageView btn_back;
    @Bind(R.id.yunrecorddetail_lv)
    ListView listview;
    private AlertDialog dialog;

    private BuyRecordInfo buyRecordInfo;
    private View headView; //listview头部
    private SimpleDraweeView img_product; //产品的图片
    private ImageView img_limit; //是否限购图标
    private TextView tv_productname; //产品名
    private TextView tv_playcount; //总共参与次数
    private TextView tv_winnername;//获奖者名字
    private TextView tv_jiexiaoTime; //揭晓时间
    private RelativeLayout Status1; //状态1未开奖
    private ProgressBar productPb; //未开奖时的进度条
    private TextView productJoinedcount; //未开奖时的参加人数
    private TextView productAllcount; //未开奖时的总需人数
    private TextView productRemaincount; //未开奖时的剩余参加人数
    private RelativeLayout Status2; //状态2已开奖
    private Button btn_gotobuy; //第66云正在进行
    private Button btn_buyagain; //继续购买


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_record);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        String data = getIntent().getStringExtra("product_data");
        buyRecordInfo = JsonUtils.getObjectfromString(data, BuyRecordInfo.class);
        initView();
    }

    private void initView() {
        dialog = new MyDialog().showLodingDialog(this);

        initHead();
        //设置产品图片
        ImageLoader.getInstance().displayImage(buyRecordInfo.getProImgUrl(), img_product);
        //设置是否为限购
        if ("1".equals(buyRecordInfo.getXianGou())) {
            img_limit.setVisibility(View.VISIBLE);
        } else {
            img_limit.setVisibility(View.GONE);
        }
        //设置产品标题
        tv_productname.setText(buyRecordInfo.getTitle());

        //状态为2已开奖时的布局,其他为未开奖时的布局
        if (buyRecordInfo.getStatus().equals("2")) {
            Status1.setVisibility(View.VISIBLE);
            Status2.setVisibility(View.GONE);
            //设置获奖者名字
            tv_winnername.setText(buyRecordInfo.getWinnerName());
            //设置揭晓时间
            tv_jiexiaoTime.setText("揭晓时间:" +
                    DateUtil.getStringbylong(buyRecordInfo.getJieXiaoTime(), DateUtil.dateFormatYMDHMS));
        } else {
            Status1.setVisibility(View.GONE);
            Status2.setVisibility(View.VISIBLE);
            //设置购买人数进度条
            float buynum = buyRecordInfo.getBuyNum();
            float totalnum = buyRecordInfo.getValue();
            productPb.setProgress((int) ((buynum / totalnum) * 100));
            //设置已购买人数
            productJoinedcount.setText("" + buyRecordInfo.getBuyNum());
            //设置总需人数
            productAllcount.setText("" + buyRecordInfo.getValue());
            //设置剩余购买人数
            productRemaincount.setText("" + (buyRecordInfo.getValue() - buyRecordInfo.getBuyNum()));
            //人数已满，揭晓中状态
            if (buyRecordInfo.getStatus().equals("1")) {
                btn_buyagain.setText("揭晓中");
                btn_buyagain.setEnabled(false);
            }
        }
        setListener();

        getYunNum();
    }

    private void setListener() {

        //第666。。。云正在进行，跳转至商品详情页面
        btn_gotobuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YunRecordDetailActivity.this, GoodsDetailsActivity.class);
                intent.putExtra("productId", buyRecordInfo.getProductId());
                intent.putExtra("yunNum", buyRecordInfo.getYunNum() + "");
                startActivity(intent);
            }
        });

        //继续购买按钮，将商品添加到购物车，并跳转到购物车页面
        btn_buyagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDto productDto = new ProductDto(buyRecordInfo.getProductId()
                        , buyRecordInfo.getTitle(), buyRecordInfo.getProImgUrl()
                        , buyRecordInfo.getXianGou(), buyRecordInfo.getValue(), buyRecordInfo.getBuyNum()
                        , buyRecordInfo.getValue(), buyRecordInfo.getYunNum());
                EventBus.getDefault().post(productDto); //添加至购物车
                EventBus.getDefault().post(new IntParameterEvent(3)); //跳转至购物车页面
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    //初始化listview的头部
    private void initHead() {
        headView = View.inflate(ConstanceUtils.CONTEXT, R.layout.yunrecorddetail_lv_head, null);
        img_product = (SimpleDraweeView) headView.findViewById(R.id.img_detail);
        img_limit = (ImageView) headView.findViewById(R.id.detail_limibg);
        tv_productname = (TextView) headView.findViewById(R.id.detail_productname);
        tv_playcount = (TextView) headView.findViewById(R.id.detail_playcount);
        tv_winnername = (TextView) headView.findViewById(R.id.tv_winnername);
        tv_jiexiaoTime = (TextView) headView.findViewById(R.id.tv_jiexiao_time);
        Status1 = (RelativeLayout) headView.findViewById(R.id.detail_status1);
        productPb = (ProgressBar) headView.findViewById(R.id.product_pb);
        productJoinedcount = (TextView) headView.findViewById(R.id.product_joinedcount);
        productAllcount = (TextView) headView.findViewById(R.id.product_allcount);
        productRemaincount = (TextView) headView.findViewById(R.id.product_remaincount);
        Status2 = (RelativeLayout) headView.findViewById(R.id.detail_status2);
        btn_gotobuy = (Button) headView.findViewById(R.id.btn_gotobuy);
        btn_buyagain = (Button) headView.findViewById(R.id.btn_buyagain);
        listview.addHeaderView(headView);
    }

    //获取云购码
    private void getYunNum() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_getYunCode")
                .addParams("userId", UserUtils.user.getId())
                .addParams("yunNumId", buyRecordInfo.getYunNumId())
                .addParams("productId", buyRecordInfo.getProductId())
                .tag("YunRecordDetailActivity")
                .build().execute(new RecordDtoCallback() {
            @Override
            public void onError(Request request, Exception e) {
                MyToast.showToast("网络链接出错");
            }

            @Override
            public void onResponse(HashMap<String, Object> map) {
                final ArrayList<RecordDto> datas = (ArrayList<RecordDto>) map.get("record");
                YunDetailLvAdapter adapter = new YunDetailLvAdapter(datas);
                listview.setAdapter(adapter);
                adapter.setOnClickListener(new MyOnClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(YunRecordDetailActivity.this, MoreShoppingNumberActivity.class);
                        intent.putExtra("yuncount", datas.get(position).getYunCode());
                        intent.putExtra("what", 1);
                        intent.putExtra("time", DateUtil.getStringByFormat(datas.get(position).getTime(), DateUtil.dateFormatYMDHMSSSS));
                        startActivity(intent);
                    }
                });

                if (buyRecordInfo.getStatus().equals("2")) {
                    buyRecordInfo.setYunNum(Integer.valueOf(map.get("count").toString()));
                    btn_gotobuy.setText("第" + map.get("count").toString() + "云正在进行...");
                }

                //设置参与次数
                String count = "" + map.get("buyNum");
                SpannableString string = new SpannableString("你已参与 " + count + " 人次");
                string.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7700")),
                        5, count.length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_playcount.setText(string);
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        OkHttpUtils.getInstance().cancelTag("YunRecordDetailActivity");
    }

    @OnClick({R.id.return_yundetail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_yundetail:
                finish();
                break;
        }
    }


}

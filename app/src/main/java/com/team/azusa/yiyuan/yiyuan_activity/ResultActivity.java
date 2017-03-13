package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.RecordDto;
import com.team.azusa.yiyuan.bean.WinnerDto;
import com.team.azusa.yiyuan.bean.YunNumDto;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.WrapHeightGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends Activity {


    @Bind(R.id.WHGV1)
    WrapHeightGridView WHGV1;                   //WrapHeightGridView
    @Bind(R.id.result_avatar)     //获奖者头像touxiang
            SimpleDraweeView result_avatar;
    @Bind(R.id.touxiang)     //商品小图
            SimpleDraweeView touxiang;
    @Bind(R.id.tv_nickname)     //获奖名
            TextView tv_nickname;
    @Bind(R.id.tv_shaidan_title)     //客户地点
            TextView tv_shaidan_title;
    @Bind(R.id.tv_time)     //揭晓时间
            TextView tv_time;
    @Bind(R.id.tv_time2)     //云购时间
            TextView tv_time2;
    @Bind(R.id.lucky_number)     //幸运码
            TextView lucky_number;
    @Bind(R.id.Calculation_details)     //计算详情按钮
            Button Calculation_details;
    @Bind(R.id.more_number)     //更多
            RelativeLayout more_number;
    @Bind(R.id.TextView_number)     //获奖者参与人次
            TextView TextView_number;
    @Bind(R.id.show_order_number)     //商品晒单数
            TextView show_order_number;
    @Bind(R.id.shopping_time)
    TextView shopping_time; //购物时间
    @Bind(R.id.jieguo_number_yun)
    TextView jieguo_number_yun;//云期数
    @Bind(R.id.btn_result_shopping)
    Button btn_result_shopping;//跳转到正在进行按钮

    private YunNumDto yunNumDto;
    private WinnerDto winner;                //获奖者
    private List<RecordDto> recordDto;        //购买记录
    private int joinnumber;
    private String imageUrls;

    private ArrayAdapter sim_adapter;
    private List<String> GV_list = new ArrayList<String>();
    private MyDialog myDialog;
    private AlertDialog loding_dialog;
    private String yunNum;                        //商品期数
    private String productId;
    private String jieguoNumbYun;
    //    private Map<String, Object> mapGV = new HashMap<String, Object>();
    private String data;
    private String strYunNumDto;
    private Intent OMoreShoppingNumberActivity;
    private Intent OpenYunNumberActivity;
    private int latestyun;

    private SpannableStringBuilder builder;
    private ForegroundColorSpan orangeSpan;
    private String blackTextView;
    private String[] strYunCode = new String[]{};
    private String[] strArrays = new String[]{};
    private int showYunCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        myDialog = new MyDialog();
        loding_dialog = myDialog.showLodingDialog(this);
        loding_dialog.setOnKeyListener(backlistener);
        iniDate();
        iniView();
    }

    private void iniDate() {

        Intent intent = getIntent();
        strYunNumDto = intent.getStringExtra("data");
        yunNumDto = JsonUtils.getObjectfromString(strYunNumDto, YunNumDto.class);

        winner = yunNumDto.getWinner();
        recordDto = winner.getRecordDto();
        latestyun = yunNumDto.getCount();
        productId = yunNumDto.getProductId();
        data = JsonUtils.getJsonStringformat(winner.getRecordDto());
        OMoreShoppingNumberActivity = new Intent(ResultActivity.this, MoreShoppingNumberActivity.class);
        OMoreShoppingNumberActivity.putExtra("yuncount", data);
        OMoreShoppingNumberActivity.putExtra("time", DateUtil.getStringByFormat(winner.getYunGouTime(), DateUtil.dateFormatYMDHMSSSS));

        OpenYunNumberActivity = new Intent(ResultActivity.this, YunNumberActivity.class);
        OpenYunNumberActivity.putExtra("yuncount", latestyun);
        OpenYunNumberActivity.putExtra("yunNum", yunNumDto.getYunNum() + "");
        OpenYunNumberActivity.putExtra("productId", yunNumDto.getProductId());
        getGV_list();
        imageUrls = winner.getWinnerImgUrl();
        jieguoNumbYun = yunNumDto.getYunNum() + "";
    }

    //得到GV_list数据
    private void getGV_list() {
        if (recordDto != null) {

            for (RecordDto r : recordDto) {
                strArrays = r.getYunCode().split(",");
                strYunCode = getMergeArray(strYunCode, strArrays);
            }
            joinnumber = strYunCode.length;
            if (joinnumber < 12)
                showYunCode = joinnumber;
            else
                showYunCode = 12;
            for (int i = 0; i < showYunCode; i++) {
                GV_list.add(strYunCode[i]);
            }
        }
    }

    //将两个字符数组合并
    public String[] getMergeArray(String[] al, String[] bl) {
        String[] a = al;
        String[] b = bl;
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private void iniView() {
        jieguo_number_yun.setText("第" + jieguoNumbYun + "云");
        blackTextView = "获得者本云总共参与";
        builder = new SpannableStringBuilder(joinnumber + "人次");
        orangeSpan = new ForegroundColorSpan(Color.parseColor("#FF7F24"));
        builder.setSpan(orangeSpan, 0, String.valueOf(joinnumber).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //        builder.setSpan(redSpan,strNumber.length() + strDetails1  .length() - 1 , strRedDetails.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        TextView_number.setText(blackTextView);
        TextView_number.append(builder);

        if (joinnumber <= 12)
            more_number.setVisibility(View.GONE);

        show_gridview();
        tv_nickname.setText(winner.getWinnerName());
        tv_shaidan_title.setText(winner.getAddress());
        tv_time.setText(DateUtil.getStringByFormat(winner.getJieXiaoTime(), "yyyy-MM-dd HH:mm:ss:sss"));
        tv_time2.setText(DateUtil.getStringByFormat(winner.getYunGouTime(), "yyyy-MM-dd HH:mm:ss:sss"));

        lucky_number.setText(winner.getWinCode());
        show_order_number.setText("(" + yunNumDto.getShowOrderCount() + ")");
        shopping_time.setText(DateUtil.getStringByFormat(winner.getYunGouTime(), "yyyy-MM-dd HH:mm:ss:sss"));
        btn_result_shopping.setText("第" + latestyun + "云(正在进行中...)");

        ImageLoader.getInstance().displayImage(imageUrls, result_avatar);
        ImageLoader.getInstance().displayImage(yunNumDto.getProImgUrl(), touxiang);
        loding_dialog.dismiss();
    }

    private void show_gridview() {
        // TODO 自动生成的方法存根
        sim_adapter = new ArrayAdapter<String>(this, R.layout.item_yun_gv, GV_list);
        WHGV1.setAdapter(sim_adapter);
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

    @OnClick({R.id.more_number, R.id.result_go_back, R.id.btn_result_shopping,
            R.id.result_layout_cart2, R.id.Calculation_details, R.id.Calculation_detail_rl,
            R.id.result_Participation_record, R.id.result_show_order, R.id.result_avatar, R.id.jieguo_number_yun})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.result_go_back:
                this.finish();
                break;
            case R.id.btn_result_shopping:
                Intent OpenGoodsDetailsActivity = new Intent(this, GoodsDetailsActivity.class);
                OpenGoodsDetailsActivity.putExtra("productId", productId);
                OpenGoodsDetailsActivity.putExtra("yunNum", latestyun + "");
                startActivity(OpenGoodsDetailsActivity);
                break;
            case R.id.result_layout_cart2:
                Intent intent = new Intent();
                intent.putExtra("result", 3);
                setResult(20, intent);//设置返回数据，20可以随便改，用于确定是何处调用。
                finish();
                break;
            case R.id.Calculation_details:
            case R.id.Calculation_detail_rl:
                Intent intent1 = new Intent(this, CalculateDetailsActivity.class);
                intent1.putExtra("time", winner.getJieXiaoTime());
                intent1.putExtra("count", yunNumDto.getValue());
                intent1.putExtra("yunNumId", yunNumDto.getYunNumId());
                startActivity(intent1);
                break;
            case R.id.result_Participation_record:
                Toast.makeText(this, "所有参与记录", Toast.LENGTH_SHORT).show();
                Intent OpenAllPartakerActivity = new Intent(this, AllPartakerActivity.class);
                OpenAllPartakerActivity.putExtra("yunNumId", yunNumDto.getYunNumId());
                startActivity(OpenAllPartakerActivity);
                break;
            case R.id.result_show_order:
                Toast.makeText(this, "晒单", Toast.LENGTH_SHORT).show();
                Intent OpenShowOrderActivity = new Intent(this, ShowOrderActivity.class);
                OpenShowOrderActivity.putExtra("productId", productId);
                startActivity(OpenShowOrderActivity);
                break;
            case R.id.more_number:
//                    OMoreShoppingNumberActivity = new Intent(this, MoreShoppingNumberActivity.class);

                startActivity(OMoreShoppingNumberActivity);
                break;
            case R.id.result_avatar:
                Toast.makeText(this, "获奖头像", Toast.LENGTH_SHORT).show();
                break;

            case R.id.jieguo_number_yun:

                startActivity(OpenYunNumberActivity);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

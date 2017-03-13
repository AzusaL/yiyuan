package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.RecordDto;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.widget.MyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreShoppingNumberActivity extends Activity {


    @Bind(R.id.more_number_GV)
    GridView moreNumberGV;
    @Bind(R.id.get_yun_number)
    TextView get_yun_number;
    @Bind(R.id.number_time)
    TextView numberTime;
    private ArrayAdapter sim_adapter;
    private List<String> GV_list;
    private String strGVdata;
    private List<RecordDto> recordDto;        //购买记录
    private Map<String, Object> mapGV = new HashMap<String, Object>();
    private int yuncount = 0;
    private SpannableStringBuilder builder;
    private ForegroundColorSpan orangeSpan;
    private String blackTextView;
    private MyDialog myDialog = new MyDialog();
    private AlertDialog loding_dialog;
    private String time; //揭晓时间

    private static final int GET_READY = 1;
    private Handler handler_result = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_READY:
                    numberTime.setText(time);
                    get_yun_number.setText(blackTextView);
                    get_yun_number.append(builder);
                    show_gridview();
                    myDialog.dismissDialog();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_shopping_number);
        ButterKnife.bind(this);
        loding_dialog = myDialog.showLodingDialog(this);
        loding_dialog.setOnKeyListener(backlistener);
//        iniDate();
        mythread();
    }

    private void show_gridview() {
        // TODO 自动生成的方法存根

//        get_data_gridview(strArrays);
//        sim_adapter = new ArrayAdapter<String>(this, R.layout.item_yun_gv,GV_list);
        sim_adapter = new ArrayAdapter(this, R.layout.item_yun_gv, GV_list);
        moreNumberGV.setAdapter(sim_adapter);
    }

    private void mythread() {
        Thread timer1 = new Thread() {
            public void run() {
                try {
                    GV_list = new ArrayList<String>();
                    Intent intent = getIntent();
                    strGVdata = intent.getStringExtra("yuncount");
                    time = intent.getStringExtra("time");
                    int what = intent.getIntExtra("what", -1);
                    if (what == 1) {
                        GV_list = StringUtil.getList(strGVdata);
                        yuncount = GV_list.size();
                        blackTextView = "获得者本次总共参与";
                    } else {
                        recordDto = JsonUtils.getRecordDtoFromString(strGVdata);

                        for (RecordDto r : recordDto) {
                            String[] strArrays = r.getYunCode().split(",");
                            yuncount = yuncount + strArrays.length;
                            for (String s : strArrays) {
                                GV_list.add(s);
                            }
                        }

                        blackTextView = "获得者本云总共参与";
                    }

                    builder = new SpannableStringBuilder(yuncount + "人次");
                    orangeSpan = new ForegroundColorSpan(Color.parseColor("#FF7F24"));
                    builder.setSpan(orangeSpan, 0, String.valueOf(yuncount).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    handler_result.sendEmptyMessage(GET_READY);
                }
            }
        };
        timer1.start();
    }

//    private void iniDate() {
//        GV_list = new ArrayList<String>();
//        Intent intent = getIntent();
//        strGVdata = intent.getStringExtra("yuncount");
//        Log.e("DATA",strGVdata);
//
//        recordDto = JsonUtils.getRecordDtoFromString(strGVdata);
//
//        for(RecordDto r: recordDto){
//            String [] strArrays = r.getYunCode() .split(",");
//            Log.e("length", strArrays.length +"");
//            yuncount = yuncount + strArrays.length;
//            for (String s :strArrays){
//                GV_list.add(s);
//            }
//        }
//        blackTextView = "获得者本云总共参与";
//        builder = new SpannableStringBuilder(yuncount + "人次");
//        orangeSpan = new ForegroundColorSpan(Color.parseColor("#FF7F24"));
//        builder.setSpan(orangeSpan, 0, String.valueOf(yuncount).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        get_yun_number.setText(blackTextView);
//        get_yun_number.append(builder);
//        show_gridview();
//        MyDialog.getInstance().dismissDialog();
//    }

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

    @OnClick(R.id.shopping_num_go_back)
    public void onClick() {
        this.finish();
    }
}

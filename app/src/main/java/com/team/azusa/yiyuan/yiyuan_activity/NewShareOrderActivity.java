package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.NewShaidanGvAdapter;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.event.AddPhotoEvent;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.FilesUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.HorizontalProgressBarWithNumber;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.WrapHeightGridView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Azusa on 2016/3/19.
 */
public class NewShareOrderActivity extends AppCompatActivity {

    @Bind(R.id.return_newshaidan)
    ImageView btn_back;
    @Bind(R.id.send_shaidan)
    Button btn_send;
    @Bind(R.id.tv_textlimi)
    TextView tvTextlimi;
    @Bind(R.id.edit_shaidantitle)
    EditText edit_title;
    @Bind(R.id.img_shaidan)
    SimpleDraweeView img_product;
    @Bind(R.id.edit_shaidancontent)
    EditText edit_content;
    @Bind(R.id.shaidan_gridview)
    WrapHeightGridView gridview;

    private NewShaidanGvAdapter adapter; //晒单图片grideview
    private ArrayList<String> imgdatas = new ArrayList<>(); //显示晒单图片的url集合
    private ArrayList<String> upload_imgdatas = new ArrayList<>(); //上传图片的url集合
    private String productimgurl; //产品图片url
    private String productId; //产品Id

    private View dialog_view;  //对话框布局
    private AlertDialog dialog; //上传对话框
    private HorizontalProgressBarWithNumber pb_upload; //上传进度条
    private TextView tv_staus; //上传进度textview，处理图片时显示处理中，上传是显示上传中
    private Button btn_cancel; //取消上传按钮

    private boolean cancelrequest = false; //是否取消网络请求

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ConstanceUtils.MESSAGE_OK) {
                tv_staus.setText("上传中...");
                sendShareOrder(); //子线程处理好图片后，上传图片
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_share_order);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initDate();
        initView();
        setListener();

    }

    private void setListener() {
        //监听内容输入框，最多不超过200字
        edit_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = edit_content.getText().toString();
                int text_count = content.length();
                if (text_count > 200) {
                    tvTextlimi.setVisibility(View.VISIBLE);
                    tvTextlimi.setText("" + (200 - text_count));
                    btn_send.setClickable(false);
                } else {
                    tvTextlimi.setVisibility(View.GONE);
                    btn_send.setClickable(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //取消发送按钮监听
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消此次发送请求
                dialog.dismiss();
                cancelrequest = true;
                OkHttpUtils.getInstance().cancelTag("NewShareOrderActivity");
            }
        });
    }

    private void initDate() {
        productimgurl = getIntent().getStringExtra("productimgurl");
        productId = getIntent().getStringExtra("productId");
        //设置商品图片
        ImageLoader.getInstance().displayImage(productimgurl, img_product);
    }

    private void initView() {
        imgdatas.add(""); //第一个位置放添加图片的图标
        adapter = new NewShaidanGvAdapter(imgdatas);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position) {
                    Intent intent = new Intent(NewShareOrderActivity.this, PhotoSelectActivity.class);
                    intent.putExtra("pic_count", 9); //最多可添加9张
                    intent.putExtra("isneedcutimg", false);  //不需要裁切图片
                    imgdatas.remove(0);  //去掉第一个无用url，将剩下的URL传给选择图片界面显示已经选择的图片
                    intent.putStringArrayListExtra("selectimglist", imgdatas);
                    startActivity(intent);
                    imgdatas.add(0, "");  //重新添加回添加图片的按钮
                }
            }
        });

        //初始化发送对话框布局
        dialog_view = View.inflate(ConstanceUtils.CONTEXT, R.layout.dialog_upload_img, null);
        pb_upload = (HorizontalProgressBarWithNumber) dialog_view.findViewById(R.id.pb_upload);
        btn_cancel = (Button) dialog_view.findViewById(R.id.btn_cancel);
        tv_staus = (TextView) dialog_view.findViewById(R.id.tv11);
    }

    //发表晒单
    private void sendShareOrder() {
        PostFormBuilder builder = OkHttpUtils.post()
                .url(Config.IP + "/yiyuan/user_postShowOrder")
                .addParams("userId", UserUtils.user.getId())
                .addParams("productId", productId)
                .addParams("title", edit_title.getText().toString())
                .addParams("content", edit_content.getText().toString())
                .tag("NewShareOrderActivity");

        for (int i = 0; i < upload_imgdatas.size(); i++) {
            builder.addFile("files", "img.jpg", new File(upload_imgdatas.get(i).replace("file://", "")));
            Log.e("main",upload_imgdatas.get(i));
        }
        //对此次请求设置连接时间，需在build之后设置
        builder.build().connTimeOut(300000000).readTimeOut(300000000).writeTimeOut(300000000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (cancelrequest) {
                            return;
                        }
                        MyToast.showToast("网络连接出错");
                    }

                    @Override
                    public void inProgress(float progress) {
                        //设置上传进度pb
                        pb_upload.setProgress((int) (progress * 100));
                        Log.e("main",progress+"");
                        super.inProgress(progress);
                    }

                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        finish();
                        MyToast.showToast_center("发表成功！");
                    }
                });
    }


    //eventbus接收添加图片界面发来的消息
    public void onEventMainThread(AddPhotoEvent event) {
        imgdatas.clear();
        imgdatas.add("");
        imgdatas.addAll(event.getImgurl());
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.return_newshaidan, R.id.send_shaidan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_newshaidan:
                finish();
                break;
            case R.id.send_shaidan:
                //如果输入都符合标准，先对图片进行压缩处理，压缩好后在上传
                if (StringUtil.isEmpty(edit_title.getText().toString()) || StringUtil.isEmpty(edit_content.getText().toString())) {
                    MyToast.showToast_center("标题和内容不能为空！");
                } else {
                    if (edit_title.getText().toString().length() > 25) {
                        MyToast.showToast_center("标题不能超过25字！");
                    } else {
                        dialog = new MyDialog().showDialog(dialog_view, NewShareOrderActivity.this);
                        tv_staus.setText("处理中...");
                        //对图片进行处理（压缩，生成临时图片文件），比较耗时，放在子线程执行
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                upload_imgdatas.addAll(imgdatas);
                                upload_imgdatas.remove(0);  //去掉第一个放置添加按钮的无效url
                                upload_imgdatas = FilesUtil.scaleFile(upload_imgdatas);
                                handler.sendEmptyMessage(ConstanceUtils.MESSAGE_OK);
                            }
                        }).start();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

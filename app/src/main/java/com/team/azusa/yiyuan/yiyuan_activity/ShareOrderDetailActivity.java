package com.team.azusa.yiyuan.yiyuan_activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.CommentAdapter;
import com.team.azusa.yiyuan.adapter.ShareDetailGvAdapter;
import com.team.azusa.yiyuan.bean.ShowOrderCommentDto;
import com.team.azusa.yiyuan.bean.ShowOrderDto;
import com.team.azusa.yiyuan.callback.ShareCommentCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.PulluptoRefreshListview;
import com.team.azusa.yiyuan.widget.WrapHeightGridView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareOrderDetailActivity extends AppCompatActivity {

    @Bind(R.id.return_shaidandetail)
    ImageView returnShaidandetail;
    @Bind(R.id.lv_sharedetail)
    PulluptoRefreshListview listview;
    @Bind(R.id.tv_like)
    TextView tvLike;
    @Bind(R.id.tv_like_anim)
    TextView tvLikeAnim;
    @Bind(R.id.rl_like)
    RelativeLayout rlLike;
    @Bind(R.id.tv_comment)
    TextView tvComment;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;
    @Bind(R.id.rl_share)
    RelativeLayout rlShare;
    @Bind(R.id.btn_sentcomment)
    Button btnSentcomment;
    @Bind(R.id.edit_commentcontent)
    EditText editCommentcontent;
    @Bind(R.id.edit_comment_rl)
    RelativeLayout editCommentRl;
    @Bind(R.id.tv_textlimi)
    TextView tvTextlimi;

    private View headView; //晒单详情的布局，listview头部
    private SimpleDraweeView head_img; //listview头部内的用户头像
    private TextView tv_name; //listview头部内的用户名字
    private TextView tv_productname; //listview头部内用户获得商品的商品名
    private TextView tv_commentcount; //listview头部内评论次数
    private TextView tv_sharetitle; //晒单标题
    private TextView tv_sharetime; //晒单时间
    private TextView tv_sharecomtent; //晒单内容
    private WrapHeightGridView gridView; //headview里面显示晒单照片的gridview
    private ShowOrderDto showOrderDto; //晒单数据
    private ArrayList<ShowOrderCommentDto> commentdatas = new ArrayList<>(); //晒单评论的集合
    private CommentAdapter adapter; //评论lv适配器
    private AlertDialog dialog;
    private boolean cancelrequest = false; //是否取消网络请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_order_detail);
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    private void setListener() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == commentdatas.size() + 1) {
                    return;
                }
                Intent intent = new Intent(ShareOrderDetailActivity.this, UsermsgActivity.class);
                intent.putExtra("user_name", commentdatas.get(position - 1).getCommentUserName());
                intent.putExtra("user_id", commentdatas.get(position - 1).getCommentUserID());
                intent.putExtra("userhead_img", commentdatas.get(position - 1).getCommentUserImgUrl());
                startActivity(intent);
            }
        });
    }

    private void initData() {
        dialog = new MyDialog().showLodingDialog(ShareOrderDetailActivity.this);
        String data = getIntent().getStringExtra("showOrderDto");
        showOrderDto = JsonUtils.getObjectfromString(data, ShowOrderDto.class);
        getCommentData(0, 0);
    }

    /**
     * 获取评论数据
     *
     * @param firstresult 第一条要加载的位置
     */
    private void getCommentData(final int what, int firstresult) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getComment")
                .addParams("showOrderId", showOrderDto.getShowOrderId())
                .addParams("firstResult", firstresult + "").tag("ShareOrderDetailActivity")
                .build().execute(new ShareCommentCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(ArrayList<ShowOrderCommentDto> response) {
                if (null == response || 0 == response.size()) {
                    if (0 != what) {
                        listview.setLoadComplete(true);
                        return;
                    }
                }
                //评论后刷新数据
                if (2 == what) {
                    commentdatas.clear();
                    commentdatas.addAll(response);
                    adapter.notifyDataSetChanged();
                    return;
                }
                commentdatas.addAll(response);
                if (0 == what) {
                    dialog.dismiss();
                    initView();
                } else {
                    listview.setLoading(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 点赞或者发表评论
     *
     * @param zan     点赞
     * @param content 评论内容
     */
    private void toCommentOrZan(final int zan, String content) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_toComment")
                .addParams("showOrderId", showOrderDto.getShowOrderId())
                .addParams("zan", zan + "").addParams("content", content)
                .tag("ShareOrderDetailActivity")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onResponse(String response) {
                String result = "";
                try {
                    JSONObject object = new JSONObject(response);
                    result = object.get("message").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ("success".equals(result)) {
                    if (1 == zan) {
                        MyToast.showToast_center("点赞成功");
                        tvLike.setText((showOrderDto.getZan() + 1) + "");
                        rlLike.setClickable(false);
                    } else {
                        getCommentData(2, 0);
                        MyToast.showToast_center("评论成功");
                        editCommentcontent.setText("");
                        editCommentRl.setVisibility(View.GONE);
                        //设置评论次数
                        tv_commentcount.setText("评论" + showOrderDto.getCommentCount() + 1);
                        tvComment.setText(showOrderDto.getCommentCount() + 1 + "");
                    }
                }
            }
        });
    }

    private void setheadView() {
        headView = View.inflate(ConstanceUtils.CONTEXT, R.layout.share_order_lv_head, null);
        head_img = (SimpleDraweeView) headView.findViewById(R.id.share_img_head);
        tv_name = (TextView) headView.findViewById(R.id.tv_shareusername);
        tv_productname = (TextView) headView.findViewById(R.id.tv_shareproductname);
        tv_commentcount = (TextView) headView.findViewById(R.id.tv_commentcount);
        tv_sharetitle = (TextView) headView.findViewById(R.id.tv_sharetitle);
        tv_sharetime = (TextView) headView.findViewById(R.id.tv_sharetime);
        tv_sharecomtent = (TextView) headView.findViewById(R.id.tv_sharecontent);
        //设置用户头像
        ImageLoader.getInstance().displayImage(showOrderDto.getUserImgUrl(), head_img);
        head_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUserMessagePage();
            }
        });
        //设置用户名
        tv_name.setText(showOrderDto.getUserName());
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUserMessagePage();
            }
        });
        //设置产品名字
        tv_productname.setText(showOrderDto.getProductName());
        //设置晒单标题
        tv_sharetitle.setText(showOrderDto.getTitle());
        //设置晒单时间
        tv_sharetime.setText(DateUtil.getStringbyDate(showOrderDto.getTime(), DateUtil.dateFormatYMDHMS));
        //设置晒单内容
        tv_sharecomtent.setText(showOrderDto.getContent());
        //设置评论次数
        tv_commentcount.setText("评论" + showOrderDto.getCommentCount());
        //设置晒单图片gridview
        gridView = (WrapHeightGridView) headView.findViewById(R.id.gv_shareimg);
        final ArrayList<String> data = StringUtil.getList(showOrderDto.getImgUrl());
        gridView.setAdapter(new ShareDetailGvAdapter(data));
        //图片点击监听
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent photo = new Intent(ShareOrderDetailActivity.this, PhotoViewActivity.class);
                photo.putExtra("photo_count", data.size());
                photo.putExtra("photo_position", position);
                photo.putStringArrayListExtra("photourl", data);
                startActivity(photo);
            }
        });
        listview.addHeaderView(headView);
        listview.setHeaderDividersEnabled(false);//去掉头部分割线
    }

    //跳转到用户信息界面
    private void gotoUserMessagePage() {
        Intent intent = new Intent(ShareOrderDetailActivity.this, UsermsgActivity.class);
        intent.putExtra("user_name", showOrderDto.getUserName());
        intent.putExtra("user_id", showOrderDto.getUserId());
        intent.putExtra("userhead_img", showOrderDto.getUserImgUrl());
        startActivity(intent);
    }

    private void initView() {
        setheadView();
        adapter = new CommentAdapter(commentdatas);
        listview.setAdapter(adapter);
        listview.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                getCommentData(1, commentdatas.size());
            }
        });
        //设置底部toolbar布局
        tvLike.setText(showOrderDto.getZan() + "");
        tvComment.setText(showOrderDto.getCommentCount() + "");
        //监听评论输入的字数，不超过200字
        editCommentcontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = editCommentcontent.getText().toString();
                int text_count = content.length();
                if (text_count > 200) {
                    tvTextlimi.setVisibility(View.VISIBLE);
                    tvTextlimi.setText("" + (200 - text_count));
                    btnSentcomment.setClickable(false);
                } else {
                    tvTextlimi.setVisibility(View.GONE);
                    btnSentcomment.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("ShareOrderDetailActivity");
        super.onDestroy();
    }

    @OnClick({R.id.return_shaidandetail, R.id.rl_like, R.id.rl_comment, R.id.rl_share, R.id.btn_sentcomment, R.id.edit_comment_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_shaidandetail:
                finish();
                break;
            case R.id.rl_like:
                tvLikeAnim.setVisibility(View.VISIBLE);
                new ObjectAnimator().ofFloat(tvLikeAnim, "translationY", 0f, -100f)
                        .setDuration(500).start();
                toCommentOrZan(1, "");
                break;
            case R.id.rl_comment:
                editCommentRl.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_share:
                MyToast.showToast("share");
                break;
            case R.id.btn_sentcomment:
                toCommentOrZan(0, editCommentcontent.getText().toString());
                break;
            case R.id.edit_comment_rl:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //点击返回键后，如果发送评论布局可见，先隐藏起来，不直接退出activity
            if (editCommentRl.getVisibility() == View.VISIBLE) {
                editCommentRl.setVisibility(View.GONE);
                return false;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
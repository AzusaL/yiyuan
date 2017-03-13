package com.team.azusa.yiyuan.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ShowOrderDto;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.MyOnClickListener;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/1/15.
 */
public class ShowOrderLvAdapter extends BaseAdapter {
    private ArrayList<ShowOrderDto> datas = null;
    private ShowOrderDto showOrderDto;
    private int resource;// 绑定的条目界面
    private LayoutInflater inflater;
    private String shaidan_time;
    private String imgUrl;                //图片，由多个图片的URL通过","连接起来
    private String[] strUrlArrays = new String[]{};
    private MyOnClickListener onClickListener;

    public void setOnClickListener(MyOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ShowOrderLvAdapter(Context context, ArrayList<ShowOrderDto> datas, int resource) {
        this.datas = datas;
        this.resource = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(resource, parent, false);// 生成条目界面对象
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        showOrderDto = datas.get(position);
        imgUrl = showOrderDto.getImgUrl();
        strUrlArrays = imgUrl.split(",");
        shaidan_time = DateUtil.getStringbyDate(showOrderDto.getTime(), "yyyy-MM-dd HH:mm:ss:sss");
        ImageLoader.getInstance().displayImage(showOrderDto.getUserImgUrl(), holder.show_order_touxiang);
        holder.tv_nickname.setText(showOrderDto.getUserName());
        holder.tv_shaidan_time.setText(shaidan_time);
        holder.tv_shaidan_title.setText(showOrderDto.getTitle());
        holder.tv_shaidan_summary.setText(datas.get(position).getContent());
        ImageLoader.getInstance().displayImage(strUrlArrays[0], holder.siv_shaidan_summary1);
        ImageLoader.getInstance().displayImage(strUrlArrays[1], holder.siv_shaidan_summary2);
        ImageLoader.getInstance().displayImage(strUrlArrays[2], holder.siv_shaidan_summary3);

        holder.tv_envy.setText(showOrderDto.getZan() + "");
        holder.tv_comment.setText(showOrderDto.getCommentCount() + "");

        //设置点赞布局点击监听
        holder.rl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tv_envy_anim.setVisibility(View.VISIBLE);
                new ObjectAnimator().ofFloat(holder.tv_envy_anim, "translationY", 0f, -100f)
                        .setDuration(500).start();
                toZan(position, holder.tv_envy, holder.rl_like);
            }
        });

        //设置评论布局点击
        holder.rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(position);
                }
            }
        });

        //设置分享布局点击监听
        holder.rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        return convertView;
    }

    /**
     * 点赞
     *
     * @param position item的position
     * @param tvLike   点赞次数tv
     * @param rlLike   点赞的父布局
     */
    private void toZan(final int position, final TextView tvLike, final RelativeLayout rlLike) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_toComment")
                .addParams("showOrderId", datas.get(position).getShowOrderId())
                .addParams("zan", "1")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
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
                    MyToast.showToast_center("点赞成功");
                    datas.get(position).setZan(datas.get(position).getZan() + 1);
                    tvLike.setText((datas.get(position).getZan()) + "");
                    rlLike.setEnabled(false);
                } else {
                    MyToast.showToast_center("点赞失败，请重试");
                }
            }
        });
    }

    static class ViewHolder {
        @Bind(R.id.show_order_touxiang)
        SimpleDraweeView show_order_touxiang;
        @Bind(R.id.siv_shaidan_summary1)
        SimpleDraweeView siv_shaidan_summary1;
        @Bind(R.id.siv_shaidan_summary2)
        SimpleDraweeView siv_shaidan_summary2;
        @Bind(R.id.siv_shaidan_summary3)
        SimpleDraweeView siv_shaidan_summary3;
        @Bind(R.id.tv_shaidan_summary)
        TextView tv_shaidan_summary;
        @Bind(R.id.tv_nickname)
        TextView tv_nickname;
        @Bind(R.id.tv_shaidan_time)
        TextView tv_shaidan_time;
        @Bind(R.id.tv_shaidan_title)
        TextView tv_shaidan_title;
        @Bind(R.id.tv_envy)
        TextView tv_envy;
        @Bind(R.id.tv_envy_anim)
        TextView tv_envy_anim;
        @Bind(R.id.tv_comment)
        TextView tv_comment;
        @Bind(R.id.rl_envy)
        RelativeLayout rl_like;
        @Bind(R.id.rl_comment)
        RelativeLayout rl_comment;
        @Bind(R.id.rl_share)
        RelativeLayout rl_share;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

package com.team.azusa.yiyuan.adapter;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ShowOrderDto;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Azusa on 2015/12/27.
 */
public class ShareOrderRviewAdapter extends RecyclerView.Adapter<ShareOrderRviewAdapter.ViewHolder> {
    public ArrayList<ShowOrderDto> datas = null;
    private RecyclerViewItemClickLitener recyclerViewItemClickLitener;

    public void setOnItemClickLitener(RecyclerViewItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public ShareOrderRviewAdapter(ArrayList<ShowOrderDto> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.usershare_orderrv_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //设置晒单标题
        holder.tv_title.setText(datas.get(position).getTitle());
        //设置晒单时间
        holder.tv_time.setText(DateUtil.getStringbyDate(datas.get(position).getTime(), "yyyy-MM-dd HH:mm:ss"));
        //设置晒单内容
        holder.tv_content.setText(datas.get(position).getContent());

        //设置晒单图片
        ArrayList<String> img_list = StringUtil.getList(datas.get(position).getImgUrl());
        if (img_list.size() == 0) {
            holder.img_ll.setVisibility(View.GONE);
        } else {
            holder.img_ll.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < img_list.size(); i++) {
            ImageLoader.getInstance().displayImage(img_list.get(i), holder.img.get(i));
            if (i == 2)
                break;
        }

        //设置点赞次数
        if (0 == datas.get(position).getZan()) {
            holder.tv_likecount.setText("");
        } else {
            holder.tv_likecount.setText("" + datas.get(position).getZan());
        }

        //设置评论次数
        if (0 == datas.get(position).getCommentCount()) {
            holder.tv_commentcount.setText("");
        } else {
            holder.tv_commentcount.setText("" + datas.get(position).getCommentCount());
        }

        if (recyclerViewItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewItemClickLitener.onItemClick(v, position);
                }
            });
        }

        //设置点赞布局点击监听
        holder.rl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tv_likeAnim.setVisibility(View.VISIBLE);
                new ObjectAnimator().ofFloat(holder.tv_likeAnim, "translationY", 0f, -100f)
                        .setDuration(500).start();
                toZan(position, holder.tv_likecount, holder.rl_like);
            }
        });

        //设置评论布局点击
        holder.rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemClickLitener.onItemClick(v, position);
            }
        });

        //设置分享布局点击监听
        holder.rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

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

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title; //标题
        public TextView tv_time; //时间
        public TextView tv_content; //晒单内容
        public SimpleDraweeView img_shaitu1; //晒图
        public SimpleDraweeView img_shaitu2;
        public SimpleDraweeView img_shaitu3;
        public ArrayList<SimpleDraweeView> img;
        public LinearLayout img_ll;
        public TextView tv_likecount; //晒单被点赞次数
        public TextView tv_likeAnim; //点赞后要显示的动画tv
        public TextView tv_commentcount; //晒单被评论次数
        public ImageView img_sharebtn; //分享按钮
        public RelativeLayout rl_like;  //点赞布局
        public RelativeLayout rl_comment;  //评论布局
        public RelativeLayout rl_share;  //分享布局

        public ViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_time = (TextView) view.findViewById(R.id.tv_shaidan_time);
            tv_content = (TextView) view.findViewById(R.id.tv_shaidan_content);
            tv_likecount = (TextView) view.findViewById(R.id.tv_envy);
            tv_likeAnim = (TextView) view.findViewById(R.id.tv_envy_anim);
            tv_commentcount = (TextView) view.findViewById(R.id.tv_comment);
            img_sharebtn = (ImageView) view.findViewById(R.id.iv_share);

            img_ll = (LinearLayout) view.findViewById(R.id.img_ll);
            img_shaitu1 = (SimpleDraweeView) view.findViewById(R.id.siv_shaidan_img1);
            img_shaitu2 = (SimpleDraweeView) view.findViewById(R.id.siv_shaidan_img2);
            img_shaitu3 = (SimpleDraweeView) view.findViewById(R.id.siv_shaidan_img3);
            img = new ArrayList<>();
            img.add(img_shaitu1);
            img.add(img_shaitu2);
            img.add(img_shaitu3);

            rl_like = (RelativeLayout) view.findViewById(R.id.rl_envy);
            rl_comment = (RelativeLayout) view.findViewById(R.id.rl_comment);
            rl_share = (RelativeLayout) view.findViewById(R.id.rl_share);
        }
    }
}

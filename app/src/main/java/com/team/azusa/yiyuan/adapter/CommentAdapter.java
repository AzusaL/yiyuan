package com.team.azusa.yiyuan.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ShowOrderCommentDto;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/3/18.
 */
public class CommentAdapter extends BaseAdapter {

    private ArrayList<ShowOrderCommentDto> datas;

    public CommentAdapter(ArrayList<ShowOrderCommentDto> datas) {
        this.datas = datas;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.comment_lv_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置评论用户的头像
        ImageLoader.getInstance().displayImage(datas.get(position).getCommentUserImgUrl(), holder.img_head);
        //设置评论用户的用户名
        holder.tv_username.setText(datas.get(position).getCommentUserName());
        //设置评论时间
        holder.tv_time.setText(DateUtil.getStringbyDate(datas.get(position).getTime(), DateUtil.dateFormatYMDHMS));
        //设置评论内容
        holder.tv_content.setText(datas.get(position).getContent());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.comment_img_head)
        SimpleDraweeView img_head;
        @Bind(R.id.tv_commentusername)
        TextView tv_username;
        @Bind(R.id.tv_commenttime)
        TextView tv_time;
        @Bind(R.id.tv_commentcontent)
        TextView tv_content;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

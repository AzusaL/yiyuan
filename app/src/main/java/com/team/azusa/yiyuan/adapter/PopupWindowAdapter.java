package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.SortType;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/1/23.
 */
public class PopupWindowAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SortType> datas;
    private int what;

    public PopupWindowAdapter(Context context, ArrayList<SortType> datas, int what) {
        this.context = context;
        this.datas = datas;
        this.what = what;
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
            convertView = View.inflate(context, R.layout.popuwin_type_lvitem, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_type.setText(datas.get(position).getStringdatas());
        if (1 == what || 2 == what) {
            if (datas.get(position).ischeck()) {
                holder.btn_Arrow.setVisibility(View.VISIBLE);
                holder.img_type.setImageResource(datas.get(position).getImgcheckid());
                holder.tv_type.setTextColor(Color.parseColor("#ff7700"));
            } else {
                holder.btn_Arrow.setVisibility(View.GONE);
                holder.img_type.setImageResource(datas.get(position).getImgid());
                holder.tv_type.setTextColor(Color.parseColor("#aaaaaa"));
            }
        } else {
            holder.img_type.setVisibility(View.GONE);
            if (datas.get(position).ischeck()) {
                holder.btn_Arrow.setVisibility(View.VISIBLE);
                holder.tv_type.setTextColor(Color.parseColor("#ff7700"));
            } else {
                holder.btn_Arrow.setVisibility(View.GONE);
                holder.tv_type.setTextColor(Color.parseColor("#aaaaaa"));
            }
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.popuwin_img)
        ImageView img_type;
        @Bind(R.id.popuwin_tv)
        TextView tv_type;
        @Bind(R.id.popuwin_btn_arrow)
        Button btn_Arrow;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

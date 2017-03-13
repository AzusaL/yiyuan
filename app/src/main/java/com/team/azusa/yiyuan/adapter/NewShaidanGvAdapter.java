package com.team.azusa.yiyuan.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/3/19.
 */
public class NewShaidanGvAdapter extends BaseAdapter {

    private ArrayList<String> imgusls;

    public NewShaidanGvAdapter(ArrayList<String> imgusls) {
        this.imgusls = imgusls;
    }

    @Override
    public int getCount() {
        return imgusls.size();
    }

    @Override
    public Object getItem(int position) {
        return imgusls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.newshaidan_girlview_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (0 == position) {
            holder.btn_delete.setVisibility(View.GONE);
            holder.addview.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
        } else {
            holder.btn_delete.setVisibility(View.VISIBLE);
            holder.addview.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(imgusls.get(position), holder.img);
        }

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgusls.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.newshaidan_itemImage)
        SimpleDraweeView img;
        @Bind(R.id.btn_delete)
        Button btn_delete;
        @Bind(R.id.addview)
        View addview;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

package com.team.azusa.yiyuan.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ImageFloder;
import com.team.azusa.yiyuan.utils.ConstanceUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/3/11.
 */
public class PhotoFloderLvAdapter extends BaseAdapter {

    //图片url集合
    private ArrayList<ImageFloder> datas;

    public PhotoFloderLvAdapter(ArrayList<ImageFloder> datas) {
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
            convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.photo_floder_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置文件夹第一张图片显示
        Uri uri = Uri.parse("file://" + datas.get(position).getFirstImagePath());
        int width = 110, height = 110;
        //压缩图片分辨率，按自己规定的尺寸加载，减少内存消耗
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.img.getController())
                .setImageRequest(request)
                .build();
        holder.img.setController(controller);

        //设置文件夹名
        holder.tv_flodername.setText(datas.get(position).getName().replace("/", ""));

        //设置文件夹内图片数量
        holder.tv_flodercount.setText("(" + datas.get(position).getCount() + ")");

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.floder_img)
        SimpleDraweeView img;
        @Bind(R.id.tv_floder)
        TextView tv_flodername;
        @Bind(R.id.tv_foloadcount)
        TextView tv_flodercount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

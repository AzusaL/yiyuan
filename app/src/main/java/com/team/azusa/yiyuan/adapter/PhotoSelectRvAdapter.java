package com.team.azusa.yiyuan.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.listener.PhotoRvItemClickLitener;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Azusa on 2016/3/12.
 */
public class PhotoSelectRvAdapter extends RecyclerView.Adapter<PhotoSelectRvAdapter.ViewHolder> {

    //图片url集合
    private ArrayList<String> url_datas;
    private HashMap<String, Boolean> check_position; //key为图片url，值为是否被选中
    private PhotoRvItemClickLitener recyclerViewItemClickLitener;

    public void setOnItemClickLitener(PhotoRvItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public PhotoSelectRvAdapter(ArrayList<String> datas, ArrayList<String> selectdatas) {
        this.url_datas = datas;
        check_position = new HashMap<>();
        for (int i = 0; i < datas.size(); i++) {
            check_position.put(datas.get(i), false);
        }
        for (int i = 0; i < selectdatas.size(); i++) {
            check_position.put(selectdatas.get(i).replace("file://",""), true);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.photo_gridview_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //position 0显示照相机图标，其他position显示图片
        if (0 == position) {
            holder.img.setVisibility(View.GONE);
            holder.img_takephoto.setVisibility(View.VISIBLE);
            holder.checkview.setVisibility(View.GONE);
        } else {
            if (check_position.get(url_datas.get(position)) == null) {
                check_position.put(url_datas.get(position), false);
            }
            holder.checkview.setVisibility(View.VISIBLE);
            //设置是否为选中状态
            if (check_position.get(url_datas.get(position))) {
                holder.checkview.setBackgroundResource(R.drawable.is_check);
                holder.img.setColorFilter(Color.parseColor("#50000000"));
            } else {
                holder.checkview.setBackgroundResource(R.drawable.empty_check);
                holder.img.setColorFilter(Color.parseColor("#00000000"));
            }
            holder.img.setVisibility(View.VISIBLE);
            holder.img_takephoto.setVisibility(View.GONE);

            //设置图片显示
            Uri uri = Uri.parse("file://" + url_datas.get(position));
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

        }

        if (recyclerViewItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != 0) {
                        //判断是否已经被点击过,改变不同状态下的显示
                        if (check_position.get(url_datas.get(position))) {
                            holder.checkview.setBackgroundResource(R.drawable.empty_check);
                            holder.img.setColorFilter(Color.parseColor("#00000000"));
                        } else {
                            holder.checkview.setBackgroundResource(R.drawable.is_check);
                            holder.img.setColorFilter(Color.parseColor("#50000000"));
                        }
                        check_position.put(url_datas.get(position),
                                !check_position.get(url_datas.get(position)));
                    }
                    //点击事件回调接口
                    recyclerViewItemClickLitener.onItemClick(v, position, "file://" + url_datas.get(position),
                            check_position.get(url_datas.get(position)));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return url_datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView img; //相册图片
        public ImageView img_takephoto;  //显示在第一个位置的照相机的图标
        public View checkview; //是否选中不同状态的View

        public ViewHolder(View view) {
            super(view);
            img = (SimpleDraweeView) view.findViewById(R.id.gridview_img);
            img_takephoto = (ImageView) view.findViewById(R.id.img_takephoto);
            checkview = view.findViewById(R.id.item_check);

        }
    }
}

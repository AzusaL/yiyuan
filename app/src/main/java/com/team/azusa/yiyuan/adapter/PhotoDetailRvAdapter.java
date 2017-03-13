package com.team.azusa.yiyuan.adapter;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Azusa on 2016/4/9.
 */
public class PhotoDetailRvAdapter extends RecyclerView.Adapter<PhotoDetailRvAdapter.ViewHolder> {

    private ArrayList<String> datas;

    public PhotoDetailRvAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(ConstanceUtils.CONTEXT, R.layout.item_rv_photodetail, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        Uri uri = Uri.parse(Config.IP + "/yiyuan/image" + datas.get(position));
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setControllerListener(new BaseControllerListener<ImageInfo>() {
//                    @Override
//                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
//                        ViewGroup.LayoutParams layoutParams = holder.img.getLayoutParams();
//                        //图片的原始宽高
//                        float h = imageInfo.getHeight();
//                        float w = imageInfo.getWidth();
//                        //按原始宽高的比例算出图片要显示的高度
//                        float height = ConstanceUtils.screenWidth * (h / w);
//                        layoutParams.width = ConstanceUtils.screenWidth;
//                        layoutParams.height = (int) height;
//                        holder.img.setLayoutParams(layoutParams);
//                        super.onFinalImageSet(id, imageInfo, animatable);
//                    }
//                })
//                .setUri(uri)
//                .build();
//        holder.img.setController(controller);
        ImageLoader.getInstance().displayWrapHightImage(datas.get(position),holder.img);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (SimpleDraweeView) itemView.findViewById(R.id.img_detail);
        }
    }
}

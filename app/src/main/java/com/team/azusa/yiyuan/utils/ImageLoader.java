package com.team.azusa.yiyuan.utils;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.ViewGroup;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;

/**
 * Created by Delete_exe on 2016/3/10.
 */
public class ImageLoader {

    public static ImageLoader imageLoader = new ImageLoader();
    //默认压缩图片的宽高大小
    public int defaultwidth = 300;
    public int defaulthight = 300;

    public static ImageLoader getInstance() {
        return imageLoader;
    }

    //设置图片要显示的宽高
    public ImageLoader setSize(int width, int hight) {
        this.defaultwidth = width;
        this.defaulthight = hight;
        return imageLoader;
    }

    public Uri getUri(String Url) {
        Uri uri;
        if (StringUtil.isEmpty(Url) || Url.startsWith("res:///") || Url.startsWith("file://")) {
            uri = Uri.parse(Url + "");
        } else {
            uri = Uri.parse(Config.IP + "/yiyuan/image" + Url);
        }
        return uri;
    }

    public void displayImage(String Url, SimpleDraweeView imageView) {
        Uri uri = getUri(Url);
        //判断是否开启无图模式
        if (Config.isNoDownload) {
            //判断是否已缓存在本地
            if (isImageDownloaded(uri)) {
                setimg(uri, imageView);
            } else {
                imageView.setImageURI(Uri.parse("res:///" + R.drawable.goods_pic_default));
            }
        } else {
            setimg(uri, imageView);
        }
    }

    //显示宽度match_parent,高度wrap_content的图片
    public void displayWrapHightImage(String Url, final SimpleDraweeView imageView) {
        Uri uri = getUri(Url);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        //图片的原始宽高
                        float h = imageInfo.getHeight();
                        float w = imageInfo.getWidth();
                        //按原始宽高的比例算出图片要显示的高度
                        float height = ConstanceUtils.screenWidth * (h / w);
                        layoutParams.width = ConstanceUtils.screenWidth;
                        layoutParams.height = (int) height;
                        imageView.setLayoutParams(layoutParams);
                        super.onFinalImageSet(id, imageInfo, animatable);
                    }
                })
                .setUri(uri)
                .build();

        if (Config.isNoDownload) {
            if (isImageDownloaded(uri)) {
                imageView.setController(controller);
            } else {
                imageView.setImageURI(Uri.parse("res:///" + R.drawable.goods_pic_default));
            }
        } else {
            imageView.setController(controller);
        }

    }

    //压缩图片显示
    public void setimg(Uri uri, SimpleDraweeView imageView) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(defaultwidth, defaulthight))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imageView.getController())
                .setImageRequest(request)
                .build();
        imageView.setController(controller);
        this.defaultwidth = 300;
        this.defaulthight = 300;
    }

    /**
     * 判断该Uri是否已经缓存到本地里
     *
     * @param loadUri
     * @return
     */
    public boolean isImageDownloaded(Uri loadUri) {
        if (loadUri == null) {
            return false;
        }
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
        return ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey) ||
                ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey);
    }

}

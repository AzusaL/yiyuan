package com.team.azusa.yiyuan.app;

import android.app.Application;
import android.app.Notification;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.database.SharedPreferenceData;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.PersistentCookieStore;

import java.net.CookieManager;
import java.net.CookiePolicy;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Azusa on 2016/2/5.
 */
public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化全局Context对象
        ConstanceUtils.CONTEXT = getApplicationContext();
        //初始化Fresco
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(ConstanceUtils.CONTEXT, config);
        //添加请求自动带上cookie
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        client.setCookieHandler(new CookieManager(
                new PersistentCookieStore(ConstanceUtils.CONTEXT),
                CookiePolicy.ACCEPT_ALL));
        //从SharedPreference中读取app是否开启无图模式
        Config.isNoDownload = SharedPreferenceData.getInstance().isUserDownloadImage(ConstanceUtils.CONTEXT);

        //初始化JPush代码
        JPushInterface.setDebugMode(true);//设置为debug模式可以查看log日志
        JPushInterface.init(getApplicationContext());
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(getApplicationContext());
        builder.statusBarDrawable = R.drawable.yyg_logo;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);


//        //初始化内存检测工具
//        LeakCanary.install(this);
    }
}

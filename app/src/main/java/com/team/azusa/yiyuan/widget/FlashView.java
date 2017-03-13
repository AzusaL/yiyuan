package com.team.azusa.yiyuan.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.listener.MyOnClickListener;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


@SuppressLint("HandlerLeak")
public class FlashView extends FrameLayout {

    //    private ImageLoaderTools imageLoaderTools;
    private ImageHandler mhandler = new ImageHandler(new WeakReference<FlashView>(this));
    private ArrayList<String> imageUris;
    private Context context;
    private ArrayList<SimpleDraweeView> imageViewsList;
    private ArrayList<ImageView> dotViewsList;
    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private MyOnClickListener mFlashViewListener;//向外提供接口
    private boolean isTwo = false;
    private int pos;
    private boolean imgClickable = true;

    public FlashView(Context context) {
        this(context, null);
    }

    public FlashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //读取该自定义控件自定义的属性
        this.context = context;
        initUI(context);
        if (!(imageUris.size() <= 0)) {
            setImageUris(imageUris);//
        }
    }

    /**
     * 设置监听
     *
     * @param mFlashViewListener
     */
    public void setOnPageClickListener(MyOnClickListener mFlashViewListener) {
        this.mFlashViewListener = mFlashViewListener;
    }

    private void initUI(Context context) {
        imageViewsList = new ArrayList<SimpleDraweeView>();
        dotViewsList = new ArrayList<ImageView>();
        imageUris = new ArrayList<String>();
//        imageLoaderTools = ImageLoaderTools.getInstance(context.getApplicationContext());
        LayoutInflater.from(context).inflate(R.layout.layout_photoadv, this, true);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearlayoutadv);
        mViewPager = (ViewPager) findViewById(R.id.viewPageradv);
    }

    public void setImageUris(ArrayList<String> imageuris) {

        if (imageUris.size() > 0) {
            imageUris.clear();
            imageViewsList.clear();
            dotViewsList.clear();
            mLinearLayout.removeAllViews();
        }

        if (imageuris.size() <= 0)// 如果得到的图片张数为0，则增加一张默认的图片
        {
            imageUris.add("drawable://" + R.drawable.defaultflashview);
        } else {
            if (imageuris.size() == 2) {
                isTwo = true;
                imageUris.addAll(imageuris);
                imageUris.addAll(imageuris);
            } else {
                isTwo = false;
                imageUris.addAll(imageuris);
            }
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
        lp.setMargins(16, 0, 0, 0);
        for (int i = 0; i < imageUris.size(); i++) {
            SimpleDraweeView imageView = new SimpleDraweeView(getContext());
            ImageLoader.getInstance().setSize(600, 600).displayImage(imageUris.get(i), imageView);
            imageViewsList.add(imageView);
            ImageView viewDot = new ImageView(getContext());
            if (i == 0) {
                viewDot.setBackgroundResource(R.drawable.dot_white);
            } else {
                viewDot.setBackgroundResource(R.drawable.dot_light);
            }
            viewDot.setLayoutParams(lp);
            if (isTwo)//为两张图片时加入的判断
            {
                if (i > 1) {

                } else {
                    dotViewsList.add(viewDot);
                    mLinearLayout.addView(viewDot);
                }
            } else {
                dotViewsList.add(viewDot);
                mLinearLayout.addView(viewDot);
            }

        }
        mViewPager.setFocusable(true);
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(new MyPageChangeListener());
        if (imageUris.size() <= 1)//图片小于等于1张时，不轮播
        {

        } else {
            mViewPager.setCurrentItem(100 * imageViewsList.size());
            mhandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE,
                    ImageHandler.MSG_DELAY);
        }
    }

    /**
     * 切换轮播小点的显示
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < dotViewsList.size(); i++) {
            if (i == selectItems % dotViewsList.size()) {
                dotViewsList.get(i).setBackgroundResource(R.drawable.point_press);
            } else {
                dotViewsList.get(i).setBackgroundResource(R.drawable.point_normal);
            }
        }
    }

    /**
     * 数据适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(View container, int position, Object object) {

        }

        @Override
        public Object instantiateItem(View container, int position) {
            position = position % imageViewsList.size();
            if (position < 0) {
                position = position + imageViewsList.size();
            }

            if (isTwo) {
                pos = position % 2;
            } else {
                pos = position;
            }
            final int posclick = pos;
            View view = imageViewsList.get(position);
            view.setTag(position);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mFlashViewListener != null && imgClickable) {
                        mFlashViewListener.onClick(posclick);
                    }
                }
            });
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewPager pager = (ViewPager) vp;
                pager.removeView(view);
            }
            ((ViewPager) container).addView(view);
            return view;
        }

        @Override
        public int getCount() {
            if (imageUris.size() <= 1) {
                return 1;
            } else {
                return Integer.MAX_VALUE;
            }

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private class MyPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

            switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    mhandler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);

                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    mhandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub
            mhandler.sendMessage(Message.obtain(mhandler, ImageHandler.MSG_PAGE_CHANGED, pos, 0));
            setImageBackground(pos);
        }

    }

    @SuppressWarnings("unused")
    private void destoryBitmaps() {
        for (int i = 0; i < imageViewsList.size(); i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                drawable.setCallback(null);
            }
        }
    }


    private static class ImageHandler extends Handler {

        protected static final int MSG_UPDATE_IMAGE = 1;

        protected static final int MSG_KEEP_SILENT = 2;

        protected static final int MSG_BREAK_SILENT = 3;

        protected static final int MSG_PAGE_CHANGED = 4;

        protected static final long MSG_DELAY = 4000;

        private WeakReference<FlashView> weakReference;
        private int currentItem = 0;

        protected ImageHandler(WeakReference<FlashView> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            FlashView activity = weakReference.get();
            if (activity == null) {
                return;
            }
            if (activity.mhandler.hasMessages(MSG_UPDATE_IMAGE)) {
                if (currentItem > 0)// 这里必须加入currentItem>0的判断，否则不能完美的自动轮播
                {
                    activity.mhandler.removeMessages(MSG_UPDATE_IMAGE);
                }
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    activity.mViewPager.setCurrentItem(currentItem);
                    activity.mhandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    break;
                case MSG_BREAK_SILENT:
                    activity.mhandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    currentItem = msg.arg1;
                    activity.mhandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                default:
                    break;
            }
        }
    }
}


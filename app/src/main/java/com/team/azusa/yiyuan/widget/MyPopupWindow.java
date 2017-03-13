package com.team.azusa.yiyuan.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.PopupWindowAdapter;
import com.team.azusa.yiyuan.bean.SortType;
import com.team.azusa.yiyuan.event.SortEvent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Azusa on 2016/1/16.
 */
public class MyPopupWindow {
    private PopupWindow popupWindow;
    private ListView mlistView;
    private PopupWindowAdapter adapter;
    private ArrayList<SortType> datas;
    private int currentposition = 0;
    private int what;
    private TextView tv_sort;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (1 == msg.what) {
                popupWindow.dismiss();
                EventBus.getDefault().post(new SortEvent(datas.get(currentposition).getStringdatas(), what));
            }
        }
    };

    public PopupWindow build(Context context, View view, View fullScreenView, int topHeight, int what, TextView tv_sort) {
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                fullScreenView.getHeight() - topHeight);
        this.what = what;
        this.tv_sort = tv_sort;
        initData();
        mlistView = (ListView) view.findViewById(R.id.popuwin_listview);
        adapter = new PopupWindowAdapter(context, datas, what);
        mlistView.setAdapter(adapter);
        setListener();
        return popupWindow;
    }

    public void setCheck(int position) {
        if (currentposition != 0) {
            datas.get(currentposition).setIscheck(false);
        }
        currentposition = position;
        datas.get(position).setIscheck(true);
        tv_sort.setText(datas.get(position).getStringdatas());
        adapter.notifyDataSetChanged();
    }

    private void setListener() {
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentposition == position) {
                    return;
                } else {
                    datas.get(currentposition).setIscheck(false);
                    currentposition = position;
                    datas.get(position).setIscheck(true);
                    tv_sort.setText(datas.get(position).getStringdatas());
                    adapter.notifyDataSetChanged();
                    handler.sendEmptyMessageDelayed(1, 50);
                }
            }
        });
    }

    public PopupWindow showLocation(PopupWindow mypopupWindow, View view, final View arrow) {
        ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
        mypopupWindow.setBackgroundDrawable(colorDrawable);
        mypopupWindow.setOutsideTouchable(true);
        mypopupWindow.setFocusable(true);
        mypopupWindow.showAsDropDown(view);
        mypopupWindow.update();
        if (arrow != null) {
            arrow.setVisibility(View.VISIBLE);
            mypopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    arrow.setVisibility(View.GONE);
                }
            });
        }
        return popupWindow;
    }


    private void initData() {
        if (1 == what || 2 == what) {
            SortType sortType1 = new SortType("全部分类", R.drawable.sort0_checked, R.drawable.sort0_normal, true);
            SortType sortType2 = new SortType("手机数码", R.drawable.sort100_checked, R.drawable.sort100_normal, false);
            SortType sortType3 = new SortType("电脑办公", R.drawable.sort106_checked, R.drawable.sort106_normal, false);
            SortType sortType4 = new SortType("家用电器", R.drawable.sort104_checked, R.drawable.sort104_normal, false);
            SortType sortType5 = new SortType("化妆个护", R.drawable.sort2_checked, R.drawable.sort2_normal, false);
            SortType sortType6 = new SortType("钟表首饰", R.drawable.sort222_checked, R.drawable.sort222_normal, false);
            SortType sortType7 = new SortType("其他商品", R.drawable.sort312_checked, R.drawable.sort312_normal, false);
            datas = new ArrayList<>();
            datas.add(sortType1);
            datas.add(sortType2);
            datas.add(sortType3);
            datas.add(sortType4);
            datas.add(sortType5);
            datas.add(sortType6);
            datas.add(sortType7);
        } else {
            SortType sortType1 = new SortType("人气", true);
            SortType sortType2 = new SortType("即将揭晓", false);
            SortType sortType3 = new SortType("价值(由高到低)", false);
            SortType sortType4 = new SortType("价值(由低到高)", false);
            SortType sortType5 = new SortType("最新", false);
            datas = new ArrayList<>();
            datas.add(sortType1);
            datas.add(sortType2);
            datas.add(sortType3);
            datas.add(sortType4);
            datas.add(sortType5);
        }
    }
}

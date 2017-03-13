package com.team.azusa.yiyuan.yiyuan_mainfragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.NewPublishLvAdapter;
import com.team.azusa.yiyuan.adapter.NewPublishedLvAdapter;
import com.team.azusa.yiyuan.bean.JieXiaoDto;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.callback.DaoJiShiCallback;
import com.team.azusa.yiyuan.callback.JieXiaoCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.PulluptoRefreshListview;
import com.team.azusa.yiyuan.yiyuan_activity.GoodsDetailsActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NewestFragment extends Fragment {
    @Bind(R.id.fgtab_three_rl)
    RelativeLayout fgtabThreeRl;
    @Bind(R.id.newpublic_lv)
    ListView newpublicingLv;
    @Bind(R.id.newpublic_ed_lv)
    PulluptoRefreshListview newpublicEdLv;
    @Bind(R.id.jiexiao_ing)
    RadioButton jiexiaoIng;
    @Bind(R.id.jiexiao_ed)
    RadioButton jiexiaoEd;
    private View view;
    private PopupWindow mPopupWindow;
    private AlertDialog loding_dialog;
    private boolean isinited;
    private ArrayList<ProductDto> datas_jiexiaoing = new ArrayList<>();
    private ArrayList<JieXiaoDto> datas_jiexiaoed = new ArrayList<>();
    private ArrayList<Long> itemBeginTime = new ArrayList<>();
    private NewPublishLvAdapter adapter_ing;
    private NewPublishedLvAdapter adapter_ed;
    private boolean isInPartjiexiaoing = true;
    private boolean isLoop = false;
    private boolean cancelrequest = false;

    /**
     * postDelayed(this,4 * 1000)方法安排一个Runnable对象到主线程队列中,后面再主动调用一次
     * postDelayed(this,4 * 1000)就可以实现循环
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!UserUtils.isNetworkAvailable(getActivity())) {
                return;
            }
            handler.sendEmptyMessage(1);
            loding_dialog = new MyDialog().showLodingDialog(getActivity());
            loding_dialog.setOnKeyListener(backlistener);
            handler.postDelayed(this, 4 * 1000);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://揭晓中 重载数据
                    OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getDaojishi")
                            .tag("NewestFragment")
                            .build().execute(new DaoJiShiCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (cancelrequest) {
                                return;
                            }
                            sendEmptyMessage(3);
                            MyToast.showToast("网络连接出错");
                        }

                        @Override
                        public void onResponse(ArrayList<ProductDto> response) {
                            initDataTime();
                            if (null != response && response.size() != 0) {
                                datas_jiexiaoing.addAll(response);
                            }
                            adapter_ing.notifyDataSetChanged();
                            sendEmptyMessage(3);
                        }
                    });
                    break;
                case 2://已揭晓 数据更新
                    final long time = 0 == datas_jiexiaoed.size() ? System.currentTimeMillis()
                            : datas_jiexiaoed.get(datas_jiexiaoed.size() - 1).getJieXiaoTime();
                    OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getJiexiao")
                            .addParams("time", time + "")
                            .tag("NewestFragment")
                            .build().execute(new JieXiaoCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (cancelrequest) {
                                return;
                            }
                            sendEmptyMessage(3);
                            MyToast.showToast("网络连接出错");
                        }

                        @Override
                        public void onResponse(ArrayList<JieXiaoDto> response) {
                            if (null == response || response.size() == 0) {
                                sendEmptyMessage(3);
                                newpublicEdLv.setLoadComplete(true);
                                return;
                            }
                            datas_jiexiaoed.addAll(response);
                            adapter_ed.notifyDataSetChanged();
                            newpublicEdLv.setLoading(false);
                            sendEmptyMessage(3);
                        }
                    });
                    break;
                case 3:
                    if (loding_dialog != null) {
                        loding_dialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fgtab03, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    //当前页面可见时再加载页面
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {                                   //viewpager切换进来该fragment时候调用
            iniView();
            isinited = true;
            if (!isLoop && isInPartjiexiaoing) {
                handler.post(runnable);
                isLoop = true;
            }
        } else {
            if (isinited && isLoop) {       //viewpager切换出该fragment时候调用
                handler.removeCallbacks(runnable);
                isLoop = false;
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void iniView() {
        initDataTime();
        adapter_ing = new NewPublishLvAdapter(datas_jiexiaoing, itemBeginTime, getContext());
        adapter_ed = new NewPublishedLvAdapter(datas_jiexiaoed, getContext());

        newpublicingLv.setAdapter(adapter_ing);
        newpublicEdLv.setAdapter(adapter_ed);

        newpublicEdLv.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                handler.sendEmptyMessage(2);
            }
        });

        //揭晓中点击监听
        newpublicingLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == datas_jiexiaoing.size()) {
                    return;
                }
                String productId = datas_jiexiaoing.get(position).getProductId();
                int yunNum = datas_jiexiaoing.get(position).getYunNum();

                Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("yunNum", yunNum + "");
                startActivity(intent);
            }
        });

        //已揭晓点击监听
        newpublicEdLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == datas_jiexiaoed.size()) {
                    return;
                }
                String productId = datas_jiexiaoed.get(position).getProductId();
                int yunNum = datas_jiexiaoed.get(position).getYunNum();

                Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("yunNum", yunNum + "");
                startActivity(intent);
            }
        });
    }

    /**
     * 辅助修正ing_ListView里面的时间错误
     */
    private void initDataTime() {
        datas_jiexiaoing.clear();
        for (int i = 0; i < 30; i++) {
            if (30 > itemBeginTime.size()) {
                itemBeginTime.add(System.currentTimeMillis());
            } else {
                itemBeginTime.set(i, System.currentTimeMillis());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("NewestFragment");
    }

    @OnClick({R.id.jiexiao_ing, R.id.jiexiao_ed})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jiexiao_ing:
                if (!isInPartjiexiaoing) {
                    togglePart();
                    if (!isLoop) {
                        handler.post(runnable);
                        isLoop = true;
                    }
                }
                break;
            case R.id.jiexiao_ed:
                if (isInPartjiexiaoing) {
                    togglePart();
                    if (isLoop) {
                        handler.removeCallbacks(runnable);
                        isLoop = false;
                    }
                    loding_dialog = new MyDialog().showLodingDialog(getActivity());
                    loding_dialog.setOnKeyListener(backlistener);
                    handler.sendEmptyMessage(2);
                }
                break;
        }
    }

    private DialogInterface.OnKeyListener backlistener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                loding_dialog.dismiss();
                return false;    //已处理
            }
            return false;
        }
    };

    /**
     * 切换页面开关
     */
    private void togglePart() {
        isInPartjiexiaoing = !isInPartjiexiaoing;
        if (isInPartjiexiaoing) {
            jiexiaoIng.setTextColor(0xffffffff);
            jiexiaoEd.setTextColor(0xffa8a8a8);
            newpublicingLv.setVisibility(View.VISIBLE);
            newpublicEdLv.setVisibility(View.GONE);
        } else {
            jiexiaoIng.setTextColor(0xffa8a8a8);
            jiexiaoEd.setTextColor(0xffffffff);
            newpublicingLv.setVisibility(View.GONE);
            newpublicEdLv.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}

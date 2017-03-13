package com.team.azusa.yiyuan.yiyuan_mainfragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.AllgoodsLvAdapter;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.callback.AllProductCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.event.SortEvent;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.widget.AddCarAnimation;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.MyPopupWindow;
import com.team.azusa.yiyuan.widget.PulluptoRefreshListview;
import com.team.azusa.yiyuan.widget.TopbarAnimation;
import com.team.azusa.yiyuan.yiyuan_activity.GoodsDetailsActivity;
import com.team.azusa.yiyuan.yiyuan_activity.MainActivity;
import com.team.azusa.yiyuan.yiyuan_activity.SearchActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Azusa on 2016/1/10.
 */
public class AllGoodsFragment extends Fragment {

    @Bind(R.id.search_orange)
    ImageView searchOrange;
    @Bind(R.id.fg2_topbar_typerl)
    RelativeLayout TopbarTyperl;
    @Bind(R.id.fg2_topbar_publicrl)
    RelativeLayout TopbarPublicrl;
    @Bind(R.id.fg2_topll)
    LinearLayout Topll;
    @Bind(R.id.allgoods_lv)
    PulluptoRefreshListview allgoodsLv;
    @Bind(R.id.sort_arrow)
    View sortArrow1;
    @Bind(R.id.sort_arrow2)
    View sortArrow2;
    @Bind(R.id.sort_tv1)
    TextView sortTv1;
    @Bind(R.id.sort_tv2)
    TextView sortTv2;

    private ArrayList<ProductDto> datas;
    private View view;
    private AllgoodsLvAdapter adapter;
    private int mTopbarHeight; //顶部toolbar高度
    private int mpwindow_TopbarHeight;
    private TopbarAnimation topbarAnimation;
    private PopupWindow mypopupWindow1, mypopupWindow2;
    private MyDialog myDialog;
    private AlertDialog loding_dialog;
    private boolean isinited = false;
    private String orderBy = ""; //排序
    private String category = ""; //分类
    private int[] car_location; //购物车的位置
    private MainActivity activity;
    private boolean cancelrequest = false; //是否取消网络请求
    private MyPopupWindow pwin1 = new MyPopupWindow();
    private MyPopupWindow pwin2 = new MyPopupWindow();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fgtab02, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        activity = (MainActivity) getActivity();
        iniView();
        setHead();
        setListener();
        return view;
    }

    private void setListener() {
        adapter.SetOnAddCarClickListener(new AllgoodsLvAdapter.AddCarClickListener() {
            @Override
            public void onAddCarClick(int position, Bitmap drawable, int[] start_location) {
                new AddCarAnimation(getActivity()).doAnim(drawable, start_location, car_location);
            }
        });

        allgoodsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == datas.size() + 1) {
                    return;
                }
                String productId = datas.get(position - 1).getProductId();
                int yunNum = datas.get(position - 1).getYunNum();

                Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("yunNum", yunNum + "");
                startActivity(intent);
            }
        });
    }

    //添加头部
    private void setHead() {
        View head = View.inflate(getContext(), R.layout.null_headview, null);
        allgoodsLv.addHeaderView(head);
        allgoodsLv.setHeaderDividersEnabled(false);
    }

    //当前页面可见时再加载页面数据
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            allgoodsLv.setLoadComplete(false);
            getData(0, 0, category, orderBy);
            if (!isinited) {
                myDialog = new MyDialog();
                loding_dialog = myDialog.showLodingDialog(getActivity());
                loding_dialog.setOnKeyListener(backlistener);
                car_location = activity.getCar_location();
                isinited = true;
            } else {
                loding_dialog.show();
                myDialog.initAnimation();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void iniView() {
        initData();
        allgoodsLv.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                getData((datas.size()), 1, category, orderBy);
            }
        });
        topbarAnimation = new TopbarAnimation();
        topbarAnimation.build(mTopbarHeight, Topll);
        allgoodsLv.setFloatTopbaranimation(topbarAnimation);
        if (!topbarAnimation.getisShow()) {
            topbarAnimation.showTopbar();
        }
    }

    private void initData() {
        mTopbarHeight = getResources().getDimensionPixelSize(R.dimen.mTopbar_height);
        mpwindow_TopbarHeight = getResources().getDimensionPixelSize(R.dimen.mpwindow_Topbar_height);
        datas = new ArrayList<>();
        adapter = new AllgoodsLvAdapter(datas, getContext());
        allgoodsLv.setAdapter(adapter);
    }

    /**
     * 从服务器获取数据
     *
     * @param firstResult 要加载的第一条数据的position
     */
    private void getData(final int firstResult, final int what, String category, String orderBy) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getCurrentYunNums")
                .addParams("category", category)
                .addParams("orderBy", orderBy)
                .addParams("firstResult", firstResult + "")
                .tag("AllGoodsFragment")
                .build().execute(new AllProductCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
                loding_dialog.dismiss();
            }

            @Override
            public void onResponse(ArrayList<ProductDto> response) {
                //response为空表示没有更多数据了
                if (response == null || response.size() == 0) {
                    allgoodsLv.setLoadComplete(true);
                    loding_dialog.dismiss();
                    return;
                } else if (0 == what) {
                    allgoodsLv.smoothScrollToPosition(0);
                    topbarAnimation.showTopbar();
                    datas.clear(); // 如果respomse不为空且what为0，表示刷新数据，先清空数据集合
                }
                if (0 == what) {
                    loding_dialog.dismiss();
                }
                allgoodsLv.setLoading(false);
                datas.addAll(response);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //eventbus接收popupwindow点击发来的消息
    public void onEventMainThread(SortEvent event) {
        switch (event.getWhat()) {
            case 1:
                category = event.getSort();
                break;
            case 2:
                category = "全部分类";
                orderBy = "人气";
                if (mypopupWindow1 != null) {
                    Log.e("main", "onEventMainThread: kkkkkkkkkk");
                    pwin1.setCheck(0);

                }
                if (mypopupWindow2 != null) {
                    pwin2.setCheck(0);
                    Log.e("main", "onEventMainThread: qqqqqqqqqqqqqq");
                }
                return;
            case 3:
                orderBy = event.getSort();
                break;
        }
        loding_dialog.show();
        myDialog.initAnimation();
        allgoodsLv.setLoadComplete(false);
        getData(0, 0, category, orderBy);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("AllGoodsFragment");
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.search_orange, R.id.fg2_topbar_typerl, R.id.fg2_topbar_publicrl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_orange:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.fg2_topbar_typerl:
                showPopupWindow(1);
                break;
            case R.id.fg2_topbar_publicrl:
                showPopupWindow(2);
                break;
        }
    }

    private void showPopupWindow(int what) {
        View pwindow = View.inflate(getActivity(), R.layout.popupwindow_alltype, null);
        switch (what) {
            case 1:
                if (null == mypopupWindow1) {
                    mypopupWindow1 = pwin1.build(ConstanceUtils.CONTEXT, pwindow, view, mpwindow_TopbarHeight, 1, sortTv1);
                }
                pwin1.showLocation(mypopupWindow1, TopbarTyperl, sortArrow1);
                break;
            case 2:
                if (null == mypopupWindow2) {
                    mypopupWindow2 = pwin2.build(ConstanceUtils.CONTEXT, pwindow, view, mpwindow_TopbarHeight, 3, sortTv2);
                }
                pwin2.showLocation(mypopupWindow2, TopbarPublicrl, sortArrow2);
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
}

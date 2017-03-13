package com.team.azusa.yiyuan.photo_fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.widget.MyPhotoView;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class PhotoFragment extends Fragment {

    private View view;
    private MyPhotoView mphotoview;
    private PhotoViewAttacher mattacher;
    private String photourl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.photo_view_fg, null);
        photourl = getArguments().getString("photourl");
        initView();
        setlistener();
        return view;
    }

    private void setlistener() {
        mattacher.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
    }

    private void initView() {
        mphotoview = (MyPhotoView) view.findViewById(R.id.photo_view_img);
        mattacher = new PhotoViewAttacher(mphotoview);

        Uri uri = ImageLoader.getInstance().getUri(photourl);
        mphotoview.setImageUri(uri, new ResizeOptions(800, 800));

    }
}

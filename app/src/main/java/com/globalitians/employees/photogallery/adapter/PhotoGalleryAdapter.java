package com.globalitians.employees.photogallery.adapter;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.photogallery.model.PhotoGalleryModel;

import java.util.ArrayList;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.MyViewHolder> {
    private ArrayList<PhotoGalleryModel> mArrListPhotoGallery;
    private Activity mActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvOptionTitle;
        public ImageView ivOption;
        public RelativeLayout relContainer;

        public MyViewHolder(View view) {
            super(view);
            tvOptionTitle = (TextView) view.findViewById(R.id.tv_student_option);
            ivOption = (ImageView) view.findViewById(R.id.iv_student_option);
            relContainer=view.findViewById(R.id.rel_container);
        }
    }


    public PhotoGalleryAdapter(Activity activity, ArrayList<PhotoGalleryModel> mArrListPhotoGallery) {
        this.mActivity=activity;
        this.mArrListPhotoGallery = mArrListPhotoGallery;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_employee_options, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try{
            PhotoGalleryModel modelStudentOption = mArrListPhotoGallery.get(position);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListPhotoGallery.size();
    }
}

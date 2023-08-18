package com.globalitians.employees.Homework.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.globalitians.employees.R;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class HomeworkImagesAdapter extends RecyclerView.Adapter<HomeworkImagesAdapter.ImageViewHolder> {

    private Activity activity;
    private ArrayList<String> mAlimages;
    private OnImageClickListner onImageClickListner;

    public HomeworkImagesAdapter(Activity activity, ArrayList<String> mAlimages,OnImageClickListner onImageClickListner) {
        this.activity = activity;
        this.mAlimages = mAlimages;
        this.onImageClickListner = onImageClickListner;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.view_image,parent,false);
        return new ImageViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        try{
            setImagesOfHomeWork(position,holder,mAlimages);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setImagesOfHomeWork(int position, ImageViewHolder holder, ArrayList<String> mAlimages) {
        if(!CommonUtil.isNullString(""+mAlimages.get(position)) ||
            !CommonUtil.isNullString(""+mAlimages.get(position))){
            holder.imageView.setVisibility(View.VISIBLE);
            CommonUtil.setCircularImageToGlide(activity,holder.imageView,""+mAlimages.get(position));
        }else{
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mAlimages.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onImageClickListner.onImageClick(getAdapterPosition());
                }
            });

        }
    }

    public interface OnImageClickListner{
        void onImageClick(int position);
    }

}

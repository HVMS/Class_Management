package com.globalitians.employees.Homework.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.globalitians.employees.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesHolder> {

    private Activity activity;
    private List<File> imagesFile;

    public ImagesAdapter(Activity activity, List<File> imagesFile) {
        this.activity = activity;
        this.imagesFile = imagesFile;
    }

    @NonNull
    @Override
    public ImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.view_image,parent,false);
        return new ImagesHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesHolder holder, int position) {
        try{
            Picasso.get()
                    .load(imagesFile.get(position))
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return imagesFile.size();
    }

    public class ImagesHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public ImagesHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);

        }
    }

}

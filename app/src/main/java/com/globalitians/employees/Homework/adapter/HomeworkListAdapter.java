package com.globalitians.employees.Homework.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.globalitians.employees.Homework.model.HomeworkListModelClass;
import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;

import java.util.ArrayList;

public class HomeworkListAdapter extends RecyclerView.Adapter<HomeworkListAdapter.MyViewHolder> {

    private Activity activity;
    private HomeworkListModelClass.Homework homeworkListModelClass;
    private ArrayList<HomeworkListModelClass.Homework> mAlhomrworklist;
    private HomeWorkListner homeWorkListner;

    public HomeworkListAdapter(Activity activity, ArrayList<HomeworkListModelClass.Homework> mAlhomrworklist, HomeWorkListner homeWorkListner) {
        this.activity = activity;
        this.mAlhomrworklist = mAlhomrworklist;
        this.homeWorkListner = homeWorkListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_list_item_layout,parent,false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            homeworkListModelClass = mAlhomrworklist.get(position);
            setHomeWorkDetails(position,holder,homeworkListModelClass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setHomeWorkDetails(int position, MyViewHolder holder, HomeworkListModelClass.Homework homeworkListModelClass) {
        holder.mTxthwdate.setText(""+homeworkListModelClass.getCreatedAt());
        holder.mTxthwname.setText(""+homeworkListModelClass.getTitle());
    }

    @Override
    public int getItemCount() {
        return mAlhomrworklist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CustomTextView mTxthwname;
        private CustomTextView mTxthwtitledate;
        private CustomTextView mTxthwdate;
        private ImageView mIvremovehw;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxthwname = itemView.findViewById(R.id.tv_homework_name);
            mTxthwtitledate = itemView.findViewById(R.id.tv_homework_date);
            mTxthwdate = itemView.findViewById(R.id.tv_homeword_actual_date);
            mIvremovehw = itemView.findViewById(R.id.iv_remove_hw);

            mIvremovehw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    homeWorkListner.onMoreOptionsClick(getAdapterPosition(),mIvremovehw);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    homeWorkListner.onHomeWorkItemClicked(getAdapterPosition());
                }
            });
        }

    }

    public interface HomeWorkListner{
        void onHomeWorkItemClicked(int position);
        void onMoreOptionsClick(int position, ImageView mIvremovehw);
    }
}

package com.globalitians.employees.faculty.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.faculty.model.FacultyListModel;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class FacultyListAdapter extends RecyclerView.Adapter<FacultyListAdapter.MyViewHolder> {

    private ArrayList<FacultyListModel.Faculty> mAlFaculties;
    private Activity mActivity;
    private FacultyClickListener mFacultyClickListener;
    private FacultyListModel.Faculty modelFaculty;

    public FacultyListAdapter(Activity activity, ArrayList<FacultyListModel.Faculty> mAlFaculty, FacultyClickListener listener) {
        this.mActivity = activity;
        this.mAlFaculties = mAlFaculty;
        this.mFacultyClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFacultyName;
        private CircularImageView ivFacultyProfile;
        private LinearLayout linMainContainer;
        private ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            linMainContainer = view.findViewById(R.id.linMainContainer);
            tvFacultyName = view.findViewById(R.id.tv_faculty_name);
            ivFacultyProfile = view.findViewById(R.id.iv_faculty);
            imageView = view.findViewById(R.id.iv_more_options);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFacultyClickListener.onDeleteFacultyFromMoreOptions(getAdapterPosition(),imageView);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFacultyClickListener.onFacultyClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_faculty_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            modelFaculty = mAlFaculties.get(position);
            //CommonUtil.setCircularImageToGlide(mActivity, holder.ivFacultyProfile, "" + mAlFaculties.get(position).getFacultyDrawable());
            setFacultyData(position,holder,modelFaculty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFacultyData(int position, MyViewHolder holder, FacultyListModel.Faculty modelFaculty) {
        holder.tvFacultyName.setText(""+mAlFaculties.get(position).getName());
        if(!CommonUtil.isNullString(""+mAlFaculties.get(position).getImage())){
            CommonUtil.setCircularImageForUser(mActivity,holder.ivFacultyProfile,""+modelFaculty.getImage());
        }else{
            holder.ivFacultyProfile.setImageResource(R.drawable.ic_user_round);
        }
    }

    @Override
    public int getItemCount() {
        return mAlFaculties.size();
    }

    public interface FacultyClickListener {
        void onFacultyClick(int position);
        void onDeleteFacultyFromMoreOptions(int position, ImageView mIvmoreoptions);
    }
}

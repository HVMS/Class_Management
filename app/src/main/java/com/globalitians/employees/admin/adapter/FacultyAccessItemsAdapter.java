package com.globalitians.employees.admin.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.globalitians.employees.R;
import com.globalitians.employees.admin.model.AssignAccessModel;
import com.globalitians.employees.faculty.model.FacultyListModel;

import java.util.ArrayList;

//
public class FacultyAccessItemsAdapter extends RecyclerView.Adapter<FacultyAccessItemsAdapter.MyViewHolder> {
    private ArrayList<FacultyListModel.Faculty.Access.AccessItem> mAlAccessItems;
    private Activity mActivity;
    private AssignAccessModel.AccessList modelAssignAcess;
    private AccessItemListener mAccessItemListener;
    private int parentPosition;

    public FacultyAccessItemsAdapter(Activity mActivity, ArrayList<FacultyListModel.Faculty.Access.AccessItem> accessItems, AccessItemListener mAccessItemListener, int parentPosition) {
        this.mActivity = mActivity;
        this.mAlAccessItems = accessItems;
        this.mAccessItemListener=mAccessItemListener;
        this.parentPosition=parentPosition;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_access_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            setAccessData(position, holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAccessData(final int position, MyViewHolder holder) {
        holder.mCbAccessItems.setText("" + mAlAccessItems.get(position).getName());

        if(mAlAccessItems.get(position).getIsActive()==1)
        {
            holder.mCbAccessItems.setChecked(true);
        }else{
            holder.mCbAccessItems.setChecked(false);
        }

        holder.mCbAccessItems.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                {
                    mAccessItemListener.onAccessItemClick(position,parentPosition,1);
                }else{
                    mAccessItemListener.onAccessItemClick(position,parentPosition,0);
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return mAlAccessItems.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CheckBox mCbAccessItems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCbAccessItems = itemView.findViewById(R.id.cb_access_items);
        }
    }

    public interface AccessItemListener
    {
        void onAccessItemClick(int position, int parentPosition, int b);
    }
}

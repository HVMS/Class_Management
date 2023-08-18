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
import com.globalitians.employees.employee.model.ModelClassForEmployeeList;

import java.util.ArrayList;

//
public class EmployeeAccessItemsAdapter extends RecyclerView.Adapter<EmployeeAccessItemsAdapter.MyViewHolder> {
    private ArrayList<ModelClassForEmployeeList.Employee.AccessItem> mAlAccessItems;
    private Activity mActivity;
    private AssignAccessModel.AccessList modelAssignAcess;
    private AccessItemListener mAccessItemListener;
    private int parentPosition;

    public EmployeeAccessItemsAdapter(Activity mActivity, ArrayList<ModelClassForEmployeeList.Employee.AccessItem> accessItems, AccessItemListener mAccessItemListener, int parentPosition) {
        this.mActivity = mActivity;
        this.mAlAccessItems = accessItems;
        this.mAccessItemListener = mAccessItemListener;
        this.parentPosition = parentPosition;
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

        if (mAlAccessItems.get(position).getIsActive() == 1) {
            holder.mCbAccessItems.setChecked(true);
        } else {
            holder.mCbAccessItems.setChecked(false);
        }

        holder.mCbAccessItems.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mAccessItemListener.onAccessItemClick(position, parentPosition, 1);
                   /* if (position == 1) {
                        mAccessItemListener.onAccessItemClick(0, parentPosition, 1);
                        mAccessItemListener.onAccessItemClick(1, parentPosition, 1);
                    }

                    if (position == 2) {
                        mAccessItemListener.onAccessItemClick(0, parentPosition, 1);
                        mAccessItemListener.onAccessItemClick(1, parentPosition, 1);
                        mAccessItemListener.onAccessItemClick(2, parentPosition, 1);
                    }

                    if (position == 3) {
                        mAccessItemListener.onAccessItemClick(0, parentPosition, 1);
                        mAccessItemListener.onAccessItemClick(1, parentPosition, 1);
                        mAccessItemListener.onAccessItemClick(2, parentPosition, 1);
                        mAccessItemListener.onAccessItemClick(3, parentPosition, 1);
                    }*/

                } else {
                    mAccessItemListener.onAccessItemClick(position, parentPosition, 0);
                    /*if (position == 0) {
                        mAccessItemListener.onAccessItemClick(0, parentPosition, 0);
                        mAccessItemListener.onAccessItemClick(1, parentPosition, 0);
                        mAccessItemListener.onAccessItemClick(2, parentPosition, 0);
                        mAccessItemListener.onAccessItemClick(3, parentPosition, 0);
                    }
                    if (position == 1) {
                        mAccessItemListener.onAccessItemClick(1, parentPosition, 0);
                        mAccessItemListener.onAccessItemClick(2, parentPosition, 0);
                        mAccessItemListener.onAccessItemClick(3, parentPosition, 0);
                    }
                    if (position == 2) {
                        mAccessItemListener.onAccessItemClick(2, parentPosition, 0);
                        mAccessItemListener.onAccessItemClick(3, parentPosition, 0);
                    }

                    if (position == 3) {
                        mAccessItemListener.onAccessItemClick(3, parentPosition, 0);
                    }*/
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

    public interface AccessItemListener extends FacultyAccessItemsAdapter.AccessItemListener {
        void onAccessItemClick(int position, int parentPosition, int b);
    }
}

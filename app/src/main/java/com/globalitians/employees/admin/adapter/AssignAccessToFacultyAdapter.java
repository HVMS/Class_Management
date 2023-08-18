package com.globalitians.employees.admin.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.globalitians.employees.R;
import com.globalitians.employees.admin.model.AssignAccessModel;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.faculty.model.FacultyListModel;

import java.util.ArrayList;

//
public class AssignAccessToFacultyAdapter extends RecyclerView.Adapter<AssignAccessToFacultyAdapter.MyViewHolder> {
    private ArrayList<FacultyListModel.Faculty.Access> mAlAccessList;
    private Activity mActivity;
    private AssignAccessListener listenerAssignAccess;
    private EmployeeAccessItemsAdapter.AccessItemListener mAccessItemListener;
    private AssignAccessModel.AccessList modelAssignAcess;

    public AssignAccessToFacultyAdapter(Activity mActivity,
                                        ArrayList<FacultyListModel.Faculty.Access> mAlAccessList, AssignAccessListener listener
            , EmployeeAccessItemsAdapter.AccessItemListener mAccessItemListener) {
        this.mActivity = mActivity;
        this.mAlAccessList = mAlAccessList;
        this.listenerAssignAccess = listener;
        this.mAccessItemListener = mAccessItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_assign_access, parent, false);
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

    private void setAccessData(final int position, final MyViewHolder holder) {
        holder.mTvAccessName.setText("" + mAlAccessList.get(position).getAccessName());
        if (mAlAccessList.get(position).getAccessItems().get(0).getIsActive() == 1) {
            holder.mSwitchModuleAccess.setChecked(true);
            //holder.mSwitchModuleAccess.setThumbResource(R.drawable.circle_green);
        } else {
            holder.mSwitchModuleAccess.setChecked(false);
            //holder.mSwitchModuleAccess.setThumbResource(R.drawable.circle_red);
        }
        holder.mSwitchModuleAccess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //holder.mSwitchModuleAccess.setThumbResource(R.drawable.circle_green);
                    listenerAssignAccess.onAccessSelection(position, 1);
                } else {
                    //holder.mSwitchModuleAccess.setThumbResource(R.drawable.circle_red);
                    listenerAssignAccess.onAccessSelection(position, 0);
                }

            }
        });
        setAdapterForAccessItems(mAlAccessList.get(position).getAccessItems(), holder, position);
    }

    private void setAdapterForAccessItems(ArrayList<FacultyListModel.Faculty.Access.AccessItem> accessItems, MyViewHolder holder, int parentPosition) {
        FacultyAccessItemsAdapter mAdapterAccessItems = new FacultyAccessItemsAdapter(mActivity, accessItems, mAccessItemListener, parentPosition);
        holder.mRvAccessItems.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        holder.mRvAccessItems.setAdapter(mAdapterAccessItems);
    }


    @Override
    public int getItemCount() {
        return mAlAccessList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView mRvAccessItems;
        private Switch mSwitchModuleAccess;
        private CustomTextView mTvAccessName;

        private LinearLayout linMainContainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            linMainContainer = itemView.findViewById(R.id.linMainContainer);

            mTvAccessName = itemView.findViewById(R.id.tv_access_module_name);
            mSwitchModuleAccess = itemView.findViewById(R.id.switch_module_access);
            mRvAccessItems = itemView.findViewById(R.id.rv_access_items);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerAssignAccess.onAccessSelection(getAdapterPosition());
                }
            });*/

        }
    }

    public interface AssignAccessListener {
        void onAccessSelection(int position, int selectedValue);
    }
}

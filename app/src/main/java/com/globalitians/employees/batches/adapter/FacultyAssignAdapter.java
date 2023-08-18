package com.globalitians.employees.batches.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.faculty.model.FacultyListModel;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class FacultyAssignAdapter extends RecyclerView.Adapter<FacultyAssignAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<FacultyListModel.Faculty> mAlfaculty;
    private FacultyListModel.Faculty facultyListModel;

    private OnSwitchClickEvent onSwitchClickEvent;

    public FacultyAssignAdapter(Activity activity, ArrayList<FacultyListModel.Faculty> mAlfaculty,OnSwitchClickEvent onSwitchClickEvent) {
        this.activity = activity;
        this.mAlfaculty = mAlfaculty;
        this.onSwitchClickEvent = onSwitchClickEvent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assign_faculty_layout, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        try{
            facultyListModel = mAlfaculty.get(position);
            setFacultyData(position,holder,facultyListModel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setFacultyData(final int position, final MyViewHolder holder, FacultyListModel.Faculty facultyListModel) {
        holder.mTxtfacultyname.setText(""+facultyListModel.getName());
        if(!CommonUtil.isNullString(""+facultyListModel.getImage())){
            CommonUtil.setCircularImageForUser(activity,holder.circleImageView,""+facultyListModel.getImage());
        }else{
            holder.circleImageView.setImageResource(R.drawable.ic_user_round);
        }

        holder.switchCompat.setChecked(mAlfaculty.get(position).isSelected());
        holder.switchCompat.setTag(position);
        holder.switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.switchCompat.getTag();

                /*Toast.makeText(activity, "Position clicked is : "+mAlfaculty.get(pos).getName(), Toast.LENGTH_SHORT).show();*/

                if(mAlfaculty.get(pos).isSelected()){
                    mAlfaculty.get(pos).setSelected(false);
                }else{
                    mAlfaculty.get(pos).setSelected(true);
                }

                onSwitchClickEvent.onClickSwitch(position,mAlfaculty.get(position).isSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlfaculty.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CustomTextView mTxtfacultyname;
        private CircularImageView circleImageView;
        private SwitchCompat switchCompat;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxtfacultyname = itemView.findViewById(R.id.tv_faculty_name);
            circleImageView = itemView.findViewById(R.id.iv_faculty);
            switchCompat = itemView.findViewById(R.id.onoffassign);
        }
    }

    public interface OnSwitchClickEvent{
        void onClickSwitch(int position, boolean value);
    }
}

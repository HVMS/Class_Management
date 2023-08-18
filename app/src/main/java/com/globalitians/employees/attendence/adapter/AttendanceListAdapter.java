package com.globalitians.employees.attendence.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.attendence.model.ModelAttendanceList;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.MyViewHolder> {
    private ArrayList<ModelAttendanceList.Attendence> mArrListAttendance;
    private Activity mActivity;
    private AttendanceClickListener mAttendanceClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView ivUser;
        private TextView tvStudentName;
        private TextView tvOut;
        private TextView tvCourseName;
        private TextView tvInTime;
        private TextView tvDate;
        private TextView tvOutTime;
        private TextView tvMakeOut;
        private TextView tvAttendanceStatus;
        private ImageView ivNav;
        private ImageView ivCall;
        private RelativeLayout linMainContainer;
        private LinearLayout linOutRequired;

        public MyViewHolder(View view) {
            super(view);
            linMainContainer = view.findViewById(R.id.linMainContainer);
            ivNav=(ImageView) view.findViewById(R.id.iv_nav);
            ivUser = (CircularImageView) view.findViewById(R.id.iv_user);
            //tvAttendanceStatus=view.findViewById(R.id.tv_attendance_status);
            ivCall = view.findViewById(R.id.iv_call);
            tvStudentName = (TextView) view.findViewById(R.id.tv_student_name);
            tvOut = (TextView) view.findViewById(R.id.tv_out);
            tvInTime = (TextView) view.findViewById(R.id.tv_intime);
            tvDate= (TextView) view.findViewById(R.id.tv_date);
            tvOutTime = (TextView) view.findViewById(R.id.tv_outtime);
            tvCourseName= (TextView) view.findViewById(R.id.tv_course_name);
            linOutRequired=view.findViewById(R.id.linOutRequired);
            tvMakeOut= (TextView) view.findViewById(R.id.tv_make_out);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAttendanceClickListener.onAttendanceClick(getAdapterPosition());
                }
            });
        }
    }

    public AttendanceListAdapter(Activity activity, ArrayList<ModelAttendanceList.Attendence> listStudentOptions, AttendanceClickListener studentAttendanceClickListener) {
        this.mActivity = activity;
        this.mArrListAttendance = listStudentOptions;
        this.mAttendanceClickListener = studentAttendanceClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_attendancelist, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            ModelAttendanceList.Attendence modelStudentAttendance = mArrListAttendance.get(position);
            Log.e(">>", "" + mArrListAttendance.get(position).getStatus());

            holder.tvDate.setText(""+modelStudentAttendance.getDate());

            if(!CommonUtil.isNullString(""+modelStudentAttendance.getIn_time())){
                if(modelStudentAttendance.getIn_time().contains(" ")){
                    String arrInTime[]=modelStudentAttendance.getIn_time().split(" ");
                    holder.tvInTime.setText("" +arrInTime[1]);
                    //holder.tvAttendanceStatus.setVisibility(View.VISIBLE);
                }else{
                    //holder.tvAttendanceStatus.setVisibility(View.GONE);
                }
            }

            if(!CommonUtil.isNullString(""+modelStudentAttendance.getOut_time())){
                if(modelStudentAttendance.getOut_time().contains(" ")){
                    String arrOutTime[]=modelStudentAttendance.getOut_time().split(" ");
                    holder.tvOutTime.setText("" + arrOutTime[1]);
                }
                holder.linOutRequired.setVisibility(View.VISIBLE);
                holder.tvMakeOut.setVisibility(View.GONE);
            }else{
                holder.tvMakeOut.setVisibility(View.VISIBLE);
                holder.linOutRequired.setVisibility(View.GONE);
                //holder.tvOutTime.setText("Out Required");
            }

            holder.tvStudentName.setText("" + modelStudentAttendance.getStudentName());
            CommonUtil.setImageToGlide(mActivity, holder.ivUser, "" + modelStudentAttendance.getStudentImage());

            if (modelStudentAttendance.getStatus().equalsIgnoreCase("out")) {
               // holder.tvOut.setVisibility(View.GONE);
                //holder.tvOut.setTextColor(Color.WHITE);
            } else {
                holder.tvOut.setVisibility(View.VISIBLE);
            }

            if(position>2)
            {
                holder.tvCourseName.setText("Android App Development");
            }else{
                holder.tvCourseName.setText("Core Java");
            }


            holder.tvMakeOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAttendanceClickListener.onOutClick(position);
                }
            });
            holder.linMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAttendanceClickListener.onAttendanceClick(position);
                }
            });
            holder.ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAttendanceClickListener.onCallClick(position);
                }
            });
            holder.ivNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAttendanceClickListener.onAttendanceClick(position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mArrListAttendance.size();
    }

    public interface AttendanceClickListener {
        void onAttendanceClick(int position);

        void onAttendanceDeleteClick(int position);

        void onCallClick(int position);

        void onOutClick(int position);
    }
}

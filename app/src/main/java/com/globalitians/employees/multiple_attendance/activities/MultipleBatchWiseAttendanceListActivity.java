package com.globalitians.employees.multiple_attendance.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.multiple_attendance.adapter.MultipleAttendanceListBatchWiseAdapter;
import com.globalitians.employees.multiple_attendance.adapter.MultipleStudentsBatchListAdapter;
import com.globalitians.employees.multiple_attendance.model.MultipleAttendanceListModelClass;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.globalitians.employees.utility.Constants.CODE_BATCH_WISE_ATTENDANCE_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class MultipleBatchWiseAttendanceListActivity extends AppCompatActivity
    implements OkHttpInterface,
        MultipleAttendanceListBatchWiseAdapter.OnAttendanceListClickListner {

    private RecyclerView mRvattendance;
    private SwipeRefreshLayout mSRattendancelist;

    private CustomTextView mTxttitledate;
    private CustomTextView mTxtcurrentdate;
    private CustomTextView mTxtnoattendance;

    private String mStrtodaydate="";
    private MaterialCalendarView materialCalendarView;

    private MultipleAttendanceListBatchWiseAdapter attendanceListBatchWiseAdapter;
    private ArrayList<MultipleAttendanceListModelClass.Batch> mAlattendanceList;
    private MultipleAttendanceListModelClass attendanceListModelClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFullScreenMode(MultipleBatchWiseAttendanceListActivity.this);
        setContentView(R.layout.activity_multiple_batch_wise_attendance_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance List");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlackLight));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBlackLight), PorterDuff.Mode.SRC_ATOP);
        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorBlackLight), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        callApiToLoadAttendaceListBatchWise();
    }

    private void callApiToLoadAttendaceListBatchWise() {

        if(!CommonUtil.isInternetAvailable(MultipleBatchWiseAttendanceListActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleBatchWiseAttendanceListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_WISE_ATTENDANCE_LIST,
                RequestParam.multiplegetAttendanceListBatchWise
                        (""+CommonUtil.getSharedPrefrencesInstance(
                                MultipleBatchWiseAttendanceListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                        ""+mTxtcurrentdate.getText().toString()),
                RequestParam.getNull(),
                CODE_BATCH_WISE_ATTENDANCE_LIST,
                false,this);
    }

    private void init() {
        mAlattendanceList = new ArrayList<>();
    }

    private void findViews() {
        mRvattendance = findViewById(R.id.rv_attendance_batch_wise_list);
        mTxttitledate = findViewById(R.id.tv_current_date);
        mTxtcurrentdate = findViewById(R.id.tv_current_actual_date);
        mTxtnoattendance = findViewById(R.id.tv_no_attendance);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        mTxtcurrentdate.setText(""+date);

        materialCalendarView = findViewById(R.id.material_date_picker);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.WEDNESDAY)
                .setMinimumDate(CalendarDay.from(1999, 5, 16))
                .setMaximumDate(CalendarDay.from(2020,12,31))
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        materialCalendarView.setDateSelected(Calendar.getInstance().getTime(),true);
        materialCalendarView.setTopbarVisible(false);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                if ((date.getMonth() + 1) < 10 && date.getDay() < 10) {
                    mStrtodaydate = ""+"0" + date.getDay() + "-0" + (date.getMonth() + 1) + "-" + date.getYear();
                } else if ((date.getMonth() + 1) < 10) {
                    mStrtodaydate = ""+"" + date.getDay() + "-0" + (date.getMonth() + 1) + "-" + date.getYear();
                } else if (date.getDay() < 10) {
                    mStrtodaydate = ""+"0" + date.getDay() + "" + (date.getMonth() + 1) + "-" + date.getYear();
                } else {
                    mStrtodaydate = ""+"" + date.getDay() + "-" + (date.getMonth() + 1) + "-" + date.getYear();
                }

                mTxtcurrentdate.setText(""+mStrtodaydate);
                callApiToLoadAttendaceListBatchWise();
            }
        });

        mSRattendancelist = findViewById(R.id.swipeToRefreshAttendanceList);
        mSRattendancelist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(mAlattendanceList.size()>0 && mAlattendanceList!=null){
                    mAlattendanceList.clear();
                }

                if(attendanceListBatchWiseAdapter!=null){
                    attendanceListBatchWiseAdapter.notifyDataSetChanged();
                }

                mAlattendanceList = new ArrayList<>();
                callApiToLoadAttendaceListBatchWise();

                if(mSRattendancelist.isRefreshing() && mSRattendancelist!=null){
                    mSRattendancelist.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if(response==null){
            return;
        }

        switch (requestId){
            case CODE_BATCH_WISE_ATTENDANCE_LIST:
                Log.e("attendance list",""+response);
                final Gson gson = new Gson();
                try{
                    mAlattendanceList = new ArrayList<>();
                    attendanceListModelClass = gson.fromJson(response, MultipleAttendanceListModelClass.class);
                    if(attendanceListModelClass.getBatches()!=null && attendanceListModelClass.getBatches().size()>0){

                        for(int i = 0 ; i < attendanceListModelClass.getBatches().size() ; i++){
                            if(attendanceListModelClass.getBatches().get(i).getIsAttandenceTaken()==true){
                                mAlattendanceList.add(attendanceListModelClass.getBatches().get(i));
                            }
                        }

                        if(mAlattendanceList.size()>0 && mAlattendanceList!=null){
                            mTxtnoattendance.setVisibility(View.GONE);
                            mRvattendance.setVisibility(View.VISIBLE);
                            setAttendanceListAdapter(mAlattendanceList);
                        }else if(mAlattendanceList==null || mAlattendanceList.size()==0){
                            mTxtnoattendance.setVisibility(View.VISIBLE);
                            mRvattendance.setVisibility(View.GONE);
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setAttendanceListAdapter(ArrayList<MultipleAttendanceListModelClass.Batch> mAlattendanceList) {
        attendanceListBatchWiseAdapter =
                new MultipleAttendanceListBatchWiseAdapter(MultipleBatchWiseAttendanceListActivity.this,mAlattendanceList,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRvattendance.setLayoutManager(linearLayoutManager);
        mRvattendance.setAdapter(attendanceListBatchWiseAdapter);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onAttendanceItenClick(int position) {
        Intent intent = new Intent(MultipleBatchWiseAttendanceListActivity.this,MultipleAttendanceDetailsActivity.class);
        intent.putExtra("attendance_id",""+mAlattendanceList.get(position).getId());
        intent.putExtra("attendance_list_item_name",""+mAlattendanceList.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if(id == R.id.students){
            startActivity(new Intent(MultipleBatchWiseAttendanceListActivity.this,MultipleStudentsListDateWiseActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.multiple_attendance_options, menu);

        MenuItem batch = menu.findItem(R.id.batch);
        batch.setVisible(true);

        MenuItem students = menu.findItem(R.id.students);
        students.setVisible(true);

        /*MenuItem faculties = menu.findItem(R.id.Faculty);
        faculties.setVisible(true);

        MenuItem employees = menu.findItem(R.id.Employee);
        employees.setVisible(true);*/

        return true;
    }
}

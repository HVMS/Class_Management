package com.globalitians.employees.multiple_attendance.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.multiple_attendance.adapter.MultipleStudentsBatchListAdapter;
import com.globalitians.employees.multiple_attendance.model.MultipleMonthWiseAttendanceReportModelClass;
import com.globalitians.employees.multiple_attendance.model.MultipleStudentsBatchListModelClass;
import com.globalitians.employees.utility.CircularImageView;
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
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS_ATTENDANCE_REPORT;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS_BATCHES;

public class MultipleBatchWiseAttedanceReportActivity extends AppCompatActivity
    implements OkHttpInterface,
        MultipleStudentsBatchListAdapter.OnBatchClickEvent {

    // all layout object stuff
    private RecyclerView mRVbatchlistofstudents;
    private MultipleStudentsBatchListAdapter studentsBatchListAdapter;

    private MultipleStudentsBatchListModelClass studentsBatchListModelClass;
    private ArrayList<MultipleStudentsBatchListModelClass.BatchDetail.Student> mAlbatchlistofstudents = new ArrayList<>();

    private MultipleMonthWiseAttendanceReportModelClass monthWiseAttendanceReportModelClass;
    private ArrayList<MultipleMonthWiseAttendanceReportModelClass.Attendence> mAlattendancelist;

    private CircularImageView mIvstudentimage;
    private CustomTextView mTxtstudentname;
    private String mStrstudentId="";

    private CustomTextView mTxtnoofpresent;
    private CustomTextView mTxtnoofabsent;
    private CustomTextView mTxtattendanceratio;
    private CustomTextView mTxtstudentintime;
    private MaterialCalendarView materialCalendarView;
    private String mStrselectedmonth="";
    private String mStrselectedyear="";
    private float ratio=0;
    private String mStrSelecteddate="";
    private String mStrSelectedBatchId="";
    private String mStrFirstBatchId="";
    private int count=0;
    private boolean yes=false;

    // store all dates which are coming from response
    private ArrayList<CalendarDay> mAlalldates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(MultipleBatchWiseAttedanceReportActivity.this);
        setContentView(R.layout.multiple_activity_batch_wise_attedance_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlackLight));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBlackLight), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        getStudentId();
    }

    private void getStudentId() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrstudentId = ""+intent.getStringExtra("id_of_student");
            if(!CommonUtil.isNullString(""+mStrstudentId)){

                callApiToLoadStudentsBaches(mStrstudentId);

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                if((month+1)<10){
                    mStrselectedmonth = ""+"0"+(month+1);
                }else{
                    mStrselectedmonth = ""+(month+1);
                }
                mStrselectedyear = ""+year;
            }
        }
    }

    private void callApiToLoadStudentsBaches(String mStrstudentId) {
        if(!CommonUtil.isInternetAvailable(MultipleBatchWiseAttedanceReportActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleBatchWiseAttedanceReportActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENTS_BATCHES,
                RequestParam.multiplegetStudentsBatches(""+mStrstudentId),
                RequestParam.getNull(),
                CODE_STUDENTS_BATCHES,
                false,this);

    }

    private void init() {
        mAlbatchlistofstudents = new ArrayList<>();
        mAlattendancelist = new ArrayList<>();
        mAlalldates = new ArrayList<>();
    }

    private void findViews() {
        mRVbatchlistofstudents = findViewById(R.id.rv_batch_list);

        mIvstudentimage = findViewById(R.id.iv_student);
        mTxtstudentname = findViewById(R.id.tv_student_name);
        mTxtstudentintime = findViewById(R.id.tv_student_in_time);

        mTxtnoofpresent = findViewById(R.id.tv_no_of_present);
        mTxtnoofabsent = findViewById(R.id.tv_no_of_absent);
        mTxtattendanceratio = findViewById(R.id.tv_ratio);

        materialCalendarView = findViewById(R.id.material_date_picker);

        setMaterialCalendarViewStuff();
    }

    private void setMaterialCalendarViewStuff() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.WEDNESDAY)
                .setMinimumDate(CalendarDay.from(1999, 5, 16))
                .setMaximumDate(CalendarDay.from(2020,12,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        if ((month + 1) < 10 && day < 10) {
            mStrSelecteddate = ""+"0" + day + "-0" + (month + 1) + "-" + year;
        } else if ((month + 1) < 10) {
            mStrSelecteddate = ""+"" + day + "-0" + (month + 1) + "-" + year;
        } else if (day < 10) {
            mStrSelecteddate = ""+"0" + day + "" + (month + 1) + "-" + year;
        } else {
            mStrSelecteddate = ""+"" + day + "-" + (month + 1) + "-" + year;
        }

        materialCalendarView.setDateSelected(Calendar.getInstance().getTime(),true);

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                if((date.getMonth()+1)<10){
                    mStrselectedmonth = ""+"0"+(date.getMonth()+1);
                }else{
                    mStrselectedmonth = ""+(date.getMonth()+1);
                }

                mStrselectedyear = ""+date.getYear();

                if(!mStrFirstBatchId.equalsIgnoreCase(""+mStrSelectedBatchId)){
                    callApiToLoadMonthWiseDataForSelectedBatch(mStrstudentId,mStrselectedmonth,mStrselectedyear,mStrSelectedBatchId);
                }else{
                    callApiToLoadMonthWiseData(mStrstudentId,mStrselectedmonth,mStrselectedyear,mStrFirstBatchId);
                }
            }
        });

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                if ((date.getMonth() + 1) < 10 && date.getDay() < 10) {
                    mStrSelecteddate = ""+"0" + date.getDay() + "-0" + (date.getMonth() + 1) + "-" + date.getYear();
                } else if ((date.getMonth() + 1) < 10) {
                    mStrSelecteddate = ""+"" + date.getDay() + "-0" + (date.getMonth() + 1) + "-" + date.getYear();
                } else if (date.getDay() < 10) {
                    mStrSelecteddate = ""+"0" + date.getDay() + "" + (date.getMonth() + 1) + "-" + date.getYear();
                } else {
                    mStrSelecteddate = ""+"" + date.getDay() + "-" + (date.getMonth() + 1) + "-" + date.getYear();
                }

                if((date.getMonth()+1)<10){
                    mStrselectedmonth = ""+"0"+(date.getMonth()+1);
                }else{
                    mStrselectedmonth = ""+(date.getMonth()+1);
                }
                mStrselectedyear = ""+(date.getYear());

                callApiToLoadMonthWiseDataForSelectedBatch(mStrstudentId,mStrselectedmonth,mStrselectedyear,mStrSelectedBatchId);
            }
        });

    }

    private void callApiToLoadMonthWiseDataForSelectedBatch(String mStrstudentId, String mStrselectedmonth, String mStrselectedyear, String mStrSelectedBatchId) {
        if(!CommonUtil.isInternetAvailable(MultipleBatchWiseAttedanceReportActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleBatchWiseAttedanceReportActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENTS_ATTENDANCE_REPORT,
                RequestParam.multiplegetMonthReport(""+mStrstudentId,
                        ""+mStrselectedmonth,
                        ""+mStrselectedyear,
                        ""+mStrSelectedBatchId),
                RequestParam.getNull(),
                CODE_STUDENTS_ATTENDANCE_REPORT,
                false,this);
    }

    private void callApiToLoadMonthWiseData(String mStrstudentId, String mStrselectedmonth, String mStrselectedyear,String mStrFirstBatchId) {

        if(!CommonUtil.isInternetAvailable(MultipleBatchWiseAttedanceReportActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleBatchWiseAttedanceReportActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENTS_ATTENDANCE_REPORT,
                RequestParam.multiplegetMonthReport(""+mStrstudentId,
                                              ""+mStrselectedmonth,
                                                ""+mStrselectedyear,
                                              ""+mStrFirstBatchId),
                RequestParam.getNull(),
                CODE_STUDENTS_ATTENDANCE_REPORT,
                false,this);

    }

    private void setBatchAdapter(ArrayList<MultipleStudentsBatchListModelClass.BatchDetail.Student> mAlbatchlistofstudents) {
        studentsBatchListAdapter = new MultipleStudentsBatchListAdapter(MultipleBatchWiseAttedanceReportActivity.this, mAlbatchlistofstudents,this);
        GridLayoutManager manager1 = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        mRVbatchlistofstudents.setLayoutManager(manager1);
        mRVbatchlistofstudents.setAdapter(studentsBatchListAdapter);
        studentsBatchListAdapter.notifyDataSetChanged();

        mStrFirstBatchId = ""+mAlbatchlistofstudents.get(0).getId();
        callApiToLoadMonthWiseData(mStrstudentId,mStrselectedmonth,mStrselectedyear,mStrFirstBatchId);
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
            case CODE_STUDENTS_BATCHES:
                Log.e("students batches",""+response);
                final Gson gson = new Gson();
                try{
                    mAlbatchlistofstudents = new ArrayList<>();
                    studentsBatchListModelClass = gson.fromJson(response,MultipleStudentsBatchListModelClass.class);
                    if(studentsBatchListModelClass.getBatchDetail().getStudents()!=null
                            && studentsBatchListModelClass.getBatchDetail().getStudents().size()>0){

                        for(int i = 0 ; i < studentsBatchListModelClass.getBatchDetail().getStudents().size() ; i++){
                            if(studentsBatchListModelClass.getBatchDetail().getStudents().get(i).getStatus().equalsIgnoreCase("Running")){
                                mAlbatchlistofstudents.add(studentsBatchListModelClass.getBatchDetail().getStudents().get(i));
                            }
                        }

                        // add last element as all
                        MultipleStudentsBatchListModelClass.BatchDetail.Student student = new MultipleStudentsBatchListModelClass.BatchDetail.Student();
                        student.setId(56);
                        student.setAlias("ALL ALIAS");
                        student.setName("ALL");
                        student.setStatus("Running");
                        mAlbatchlistofstudents.add(student);

                        setImageAndNameOfStudent(studentsBatchListModelClass);

                        setBatchAdapter(mAlbatchlistofstudents);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_STUDENTS_ATTENDANCE_REPORT:
                Log.e("student report",""+response);
                final Gson attendancemonth = new Gson();
                try{
                    monthWiseAttendanceReportModelClass = attendancemonth.fromJson(response,MultipleMonthWiseAttendanceReportModelClass.class);
                        if(monthWiseAttendanceReportModelClass.getAttendences().size()>0
                                && monthWiseAttendanceReportModelClass.getAttendences()!=null){
                            mAlattendancelist = monthWiseAttendanceReportModelClass.getAttendences();
                            if(mAlattendancelist!=null && mAlattendancelist.size()>0){

                                for(int i = 0 ; i < mAlattendancelist.size()-1 ; i++){
                                    mAlalldates.add(callFirstStringToDate(mAlattendancelist.get(i).getDate()));
                                }

                                /*Toast.makeText(this, ""+mAlalldates.get(0).getMonth(), Toast.LENGTH_SHORT).show();*/

                                setTotalAbsentAndPresentAndRatio(mAlattendancelist);
                                matchDateAndSetInTime(mAlattendancelist);
                            }
                        }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private CalendarDay callFirstStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY");
        Calendar calendar = Calendar.getInstance();
        try {
            Date myDate = simpleDateFormat.parse(date);
            calendar.setTime(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return CalendarDay.from(calendar);
    }

    private void matchDateAndSetInTime(ArrayList<MultipleMonthWiseAttendanceReportModelClass.Attendence> mAlattendancelist) {

        for(int i = 0 ; i < mAlattendancelist.size() ; i++){
            if(mStrSelecteddate.equalsIgnoreCase(""+mAlattendancelist.get(i).getDate())
                && mStrSelectedBatchId.equalsIgnoreCase(""+mAlattendancelist.get(i).getBatchId())
                && mAlattendancelist.get(i).getStatus().equalsIgnoreCase("P")) {
                mTxtstudentintime.setText("" + mAlattendancelist.get(i).getIn());
                break;
            }else if(mStrSelecteddate.equalsIgnoreCase(""+mAlattendancelist.get(i).getDate())
                && mStrSelectedBatchId.equalsIgnoreCase(""+mAlattendancelist.get(i).getBatchId())
                && mAlattendancelist.get(i).getStatus().equalsIgnoreCase("A")){
                mTxtstudentintime.setText("Absent");
                break;
            }else{
                mTxtstudentintime.setText("Not Taken");
            }
        }
    }

    private void setTotalAbsentAndPresentAndRatio(ArrayList<MultipleMonthWiseAttendanceReportModelClass.Attendence> mAlattendancelist) {
        int lastPosition = mAlattendancelist.size()-1;

        float totalPresent = mAlattendancelist.get(lastPosition).getTotalPresent();
        float totalAbsent = mAlattendancelist.get(lastPosition).getTotalAbsent();

        if(totalAbsent==0 && totalPresent!=0){
            ratio = 100;
            mTxtattendanceratio.setText(""+ratio+" %");
        }else if(totalPresent==0 && totalAbsent!=0){
            ratio = 0;
            mTxtattendanceratio.setText(""+ratio+" %");
        }else if(totalAbsent==0 && totalPresent==0) {
            ratio = 0;
            mTxtattendanceratio.setText(""+ratio+" %");
        }else {
            ratio = ratio + ( ( (totalPresent) / (totalAbsent + totalPresent) ) * 100 );
            String formattedString = String.format("%.02f",ratio);
            mTxtattendanceratio.setText(""+formattedString+" %");
        }
        mTxtnoofpresent.setText(""+mAlattendancelist.get(lastPosition).getTotalPresent());
        mTxtnoofabsent.setText(""+mAlattendancelist.get(lastPosition).getTotalAbsent());

        ratio = 0;
    }

    private void setImageAndNameOfStudent(MultipleStudentsBatchListModelClass studentsBatchListModelClass) {
        mTxtstudentname.setText(""+studentsBatchListModelClass.getBatchDetail().getFname()+" "+studentsBatchListModelClass.getBatchDetail().getLname());
        if(!CommonUtil.isNullString(""+studentsBatchListModelClass.getBatchDetail().getImg())){
            CommonUtil.setCircularImageForUser(MultipleBatchWiseAttedanceReportActivity.this,mIvstudentimage,""+studentsBatchListModelClass.getBatchDetail().getImg());
        }else{
            mIvstudentimage.setImageResource(R.drawable.ic_user_round);
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onBatchClick(int position) {

        mStrSelectedBatchId = ""+mAlbatchlistofstudents.get(position).getId();

        callApiToLoadMonthWiseDataForSelectedBatch(mStrstudentId,mStrselectedmonth,mStrselectedyear,mStrSelectedBatchId);
        String batchName = ""+mAlbatchlistofstudents.get(position).getName();

        if(batchName.equalsIgnoreCase("ALL")){
            Intent intent = new Intent(MultipleBatchWiseAttedanceReportActivity.this,MultiplePieChartActivity.class);
            intent.putExtra("student_id",""+mStrstudentId);
            startActivity(intent);
        }

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
        return super.onOptionsItemSelected(item);
    }

}

package com.globalitians.employees.multiple_attendance.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.globalitians.employees.R;
import com.globalitians.employees.multiple_attendance.adapter.MultipleAttendanceDetailsBatchWiseAdapter;
import com.globalitians.employees.multiple_attendance.model.MultipleAttendanceDetailsModelClass;
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

import androidx.cardview.widget.CardView;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_WISE_ATTENDANCE_DETAILS;

public class MultipleAttendanceDetailsActivity extends AppCompatActivity
    implements OkHttpInterface,
        MultipleAttendanceDetailsBatchWiseAdapter.onStudentItemClickEvent {

    private MultipleAttendanceDetailsModelClass attendanceDetailsModelClass;
    private MaterialCalendarView materialCalendarView;
    private CardView cardView;
    private RecyclerView mRvstudentattendance;
    private ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlstudentlist;
    private MultipleAttendanceDetailsBatchWiseAdapter attendanceDetailsBatchWiseAdapter;
    private String mStrAttendanceId="";
    private String mStrdate="";
    private String mStrAttendanceBatchName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(MultipleAttendanceDetailsActivity.this);
        setContentView(R.layout.activity_multiple_attendance_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlackLight));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBlackLight), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        getAttendanceIdForDetails();
    }

    private void init() {
        mAlstudentlist = new ArrayList<>();
    }

    private void getAttendanceIdForDetails() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrAttendanceId = ""+intent.getStringExtra("attendance_id");
            mStrAttendanceBatchName = ""+intent.getStringExtra("attendance_list_item_name");
            getSupportActionBar().setTitle(""+mStrAttendanceBatchName+" Attendance");
            if(!CommonUtil.isNullString(""+mStrAttendanceId)){
                mStrdate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                callApiToLoadAttendanceDetails(mStrAttendanceId,mStrdate);
            }
        }
    }

    private void callApiToLoadAttendanceDetails(String mStrAttendanceId, String mStrdate) {

        if(!CommonUtil.isInternetAvailable(MultipleAttendanceDetailsActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleAttendanceDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_WISE_ATTENDANCE_DETAILS,
                RequestParam.multiplegetAttendanceDetails(""+mStrAttendanceId,
                                                    ""+mStrdate),
                RequestParam.getNull(),
                CODE_BATCH_WISE_ATTENDANCE_DETAILS,
                false,this);
    }

    private void findViews() {
        mRvstudentattendance = findViewById(R.id.rv_batch_wise_students);
        materialCalendarView = findViewById(R.id.material_datepicker);
        cardView = findViewById(R.id.cardview_material_datepicker);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.WEDNESDAY)
                .setMinimumDate(CalendarDay.from(1999, 5, 16))
                .setMaximumDate(CalendarDay.from(year,month,day))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.setDateSelected(Calendar.getInstance().getTime(),true);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                if ((date.getMonth() + 1) < 10 && date.getDay() < 10) {
                    mStrdate = ""+"0" + date.getDay() + "-0" + (date.getMonth() + 1) + "-" + date.getYear();
                } else if ((date.getMonth() + 1) < 10) {
                    mStrdate = ""+"" + date.getDay() + "-0" + (date.getMonth() + 1) + "-" + date.getYear();
                } else if (date.getDay() < 10) {
                    mStrdate= ""+"0" + date.getDay() + "" + (date.getMonth() + 1) + "-" + date.getYear();
                } else {
                    mStrdate= ""+"" + date.getDay() + "-" + (date.getMonth() + 1) + "-" + date.getYear();
                }

                callApiToLoadAttendanceDetails(mStrAttendanceId,mStrdate);
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
            case CODE_BATCH_WISE_ATTENDANCE_DETAILS:
                Log.e("attendance details",""+response);
                final Gson gson = new Gson();
                try{
                    attendanceDetailsModelClass = gson.fromJson(response,MultipleAttendanceDetailsModelClass.class);
                    if(attendanceDetailsModelClass.getBatchDetail().getStudents()!=null
                      && attendanceDetailsModelClass.getBatchDetail().getStudents().size()>0){
                        mAlstudentlist = attendanceDetailsModelClass.getBatchDetail().getStudents();
                        setStudentAdapter(mAlstudentlist);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setStudentAdapter(ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlstudentlist) {
        attendanceDetailsBatchWiseAdapter =
                new MultipleAttendanceDetailsBatchWiseAdapter(MultipleAttendanceDetailsActivity.this,mAlstudentlist,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRvstudentattendance.setLayoutManager(linearLayoutManager);
        mRvstudentattendance.setAdapter(attendanceDetailsBatchWiseAdapter);
        attendanceDetailsBatchWiseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onStudentClick(int position) {
        Intent intent = new Intent(MultipleAttendanceDetailsActivity.this,MultipleBatchWiseAttedanceReportActivity.class);
        intent.putExtra("id_of_student",""+mAlstudentlist.get(position).getId());
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multiple_menu_search_black, menu);
        return true;
    }
}

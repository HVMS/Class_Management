package com.globalitians.employees.multiple_attendance.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.multiple_attendance.adapter.MultipleDateWiseStudentListAdapter;
import com.globalitians.employees.multiple_attendance.model.MultipleDatewisestudentsattendancemodel;
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

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ALL_STUDENTS_ATTENDANCE_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class MultipleStudentsListDateWiseActivity extends AppCompatActivity
    implements OkHttpInterface,
        MultipleDateWiseStudentListAdapter.OnDateWiseStudentClick {

    private RecyclerView mRvattendance;
    private SwipeRefreshLayout mSRattendancelist;

    private CustomTextView mTxttitledate;
    private CustomTextView mTxtcurrentdate;
    private CustomTextView mTxtnoattendance;

    private String mStrtodaydate="";
    private MaterialCalendarView materialCalendarView;

    private MultipleDateWiseStudentListAdapter dateWiseStudentListAdapter;
    private MultipleDatewisestudentsattendancemodel datewisestudentsattendancemodel;
    private ArrayList<MultipleDatewisestudentsattendancemodel.StudentAttendence> mAlstudentslist;

    private SearchView mSearchView;
    private String mStrSearchValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(MultipleStudentsListDateWiseActivity.this);
        setContentView(R.layout.activity_multiple_students_list_date_wise);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Students Attendance");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlackLight));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBlackLight), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        callApiToLoadStudentsDateWise();
    }

    private void callApiToLoadStudentsDateWise() {
        if(!CommonUtil.isInternetAvailable(MultipleStudentsListDateWiseActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleStudentsListDateWiseActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ALL_STUDENTS_ATTENDANCE_LIST,
                RequestParam.multiplegetAllStudentsAttendanceDateWise(""+CommonUtil.getSharedPrefrencesInstance(
                        MultipleStudentsListDateWiseActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                        ""+mTxtcurrentdate.getText().toString()),
                RequestParam.getNull(),
                CODE_ALL_STUDENTS_ATTENDANCE_LIST,
                false,this);
    }

    private void init() {
        mAlstudentslist = new ArrayList<>();
    }

    private void findViews() {
        mRvattendance = findViewById(R.id.rv_attendance_students_date_wise);
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
                callApiToLoadStudentsDateWise();
            }
        });

        mSRattendancelist = findViewById(R.id.swipeToRefreshAttendanceList);
        mSRattendancelist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshData();
            }
        });
    }

    private void refreshData() {
        if(mAlstudentslist.size()>0 && mAlstudentslist!=null){
            mAlstudentslist.clear();
        }

        if(dateWiseStudentListAdapter!=null){
            dateWiseStudentListAdapter.notifyDataSetChanged();
        }

        mAlstudentslist = new ArrayList<>();

        if(!CommonUtil.isNullString(""+mStrSearchValue)){
            callApiToSearchStudent(mStrSearchValue);
        }else{
            callApiToLoadStudentsDateWise();
        }

        if(mSRattendancelist.isRefreshing() && mSRattendancelist!=null){
            mSRattendancelist.setRefreshing(false);
        }

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
            case CODE_ALL_STUDENTS_ATTENDANCE_LIST:
                Log.e("all students",""+response);
                final Gson gson = new Gson();
                try{
                    mAlstudentslist = new ArrayList<>();
                    datewisestudentsattendancemodel = gson.fromJson(response,MultipleDatewisestudentsattendancemodel.class);

                    if(datewisestudentsattendancemodel.getStatus().equalsIgnoreCase("Success")){

                        if(datewisestudentsattendancemodel.getStudentAttendences().size()>0 &&
                           datewisestudentsattendancemodel.getStudentAttendences()!=null){

                            mAlstudentslist = datewisestudentsattendancemodel.getStudentAttendences();
                            mTxtnoattendance.setVisibility(View.GONE);
                            mRvattendance.setVisibility(View.VISIBLE);
                            setAllStudentsToAdapter(mAlstudentslist);

                        } else if(datewisestudentsattendancemodel.getStudentAttendences().size()==0 ||
                           datewisestudentsattendancemodel.getStudentAttendences()==null){

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

    private void setAllStudentsToAdapter(ArrayList<MultipleDatewisestudentsattendancemodel.StudentAttendence> mAlstudentslist) {
        dateWiseStudentListAdapter = new MultipleDateWiseStudentListAdapter(MultipleStudentsListDateWiseActivity.this,mAlstudentslist,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRvattendance.setLayoutManager(linearLayoutManager);
        mRvattendance.setAdapter(dateWiseStudentListAdapter);
        dateWiseStudentListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onStudentClick(int position) {
        Toast.makeText(this, ""+mAlstudentslist.get(position).getStudentName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MultipleStudentsListDateWiseActivity.this,MultipleBatchWiseAttedanceReportActivity.class);
        intent.putExtra("id_of_student",""+mAlstudentslist.get(position).getStudentId());
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

        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemSearch.setVisible(true);

        /*MenuItem itemFilter = menu.findItem(R.id.action_filter);
        itemFilter.setVisible(false);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem itemAddStudent = menu.findItem(R.id.action_add);
        itemAddStudent.setVisible(false);

        MenuItem itemPdf = menu.findItem(R.id.action_pdf);
        itemPdf.setVisible(false);

        MenuItem itemExcel = menu.findItem(R.id.action_excel);
        itemExcel.setVisible(false);

        MenuItem menuItem = menu.findItem(R.id.action_edit);
        menuItem.setVisible(false);
        */

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        if (mSearchView != null) {

            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(false);

            SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mStrSearchValue = query;

                    if (query.length() > 0) {
                        OkHttpRequest.cancelOkHttpRequest(Constants.WS_ALL_STUDENTS_ATTENDANCE_LIST);
                        callApiToSearchStudent(mStrSearchValue);
                    } else {
                        mStrSearchValue = "";
                        refreshData();
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    mStrSearchValue = newText;
                    if (newText.length() > 0) {
                        OkHttpRequest.cancelOkHttpRequest(Constants.WS_ALL_STUDENTS_ATTENDANCE_LIST);
                        callApiToSearchStudent(mStrSearchValue);
                    } else {
                        mStrSearchValue = "";
                        refreshData();
                    }
                    return true;
                }
            };

            ImageView closeButton = (ImageView) mSearchView.findViewById(R.id.search_close_btn);

            // Set on click listener
            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    EditText et = (EditText) findViewById(R.id.search_src_text);

                    et.setTextColor(getResources().getColor(R.color.colorBlackLight));
                    et.setHintTextColor(getResources().getColor(R.color.colorBlackLight));

                    //Clear the text from EditText view
                    et.setText("");

                    //Clear query
                    mSearchView.setQuery("", false);
                    //Collapse the action view
                    mSearchView.onActionViewCollapsed();
                    //Collapse the search widget
                    itemSearch.collapseActionView();

                    mStrSearchValue = "";
                    mAlstudentslist.clone();
                    dateWiseStudentListAdapter.notifyDataSetChanged();
                    callApiToLoadStudentsDateWise();
                }
            });

            mSearchView.setOnQueryTextListener(onQueryTextListener);
        }

        return true;

    }


    private void callApiToSearchStudent(String mStrSearchValue) {

        if(!CommonUtil.isInternetAvailable(MultipleStudentsListDateWiseActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleStudentsListDateWiseActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ALL_STUDENTS_ATTENDANCE_LIST,
                RequestParam.multiplesearchStudentInAttendanceReport(""+CommonUtil.getSharedPrefrencesInstance(
                        MultipleStudentsListDateWiseActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                        ""+mStrtodaydate,
                        ""+mStrSearchValue),
                RequestParam.getNull(),
                CODE_ALL_STUDENTS_ATTENDANCE_LIST,
                false,this);
    }
}

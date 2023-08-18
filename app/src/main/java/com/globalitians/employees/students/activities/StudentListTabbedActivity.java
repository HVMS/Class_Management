package com.globalitians.employees.students.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.globalitians.employees.R;
import com.globalitians.employees.students.adapters.SearchStudentListAdapter;
import com.globalitians.employees.students.adapters.StudentListAdapter;
import com.globalitians.employees.students.adapters.StudentListPagerAdapter;
import com.globalitians.employees.students.models.ModelSearchStudentData;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.HTTPUtils;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_FILTER_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_SEARCH_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_STUDENT_SELECTION;
import static com.globalitians.employees.utility.Constants.KEY_STUDENT_SELECTION_FOR_UNPAID_STUDENT_FEES;
import static com.globalitians.employees.utility.Constants.WS_DELETE_STUDENT;
import static com.globalitians.employees.utility.Constants.WS_STUDENT_FILTER;

public class StudentListTabbedActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, SearchStudentListAdapter.OnStudentListListener {

    private ShimmerFrameLayout mShimmerContainer;
    private TextView mTvAddNewStudents;

    private SwipeRefreshLayout mSwipeToRefresh;

    private SwipeRefreshLayout mSwipeToRefreshSearchStudentList;
    private RecyclerView mRvSearchStudent;
    private TextView tvSearchDefault;

    private View mListFooter;

    private ArrayList<ModelStudent.Student> mArrListStudents = null;
    private ArrayList<ModelSearchStudentData.Student> mArrListSearchStudents = null;
    private StudentListAdapter mAdapterStudentList = null;
    private int deletePosition;
    private boolean isFromManualAttendance = false;
    private boolean isFromAddFees = false;
    int backCount = 0;

    private TabLayout tabLayoutStudentList;
    private ViewPager vpStudentList;
    private StudentListPagerAdapter studentListPagerAdapter;

    private StudentListTabbedActivity.PaidStudentListDataReceivedListener mPaidStudentListDataListener;
    private StudentListTabbedActivity.UnPaidStudentListDataReceivedListener mUnPaidStudentListDataListener;
    private SearchStudentListAdapter mSearchStudentListAdapter;

    @Override
    public void onStudentRawClick(int position) {

    }

    @Override
    public void onMoreOptionClick(int adapterPosition, ImageView ivMoreOptions) {

    }

    public interface PaidStudentListDataReceivedListener {
        void onPaidStudentListDataReceived(ModelStudent model, String strFrom);
    }

    public interface UnPaidStudentListDataReceivedListener {
        void onUnPaidStudentListDataReceived(ModelStudent model, String strFrom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(StudentListTabbedActivity.this);
        setContentView(R.layout.activity_tabbed_student_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentData();
        init();
        findViews();
        callApiToLoadStudentList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backCount > 0) {
            callApiToLoadStudentList();
        }
    }

    public void setPaidStudentReceivedDataListener(StudentListTabbedActivity.PaidStudentListDataReceivedListener listener) {
        this.mPaidStudentListDataListener = listener;
    }

    public void setUnpaidFeesReceivedDataListener(StudentListTabbedActivity.UnPaidStudentListDataReceivedListener listener) {
        this.mUnPaidStudentListDataListener = listener;
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().hasExtra(KEY_STUDENT_SELECTION)) {
            isFromManualAttendance = true;
            setTitle("--Select Student--");
        } else if (getIntent() != null && getIntent().hasExtra(KEY_STUDENT_SELECTION_FOR_UNPAID_STUDENT_FEES)) {
            isFromAddFees = true;
            setTitle("--Select Student--");
        } else {
            setTitle("Global IT - Students");
        }
    }


    private void init() {
        if (mArrListStudents != null && mArrListStudents.size() > 0) {
            mArrListStudents.clear();
        }
        mArrListStudents = new ArrayList<>();
        mListFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_list_footer, null);
    }

    private void callApiToLoadStudentList() {
        if (!CommonUtil.isInternetAvailable(StudentListTabbedActivity.this)) {
            return;
        }

        mShimmerContainer.setVisibility(View.VISIBLE);
        mShimmerContainer.startShimmerAnimation();
        new OkHttpRequest(StudentListTabbedActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_LIST,
                RequestParam.studentList(
                        "" + getSharedPrefrencesInstance(StudentListTabbedActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_STUDENTS,
                true, this);
    }

    private void findViews() {
        mArrListStudents = new ArrayList<>();
        mArrListSearchStudents = new ArrayList<>();
        mShimmerContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerContainer.setVisibility(View.GONE);
        //  mLvStudents = findViewById(R.id.lv_students);
        mTvAddNewStudents = findViewById(R.id.tv_add_new_student);
        mTvAddNewStudents.setOnClickListener(this);

        mRvSearchStudent = (RecyclerView) findViewById(R.id.rv_search_student_list);
        tvSearchDefault = (TextView) findViewById(R.id.tv_search_defaut);
        mSwipeToRefreshSearchStudentList = findViewById(R.id.swipeToRefreshSearchStudentList);

        mSwipeToRefresh = findViewById(R.id.swipeToRefreshStudentList);
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mArrListStudents != null && mArrListStudents.size() > 0) {
                    mArrListStudents.clear();
                }
                mArrListStudents = new ArrayList<>();
                callApiToLoadStudentList();
            }
        });

        vpStudentList = (ViewPager) findViewById(R.id.viewPagerStudentList);
        studentListPagerAdapter = new StudentListPagerAdapter(getSupportFragmentManager(), "student");
        vpStudentList.setAdapter(studentListPagerAdapter);
        vpStudentList.setOffscreenPageLimit(2);

        tabLayoutStudentList = (TabLayout) findViewById(R.id.tabsStudentList);
        tabLayoutStudentList.setupWithViewPager(vpStudentList);

    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e("response:studentList>> ", "" + response);
        /*
         *
         * {"status":"success","message":"Students Found",
         * "students":
         * [{"id":25,"fname":"Jaimin","lname":"Modi","contact":"9999999956",
         * "branch_name":"Vastral","image":"","parentname":"Dinesh",
         * "parentmobileno":"8888998877","address":"Amarjyot","email":"jaimin@yahoo.in",
         * "reference":"modi","joining_date":"10-06-2018","dob":"07-05-1996",
         * "courses":[{"id":"11","name":"CCC","fees":"2300","duration":"6 Month",
         * "course_status":"Running","book_material":"Yes","bag":"No","certificate":"Yes"}]
         * ,"in_out_status":"Out"},{"id":23,"fname":"Bharga","lname":"Modi","contact":"9999999956","branch_name":"Vastral","image":"http:\/\/globalitians.com\/student\/ZeUrqb9E.jpg","parentname":"Dinesh","parentmobileno":"8888998877","address":"Amarjyot","email":"bhargav@gmail.in","reference":"modi","joining_date":"10-06-2018","dob":"07-05-1996","courses":[{"id":"11","name":"CCC","fees":"2300","duration":"6 Month","course_status":"Running","book_material":"Yes","bag":"No","certificate":"Yes"}],"in_out_status":"Out"},{"id":21,"fname":"Nik","lname":"patel","contact":"9974583968","branch_name":"Odhav","image":"http:\/\/globalitians.com\/student\/J1N2CB73.jpg","parentname":"Nme","parentmobileno":"7895653215","address":"vastral","email":"vastrlbranch@gmail.com","reference":"-----------","joining_date":"10-06-1992","dob":"01-01-1992","courses":[{"id":"11","name":"CCC","fees":"2500","duration":"2 Month","course_status":"Running","book_material":"No","bag":"No","certificate":"Yes"},{"id":"12","name":"Corporate Tally","fees":"15000","duration":"6 Month","course_status":"Running","book_material":"No","bag":"No","certificate":"Yes"}],"in_out_status":"Out"},{"id":17,"fname":"Urvish","lname":"Desai","contact":"9978631800","branch_name":"Vastral","image":"","parentname":"Nasme","parentmobileno":"4567896778","address":"Address","email":"urvishdesai@gmail.com","reference":"FRIEND","joining_date":"01-05-2019","dob":"01-05-2000","courses":[{"id":"14","name":"C ","fees":"4000","duration":"2 Month","course_status":"Running","book_material":"No","bag":"No","certificate":"No"}],"in_out_status":"Out"},{"id":13,"fname":"Digvijaysinh","lname":"jadeja","contact":"9428808619","branch_name":"Vastral","image":"","parentname":"name ","parentmobileno":"3456789045","address":"Address","email":"niharikapatel1118@gmail.com","reference":"-","joining_date":"07-05-2019","dob":"14-05-2010","courses":[{"id":"7","name":"Xpert Tally","fees":"10000","duration":"4 Month","course_status":"Running","book_material":"Yes","bag":"Yes","certificate":"Yes"}],"in_out_status":"Out"},{"id":11,"fname":"Niharika","lname":"Patel","contact":"9974583968","branch_name":"Vastral","image":"","parentname":"Vishnubhai","parentmobileno":"9974583968","address":"Suryam Sky","email":"niharikapatel1192@gmail.com","reference":"-","joining_date":"10-05-2019","dob":"01-01-1992","courses":[{"id":"1","name":"PHP","fees":"15000","duration":"6 Month","course_status":"Running","book_material":"Yes","bag":"Yes","certificate":"Yes"},{"id":"4","name":"Web Designing","fees":"15000","duration":"1 Year","course_status":"Running","book_material":"No","bag":"No","certificate":"No"}],"in_out_status":"Out"}]}
         * */
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_STUDENTS:
                final Gson studentListGson = new Gson();
                try {
                    ModelStudent modelStudent = studentListGson
                            .fromJson(response, ModelStudent.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        if (modelStudent.getStudents() != null && modelStudent.getStudents().size() > 0) {
                            mPaidStudentListDataListener.onPaidStudentListDataReceived(modelStudent, "");
                            mUnPaidStudentListDataListener.onUnPaidStudentListDataReceived(modelStudent, "");
                        }
                    }
                    if (mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }
                    mShimmerContainer.stopShimmerAnimation();
                    mShimmerContainer.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_SEARCH_STUDENT:
                final Gson searchStudentListGson = new Gson();
                try {
                    ModelSearchStudentData modelSearchStudent = searchStudentListGson
                            .fromJson(response, ModelSearchStudentData.class);

                    if (modelSearchStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        if (modelSearchStudent.getStudents() != null && modelSearchStudent.getStudents().size() > 0) {
                            mSwipeToRefreshSearchStudentList.setVisibility(View.VISIBLE);
                            mSwipeToRefresh.setVisibility(View.GONE);
                            tvSearchDefault.setVisibility(View.GONE);
                            mArrListSearchStudents.addAll(modelSearchStudent.getStudents());
                            setOrNotifyStudentSearchAdapter();
                        }else{
                            mSwipeToRefreshSearchStudentList.setVisibility(View.VISIBLE);
                            mSwipeToRefresh.setVisibility(View.GONE);
                            tvSearchDefault.setVisibility(View.GONE);
                            tvSearchDefault.setText(""+getResources().getString(R.string.strNoDataFound));
                        }
                    }
                    if (mSwipeToRefreshSearchStudentList.isRefreshing()) {
                        mSwipeToRefreshSearchStudentList.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_DELETE_STUDENT:
                try {
                    JSONObject jsonDeleteInquiry = new JSONObject(response);
                    if (jsonDeleteInquiry.has("status")) {
                        if (jsonDeleteInquiry.getString("status").equalsIgnoreCase("success")) {
                            Toast.makeText(this, "" + jsonDeleteInquiry.getString("message"), Toast.LENGTH_SHORT).show();
                            playSoundForAttendance("" + jsonDeleteInquiry.getString("message"), StudentListTabbedActivity.this);
                            mArrListStudents.remove(deletePosition);
                            mAdapterStudentList.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void setOrNotifyStudentSearchAdapter() {
        if(mArrListSearchStudents.size()>0)
        {
            mSearchStudentListAdapter = new SearchStudentListAdapter(StudentListTabbedActivity.this, mArrListSearchStudents,this);
            LinearLayoutManager manager = new LinearLayoutManager(StudentListTabbedActivity.this);
            mRvSearchStudent.setLayoutManager(manager);
            mRvSearchStudent.setAdapter(mSearchStudentListAdapter);

        }else{
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_new_student://Currently view is commnented in layout.
                //Added + icon on Top
                startActivity(new Intent(StudentListTabbedActivity.this, AddStudentActivity.class));
                backCount = 1;
                break;
        }
    }

    private void navigateToAddStudentActivity() {
        startActivity(new Intent(StudentListTabbedActivity.this, AddStudentActivity.class));
        backCount = 1;
    }

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
        if (id == R.id.action_add) {
            navigateToAddStudentActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onDeleteStudent(int position) {
        deletePosition = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(StudentListTabbedActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callApiToDeleteStudent();
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }*/

    private void callApiToDeleteStudent() {
        if (!CommonUtil.isInternetAvailable(StudentListTabbedActivity.this)) {
            return;
        }

        new OkHttpRequest(StudentListTabbedActivity.this,
                OkHttpRequest.Method.POST,
                WS_DELETE_STUDENT,
                RequestParam.deleteStudent("" + mArrListStudents.get(deletePosition).getId(),
                        "" + getSharedPrefrencesInstance(StudentListTabbedActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_DELETE_STUDENT,
                true, this);
    }


    private void callApiToSearchList(String query) {
        if (!CommonUtil.isInternetAvailable(StudentListTabbedActivity.this)) {
            return;
        }

        new OkHttpRequest(StudentListTabbedActivity.this,
                OkHttpRequest.Method.POST,
                WS_STUDENT_FILTER,
                RequestParam.searchFilterStudent(query,
                        "" + getSharedPrefrencesInstance(StudentListTabbedActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_FILTER_STUDENT,
                true, this);
    }

    /*private void setSearchStudentListAdapter() {
        mAdapterStudentList = null;
        mAdapterStudentList = new StudentListAdapter(StudentListTabbedActivity.this, mArrListSearchStudents, this);
        mListFooter.setVisibility(View.GONE);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //searchView.setMinimumWidth(Integer.MAX_VALUE);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
        MenuItem item1 = menu.findItem(R.id.action_settings);
        item1.setVisible(false);

        searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSwipeToRefreshSearchStudentList.setVisibility(View.GONE);
                mSwipeToRefresh.setVisibility(View.VISIBLE);
                tvSearchDefault.setText(""+getResources().getString(R.string.strSearchDefaultString));
                return true;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //some operation
                /*if (mALSearchAttendaceList != null && mALSearchAttendaceList.size() > 0) {
                    mALSearchAttendaceList.clear();
                }
                mALSearchAttendaceList = new ArrayList<>();*/
            }
        });
        EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setHint("Search Student");
        searchPlate.setTextColor(Color.WHITE);
        searchPlate.setHintTextColor(Color.WHITE);
        searchPlate.setTextSize(16);
        //ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorWhite));
        //searchPlate.setBackgroundTintList(colorStateList);
        searchPlate.setPadding(2, 2, 2, 2);

        //searchPlate.setBackgroundResource(R.drawable.drawable_search_background);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*try {
                    //HTTPUtils.cancelRequest(WS_STUDENT_FILTER);
                    //callApiToSearchStudent(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if(newText.length()>2)
                    {
                        tvSearchDefault.setText("Searching...");
                        mSwipeToRefreshSearchStudentList.setVisibility(View.VISIBLE);
                        mSwipeToRefresh.setVisibility(View.GONE);
                        HTTPUtils.cancelRequest(WS_STUDENT_FILTER);
                        callApiToSearchList(newText);
                    }else{
                        tvSearchDefault.setText(""+getResources().getString(R.string.strSearchDefaultString));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return true;
    }
}

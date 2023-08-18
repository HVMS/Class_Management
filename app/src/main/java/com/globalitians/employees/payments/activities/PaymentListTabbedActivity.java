package com.globalitians.employees.payments.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.globalitians.employees.MonthFilterSelectionAdapter;
import com.globalitians.employees.MonthModel;
import com.globalitians.employees.R;
import com.globalitians.employees.YearFilterSelectionAdapter;
import com.globalitians.employees.YearModel;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.payments.adapters.FeesPaymentPagerAdapter;
import com.globalitians.employees.students.activities.AddStudentActivity;
import com.globalitians.employees.students.adapters.StudentListAdapter;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_FILTER_PAYMENT;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.WS_DELETE_STUDENT;

public class PaymentListTabbedActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, MonthFilterSelectionAdapter.MonthClickListener, YearFilterSelectionAdapter.YearClickListener {

    private ShimmerFrameLayout mShimmerContainer;
    private TextView mTvAddNewStudents;
    //private SwipeRefreshLayout mSwipeToRefresh;
    private View mListFooter;

    private ArrayList<ModelStudent.Student> mArrListStudents = null;
    private ArrayList<ModelStudent.Student> mArrListSearchStudents = null;
    private StudentListAdapter mAdapterStudentList = null;
    private int deletePosition;
    private boolean isFromManualAttendance = false;
    private boolean isFromAddFees = false;
    int backCount = 0;

    private TabLayout tabLayoutStudentList;
    private ViewPager vpStudentList;
    private FeesPaymentPagerAdapter studentListPagerAdapter;

    private PaymentListTabbedActivity.PaidStudentListDataReceivedListener mPaidStudentListDataListener;
    private PaymentListTabbedActivity.UnPaidStudentListDataReceivedListener mUnPaidStudentListDataListener;


    /*
     * Filter Bottonsheet Stuff
     * */
    private BottomSheetDialog mDialogFilterPayment;
    private CustomTextView tvOpen;

    private CustomTextView mTvStartDate;
    private CustomTextView mTvEndDate;

    private DatePickerDialog startDatePickerDialog = null;
    private DatePickerDialog endDatePickerDialog = null;

    private ArrayList<MonthModel> mAlMonths = new ArrayList<>();
    private ArrayList<YearModel> mAlYears = new ArrayList<>();
    private RecyclerView mRvMonths;
    private RecyclerView mRvYear;

    private String strSelectedBranch = "";
    private String strSelectedStartDate = "";
    private String strSelectedEndDate = "";
    private String strFitleredMonthAndYear = "";
    private boolean isMonthSelected = false;
    private boolean isYearSelected = false;
    private MonthFilterSelectionAdapter mMonthFilterAdapter = null;
    private YearFilterSelectionAdapter mYearFilterAdapter = null;

    @Override
    public void onMonthClick(int position, boolean isChecked) {
        mAlMonths.get(position).isSelected = isChecked;
    }

    @Override
    public void onYearClick(int position, boolean isChecked) {
        mAlYears.get(position).isSelected = isChecked;
    }

    public interface PaidStudentListDataReceivedListener {
        void onPaidStudentListDataReceived(ModelStudent model, String strFrom);
    }

    public interface UnPaidStudentListDataReceivedListener {
        void onUnPaidStudentListDataReceived(ModelStudent model, String strFrom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
        // Retrieve the SearchView and plug it into SearchManager
        //searchView.setMinimumWidth(Integer.MAX_VALUE);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        MenuItem itemedit = menu.findItem(R.id.action_edit);
        itemedit.setVisible(false);
        MenuItem itemadd = menu.findItem(R.id.action_add);
        itemadd.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.action_settings);
        item1.setVisible(false);
        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        itemFilter.setVisible(true);
        itemFilter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mDialogFilterPayment != null && !mDialogFilterPayment.isShowing()) {
                    mDialogFilterPayment.show();
                } else {
                    mDialogFilterPayment.dismiss();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(PaymentListTabbedActivity.this);
        setContentView(R.layout.activity_tabbed_student_list);
        setTitle("Payments");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        findViews();

        callApiToLoadStudentList();

        /*
         * Filter Bottomsheet stuff
         * */
        initMonthFilterData();
        initYearFilterData();
        initializeFilterPaymentBottomSheet();

    }

    private void setPagerAdapterForPayment(ModelStudent modelStudent) {
        studentListPagerAdapter = new FeesPaymentPagerAdapter(getSupportFragmentManager(), modelStudent);
        vpStudentList.setAdapter(studentListPagerAdapter);
        vpStudentList.setOffscreenPageLimit(2);

        tabLayoutStudentList = (TabLayout) findViewById(R.id.tabsStudentList);
        tabLayoutStudentList.setupWithViewPager(vpStudentList);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (backCount > 0) {
            callApiToLoadStudentList();
        }
    }

    private void init() {
        if (mArrListStudents != null && mArrListStudents.size() > 0) {
            mArrListStudents.clear();
        }
        mArrListStudents = new ArrayList<>();
        mListFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_list_footer, null);
    }

    public void setPaidStudentReceivedDataListener(PaymentListTabbedActivity.PaidStudentListDataReceivedListener listener) {
        this.mPaidStudentListDataListener = listener;
    }

    public void setUnpaidFeesReceivedDataListener(PaymentListTabbedActivity.UnPaidStudentListDataReceivedListener listener) {
        this.mUnPaidStudentListDataListener = listener;
    }

    private void callApiToLoadStudentList() {
        if (!CommonUtil.isInternetAvailable(PaymentListTabbedActivity.this)) {
            return;
        }

        mShimmerContainer.setVisibility(View.VISIBLE);
        mShimmerContainer.startShimmerAnimation();
        new OkHttpRequest(PaymentListTabbedActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_LIST,
                RequestParam.studentList(
                        "" + getSharedPrefrencesInstance(PaymentListTabbedActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_STUDENTS,
                true, this);
    }

    private void callApiToFilterPayment() {
        if (!CommonUtil.isInternetAvailable(PaymentListTabbedActivity.this)) {
            return;
        }

        mShimmerContainer.setVisibility(View.VISIBLE);
        mShimmerContainer.startShimmerAnimation();
        new OkHttpRequest(PaymentListTabbedActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_FILTER_PAYMENT,
                RequestParam.filterPayment(
                        strSelectedBranch, strFitleredMonthAndYear, strSelectedStartDate, strSelectedEndDate),
                RequestParam.getNull(),
                CODE_FILTER_PAYMENT,
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

        setSearchViewListener();

        vpStudentList = (ViewPager) findViewById(R.id.viewPagerStudentList);

       /* mSwipeToRefresh = findViewById(R.id.swipeToRefreshStudentList);
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mArrListStudents != null && mArrListStudents.size() > 0) {
                    mArrListStudents.clear();
                }
                mArrListStudents = new ArrayList<>();
                callApiToLoadStudentList();
            }
        });*/
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
            case CODE_FILTER_PAYMENT:
                final Gson studentListGson = new Gson();
                try {
                    ModelStudent modelStudent = studentListGson
                            .fromJson(response, ModelStudent.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        if (modelStudent.getStudents() != null && modelStudent.getStudents().size() > 0) {

                            setPagerAdapterForPayment(modelStudent);

                            mPaidStudentListDataListener.onPaidStudentListDataReceived(modelStudent, "");
                            mUnPaidStudentListDataListener.onUnPaidStudentListDataReceived(modelStudent, "");
                        }else{
                            mPaidStudentListDataListener.onPaidStudentListDataReceived(modelStudent, "");
                            mUnPaidStudentListDataListener.onUnPaidStudentListDataReceived(modelStudent, "");
                        }
                    }else{
                        mPaidStudentListDataListener.onPaidStudentListDataReceived(modelStudent, "");
                        mUnPaidStudentListDataListener.onUnPaidStudentListDataReceived(modelStudent, "");
                    }
                   /* if (mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }*/
                    mShimmerContainer.stopShimmerAnimation();
                    mShimmerContainer.setVisibility(View.GONE);
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
                            playSoundForAttendance("" + jsonDeleteInquiry.getString("message"), PaymentListTabbedActivity.this);
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

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_new_student:
                startActivity(new Intent(PaymentListTabbedActivity.this, AddStudentActivity.class));
                backCount = 1;
                break;
        }
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

        return super.onOptionsItemSelected(item);
    }

    private void initMonthFilterData() {
        mAlMonths = new ArrayList<>();
        MonthModel m1 = new MonthModel("JAN", 1, false);
        MonthModel m2 = new MonthModel("FEB", 2, false);
        MonthModel m3 = new MonthModel("MAR", 3, false);
        MonthModel m4 = new MonthModel("APR", 4, false);
        MonthModel m5 = new MonthModel("MAY", 5, false);
        MonthModel m6 = new MonthModel("JUN", 6, false);
        MonthModel m7 = new MonthModel("JUL", 7, false);
        MonthModel m8 = new MonthModel("AUG", 8, false);
        MonthModel m9 = new MonthModel("SEP", 9, false);
        MonthModel m10 = new MonthModel("OCT", 10, false);
        MonthModel m11 = new MonthModel("NOV", 11, false);
        MonthModel m12 = new MonthModel("DEC", 12, false);

        mAlMonths.add(m1);
        LogUtil.e(">> m1", "" + m1.strMonthName);
        mAlMonths.add(m2);
        LogUtil.e(">> m2", "" + m2.strMonthName);
        mAlMonths.add(m3);
        mAlMonths.add(m4);
        mAlMonths.add(m5);
        mAlMonths.add(m6);
        mAlMonths.add(m7);
        mAlMonths.add(m8);
        mAlMonths.add(m9);
        mAlMonths.add(m10);
        mAlMonths.add(m11);
        mAlMonths.add(m12);
    }

    private void initYearFilterData() {
        mAlYears = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 10; i > 1; i--) {
            YearModel y1 = new YearModel("" + currentYear, false);
            mAlYears.add(y1);
            currentYear -= 1;
        }
    }

    private void initDatePickers() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (day < 10) {
            if (month < 10) {
                mTvStartDate.setText("0" + day + "-0" + (month + 1) + "-" + year);
                mTvEndDate.setText("0" + day + "-0" + (month + 1) + "-" + year);
            } else {
                mTvStartDate.setText("0" + day + "-" + (month + 1) + "-" + year);
                mTvEndDate.setText("0" + day + "-" + (month + 1) + "-" + year);
            }
        } else {
            if (month < 10) {
                mTvStartDate.setText(day + "-0" + (month + 1) + "-" + year);
                mTvEndDate.setText(day + "-0" + (month + 1) + "-" + year);
            } else {
                mTvStartDate.setText(day + "-" + (month + 1) + "-" + year);
                mTvEndDate.setText(day + "-" + (month + 1) + "-" + year);
            }
        }


        DatePickerDialog.OnDateSetListener startDateListener;
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (mAlMonths != null && mAlMonths.size() > 0) {
                    for (int i = 0; i < mAlMonths.size(); i++) {
                        mAlMonths.get(i).isSelected = false;
                    }
                    mMonthFilterAdapter.notifyDataSetChanged();
                }

                if (mAlYears != null && mAlYears.size() > 0) {
                    for (int i = 0; i < mAlYears.size(); i++) {
                        mAlYears.get(i).isSelected = false;
                    }
                    mYearFilterAdapter.notifyDataSetChanged();
                }

                if (dayOfMonth < 10) {
                    if (month < 10) {
                        mTvStartDate.setText("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mTvStartDate.setText("0" + dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                } else {
                    if (month < 10) {
                        mTvStartDate.setText(dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mTvStartDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }
            }
        };

        DatePickerDialog.OnDateSetListener endDateListener;
        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (dayOfMonth < 10) {
                    if (month < 10) {
                        mTvEndDate.setText("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mTvEndDate.setText("0" + dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                } else {
                    if (month < 10) {
                        mTvEndDate.setText(dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mTvEndDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }
            }
        };

        startDatePickerDialog = new DatePickerDialog(
                PaymentListTabbedActivity.this, startDateListener, year, month, day);
        startDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        endDatePickerDialog = new DatePickerDialog(
                PaymentListTabbedActivity.this, endDateListener, year, month, day);
        endDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
    }


    private void initializeFilterPaymentBottomSheet() {
        final View mViewFilterPayment = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_payment, null);
        mDialogFilterPayment = new BottomSheetDialog(PaymentListTabbedActivity.this);
        mDialogFilterPayment.setContentView(mViewFilterPayment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CommonUtil.setWhiteNavigationBar(mDialogFilterPayment);
        }

        final RadioGroup rgBranch = mViewFilterPayment.findViewById(R.id.rgBranch);
        CustomTextView mTitleMonth = mViewFilterPayment.findViewById(R.id.title_month);
        mRvMonths = (RecyclerView) mViewFilterPayment.findViewById(R.id.rv_months);
        setAdapterForMonthFilter();
        CustomTextView mTitleYear = mViewFilterPayment.findViewById(R.id.title_year);
        mRvYear = (RecyclerView) mViewFilterPayment.findViewById(R.id.rv_year);
        setAdapterForYearFilter();
        CustomTextView mTitleStartDate = mViewFilterPayment.findViewById(R.id.title_start_date);
        mTvStartDate = mViewFilterPayment.findViewById(R.id.tv_start_date);
        CustomTextView mTitleEndDate = mViewFilterPayment.findViewById(R.id.title_end_date);
        mTvEndDate = mViewFilterPayment.findViewById(R.id.tv_end_date);
        LinearLayout mLlStartDate = mViewFilterPayment.findViewById(R.id.ll_startDate);
        LinearLayout mLlEndDate = mViewFilterPayment.findViewById(R.id.ll_endDate);

        initDatePickers();

        mLlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        mLlEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        CustomTextView tvCancel = mViewFilterPayment.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogFilterPayment.isShowing() == true) {
                    mDialogFilterPayment.dismiss();
                }
            }
        });

        CustomButton tvSave = mViewFilterPayment.findViewById(R.id.btnApplyFilter);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = rgBranch.getCheckedRadioButtonId();
                // find the radiobutton by returned id

                strSelectedBranch = "";
                strSelectedStartDate = "";
                strSelectedEndDate = "";
                strFitleredMonthAndYear = "";

                isYearSelected = false;
                isMonthSelected = false;
                for (int i = 0; i < mAlYears.size(); i++) {
                    if (mAlYears.get(i).isSelected) {
                        isYearSelected = true;
                        for (int j = 0; j < mAlMonths.size(); j++) {
                            if (mAlMonths.get(j).isSelected) {
                                isMonthSelected = true;
                                strFitleredMonthAndYear = strFitleredMonthAndYear + mAlMonths.get(j).monthIndex + "-" + mAlYears.get(i).strYearName + ",";
                            }
                        }
                    }
                }

                for (int i = 0; i < mAlMonths.size(); i++) {
                    if (mAlMonths.get(i).isSelected) {
                        isMonthSelected = true;
                    }
                }
                if (isYearSelected && isMonthSelected) {

                } else {
                    strFitleredMonthAndYear = "";
                    if (isYearSelected) {
                        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
                        currentMonth = currentMonth + 1;
                        for (int i = 0; i < mAlYears.size(); i++) {
                            if (mAlYears.get(i).isSelected) {
                                strFitleredMonthAndYear += "" + currentMonth + "-" + mAlYears.get(i).strYearName + ",";
                            }
                        }
                    } else if (isMonthSelected) {
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        for (int i = 0; i < mAlMonths.size(); i++) {
                            if (mAlMonths.get(i).isSelected) {
                                strFitleredMonthAndYear += "" + mAlMonths.get(i).monthIndex + "-" + currentYear + ",";
                            }
                        }
                    } else {
                        //when no year and month is selected.
                        strFitleredMonthAndYear = "";
                    }
                }

                if (strFitleredMonthAndYear.length() > 0) {
                    strFitleredMonthAndYear = strFitleredMonthAndYear.substring(0, strFitleredMonthAndYear.length() - 1);
                }
                final RadioButton rBtnBranch = rgBranch.findViewById(selectedId);
                strSelectedBranch = rBtnBranch.getText().toString();
                if (strSelectedBranch.equalsIgnoreCase("Odhav")) {
                    strSelectedBranch = "2";
                } else {
                    strSelectedBranch = "1";
                }

                strSelectedStartDate = mTvStartDate.getText().toString();
                strSelectedEndDate = mTvEndDate.getText().toString();

                LogUtil.e(">>Branch", "" + strSelectedBranch);
                LogUtil.e(">>start Date", "" + strSelectedStartDate);
                LogUtil.e(">>end Date", "" + strSelectedEndDate);
                LogUtil.e(">>Filtered data", "" + strFitleredMonthAndYear);

                if (mDialogFilterPayment.isShowing() == true) {
                    mDialogFilterPayment.dismiss();
                }

                callApiToFilterPayment();
            }
        });
    }

    private void setAdapterForMonthFilter() {
        mMonthFilterAdapter = new MonthFilterSelectionAdapter(PaymentListTabbedActivity.this, mAlMonths, this);
        LinearLayoutManager manager = new LinearLayoutManager(PaymentListTabbedActivity.this);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRvMonths.setLayoutManager(gridLayoutManager);
        mRvMonths.setAdapter(mMonthFilterAdapter);
    }

    private void setAdapterForYearFilter() {
        mYearFilterAdapter = new YearFilterSelectionAdapter(PaymentListTabbedActivity.this, mAlYears, this);
        LinearLayoutManager manager = new LinearLayoutManager(PaymentListTabbedActivity.this);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRvYear.setLayoutManager(gridLayoutManager);
        mRvYear.setAdapter(mYearFilterAdapter);
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
        if (!CommonUtil.isInternetAvailable(PaymentListTabbedActivity.this)) {
            return;
        }

        new OkHttpRequest(PaymentListTabbedActivity.this,
                OkHttpRequest.Method.POST,
                WS_DELETE_STUDENT,
                RequestParam.deleteStudent("" + mArrListStudents.get(deletePosition).getId(),
                        "" + getSharedPrefrencesInstance(PaymentListTabbedActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_DELETE_STUDENT,
                true, this);
    }

    private void setSearchViewListener() {
       /* mSearchViewStudents.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                String query = queryString.toLowerCase();
                mArrListSearchStudents.clear();
                if (mArrListStudents != null && mArrListStudents.size() > 0) {
                    for (int i = 0; i < mArrListStudents.size(); i++) {
                        if (mArrListStudents.get(i).getFname().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getLname().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getAddress().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getContact().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getBranchName().contains(query) ||
                                mArrListStudents.get(i).getDob().contains(query) ||
                                mArrListStudents.get(i).getEmail().contains(query) ||
                                mArrListStudents.get(i).getJoiningDate().contains(query) ||
                                mArrListStudents.get(i).getParentmobileno().contains(query) ||
                                mArrListStudents.get(i).getParentname().contains(query) ||
                                mArrListStudents.get(i).getReference().contains(query)) {
                            if (mArrListSearchStudents != null && mArrListSearchStudents.size() > 0) {
                                for (int j = 0; j < mArrListSearchStudents.size(); j++) {
                                    if (mArrListSearchStudents.get(j).getId().equals(mArrListStudents.get(i).getId())) {
                                        //nothing
                                    } else {
                                        mArrListSearchStudents.add(mArrListStudents.get(i));
                                    }
                                }
                            } else {
                                mArrListSearchStudents.add(mArrListStudents.get(i));
                            }
                        }
                    }
                }
                setSearchStudentListAdapter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                mArrListSearchStudents.clear();
                if (mArrListStudents != null && mArrListStudents.size() > 0) {
                    for (int i = 0; i < mArrListStudents.size(); i++) {
                        if (mArrListStudents.get(i).getFname().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getLname().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getAddress().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getContact().toLowerCase().contains(query) ||
                                mArrListStudents.get(i).getBranchName().contains(query) ||
                                mArrListStudents.get(i).getDob().contains(query) ||
                                mArrListStudents.get(i).getEmail().contains(query) ||
                                mArrListStudents.get(i).getJoiningDate().contains(query) ||
                                mArrListStudents.get(i).getParentmobileno().contains(query) ||
                                mArrListStudents.get(i).getParentname().contains(query) ||
                                mArrListStudents.get(i).getReference().contains(query)) {
                            if (mArrListSearchStudents != null && mArrListSearchStudents.size() > 0) {
                                for (int j = 0; j < mArrListSearchStudents.size(); j++) {
                                    if (mArrListSearchStudents.get(j).getId().equals(mArrListStudents.get(i).getId())) {
                                        //nothing
                                    } else {
                                        mArrListSearchStudents.add(mArrListStudents.get(i));
                                    }
                                }
                            } else {
                                mArrListSearchStudents.add(mArrListStudents.get(i));
                            }
                        }
                    }
                }
                setSearchStudentListAdapter();
                return false;
            }
        });*/

        /*mSearchViewStudents.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchViewStudents.setQueryHint("");
                setStudentListAdapter(mArrListStudents);
                return false;
            }
        });*/
    }

    /*private void setSearchStudentListAdapter() {
        mAdapterStudentList = null;
        mAdapterStudentList = new StudentListAdapter(StudentListTabbedActivity.this, mArrListSearchStudents, this);
        mListFooter.setVisibility(View.GONE);
    }*/
}

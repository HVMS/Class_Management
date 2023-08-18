package com.globalitians.employees.courses.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.globalitians.employees.R;
import com.globalitians.employees.courses.adapters.CourseListAdapter;
import com.globalitians.employees.courses.model.ModelCourseList;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_SELECTION_FOR_ADD_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_COURSE;
import static com.globalitians.employees.utility.Constants.CODE_SEARCH_COURSES;
import static com.globalitians.employees.utility.Constants.INTENT_KEY_COURSE_ID;
import static com.globalitians.employees.utility.Constants.KEY_COURSE_SELECTION_TAG;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_COURSE_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_COURSE_IMAGE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_COURSE_NAME;
import static com.globalitians.employees.utility.Constants.WS_SEARCH_COURSES;

public class CourseListActivity extends AppCompatActivity implements OkHttpInterface, CourseListAdapter.OnCourseListListener, View.OnClickListener {

    private ShimmerFrameLayout mShimmerContainer;
    private ListView mLvCourses;
    private TextView mTvLiteratureGallery;
    private SwipeRefreshLayout mSwipeToRefresh;
    private View mListFooter;

    private TextView mTvNoCourses;

    private ArrayList<ModelCourseList.Course> mArrListCourses = null;
    private ArrayList<ModelCourseList.Course> mArrListSearchCourses = null;
    private CourseListAdapter mAdapterCourseList = null;

    private int deleteCoursePosition = 0;
    private boolean isFromManualAttendance = false;
    private boolean isFromAddStudent = false;
    private android.support.v7.widget.SearchView mSearchView;
    private String mStrSearchValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(CourseListActivity.this);
        setContentView(R.layout.activity_course_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();
        findViews();
        callApiToLoadCourseList(true);

        getIntentData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);

        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        itemFilter.setVisible(false);

        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemSearch.setVisible(true);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem itemAddStudent = menu.findItem(R.id.action_add);
        itemAddStudent.setVisible(false);

        MenuItem itemEdit = menu.findItem(R.id.action_edit);
        itemEdit.setVisible(false);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(false);

            android.support.v7.widget.SearchView.OnQueryTextListener queryTextListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {

                    mStrSearchValue = newText;
                    if (newText.length() > 1) {
                        OkHttpRequest.cancelOkHttpRequest(Constants.WS_STUDENT_FILTER);
                        callApiToFilterCourseList(newText);
                    } else {
                        mStrSearchValue = "";
                        refreshCourseListData();
                    }
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    mStrSearchValue = query;
                    if (query.length() > 1) {
                        OkHttpRequest.cancelOkHttpRequest(Constants.WS_STUDENT_FILTER);
                        callApiToFilterCourseList(query);
                    } else {
                        mStrSearchValue = "";
                        refreshCourseListData();
                    }
                    return true;
                }
            };

            // Get the search close button image view
            ImageView closeButton = (ImageView) mSearchView.findViewById(R.id.search_close_btn);

            // Set on click listener
            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EditText et = (EditText) findViewById(R.id.search_src_text);

                    //Clear the text from EditText view
                    et.setText("");

                    //Clear query
                    mSearchView.setQuery("", false);
                    //Collapse the action view
                    mSearchView.onActionViewCollapsed();
                    //Collapse the search widget
                    itemSearch.collapseActionView();

                    mStrSearchValue = "";
                    refreshCourseListData();
                }
            });

            mSearchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
    }

    private void callApiToFilterCourseList(String query) {
        if (!CommonUtil.isInternetAvailable(CourseListActivity.this)) {
            return;
        }
        new OkHttpRequest(CourseListActivity.this,
                OkHttpRequest.Method.POST,
                WS_SEARCH_COURSES,
                RequestParam.searchCourses(query),
                RequestParam.getNull(),
                CODE_SEARCH_COURSES,
                false, this);
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().hasExtra(KEY_COURSE_SELECTION_TAG)) {
            isFromAddStudent = true;
            setTitle("--Select Course--");
        }
    }

    private void initialize() {
        mArrListSearchCourses = new ArrayList<>();
        mArrListCourses = new ArrayList<>();

        mListFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_list_footer, null);
    }

    private void findViews() {
        mTvNoCourses = findViewById(R.id.tv_no_courses);

        mShimmerContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerContainer.setVisibility(View.GONE);

        mLvCourses = findViewById(R.id.lv_courses);
        mTvLiteratureGallery = findViewById(R.id.tv_add_new_inquiry);
        mTvLiteratureGallery.setOnClickListener(this);
        mSwipeToRefresh = findViewById(R.id.swipeToRefreshInquiryList);
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCourseListData();
            }
        });
    }

    private void refreshCourseListData() {
        if (mArrListCourses != null && mArrListCourses.size() > 0) {
            mArrListCourses.clear();
        }
        if (mArrListSearchCourses != null && mArrListSearchCourses.size() > 0) {
            mArrListSearchCourses.clear();
        }
        mArrListCourses = new ArrayList<>();
        mArrListSearchCourses = new ArrayList<>();
        callApiToLoadCourseList(false);
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

    private void callApiToLoadCourseList(boolean isShowProgress) {
        if (!CommonUtil.isInternetAvailable(CourseListActivity.this)) {
            return;
        }

        mShimmerContainer.setVisibility(View.VISIBLE);
        mShimmerContainer.startShimmerAnimation();
        new OkHttpRequest(CourseListActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_COURSE_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_COURSE_LIST,
                isShowProgress, this);
    }


    private void callApiToDeleteCourseList(int deleteCoursePosition) {
        if (!CommonUtil.isInternetAvailable(CourseListActivity.this)) {
            return;
        }

        new OkHttpRequest(CourseListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_DELETE_COURSE,
                RequestParam.deleteCourse("" + mArrListCourses.get(deleteCoursePosition).getId(),
                        "" + getSharedPrefrencesInstance(CourseListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_DELETE_COURSE,
                true, this);
    }


    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {


        Log.e(">>course list response ", "" + response);

        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_COURSE_LIST:
                final Gson courseListGson = new Gson();
                try {
                    ModelCourseList modelCourseList = courseListGson
                            .fromJson(response, ModelCourseList.class);

                    if (modelCourseList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListCourses = modelCourseList.getCourses();
                        if (mArrListCourses != null && mArrListCourses.size() > 0) {
                            setCoursesListAdapter(mArrListCourses);
                            mTvNoCourses.setVisibility(View.GONE);
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
            case CODE_SEARCH_COURSES:
                final Gson courseListSearchGson = new Gson();
                try {
                    ModelCourseList modelCourseList = courseListSearchGson
                            .fromJson(response, ModelCourseList.class);

                    if (modelCourseList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mTvNoCourses.setVisibility(View.GONE);
                        if (mArrListCourses != null && mArrListCourses.size() > 0) {
                            mArrListCourses.clear();
                            mArrListCourses = new ArrayList<>();
                        }
                        if (mArrListSearchCourses != null && mArrListSearchCourses.size() > 0) {
                            mArrListSearchCourses.clear();
                            mArrListSearchCourses = new ArrayList<>();
                        }

                        if (mAdapterCourseList != null) {
                            mAdapterCourseList.notifyDataSetChanged();
                        }
                        mArrListSearchCourses = modelCourseList.getCourses();
                        if (modelCourseList.getCourses() != null && modelCourseList.getCourses().size() > 0) {
                            mTvNoCourses.setVisibility(View.GONE);
                            setCoursesListAdapter(mArrListSearchCourses);
                        } else {
                            mArrListSearchCourses.clear();
                            mTvNoCourses.setVisibility(View.VISIBLE);
                            if (mAdapterCourseList != null) {
                                mAdapterCourseList.notifyDataSetChanged();
                            }
                        }

                    } else {
                        mTvNoCourses.setVisibility(View.VISIBLE);
                        if (mArrListCourses != null && mArrListCourses.size() > 0) {
                            mArrListCourses.clear();
                            mArrListCourses = new ArrayList<>();
                        }
                        if (mArrListSearchCourses != null && mArrListSearchCourses.size() > 0) {
                            mArrListSearchCourses.clear();
                            mArrListSearchCourses = new ArrayList<>();
                        }

                        if (mAdapterCourseList != null) {
                            mAdapterCourseList.notifyDataSetChanged();
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
            case CODE_DELETE_COURSE:
                if (mArrListSearchCourses != null && mArrListSearchCourses.size() > 0) {
                    mAdapterCourseList.notifyDataSetChanged();
                    for (int i = 0; i < mArrListCourses.size(); i++) {
                        if (mArrListCourses.get(i).getId().equals(mArrListSearchCourses.get(deleteCoursePosition).getId())) {
                            mArrListCourses.remove(i);
                            mArrListSearchCourses.remove(deleteCoursePosition);
                            mAdapterCourseList.notifyDataSetChanged();
                        }
                    }
                } else {
                    if (mArrListCourses != null && mArrListCourses.size() > 0) {
                        mArrListCourses.remove(deleteCoursePosition);
                        mAdapterCourseList.notifyDataSetChanged();
                    }
                }
                Log.e(">>>>>>> delete >> ", "" + response);
                break;
        }

    }

    private void setCoursesListAdapter(ArrayList<ModelCourseList.Course> mALCourses) {
        mAdapterCourseList = new CourseListAdapter(CourseListActivity.this, mALCourses, this, "course_list");
        mLvCourses.setAdapter(mAdapterCourseList);
        if (mLvCourses.getFooterViewsCount() == 0) {
            mLvCourses.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
    }

    private void setSearchCoursesListAdapter() {
        mAdapterCourseList = new CourseListAdapter(CourseListActivity.this, mArrListSearchCourses, this, "course_list");
        mLvCourses.setAdapter(mAdapterCourseList);
        if (mLvCourses.getFooterViewsCount() == 0) {
            mLvCourses.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
    }


    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onCourseRawClick(int position) {
        if (isFromAddStudent) {

            if (CommonUtil.isNullString(mStrSearchValue)) {
                if (mArrListCourses != null && mArrListCourses.size() > 0) {
                    Log.e("@@@@@@", "" + mArrListCourses.get(position).getId());
                    Intent intent = new Intent()
                            .putExtra(KEY_INTENT_COURSE_ID, "" + mArrListCourses.get(position).getId())
                            .putExtra(KEY_INTENT_COURSE_NAME,
                                    "" + mArrListCourses.get(position).getName())
                            .putExtra(KEY_INTENT_COURSE_IMAGE, "" + mArrListCourses.get(position).getImage());
                    setResult(CODE_COURSE_SELECTION_FOR_ADD_STUDENT, intent);
                    finish();
                }
            } else {
                if (mArrListSearchCourses != null && mArrListSearchCourses.size() > 0) {
                    Log.e("@@@@@@", "" + mArrListSearchCourses.get(position).getId());
                    Intent intent = new Intent()
                            .putExtra(KEY_INTENT_COURSE_ID, "" + mArrListSearchCourses.get(position).getId())
                            .putExtra(KEY_INTENT_COURSE_NAME,
                                    "" + mArrListSearchCourses.get(position).getName())
                            .putExtra(KEY_INTENT_COURSE_IMAGE, "" + mArrListSearchCourses.get(position).getImage());
                    setResult(CODE_COURSE_SELECTION_FOR_ADD_STUDENT, intent);
                    finish();
                }
            }
        } else {
            if(CommonUtil.isNullString(mStrSearchValue))
            {
                startActivity(new Intent(CourseListActivity.this, CourseDetailsActivity.class)
                        .putExtra(INTENT_KEY_COURSE_ID, "" + mArrListCourses.get(position).getId()));
            }else{
                startActivity(new Intent(CourseListActivity.this, CourseDetailsActivity.class)
                        .putExtra(INTENT_KEY_COURSE_ID, "" + mArrListSearchCourses.get(position).getId()));
            }

        }
    }

    @Override
    public void onDeleteCourse(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CourseListActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCoursePosition = position;
                callApiToDeleteCourseList(deleteCoursePosition);
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_new_inquiry:
                startActivity(new Intent(CourseListActivity.this, CourseLiteratureActivity.class));
                break;
        }
    }
}

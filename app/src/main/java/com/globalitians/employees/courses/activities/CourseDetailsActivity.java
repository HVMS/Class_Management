package com.globalitians.employees.courses.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.model.ModelCourseDetails;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_DETAILS;
import static com.globalitians.employees.utility.Constants.INTENT_KEY_COURSE_ID;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class CourseDetailsActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener {

    private String mStrCourseId = "";
    private SwipeRefreshLayout mSwipeToRefresh;
    private TextView mTvCourseName;
    private TextView mTvCourseDuration;
    private TextView mTvCourseFees;
    private TextView mTvCourseMarquee;
    private TextView mTvCourseDescription;
    private ImageView mIvReadPdf;
    private ImageView mIvCourse;
    private ModelCourseDetails modelCourseDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(CourseDetailsActivity.this);
        setContentView(R.layout.activity_course_detais);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        findViews();
        callApiToLoadCourseList();
        mTvCourseName.setSelected(true);
    }

    private void initialize() {
        Intent intent_Course = getIntent();
        if (intent_Course != null) {
            mStrCourseId = intent_Course.getStringExtra(INTENT_KEY_COURSE_ID);
        }
    }

    private void findViews() {
        mSwipeToRefresh = findViewById(R.id.swipeToRefreshCoursesDetails);

        mTvCourseName = findViewById(R.id.tv_course_name);
        mTvCourseDuration = findViewById(R.id.tv_course_duration);
        mTvCourseFees = findViewById(R.id.tv_course_fees);
        mTvCourseDescription = findViewById(R.id.tv_course_description);
        mTvCourseMarquee = findViewById(R.id.tv_course_marquee);

        mIvCourse = findViewById(R.id.iv_course_unique);
        mIvReadPdf = findViewById(R.id.iv_read_pdf);
        mIvReadPdf.setOnClickListener(this);

        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (modelCourseDetails != null) {
                    modelCourseDetails = null;
                }
                callApiToLoadCourseList();
            }
        });
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

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_COURSE_DETAILS:
                final Gson courseDetailsGson = new Gson();
                try {
                    modelCourseDetails = courseDetailsGson
                            .fromJson(response, ModelCourseDetails.class);

                    if (modelCourseDetails.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mTvCourseName.setText("" + modelCourseDetails.getCourseDetail().getName());
                        mTvCourseFees.setText("Fees(Apx.):" + modelCourseDetails.getCourseDetail().getFees());
                        mTvCourseDuration.setText("Duration: " + modelCourseDetails.getCourseDetail().getDuration());
                        CommonUtil.setImageToGlide(CourseDetailsActivity.this, mIvCourse, "" + modelCourseDetails.getCourseDetail().getImage().getFilePath());
                        Log.e(">>>>>",""+modelCourseDetails.getCourseDetail().getImage());
                    }
                    if (mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }
                    Toast.makeText(this, "" + modelCourseDetails.getMessage(), Toast.LENGTH_SHORT).show();
                    playSoundForAttendance("" + modelCourseDetails.getMessage(),CourseDetailsActivity.this);
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

    private void callApiToLoadCourseList() {
        if (!CommonUtil.isInternetAvailable(CourseDetailsActivity.this)) {
            return;
        }

        new OkHttpRequest(CourseDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_COURSE_DETAILS,
                RequestParam.loadCourseDetails(mStrCourseId,
                        ""+getSharedPrefrencesInstance(CourseDetailsActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_COURSE_DETAILS,
                true, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_read_pdf:
                if (!CommonUtil.isInternetAvailable(CourseDetailsActivity.this)) {
                    return;
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + modelCourseDetails.getCourseDetail().getSyllabus().getFilePath()));
                startActivity(browserIntent);
                break;
        }
    }
}

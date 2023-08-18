package com.globalitians.employees.courses.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.adapters.CourseLiteraturePagerAdapter;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;

public class CourseLiteratureActivity extends AppCompatActivity {

    private ViewPager mVpCourseLiterature;
    private TabLayout mCircleIndicatorTabLayout;
    private int mArrCourseLiterature[] = {
            R.drawable.ic_course_literature1,
            R.drawable.ic_course_literature2,
            R.drawable.ic_course_literature3,
            R.drawable.ic_course_literature4,
            R.drawable.ic_course_literature5,
            R.drawable.ic_course_literature6,
            R.drawable.ic_course_literature7,};
    private CourseLiteraturePagerAdapter mAdapterCourseLiterature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(CourseLiteratureActivity.this);
        setContentView(R.layout.activity_course_literature_gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        setCourseLiteratureAdapter();
    }

    private void setCourseLiteratureAdapter() {
        CourseLiteraturePagerAdapter mAdapter = new CourseLiteraturePagerAdapter(CourseLiteratureActivity.this, mArrCourseLiterature);
        mVpCourseLiterature.setAdapter(mAdapter);
        mCircleIndicatorTabLayout.setupWithViewPager(mVpCourseLiterature, true);
    }

    private void findViews() {
        mVpCourseLiterature = findViewById(R.id.vp_course_literature);
        mCircleIndicatorTabLayout = findViewById(R.id.tabDots);
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

}

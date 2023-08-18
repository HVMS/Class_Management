package com.globalitians.employees.students.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.globalitians.employees.students.fragments.Filter_CoursesListFragment;
import com.globalitians.employees.students.fragments.Filter_MonthListFragment;
import com.globalitians.employees.students.fragments.Filter_StudentTypesListFragment;

public class StudentFilterPagerAdapter extends FragmentPagerAdapter {

    public StudentFilterPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {
            fragment = new Filter_MonthListFragment();
        } else if (position == 1) {
            fragment = new Filter_CoursesListFragment();
        } else if (position == 2) {
            fragment = new Filter_StudentTypesListFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

}

package com.globalitians.employees.students.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.globalitians.employees.students.fragments.CompletedStudentListFragment;
import com.globalitians.employees.students.fragments.RunningStudentListFragment;

public class StudentListPagerAdapter extends FragmentPagerAdapter {

    //from is for From TabbedStudentList(complete, uncomplete)
    //and From TabbedFeesList (paid, unpaid)
    String strFrom="";
    public StudentListPagerAdapter(FragmentManager fm,String from) {
        super(fm);
        this.strFrom=from;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new RunningStudentListFragment();
        }
        else if (position == 1)
        {
            fragment = new CompletedStudentListFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "RUNNING";
        }
        else if (position == 1)
        {
            title = "COMPLETED";
        }
        return title;
    }
}
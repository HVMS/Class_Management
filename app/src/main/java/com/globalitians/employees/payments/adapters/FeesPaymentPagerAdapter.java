package com.globalitians.employees.payments.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.globalitians.employees.payments.fragments.PaidFeesListFragment;
import com.globalitians.employees.payments.fragments.UnPaidFeesListFragment;
import com.globalitians.employees.students.models.ModelStudent;

public class FeesPaymentPagerAdapter extends FragmentPagerAdapter {

    private ModelStudent modelStudent;
    public FeesPaymentPagerAdapter(FragmentManager fm, ModelStudent modelStudent) {
        super(fm);
        this.modelStudent=modelStudent;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new PaidFeesListFragment();
        }
        else if (position == 1)
        {
            fragment = new UnPaidFeesListFragment();
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
        float totalPaidAmount;
        float totalUnpaidAmount;
        for(int i=0;i<modelStudent.getStudents().size();i++)
        if (position == 0)
        {
            title = "Paid Fees";
        }
        else if (position == 1)
        {
            title = "Unpaid Fees";
        }
        else if (position == 2)
        {
            title = "Total Fees";
        }
        return title;
    }
}
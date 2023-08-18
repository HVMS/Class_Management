package com.globalitians.employees.payments.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.globalitians.employees.payments.fragments.details.PaidFeesAmountFragment;
import com.globalitians.employees.payments.fragments.details.TotalFeesAmountFragment;
import com.globalitians.employees.payments.fragments.details.UnpaidFeesAmountFragment;

public class StudentFeesPaymentPagerAdapter extends FragmentPagerAdapter {

    public StudentFeesPaymentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new PaidFeesAmountFragment();
        }
        else if (position == 1)
        {
            fragment = new UnpaidFeesAmountFragment();
        }else if(position==2)
        {
            fragment = new TotalFeesAmountFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
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
package com.globalitians.employees.courses.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.globalitians.employees.R;
import com.globalitians.employees.utility.TouchImageView;

public class CourseLiteraturePagerAdapter extends PagerAdapter {
    private LayoutInflater layoutInflater;
    private Activity mContext;
    private int mArrCourseLiteratuer[] = null;

    public CourseLiteraturePagerAdapter(Activity context, int[] mArrCourseLiteratuer) {
        this.mContext = context;
        this.mArrCourseLiteratuer = mArrCourseLiteratuer;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mArrCourseLiteratuer.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.view_course_literature, container, false);
        TouchImageView ivPostImage = itemView.findViewById(R.id.iv_course_literature);
        ivPostImage.setImageResource(mArrCourseLiteratuer[position]);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
        //container.removeView((RelativeLayout) object);
    }
}

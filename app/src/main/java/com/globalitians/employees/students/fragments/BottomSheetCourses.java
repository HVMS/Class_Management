package com.globalitians.employees.students.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalitians.employees.R;

public class BottomSheetCourses extends BottomSheetDialogFragment {

    private RecyclerView mRvCourses;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_filter_courses, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        mRvCourses=rootView.findViewById(R.id.rv_course_list);
        /*mAdapterCourseList = new FilterCourseListAdapter(getActivity(), mArrListCourses, this, "add_inquiry");
        lvCourses.setAdapter(mAdapterCourseList);*/
    }
}

package com.example.caloriecounter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RoutinePagerAdapter extends FragmentPagerAdapter {

    private final String[] daysOfWeek = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    public RoutinePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return RoutineDayFragment.newInstance(daysOfWeek[position]);
    }

    @Override
    public int getCount() {
        return daysOfWeek.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return daysOfWeek[position];
    }
}
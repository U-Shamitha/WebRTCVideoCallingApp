package com.mad_lab.webrtcvideocallapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerExploreAdapter extends FragmentStateAdapter {

    ArrayList<Fragment> arrExploreFragments = new ArrayList<>();
    ArrayList<String> arrExploreFragmentTitles = new ArrayList<>();

    public ViewPagerExploreAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public String getTabTitle(int position){
        return arrExploreFragmentTitles.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        arrExploreFragments.add(fragment);
        arrExploreFragmentTitles.add(title);
    }

    @Override
    public Fragment createFragment(int position) {
        return arrExploreFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return arrExploreFragments.size(); //no. of tabs
    }


}

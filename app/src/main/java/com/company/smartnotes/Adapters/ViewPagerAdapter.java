package com.company.smartnotes.Adapters;



import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.company.smartnotes.Fragments.ReminderFragment;
import com.company.smartnotes.Fragments.SimpleNoteFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position)
        {
            case 0:
            {
                fragment= SimpleNoteFragment.getInstance();
                break;
            }

            case 1:
            {
                fragment = ReminderFragment.getInstance();
                break;
            }

            default:
                return null;

        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

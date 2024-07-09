package com.bingwa.bingwasokonibot.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bingwa.bingwasokonibot.fragments.AutorenewalsFragment;
import com.bingwa.bingwasokonibot.fragments.InboxFragment;
import com.bingwa.bingwasokonibot.fragments.MainContentFragment;
import com.bingwa.bingwasokonibot.fragments.MakeOfferFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MainContentFragment();
            case 1:
                return new MakeOfferFragment();
            case 2:
                return new AutorenewalsFragment();
            case 3:
                return new InboxFragment();
            default:
                return new MainContentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}

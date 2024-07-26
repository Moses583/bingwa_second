package com.ujuzi.bingwasokonibot.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ujuzi.bingwasokonibot.fragments.AutorenewalsFragment;
import com.ujuzi.bingwasokonibot.fragments.InboxFragment;
import com.ujuzi.bingwasokonibot.fragments.MainContentFragment;
import com.ujuzi.bingwasokonibot.fragments.MakeOfferFragment;
import com.ujuzi.bingwasokonibot.fragments.TransactionsFragment;

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
                return new TransactionsFragment();
            case 2:
                return new MakeOfferFragment();
            case 3:
                return new AutorenewalsFragment();
            case 4:
                return new InboxFragment();
            default:
                return new MainContentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

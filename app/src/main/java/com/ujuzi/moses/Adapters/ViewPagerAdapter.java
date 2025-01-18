package com.ujuzi.moses.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ujuzi.moses.fragments.InboxFragment;
import com.ujuzi.moses.fragments.MainContentFragment;
import com.ujuzi.moses.fragments.MakeOfferFragment;
import com.ujuzi.moses.fragments.OffersFragment;
import com.ujuzi.moses.fragments.TransactionsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new TransactionsFragment();
            case 2:
                return new MakeOfferFragment();
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
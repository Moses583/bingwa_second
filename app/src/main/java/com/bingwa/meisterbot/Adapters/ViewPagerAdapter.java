package com.bingwa.meisterbot.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bingwa.meisterbot.fragments.InboxFragment;
import com.bingwa.meisterbot.fragments.MainContentFragment;
import com.bingwa.meisterbot.fragments.MakeOfferFragment;

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
                return new InboxFragment();
            default:
                return new MainContentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

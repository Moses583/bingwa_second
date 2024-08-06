package com.ujuzi.moses.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ujuzi.moses.R;
import com.google.android.material.tabs.TabLayout;


public class OffersFragment extends Fragment {


        public OffersFragment() {
        // Required empty public constructor
    }

    private TabLayout myTabs;
    private FrameLayout frame;
    private MakeOfferFragment makeOfferFragment;
    private AutorenewalsFragment autorenewalsFragment;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offers, container, false);
        initViews(view);

        myTabs.addTab(myTabs.newTab().setText("Offers"));
        myTabs.addTab(myTabs.newTab().setText("Auto-renewals"));
        makeOfferFragment = new MakeOfferFragment();
        autorenewalsFragment = new AutorenewalsFragment();
        autorenewalsFragment.setContext(getActivity());

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.offersFrameContainer,makeOfferFragment)
                .commit();

        myTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.offersFrameContainer,makeOfferFragment)
                                .commit();
                        break;
                    case 1:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.offersFrameContainer,autorenewalsFragment)
                                .commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    private void initViews(View view) {
        myTabs = view.findViewById(R.id.offersTabs);
    }
}
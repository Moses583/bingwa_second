package com.ujuzi.moses.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ujuzi.moses.R;
import com.google.android.material.tabs.TabLayout;


public class TransactionsFragment extends Fragment {


    public TransactionsFragment() {
        // Required empty public constructor
    }

    private TabLayout myTabs;
    private SuccessfulFragment successfulFragment;
    private FailedFragment failedFragment;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        initViews(view);

        myTabs.addTab(myTabs.newTab().setText("Successfull"));
        myTabs.addTab(myTabs.newTab().setText("Failed"));
        successfulFragment = new SuccessfulFragment();
        failedFragment = new FailedFragment();

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.transactionsFrameContainer,successfulFragment)
                .commit();

        myTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.transactionsFrameContainer,successfulFragment)
                                .commit();
                        break;
                    case 1:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.transactionsFrameContainer,failedFragment)
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
        myTabs = view.findViewById(R.id.transactionTabs);
    }
}
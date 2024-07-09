package com.bingwa.bingwasokonibot.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bingwa.bingwasokonibot.Adapters.ItemListAdapter;
import com.bingwa.bingwasokonibot.Adapters.RenewalsListAdapter;
import com.bingwa.bingwasokonibot.CreateRenewalActivity;
import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.R;
import com.bingwa.bingwasokonibot.models.OfferPOJO;
import com.bingwa.bingwasokonibot.models.RenewalPOJO;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutorenewalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutorenewalsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView myRecycler;
    private ExtendedFloatingActionButton create,upload;
    private FloatingActionButton actions;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean show = true;
    private List<RenewalPOJO> pojos = new ArrayList<>();
    private DBHelper dbHelper;
    private RenewalsListAdapter listAdapter;


    public AutorenewalsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AutorenewalsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AutorenewalsFragment newInstance(String param1, String param2) {
        AutorenewalsFragment fragment = new AutorenewalsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autorenewals, container, false);
        initViews(view);

        dbHelper = new DBHelper(getActivity());
        showData();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateRenewalActivity.class));
            }
        });

        actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show){
                    create.show();
                    upload.show();
                    actions.setImageResource(R.drawable.ic_clear);
                    show = false;
                }else{
                    create.hide();
                    upload.hide();
                    show = true;
                    actions.setImageResource(R.drawable.ic_actions);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    private void refresh() {
        showData();
    }

    private void showData() {
        pojos.clear();
        Cursor cursor = dbHelper.getRenewals();
        if (cursor.getCount() == 0){
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            while (cursor.moveToNext()){
                String frequency = cursor.getString(1);
                String ussdCode = cursor.getString(2);
                String period = cursor.getString(3);
                String tillNumber = cursor.getString(4);
                String time = cursor.getString(5);
                RenewalPOJO pojo = new RenewalPOJO(frequency,ussdCode,period,tillNumber,time);
                pojos.add(pojo);
            }
            swipeRefreshLayout.setRefreshing(false);
        }
        cursor.close();
        listAdapter = new RenewalsListAdapter(getActivity());
        listAdapter.setRenewalList(pojos);
        myRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecycler.setHasFixedSize(true);
        myRecycler.setAdapter(listAdapter);
    }

    public String tillNumber(){
        Cursor cursor = dbHelper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "till number absent", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }
    private void initViews(View view) {
        myRecycler = view.findViewById(R.id.autorenewalsRecycler);
        create = view.findViewById(R.id.btnCreateRenewals);
        upload = view.findViewById(R.id.btnUploadAllRenewals);
        actions = view.findViewById(R.id.btnChoices2);
        swipeRefreshLayout = view.findViewById(R.id.swipeRenewalsOffers);
    }
}
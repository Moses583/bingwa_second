package com.ujuzi.moses.fragments;

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

import com.ujuzi.moses.Adapters.ItemListAdapter;
import com.ujuzi.moses.CreateOfferActivity;
import com.ujuzi.moses.DBHelper;
import com.ujuzi.moses.R;
import com.ujuzi.moses.RequestManager;
import com.ujuzi.moses.listeners.PostOfferListener;
import com.ujuzi.moses.models.OfferListResponse;
import com.ujuzi.moses.models.OfferPOJO;
import com.ujuzi.moses.models.PostOfferApiResponse;
import com.ujuzi.moses.models.PostOfferOne;
import com.ujuzi.moses.models.PostOfferTwo;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeOfferFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private List<OfferListResponse> offerPOJOS = new ArrayList<>();
    private List<OfferPOJO> pojos = new ArrayList<>();
    private ItemListAdapter listAdapter;
    RequestManager manager;
    DBHelper dbHelper,helper2;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ExtendedFloatingActionButton fab,fab2;
    private FloatingActionButton actions;
    private boolean show = true;


    public MakeOfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MakeOfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakeOfferFragment newInstance(String param1, String param2) {
        MakeOfferFragment fragment = new MakeOfferFragment();
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
        View view = inflater.inflate(R.layout.fragment_make_offer, container, false);
        initViews(view);

        manager = new RequestManager(getActivity());
        String id = Build.ID;
        dbHelper = new DBHelper(getActivity());
        listAdapter = new ItemListAdapter(getActivity());



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), CreateOfferActivity.class));
            }
        });
        actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show){
                    fab.show();
                    fab2.show();
                    actions.setImageResource(R.drawable.ic_clear);
                    show = false;
                }else{
                    fab.hide();
                    fab2.hide();
                    show = true;
                    actions.setImageResource(R.drawable.ic_actions);
                }
            }
        });
        showData();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listAdapter);
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
        Cursor cursor = dbHelper.getOffers();
        if (cursor.getCount() == 0){
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            while (cursor.moveToNext()){
                String name = cursor.getString(1);
                String amount = cursor.getString(2);
                String ussdCode = cursor.getString(3);
                String dialSim = cursor.getString(4);
                String dialSimId = cursor.getString(5);
                String paymentSim = cursor.getString(6);
                String paymentSimId = cursor.getString(7);
                String offerTill = cursor.getString(8);
                OfferPOJO pojo = new OfferPOJO(name,amount,ussdCode,dialSim,Build.ID,dialSimId,paymentSim,paymentSimId,offerTill);
                pojos.add(pojo);
            }
            swipeRefreshLayout.setRefreshing(false);
        }
        cursor.close();
        listAdapter.setOfferList(pojos);
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
        recyclerView = view.findViewById(R.id.offersRecycler);
        fab = view.findViewById(R.id.btnCreateOffer);
        fab2 = view.findViewById(R.id.btnUploadAllOffers);
        actions = view.findViewById(R.id.btnChoices);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshOffers);
    }
}
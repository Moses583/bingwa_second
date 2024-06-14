package com.example.meisterbot.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.meisterbot.Adapters.ItemListAdapter;
import com.example.meisterbot.CreateOfferActivity;
import com.example.meisterbot.DBHelper;
import com.example.meisterbot.HelpActivity;
import com.example.meisterbot.HomeActivity;
import com.example.meisterbot.R;
import com.example.meisterbot.RequestManager;
import com.example.meisterbot.listeners.GetOffersListener;
import com.example.meisterbot.models.GetOfferApiResponse;
import com.example.meisterbot.models.OfferListResponse;
import com.example.meisterbot.models.OfferPOJO;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;

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
    DBHelper dbHelper;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ExtendedFloatingActionButton fab;

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

//        manager = new RequestManager(getActivity());
        String id = Build.ID;
//        manager.getOffers(listener,id);

        dbHelper = new DBHelper(getActivity());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), CreateOfferActivity.class));
            }
        });
        showData();
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

    private final GetOffersListener listener = new GetOffersListener() {
        @Override
        public void didFetch(List<GetOfferApiResponse> responseList, String message) {

        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    };


    private void showData() {
        pojos.clear();
        Cursor cursor = dbHelper.getOffers();
        if (cursor.getCount() == 0){
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            while (cursor.moveToNext()){
                String amount = cursor.getString(1);
                String ussdCode = cursor.getString(2);
                String dialSim = cursor.getString(3);
                String dialSimId = cursor.getString(4);
                String paymentSim = cursor.getString(5);
                String paymentSimId = cursor.getString(6);
                String offerTill = cursor.getString(7);
                OfferPOJO pojo = new OfferPOJO(amount,ussdCode,dialSim,Build.ID,dialSimId,paymentSim,paymentSimId,offerTill);
                pojos.add(pojo);
            }
            swipeRefreshLayout.setRefreshing(false);
        }
        cursor.close();
        listAdapter = new ItemListAdapter(getActivity());
        listAdapter.setOfferList(pojos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.offersRecycler);
        fab = view.findViewById(R.id.btnCreateOffer);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshOffers);
    }
}
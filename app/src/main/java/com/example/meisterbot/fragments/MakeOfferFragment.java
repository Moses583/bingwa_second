package com.example.meisterbot.fragments;

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

import com.example.meisterbot.Adapters.ItemListAdapter;
import com.example.meisterbot.CreateOfferActivity;
import com.example.meisterbot.DBHelper;
import com.example.meisterbot.R;
import com.example.meisterbot.RequestManager;
import com.example.meisterbot.listeners.GetOffersListener;
import com.example.meisterbot.listeners.PostOfferListener;
import com.example.meisterbot.models.GetOffersBody;
import com.example.meisterbot.models.GetOffersList;
import com.example.meisterbot.models.GetOffersResponse;
import com.example.meisterbot.models.OfferListResponse;
import com.example.meisterbot.models.OfferPOJO;
import com.example.meisterbot.models.PostOfferApiResponse;
import com.example.meisterbot.models.PostOfferOne;
import com.example.meisterbot.models.PostOfferTwo;
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
    DBHelper dbHelper;

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
        callPostOfferApi();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), CreateOfferActivity.class));
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        callPostOfferApi();
    }

    private void callPostOfferApi(){
        manager.postOffer(listener2, postOffers());
    }

    private final PostOfferListener listener2 = new PostOfferListener() {
        @Override
        public void didFetch(PostOfferApiResponse response, String message) {
            Toast.makeText(getActivity(), response.message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didError(String message) {
            if (message.contains("Unable to resolve host")){
                Toast.makeText(getActivity(), "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private PostOfferOne postOffers(){
        String till = tillNumber();
        List<PostOfferTwo> list = new ArrayList<>();
        for (OfferPOJO pojo :
                pojos) {
            list.add(new PostOfferTwo(pojo.getName(), pojo.getAmount(), pojo.getUssdCode()));
        }

        return new PostOfferOne(till, list);
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
        listAdapter = new ItemListAdapter(getActivity());
        listAdapter.setOfferList(pojos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listAdapter);
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
        callPostOfferApi();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.offersRecycler);
        fab = view.findViewById(R.id.btnCreateOffer);
        fab2 = view.findViewById(R.id.btnUploadAllOffers);
        actions = view.findViewById(R.id.btnChoices);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshOffers);
    }
}
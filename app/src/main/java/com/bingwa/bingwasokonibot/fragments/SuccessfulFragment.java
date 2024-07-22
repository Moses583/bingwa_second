package com.bingwa.bingwasokonibot.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.bingwa.bingwasokonibot.Adapters.TransactionAdapter;
import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.R;
import com.bingwa.bingwasokonibot.models.TransactionPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SuccessfulFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuccessfulFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private List<TransactionPOJO> pojoList;
    private DBHelper helper;
    private TransactionAdapter adapter;

    public SuccessfulFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuccessfulFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SuccessfulFragment newInstance(String param1, String param2) {
        SuccessfulFragment fragment = new SuccessfulFragment();
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
        View view = inflater.inflate(R.layout.fragment_successful, container, false);
        initViews(view);
        pojoList = new ArrayList<>();
        helper = new DBHelper(getActivity());
        adapter = new TransactionAdapter(getActivity());
        showSuccess();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        searchView.clearFocus();
        searchView.setOnQueryTextListener(searchViewListener);
        showRecycler();
        return view;
    }

    private final SearchView.OnQueryTextListener searchViewListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            setFilteredList(newText);
            return true;
        }
    };

    private void setFilteredList(String text) {
        List<TransactionPOJO> list = new ArrayList<>();
        for (TransactionPOJO pojo :
                pojoList) {
            if (pojo.getUssd().toLowerCase().contains(text.toLowerCase())){
                list.add(pojo);
            }
        }
        if (list.isEmpty()){
            Log.d("SEARCH","Phone number doesn't exist");
        }else{
            Log.d("Search","found!!");
            showFilteredRecycler(list);
        }
    }

    private void showFilteredRecycler(List<TransactionPOJO> list) {
        adapter.setFilteredList(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void refresh() {
        showSuccess();
    }

    @Override
    public void onResume() {
        super.onResume();
        showSuccess();
    }

    private void showSuccess(){
        pojoList.clear();
        Cursor cursor = helper.getSuccessfulTransactions();
        if (cursor.getCount() == 0){
            swipeRefreshLayout.setRefreshing(false);
        }else{
            while (cursor.moveToNext()){
                String ussdResponse = cursor.getString(1);
                String amount = cursor.getString(2);
                String timeStamp = cursor.getString(3);
                String recipient = cursor.getString(4);
                String status = cursor.getString(5);
                int subId = cursor.getInt(6);
                String ussd = cursor.getString(7);
                int till = cursor.getInt(8);
                String messageFull = cursor.getString(9);
                pojoList.add(new TransactionPOJO(ussdResponse,amount,timeStamp,recipient,status,subId,ussd,till,messageFull));
            }
            swipeRefreshLayout.setRefreshing(false);
        }
        cursor.close();
        adapter.setPojoList(pojoList);
    }
    private void showRecycler(){
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshSuccess);
        recyclerView = view.findViewById(R.id.successRecycler);
        searchView = view.findViewById(R.id.searchSuccessful);
    }
}
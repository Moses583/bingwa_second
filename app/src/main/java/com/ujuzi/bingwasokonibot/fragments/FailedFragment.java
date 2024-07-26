package com.ujuzi.bingwasokonibot.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.ujuzi.bingwasokonibot.Adapters.TransactionAdapter;
import com.ujuzi.bingwasokonibot.DBHelper;
import com.ujuzi.bingwasokonibot.R;
import com.ujuzi.bingwasokonibot.models.TransactionPOJO;
import com.ujuzi.bingwasokonibot.services.RetryService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FailedFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<TransactionPOJO> pojoList;
    private DBHelper helper;
    private TransactionAdapter adapter;
    private SearchView searchView;
    private FloatingActionButton retry;

    public FailedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_failed, container, false);
        initViews(view);
        pojoList = new ArrayList<>();
        helper = new DBHelper(getActivity());
        adapter = new TransactionAdapter(getActivity());
        showFailed();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setFilteredList(newText);
                return true;
            }
        });
        showRecycler();
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Retrying failed transactions", Toast.LENGTH_SHORT).show();
                if (myService(getActivity())) {
                    Intent intent = new Intent(getActivity(), RetryService.class);
                    getActivity().stopService(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent intent = new Intent(getActivity(), RetryService.class);
                        getActivity().startForegroundService(intent);
                    }
                }
            }
        });

        return view;
    }
    public boolean myService(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RetryService.class.getName().equalsIgnoreCase(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void showRecycler() {
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

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
        adapter.setPojoList(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void refresh() {
        showFailed();
    }

    private void showFailed(){
        pojoList.clear();
        Cursor cursor = helper.getFailedTransactions();
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

    @Override
    public void onResume() {
        super.onResume();
        showFailed();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshFailed);
        recyclerView = view.findViewById(R.id.failedRecycler);
        searchView = view.findViewById(R.id.searchFailed);
        retry = view.findViewById(R.id.btnRetryFailedTransactions);
    }
}
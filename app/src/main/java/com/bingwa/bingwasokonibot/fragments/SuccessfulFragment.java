package com.bingwa.bingwasokonibot.fragments;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.bingwa.bingwasokonibot.Adapters.TransactionAdapter;
import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.R;
import com.bingwa.bingwasokonibot.models.TransactionPOJO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SuccessfulFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private List<TransactionPOJO> pojoList;
    private DBHelper helper;
    private TransactionAdapter adapter;
    private FloatingActionButton delete;
    private Dialog confirmDeletionDialog;
    private Button deleteTransaction;

    public SuccessfulFragment() {
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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletionDialog();
            }
        });
        return view;
    }
    private void showDeletionDialog(){
        confirmDeletionDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_transactions,null);
        deleteTransaction = view.findViewById(R.id.btnDeleteTransactions);
        confirmDeletionDialog.setContentView(view);
        confirmDeletionDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        confirmDeletionDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        confirmDeletionDialog.setCancelable(false);
        deleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
        confirmDeletionDialog.show();

    }
    private void deleteData(){
        boolean deleteData = helper.deleteSuccessfulTransactions();
        if (deleteData){
            Log.d("TAG","Data deleted");
        }
        else{
            Log.d("TAG", "Data not deleted");
        }
        pojoList.clear();
        adapter.setPojoList(pojoList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        confirmDeletionDialog.dismiss();
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
        delete = view.findViewById(R.id.btnDeleteSuccessfulTransactions);
    }
}
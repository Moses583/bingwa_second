package com.ujuzi.bingwasokonibot.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ujuzi.bingwasokonibot.Adapters.InboxListAdapter;
import com.ujuzi.bingwasokonibot.DBHelper;
import com.ujuzi.bingwasokonibot.R;
import com.ujuzi.bingwasokonibot.models.InboxListPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean bound = false;

    private RecyclerView recyclerView;
    private InboxListAdapter adapter;
    List<InboxListPOJO> inboxListPOJOList = new ArrayList<>();
    DBHelper helper;

    private SwipeRefreshLayout swipeRefreshLayout;

    public InboxFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InboxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InboxFragment newInstance(String param1, String param2) {
        InboxFragment fragment = new InboxFragment();
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
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        initViews(view);

        helper = new DBHelper(getActivity());
        adapter = new InboxListAdapter(getActivity());
        showData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void refresh() {
        showData();
    }

    private void showData() {
        inboxListPOJOList.clear();
        Cursor cursor = helper.getData();
        if (cursor.getCount() == 0){
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            while (cursor.moveToNext()){
                String message = cursor.getString(1);
                String timeStamp = cursor.getString(2);
                String sender = cursor.getString(3);
                inboxListPOJOList.add(new InboxListPOJO(message,timeStamp,sender));
            }
            swipeRefreshLayout.setRefreshing(false);
        }
        cursor.close();
        adapter.setInboxListPOJOList(inboxListPOJOList);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.inboxRecycler);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshInbox);
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }
}
package com.example.meisterbot.fragments;


import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meisterbot.Adapters.TransactionAdapter;
import com.example.meisterbot.CreateOfferActivity;
import com.example.meisterbot.CreatePasswordActivity;
import com.example.meisterbot.DBHelper;
import com.example.meisterbot.EnableServiceActivity;
import com.example.meisterbot.LoginActivity;
import com.example.meisterbot.PaymentPlanActivity;
import com.example.meisterbot.R;
import com.example.meisterbot.RequestManager;
import com.example.meisterbot.SettingsActivity;
import com.example.meisterbot.listeners.PaymentListener;
import com.example.meisterbot.models.Payment;
import com.example.meisterbot.models.TransactionPOJO;
import com.example.meisterbot.services.MyService;
import com.example.meisterbot.services.RetryService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainContentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DBHelper helper;
    List<TransactionPOJO> pojoList = new ArrayList<>();
    List<TransactionPOJO> failList = new ArrayList<>();
    TransactionAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView airtimeBalance, totalTransactions,failedTransactions;
    private RelativeLayout cancel, okay;
    Spinner spinner;

    private Button button;

    private Map<Integer, Integer> simMap;
    private ArrayList<String> simNames;
    private ArrayList<Integer> slotIndex;
    private TelephonyManager manager;
    private TelephonyManager.UssdResponseCallback callback;
    private Handler handler;
    private AlertDialog dialog;



    public MainContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainContentFragment newInstance(String param1, String param2) {
        MainContentFragment fragment = new MainContentFragment();
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
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        initViews(view);
        helper = new DBHelper(getActivity());
        showData();
        getFailedTransactions();
        simMap = new HashMap<>();
        simNames = new ArrayList<>();
        slotIndex = new ArrayList<>();




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        view.findViewById(R.id.txtCheckAirtimeBalance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myService(getActivity())) {
                    // If the service is running, stop it
                    Intent intent = new Intent(getActivity(), RetryService.class);
                    getActivity().stopService(intent);
                } else {
                    // If the service is not running, start it
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent intent = new Intent(getActivity(), RetryService.class);
                        getActivity().startForegroundService(intent);
                    }
                }
            }
        });

        return view;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_choose_sim,null);
        spinner = dialogView.findViewById(R.id.airtimeSpinner);
        cancel = dialogView.findViewById(R.id.airtimeCancel);
        okay = dialogView.findViewById(R.id.airtimeOkay);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
        listSimInfo();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialUssdCode(getDialSimCard(),"*144#");
            }
        });


    }
    private void getFailedTransactions(){
        Cursor cursor = helper.getFailedResponses();
        List<TransactionPOJO> queue1 = new ArrayList<>();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "There are no failed transactions", Toast.LENGTH_SHORT).show();
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
                queue1.add(new TransactionPOJO(ussdResponse,amount,timeStamp,recipient,status,subId,ussd,till,messageFull));
            }
        }
        failedTransactions.setText(String.valueOf(queue1.size()));
    }



    public void refresh(){
        showData();
        getFailedTransactions();
    }

    private void showData(){
        pojoList.clear();
        Cursor cursor = helper.getTransactions();
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
        totalTransactions.setText(String.valueOf(pojoList.size()));
        adapter = new TransactionAdapter(getActivity());
        adapter.setPojoList(pojoList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }
    public void listSimInfo(){
        simMap.clear();
        simNames.clear();
        slotIndex.clear();
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},101);
            return;
        }
        List<SubscriptionInfo> infoList = SubscriptionManager.from(getActivity()).getActiveSubscriptionInfoList();
        for (SubscriptionInfo info:
                infoList) {
            simMap.put(info.getSimSlotIndex(),info.getSubscriptionId());
            String slot = String.valueOf(info.getSimSlotIndex()+1);
            simNames.add(info.getCarrierName().toString()+" SIM: "+slot);
            slotIndex.add(info.getSimSlotIndex());
        }
        ArrayAdapter adapter1 = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,simNames);
        spinner.setAdapter(adapter1);
    }
    public int getDialSimCard(){
        String dialSim1;
        dialSim1 = spinner.getSelectedItem().toString();
        int subscriptionId = 0;
        if (dialSim1.contains("SIM: 1")){
            subscriptionId = simMap.get(0);
            dialUssdCode(subscriptionId,"*144#");
        }else if(dialSim1.contains("SIM: 2")){
            subscriptionId = simMap.get(1);
            dialUssdCode(subscriptionId,"*144#");
        }
        return subscriptionId;
    }
    private void dialUssdCode( int subscriptionId, String ussdCode) {
        manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            callback = new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    extractBalance(response.toString());
                    dialog.dismiss();
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    if (failureCode == -1){
                        airtimeBalance.setText("0");
                        dialog.dismiss();
                    }
                    dialog.dismiss();

                }
            };
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getActivity(),new String[]{Manifest.permission.CALL_PHONE},101);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createForSubscriptionId(subscriptionId).sendUssdRequest(ussdCode, callback, handler);
        }

    }
    private void extractBalance(String response){
        String regex = "Bal: (.*?)KSH";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            airtimeBalance.setText(matcher.group(1));
        } else {
            airtimeBalance.setText(0);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        showData();
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

    public String tillNumber(){
        Cursor cursor = helper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.transactionsRecycler);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshTransactions);
        airtimeBalance = view.findViewById(R.id.txtAirtimeBalance);
        totalTransactions = view.findViewById(R.id.txtTransactionsToday);
        button = view.findViewById(R.id.executeFailedTransactions);
        failedTransactions = view.findViewById(R.id.txtFailedTransactions);
    }
}
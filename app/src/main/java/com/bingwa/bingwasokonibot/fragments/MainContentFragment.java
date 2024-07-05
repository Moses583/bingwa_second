package com.bingwa.bingwasokonibot.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bingwa.bingwasokonibot.Adapters.TransactionAdapter;
import com.bingwa.bingwasokonibot.CreateOfferActivity;
import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.PaymentPlanActivity;
import com.bingwa.bingwasokonibot.R;
import com.bingwa.bingwasokonibot.RequestManager;
import com.bingwa.bingwasokonibot.listeners.PaymentListener;
import com.bingwa.bingwasokonibot.models.Payment;
import com.bingwa.bingwasokonibot.models.TransactionPOJO;
import com.bingwa.bingwasokonibot.services.MyService;
import com.bingwa.bingwasokonibot.services.RetryService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    private TextView airtimeBalance,totalTransactions,failedTransactions,executeFailedTransactions,checkBalance;
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
    private RequestManager requestManager;

    String till = "";
    private androidx.appcompat.app.AlertDialog offerCreationDialog,firstTimePayDialog,renewPlanDialog;



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
        requestManager = new RequestManager(getActivity());
        showData();
        getFailedTransactions();
        simMap = new HashMap<>();
        simNames = new ArrayList<>();
        slotIndex = new ArrayList<>();

        till = tillNumber();

        checkOffersOne();




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        checkBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        executeFailedTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            Log.d("TAG","There are no failed transactions");
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

    public void checkOffersOne(){
        Cursor cursor = helper.getOffers();
        if(cursor.getCount() == 0){
            showOfferCreationDialog();
        }else{
            callPaymentApi(till);
        }
    }

    private void showOfferCreationDialog(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_offer,null);
        Button btn = view.findViewById(R.id.btnNavigateToCreateOffer);
        builder.setView(view);
        offerCreationDialog = builder.create();
        offerCreationDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreateOfferActivity();
            }
        });
    }

    private void navigateToCreateOfferActivity() {
        startActivity(new Intent(getActivity(), CreateOfferActivity.class));
        offerCreationDialog.dismiss();
    }

    private void callPaymentApi(String till) {
        requestManager.getPaymentStatus(paymentListener,till);
    }


    private final PaymentListener paymentListener = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            confirm1(payment);
        }

        @Override
        public void didError(String message) {
            stopServiceOne();
            if (message.contains("Unable to resolve host")){
                Toast.makeText(getActivity(), "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void confirm1(Payment payment){
        if (payment.status.equalsIgnoreCase("error")){
            showFirstTimePayDialog();
        }else if (payment.status.equals("success")){
            compareDates(payment.data.timestamp);
        }
    }

    private void showFirstTimePayDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_first_time_pay,null);
        Button btn = view.findViewById(R.id.btnCheckAvailablePlans);
        builder.setView(view);
        firstTimePayDialog = builder.create();
        firstTimePayDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPlansActivity();
            }
        });
    }

    private void navigateToPlansActivity() {
        startActivity(new Intent(getActivity(), PaymentPlanActivity.class));
        firstTimePayDialog.dismiss();
    }

    public void compareDates(String two){
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(currentTimeMillis);
        Date current, expiry;
        try {
            current =  sdf.parse(time);
            expiry = sdf.parse(two);
            if (current.compareTo(expiry) > 0) {
                stopServiceOne();
                showRenewPlanDialog();
            } else if (current.compareTo(expiry) < 0) {
                startService();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void showRenewPlanDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_subscription_expired,null);
        Button btn = view.findViewById(R.id.btnRenewPlan);
        builder.setView(view);
        renewPlanDialog = builder.create();
        renewPlanDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPlansActivity2();
            }
        });
    }

    private void navigateToPlansActivity2() {
        startActivity(new Intent(getActivity(), PaymentPlanActivity.class));
        renewPlanDialog.dismiss();
    }

    public void stopServiceOne() {
        if (myService2()){
            Intent intent = new Intent(getActivity(), MyService.class);
            getActivity().stopService(intent);
            Toast.makeText(getActivity(), "App paused, you need to check your subscription", Toast.LENGTH_LONG).show();
        }
    }

    public void startService(){
        if (myService2()) {
            Log.d("MAIN_SERVICE","Service already running");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(getActivity(), MyService.class);
                getActivity().startForegroundService(intent);
                Toast.makeText(getActivity(), "App services are now available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean myService2(){
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equalsIgnoreCase(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    public String tillNumber(){
        Cursor cursor = helper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            Log.d("TILL","Till number absent");
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
        executeFailedTransactions = view.findViewById(R.id.executeFailedTransactions);
        failedTransactions = view.findViewById(R.id.txtFailedTransactions);
        checkBalance = view.findViewById(R.id.txtCheckAirtimeBalance);
    }
}
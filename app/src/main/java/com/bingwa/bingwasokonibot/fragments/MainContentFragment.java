package com.bingwa.bingwasokonibot.fragments;


import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getDrawable;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bingwa.bingwasokonibot.listeners.GetOffersListener;
import com.bingwa.bingwasokonibot.listeners.PaymentListener;
import com.bingwa.bingwasokonibot.models.GetOffersBody;
import com.bingwa.bingwasokonibot.models.GetOffersResponse;
import com.bingwa.bingwasokonibot.models.Payment;
import com.bingwa.bingwasokonibot.models.TransactionPOJO;
import com.bingwa.bingwasokonibot.services.MyService;
import com.bingwa.bingwasokonibot.services.RetryService;
import com.google.android.material.chip.ChipGroup;

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
public class MainContentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DBHelper helper,helper2;
    List<TransactionPOJO> pojoList = new ArrayList<>();

    private TextView airtimeBalance,totalTransactions,failedTransactions,txtLoading;
    private Button executeFailedTransactions,checkAirtimeBalance;
    private Button cancel, okay,btnContinue;
    Spinner spinner;
    private ChipGroup chipGroup;

    private Button deleteTransaction,navigateToCreateOffer,checkAvailablePlans,renewPlan,loadExistingOffers;

    private ImageView img;

    private Map<Integer, Integer> simMap;
    private ArrayList<String> simNames;
    private ArrayList<Integer> slotIndex;
    private TelephonyManager manager;
    private TelephonyManager.UssdResponseCallback callback;
    private Handler handler;

    private RequestManager requestManager;

    String till = "";
    private Dialog offerCreationDialog,firstTimePayDialog,renewPlanDialog,dialog,progressDialog,offerContinuationDialog;
    private BroadcastReceiver dateBroadCast;
    private Dialog confirmDeletionDialog;
    private ProgressBar progressBar;


    public MainContentFragment() {
        // Required empty public constructor
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
        simMap = new HashMap<>();
        simNames = new ArrayList<>();
        slotIndex = new ArrayList<>();
        till = tillNumber();

        checkOffersOne();
        showProgressDialog();
        showContinueDialog();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOffersOne();
                offerContinuationDialog.dismiss();
            }
        });
        checkAirtimeBalance.setOnClickListener(new View.OnClickListener() {
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
        dateBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
                    pojoList.clear();
                }
            }
        };

        return view;
    }
//    private void showDeletionDialog(){
//        confirmDeletionDialog = new Dialog(getActivity());
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_transactions,null);
//        deleteTransaction = view.findViewById(R.id.btnDeleteTransactions);
//        confirmDeletionDialog.setContentView(view);
//        confirmDeletionDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        confirmDeletionDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
//        confirmDeletionDialog.setCancelable(false);
//        deleteTransaction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteData();
//            }
//        });
//        confirmDeletionDialog.show();
//
//    }
//    private void deleteData(){
//        boolean deleteData = helper.deleteSuccessfulTransactions();
//        if (deleteData){
//            Log.d("TAG","Data deleted");
//        }
//        else{
//            Log.d("TAG", "Data not deleted");
//        }
//        pojoList.clear();
//        totalTransactions.setText(String.valueOf(pojoList.size()));
//        adapter.setPojoList(pojoList);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setHasFixedSize(true);
//        confirmDeletionDialog.dismiss();
//    }
    private void showAlertDialog() {
        dialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_choose_sim,null);
        cancel = view.findViewById(R.id.airtimeCancel);
        okay = view.findViewById(R.id.airtimeOkay);
        spinner = view.findViewById(R.id.airtimeSpinner);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        dialog.setCancelable(true);
        listSimInfo();
        dialog.show();
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
    public void listSimInfo(){
        simMap.clear();
        simNames.clear();
        slotIndex.clear();
        if (checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
        if (checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        getActivity().registerReceiver(dateBroadCast, new IntentFilter(Intent.ACTION_DATE_CHANGED));

    }
    @Override
    public void onPause() {
        super.onPause();
        getActivity().registerReceiver(dateBroadCast, new IntentFilter(Intent.ACTION_DATE_CHANGED));
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
        offerCreationDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_offer,null);
        navigateToCreateOffer = view.findViewById(R.id.btnNavigateToCreateOffer);
        loadExistingOffers = view.findViewById(R.id.btnLoadExistingOffers);
        offerCreationDialog.setContentView(view);
        offerCreationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        offerCreationDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        offerCreationDialog.setCancelable(false);
        navigateToCreateOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreateOfferActivity();
            }
        });
        loadExistingOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callGetOffersApi();
            }
        });
        offerCreationDialog.show();
    }
    private void callGetOffersApi() {
        offerCreationDialog.dismiss();
        progressDialog.show();
        requestManager.getOffers(getOffersListener,new GetOffersBody(tillNumber()),token());
    }
    private final GetOffersListener getOffersListener = new GetOffersListener() {
        @Override
        public void didFetch(List<GetOffersResponse> responses, String message) {
            progressDialog.dismiss();
            createOffersFromList(responses);
        }

        @Override
        public void didError(String message) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    };
    private void createOffersFromList(List<GetOffersResponse> responses) {
        boolean checkInsertOffer = true;
        for (GetOffersResponse response :
                responses) {
            checkInsertOffer = helper.insertOffer(
                    response.offer,
                    response.cost,
                    response.ussd,
                    response.dialSim,
                    response.subscriptionId,
                    response.paymentSim,
                    response.paymentSimId,
                    response.offerTill
            );
        }
        if (checkInsertOffer){
            offerContinuationDialog.show();
        }else{
            checkOffersOne();
        }
    }
    private void showContinueDialog() {
        offerContinuationDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_offer_continue,null);
        btnContinue = view.findViewById(R.id.btnOfferProceed);
        offerContinuationDialog.setContentView(view);
        offerContinuationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        offerContinuationDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        offerContinuationDialog.setCancelable(false);
    }
    private void showProgressDialog() {
        progressDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_progress_layout,null);
        progressBar = view.findViewById(R.id.myProgressBar);
        txtLoading = view.findViewById(R.id.txtProgress);
        progressDialog.setContentView(view);
        int widthInDp = 250;

        final float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        progressDialog.getWindow().setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        progressDialog.setCancelable(false);
        txtLoading.setText("Loading offers...");
    }
    private void navigateToCreateOfferActivity() {
        startActivity(new Intent(getActivity(), CreateOfferActivity.class));
        offerCreationDialog.dismiss();
    }
    private void callPaymentApi(String till) {
        requestManager.getPaymentStatus(paymentListener,till,token());
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
        firstTimePayDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_first_time_pay,null);
        checkAvailablePlans = view.findViewById(R.id.btnCheckAvailablePlans);
        firstTimePayDialog.setContentView(view);
        firstTimePayDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        firstTimePayDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        firstTimePayDialog.setCancelable(false);
        checkAvailablePlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPlansActivity();
            }
        });
        firstTimePayDialog.show();
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
        renewPlanDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_subscription_expired,null);
        renewPlan = view.findViewById(R.id.btnRenewPlan);
        renewPlanDialog.setContentView(view);
        renewPlanDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        renewPlanDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        renewPlanDialog.setCancelable(false);
        renewPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPlansActivity2();
            }
        });
        renewPlanDialog.show();
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
    public String token(){
        helper2 = new DBHelper(getActivity());
        Cursor cursor = helper2.getToken();
        String token = "";
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "token not found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){

                token = cursor.getString(0);
            }
        }
        cursor.close();
        return "Bearer "+token;
    }
    private void initViews(View view) {
        airtimeBalance = view.findViewById(R.id.txtAirtimeBalance);
        totalTransactions = view.findViewById(R.id.txtTransactionsToday);
        executeFailedTransactions = view.findViewById(R.id.retryFailedTransactions);
        failedTransactions = view.findViewById(R.id.txtFailedTransactions);
        checkAirtimeBalance = view.findViewById(R.id.checkAirtimeBalance);
    }
}
package com.ujuzi.moses.fragments;


import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getDrawable;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.work.impl.model.Preference;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ujuzi.moses.CreateOfferActivity;
import com.ujuzi.moses.DBHelper;
import com.ujuzi.moses.R;
import com.ujuzi.moses.RequestManager;
import com.ujuzi.moses.models.InboxListPOJO;
import com.ujuzi.moses.models.OfferPOJO;
import com.ujuzi.moses.models.RenewalPOJO;
import com.ujuzi.moses.models.TransactionPOJO;
import com.ujuzi.moses.services.MyService;
import com.ujuzi.moses.utilities.Constants;
import com.ujuzi.moses.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MainContentFragment extends Fragment {

    private DBHelper helper;
    List<TransactionPOJO> pojoList = new ArrayList<>();
    List<TransactionPOJO> pojoList2 = new ArrayList<>();
    List<OfferPOJO> pojos = new ArrayList<>();
    List<RenewalPOJO> pojos2 = new ArrayList<>();
    List<InboxListPOJO> inboxListPOJOList = new ArrayList<>();

    private TextView txtStoreName,txtLink,txtTillNumber,txtTotal,txtSuccess,txtFailed,txtAirtime,txtOffers,txtRenewals,txtMessages,refreshAirtimeBalance;
    private Button cancel, okay;
    Spinner spinner;

    private Button navigateToCreateOffer;

    private Map<Integer, Integer> simMap;
    private ArrayList<String> simNames;
    private ArrayList<Integer> slotIndex;
    private TelephonyManager manager;
    private TelephonyManager.UssdResponseCallback callback;
    private Handler handler;

    private RequestManager requestManager;
    private PreferenceManager preferenceManager;

    String till = "12345678";
    private Dialog offerCreationDialog,dialog;


    public MainContentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        initViews(view);
        helper = new DBHelper(getActivity());
        requestManager = new RequestManager(getActivity());
        preferenceManager = new PreferenceManager(requireActivity());
        simMap = new HashMap<>();
        simNames = new ArrayList<>();
        slotIndex = new ArrayList<>();

        checkOffersOne();
        showSuccess();
        showFailed();
        getOffers();
        getRenewals();
        getMessages();
        refreshAirtimeBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        showDashStatistics();
        return view;
    }

    private void showDashStatistics(){
        txtTillNumber.setText(till);
        txtLink.setSelected(true);
        txtLink.setText("www.ravemaster.ra.ke");
        txtSuccess.setText(String.valueOf(pojoList.size()));
        txtFailed.setText(String.valueOf(pojoList2.size()));
        int total = pojoList.size()+pojoList2.size();
        txtTotal.setText(String.valueOf(total));
        txtOffers.setText(String.valueOf(pojos.size()));
        txtRenewals.setText(String.valueOf(pojos2.size()));
        txtMessages.setText(String.valueOf(inboxListPOJOList.size()));
        txtStoreName.setText("Ravemaster");
        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, txtLink.getText().toString());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }
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
                        txtAirtime.setText("0");
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
            txtAirtime.setText(matcher.group(1));
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        checkOffersOne();
    }
    @Override
    public void onPause() {
        super.onPause();
        checkOffersOne();
    }

    public void checkOffersOne(){
        Cursor cursor = helper.getOffers();
        if(cursor.getCount() == 0){
            stopServiceOne();
            showOfferCreationDialog();
        }else{
            checkPause();
        }
    }

    private void checkPause() {
        boolean isAppPaused = preferenceManager.getBoolean(Constants.KEY_IS_APP_PAUSED);
        if (isAppPaused){
            Toast.makeText(requireActivity(), "App is paused", Toast.LENGTH_SHORT).show();
        } else {
            startService();
        }
    }

    private void showOfferCreationDialog(){
        offerCreationDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_offer,null);
        navigateToCreateOffer = view.findViewById(R.id.btnNavigateToCreateOffer);
        offerCreationDialog.setContentView(view);
        offerCreationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        offerCreationDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        offerCreationDialog.setCancelable(false);
        navigateToCreateOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerCreationDialog.dismiss();
                navigateToCreateOfferActivity();
            }
        });
        offerCreationDialog.show();
    }
    private void navigateToCreateOfferActivity() {
        startActivity(new Intent(getActivity(), CreateOfferActivity.class));
    }
    public void stopServiceOne() {
        if (myService2()){
            Intent intent = new Intent(getActivity(), MyService.class);
            getActivity().stopService(intent);
            Toast.makeText(getActivity(), "App paused, you need to check your offers list", Toast.LENGTH_LONG).show();
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
    private void showSuccess(){
        pojoList.clear();
        Cursor cursor = helper.getSuccessfulTransactions();
        if (cursor.getCount() == 0){
            Log.d("TAG","Success empty");
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
        }
        cursor.close();
    }
    private void showFailed(){
        pojoList2.clear();
        Cursor cursor = helper.getFailedTransactions();
        if (cursor.getCount() == 0){
            Log.d("TAG","Showing failed transactions failed");
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
                pojoList2.add(new TransactionPOJO(ussdResponse,amount,timeStamp,recipient,status,subId,ussd,till,messageFull));
            }
        }
        cursor.close();
    }
    private void getOffers() {
        pojos.clear();
        Cursor cursor = helper.getOffers();
        if (cursor.getCount() == 0){
            Log.d("TAG","Loading offers failed");
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
        }
        cursor.close();
    }
    private void getRenewals() {
        pojos2.clear();
        Cursor cursor = helper.getRenewals();
        if (cursor.getCount() == 0){
            Log.d("TAG","Loading renewals failed");
        }
        else {
            while (cursor.moveToNext()){
                String frequency = cursor.getString(1);
                String ussdCode = cursor.getString(2);
                int period = cursor.getInt(3);
                String tillNumber = cursor.getString(4);
                String time = cursor.getString(5);
                String dialSim = cursor.getString(6);
                String money = cursor.getString(7);
                String date1 = cursor.getString(8);
                String date2 = cursor.getString(9);
                RenewalPOJO pojo = new RenewalPOJO(frequency,ussdCode,period,tillNumber,time,dialSim,money,date1,date2);
                pojos2.add(pojo);
            }
        }
        cursor.close();
    }
    private void getMessages() {
        inboxListPOJOList.clear();
        Cursor cursor = helper.getData();
        if (cursor.getCount() == 0){
            Log.d("TAG","Loading inbox failed");
        }
        else {
            while (cursor.moveToNext()){
                String message = cursor.getString(1);
                String timeStamp = cursor.getString(2);
                String sender = cursor.getString(3);
                inboxListPOJOList.add(new InboxListPOJO(message,timeStamp,sender));
            }
        }
        cursor.close();
    }
    private void initViews(View view) {
        txtStoreName = view.findViewById(R.id.txtDashStoreName);
        txtLink = view.findViewById(R.id.txtDashLink);
        txtTillNumber = view.findViewById(R.id.txtDashTillNumber);
        txtTotal = view.findViewById(R.id.txtDashTotalTransactions);
        txtSuccess = view.findViewById(R.id.txtDashSuccessfulTransactions);
        txtFailed = view.findViewById(R.id.txtDashFailedTransactions);
        txtAirtime = view.findViewById(R.id.txtDashAirtimeBalance);
        txtOffers = view.findViewById(R.id.txtDashOffers);
        txtRenewals = view.findViewById(R.id.txtDashRenewals);
        txtMessages = view.findViewById(R.id.txtDashMessages);
        refreshAirtimeBalance = view.findViewById(R.id.refreshAirtime);
    }
}
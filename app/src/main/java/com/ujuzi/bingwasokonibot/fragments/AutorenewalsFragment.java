package com.ujuzi.bingwasokonibot.fragments;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.Button;
import android.widget.Toast;

import com.ujuzi.bingwasokonibot.Adapters.RenewalsListAdapter;
import com.ujuzi.bingwasokonibot.CreateRenewalActivity;
import com.ujuzi.bingwasokonibot.DBHelper;
import com.ujuzi.bingwasokonibot.PaymentPlanActivity;
import com.ujuzi.bingwasokonibot.R;
import com.ujuzi.bingwasokonibot.RequestManager;
import com.ujuzi.bingwasokonibot.listeners.PaymentListener;
import com.ujuzi.bingwasokonibot.models.Payment;
import com.ujuzi.bingwasokonibot.models.RenewalPOJO;
import com.ujuzi.bingwasokonibot.services.RenewalsJobService;
import com.ujuzi.bingwasokonibot.services.RenewalsService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AutorenewalsFragment extends Fragment {

    private RecyclerView myRecycler;
    private ExtendedFloatingActionButton create,upload;
    private FloatingActionButton actions;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean show = true;
    private List<RenewalPOJO> pojos = new ArrayList<>();
    private DBHelper dbHelper;
    private RenewalsListAdapter listAdapter;

    private Dialog offerCreationDialog,firstTimePayDialog,renewPlanDialog;
    private RequestManager requestManager;
    private Button navigateToCreateOffer,checkAvailablePlans,renewPlan;


    public AutorenewalsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_autorenewals, container, false);
        initViews(view);

        dbHelper = new DBHelper(getActivity());
        requestManager = new RequestManager(getActivity());
        listAdapter = new RenewalsListAdapter(getActivity());
        showData();
        checkOffersOne();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateRenewalActivity.class));
            }
        });

        actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show){
                    create.show();
                    actions.setImageResource(R.drawable.ic_clear);
                    show = false;
                }else{
                    create.hide();
                    show = true;
                    actions.setImageResource(R.drawable.ic_actions);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        myRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecycler.setHasFixedSize(true);
        myRecycler.setAdapter(listAdapter);

        return view;
    }
    public void checkOffersOne(){
        Cursor cursor = dbHelper.getRenewals();
        if(cursor.getCount() == 0){
            showOfferCreationDialog();
        }else{
            callPaymentApi(tillNumber());
        }
    }

    private void showOfferCreationDialog(){
        offerCreationDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_renewal,null);
        navigateToCreateOffer = view.findViewById(R.id.btnNavigateToCreateOffer);
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
        offerCreationDialog.show();
    }

    private void navigateToCreateOfferActivity() {
        startActivity(new Intent(getActivity(), CreateRenewalActivity.class));
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
                showRenewPlanDialog();
            } else if (current.compareTo(expiry) < 0) {
                scheduleJob();
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

    private void scheduleJob() {
        final JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);

        // The JobService that we want to run
        final ComponentName name = new ComponentName(getActivity(), RenewalsJobService.class);
        boolean jobAlreadyScheduled = false;

        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == 123) {
                jobAlreadyScheduled = true;
                break;
            }
        }
        if (!jobAlreadyScheduled) {
            // Schedule the job
            final int result = jobScheduler.schedule(getJobInfo(123, 1, name));

            // If successfully scheduled, log this thing
            if (result == JobScheduler.RESULT_SUCCESS) {
                Toast.makeText(getActivity(), "Job scheduled successfully", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private JobInfo getJobInfo(final int id, final long hour, final ComponentName name) {
        final long interval =  24 * 60 * 60 * 1000; // run every 15 mins
        final boolean isPersistent = true; // persist through boot
        final int networkType = JobInfo.NETWORK_TYPE_ANY; // Requires some sort of connectivity

        final JobInfo jobInfo;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(id, name).build();
        } else {
            jobInfo = new JobInfo.Builder(id, name).setPersisted(true).build();
        }

        return jobInfo;
    }

    public boolean myService(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RenewalsService.class.getName().equalsIgnoreCase(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void refresh() {
        showData();
    }

    private void showData() {
        pojos.clear();
        Cursor cursor = dbHelper.getRenewals();
        if (cursor.getCount() == 0){
            swipeRefreshLayout.setRefreshing(false);
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
                pojos.add(pojo);
            }
            swipeRefreshLayout.setRefreshing(false);
        }
        cursor.close();
        listAdapter.setRenewalList(pojos);
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
        checkOffersOne();
    }
    public String token(){
        Cursor cursor = dbHelper.getToken();
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
        myRecycler = view.findViewById(R.id.autorenewalsRecycler);
        create = view.findViewById(R.id.btnCreateRenewals);
        actions = view.findViewById(R.id.btnChoices2);
        swipeRefreshLayout = view.findViewById(R.id.swipeRenewalsOffers);
    }

}
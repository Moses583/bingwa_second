package com.bingwa.bingwasokonibot.fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bingwa.bingwasokonibot.Adapters.ItemListAdapter;
import com.bingwa.bingwasokonibot.Adapters.RenewalsListAdapter;
import com.bingwa.bingwasokonibot.CreateOfferActivity;
import com.bingwa.bingwasokonibot.CreateRenewalActivity;
import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.PaymentPlanActivity;
import com.bingwa.bingwasokonibot.R;
import com.bingwa.bingwasokonibot.RequestManager;
import com.bingwa.bingwasokonibot.listeners.PaymentListener;
import com.bingwa.bingwasokonibot.models.OfferPOJO;
import com.bingwa.bingwasokonibot.models.Payment;
import com.bingwa.bingwasokonibot.models.RenewalPOJO;
import com.bingwa.bingwasokonibot.services.RenewalsJobService;
import com.bingwa.bingwasokonibot.services.RenewalsService;
import com.bingwa.bingwasokonibot.services.RetryService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutorenewalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutorenewalsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView myRecycler;
    private ExtendedFloatingActionButton create,upload;
    private FloatingActionButton actions;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean show = true;
    private List<RenewalPOJO> pojos = new ArrayList<>();
    private DBHelper dbHelper;
    private RenewalsListAdapter listAdapter;

    private AlertDialog offerCreationDialog,firstTimePayDialog,renewPlanDialog;
    private RequestManager requestManager;


    public AutorenewalsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AutorenewalsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AutorenewalsFragment newInstance(String param1, String param2) {
        AutorenewalsFragment fragment = new AutorenewalsFragment();
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
        View view = inflater.inflate(R.layout.fragment_autorenewals, container, false);
        initViews(view);

        dbHelper = new DBHelper(getActivity());
        requestManager = new RequestManager(getActivity());
        showData();
//        checkOffersOne();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateRenewalActivity.class));
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOffersOne();
            }
        });

        actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show){
                    create.show();
                    upload.show();
                    actions.setImageResource(R.drawable.ic_clear);
                    show = false;
                }else{
                    create.hide();
                    upload.hide();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                showRenewPlanDialog();
            } else if (current.compareTo(expiry) < 0) {
                scheduleJob();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void showRenewPlanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            jobInfo = new JobInfo.Builder(id, name)
                    .setPersisted(true).build();
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
        listAdapter = new RenewalsListAdapter(getActivity());
        listAdapter.setRenewalList(pojos);
        myRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecycler.setHasFixedSize(true);
        myRecycler.setAdapter(listAdapter);
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
        upload = view.findViewById(R.id.btnUploadAllRenewals);
        actions = view.findViewById(R.id.btnChoices2);
        swipeRefreshLayout = view.findViewById(R.id.swipeRenewalsOffers);
    }

}
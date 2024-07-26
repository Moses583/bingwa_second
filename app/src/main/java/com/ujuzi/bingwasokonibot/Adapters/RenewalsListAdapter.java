package com.ujuzi.bingwasokonibot.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.ujuzi.bingwasokonibot.DBHelper;
import com.ujuzi.bingwasokonibot.EditRenewalActivity;
import com.ujuzi.bingwasokonibot.R;
import com.ujuzi.bingwasokonibot.models.RenewalPOJO;

import java.util.List;

public class RenewalsListAdapter extends RecyclerView.Adapter<RenewalsViewHolder> {
    private Context context;
    private List<RenewalPOJO> offerList;
    public RenewalsListAdapter(Context context) {
        this.context = context;
    }

    public void setRenewalList(List<RenewalPOJO> offerList) {
        this.offerList = offerList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RenewalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.renewals_list_item,parent,false);
        return new RenewalsViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull RenewalsViewHolder holder, int position) {
        String ussdCode = offerList.get(holder.getAdapterPosition()).getUssdCode();
        String money = offerList.get(holder.getAdapterPosition()).getMoney();
        String period = String.valueOf(offerList.get(holder.getAdapterPosition()).getPeriod());
        holder.frequency.setText(offerList.get(holder.getAdapterPosition()).getFrequency());
        holder.ussdCode.setText(ussdCode);
        holder.period.setText(period);
        holder.till.setText(offerList.get(holder.getAdapterPosition()).getTill());
        holder.time.setText(offerList.get(holder.getAdapterPosition()).getDialSimCard());
        holder.money.setText(money);
        holder.startDate.setText(offerList.get(holder.getAdapterPosition()).getDateCreation());
        holder.endDate.setText(offerList.get(holder.getAdapterPosition()).getDateExpiry());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(holder.getAdapterPosition(), offerList.get(holder.getAdapterPosition()).getUssdCode());
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditRenewalActivity.class);
                intent.putExtra("ussdCode",ussdCode);
                intent.putExtra("period",period);
                intent.putExtra("money",money);
                context.startActivity(intent);
            }
        });
        holder.renewalDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ussdDummy.getVisibility() == View.GONE){
                    holder.renewalDown.setVisibility(View.GONE);
                    holder.renewalUp.setVisibility(View.VISIBLE);
                    holder.ussdDummy.setVisibility(View.VISIBLE);
                    holder.perLay.setVisibility(View.VISIBLE);
                    holder.tillLay.setVisibility(View.VISIBLE);
                    holder.dialLay.setVisibility(View.VISIBLE);
                    holder.moneyLay.setVisibility(View.VISIBLE);
                    holder.startLay.setVisibility(View.VISIBLE);
                    holder.endLay.setVisibility(View.VISIBLE);
                    holder.btnEdit.setVisibility(View.VISIBLE);
                    holder.btnDelete.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(holder.mainLay,new AutoTransition());
                }
            }
        });
        holder.renewalUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ussdDummy.getVisibility() == View.VISIBLE){
                    holder.renewalDown.setVisibility(View.VISIBLE);
                    holder.renewalUp.setVisibility(View.GONE);
                    holder.ussdDummy.setVisibility(View.GONE);
                    holder.perLay.setVisibility(View.GONE);
                    holder.tillLay.setVisibility(View.GONE);
                    holder.dialLay.setVisibility(View.GONE);
                    holder.moneyLay.setVisibility(View.GONE);
                    holder.startLay.setVisibility(View.GONE);
                    holder.endLay.setVisibility(View.GONE);
                    holder.btnEdit.setVisibility(View.GONE);
                    holder.btnDelete.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(holder.mainLay,new AutoTransition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public void delete(int itemId, String code){
        DBHelper helper = new DBHelper(context);
        boolean checkDelete = helper.deleteRenewal(code);
        if (checkDelete){
            Toast.makeText(context, "Renewal deleted successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "unable to delete renewal", Toast.LENGTH_SHORT).show();
        }
        offerList.remove(itemId);
        notifyItemRemoved(itemId);
    }
}
class RenewalsViewHolder extends RecyclerView.ViewHolder {
    TextView frequency, ussdCode, period,till,time,money,startDate,endDate,ussdDummy;
    CardView selectOffer;
    Button btnEdit,btnDelete;
    ImageView renewalUp,renewalDown;
    LinearLayout mainLay,perLay,tillLay,dialLay,moneyLay,startLay,endLay;
    public RenewalsViewHolder(@NonNull View itemView) {
        super(itemView);
        frequency = itemView.findViewById(R.id.renewalFrequency);
        ussdCode = itemView.findViewById(R.id.renewalUssd);
        period = itemView.findViewById(R.id.renewalPeriod);
        till = itemView.findViewById(R.id.renewalTill);
        time = itemView.findViewById(R.id.renewalTime);
        money = itemView.findViewById(R.id.renewalMoney);
        startDate = itemView.findViewById(R.id.renewalStartDate);
        endDate = itemView.findViewById(R.id.renewalEndDate);
        selectOffer = itemView.findViewById(R.id.selectRenewal);
        btnEdit = itemView.findViewById(R.id.btnEditRenewal);
        btnDelete = itemView.findViewById(R.id.btnDeleteRenewal);
        ussdDummy = itemView.findViewById(R.id.renewalUssdDummy);
        mainLay = itemView.findViewById(R.id.mainRenewalLayout);
        renewalDown = itemView.findViewById(R.id.renewalsDropDown);
        renewalUp = itemView.findViewById(R.id.renewalsDropUp);
        perLay = itemView.findViewById(R.id.renewalPerLay);
        tillLay = itemView.findViewById(R.id.renewalTillLay);
        dialLay = itemView.findViewById(R.id.renewalDialLay);
        startLay = itemView.findViewById(R.id.renewalStartLay);
        endLay = itemView.findViewById(R.id.renewalEndLay);
        moneyLay = itemView.findViewById(R.id.renewalMoneyLay);
    }
}


package com.bingwa.bingwasokonibot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.R;
import com.bingwa.bingwasokonibot.models.RenewalPOJO;

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
        holder.frequency.setText(offerList.get(holder.getAdapterPosition()).getFrequency());
        holder.ussdCode.setText(offerList.get(holder.getAdapterPosition()).getUssdCode());
        holder.period.setText(offerList.get(holder.getAdapterPosition()).getPeriod());
        holder.till.setText(offerList.get(holder.getAdapterPosition()).getTill());
        holder.time.setText(offerList.get(holder.getAdapterPosition()).getDialSimCard());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(holder.getAdapterPosition(), offerList.get(holder.getAdapterPosition()).getUssdCode());
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
    TextView frequency, ussdCode, period,till,time;
    LinearLayout selectOffer;
    ImageView btnEdit;
    public RenewalsViewHolder(@NonNull View itemView) {
        super(itemView);
        frequency = itemView.findViewById(R.id.renewalFrequency);
        ussdCode = itemView.findViewById(R.id.renewalUssd);
        period = itemView.findViewById(R.id.renewalPeriod);
        till = itemView.findViewById(R.id.renewalTill);
        time = itemView.findViewById(R.id.renewalTime);
        selectOffer = itemView.findViewById(R.id.selectRenewal);
        btnEdit = itemView.findViewById(R.id.btnEditRenewal);
    }
}


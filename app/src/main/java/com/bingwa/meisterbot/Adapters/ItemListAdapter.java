package com.bingwa.meisterbot.Adapters;

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

import com.bingwa.meisterbot.DBHelper;
import com.bingwa.meisterbot.R;
import com.bingwa.meisterbot.models.OfferPOJO;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<OfferViewHolder> {
    private Context context;
    private List<OfferPOJO> offerList;
    public ItemListAdapter(Context context) {
        this.context = context;
    }

    public void setOfferList(List<OfferPOJO> offerList) {
        this.offerList = offerList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.offer_list_item_layout,parent,false);
        return new OfferViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        holder.name.setText(offerList.get(holder.getAdapterPosition()).getName());
        holder.amount.setText(offerList.get(holder.getAdapterPosition()).getAmount());
        holder.dial.setText(offerList.get(holder.getAdapterPosition()).getDialSim());
        holder.ussdCode.setText(offerList.get(holder.getAdapterPosition()).getUssdCode());
        holder.paySim.setText(offerList.get(holder.getAdapterPosition()).getPaymentSim());
        holder.till.setText(offerList.get(holder.getAdapterPosition()).getOfferTill());
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
        boolean checkDelete = helper.deleteData(code);
        if (checkDelete){
            Toast.makeText(context, "offer deleted successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "unable to delete offer", Toast.LENGTH_SHORT).show();
        }
        offerList.remove(itemId);
        notifyItemRemoved(itemId);
    }
}
class OfferViewHolder extends RecyclerView.ViewHolder {
    TextView amount, dial, ussdCode, paySim, till,name;
    LinearLayout selectOffer;
    ImageView btnEdit;
    public OfferViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.recyclerName);
        amount = itemView.findViewById(R.id.recyclerAmount);
        dial = itemView.findViewById(R.id.recyclerDialSim);
        paySim = itemView.findViewById(R.id.recyclerPaySim);
        ussdCode = itemView.findViewById(R.id.recyclerUssdCode);
        till = itemView.findViewById(R.id.recyclerDialTill);
        selectOffer = itemView.findViewById(R.id.selectOffer);
        btnEdit = itemView.findViewById(R.id.btnEditOffer);
    }
}


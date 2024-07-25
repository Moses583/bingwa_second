package com.bingwa.bingwasokonibot.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.EditOfferActivity;
import com.bingwa.bingwasokonibot.R;
import com.bingwa.bingwasokonibot.models.OfferPOJO;

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
        String name = offerList.get(holder.getAdapterPosition()).getName();
        String amount = offerList.get(holder.getAdapterPosition()).getAmount();
        String ussd = offerList.get(holder.getAdapterPosition()).getUssd();
        String dialSim = offerList.get(holder.getAdapterPosition()).getDialSim();
        String deviceId = offerList.get(holder.getAdapterPosition()).getDeviceId();
        String subscriptionId = offerList.get(holder.getAdapterPosition()).getSubscriptionId();
        String paymentSim = offerList.get(holder.getAdapterPosition()).getPaymentSim();
        String paymentSimId = offerList.get(holder.getAdapterPosition()).getPaymentSimId();
        String offerTill = offerList.get(holder.getAdapterPosition()).getOfferTill();


        holder.name.setText(name);
        holder.amount.setText(amount);
        holder.dial.setText(dialSim);
        holder.ussdCode.setText(ussd);
        holder.paySim.setText(paymentSim);
        holder.till.setText(offerTill);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditOfferActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("amount",amount);
                intent.putExtra("ussd",ussd);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.btnDeleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(holder.getAdapterPosition(), offerList.get(holder.getAdapterPosition()).getUssd());
            }
        });
        holder.offerDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.amountDummy.getVisibility() == View.GONE){
                    holder.offerDropDown.setVisibility(View.GONE);
                    holder.offerDropUp.setVisibility(View.VISIBLE);
                    holder.amountDummy.setVisibility(View.VISIBLE);
                    holder.ussdLayout.setVisibility(View.VISIBLE);
                    holder.paymentLayout.setVisibility(View.VISIBLE);
                    holder.dialLayout.setVisibility(View.VISIBLE);
                    holder.tillLayout.setVisibility(View.VISIBLE);
                    holder.btnEdit.setVisibility(View.VISIBLE);
                    holder.btnDeleteOffer.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(holder.mainOfferLayout,new AutoTransition());
                }
            }
        });
        holder.offerDropUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.amountDummy.getVisibility() == View.VISIBLE){
                    holder.offerDropDown.setVisibility(View.VISIBLE);
                    holder.offerDropUp.setVisibility(View.GONE);
                    holder.amountDummy.setVisibility(View.GONE);
                    holder.ussdLayout.setVisibility(View.GONE);
                    holder.paymentLayout.setVisibility(View.GONE);
                    holder.dialLayout.setVisibility(View.GONE);
                    holder.tillLayout.setVisibility(View.GONE);
                    holder.btnEdit.setVisibility(View.GONE);
                    holder.btnDeleteOffer.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(holder.mainOfferLayout,new AutoTransition());
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
    TextView amount, dial, ussdCode, paySim, till,name,amountDummy;
    CardView selectOffer;
    ImageView btnEdit,btnDeleteOffer,offerDropDown,offerDropUp;
    LinearLayout ussdLayout,paymentLayout,dialLayout,tillLayout,mainOfferLayout;
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
        btnDeleteOffer = itemView.findViewById(R.id.btnDeleteOffer);
        offerDropDown = itemView.findViewById(R.id.offersDropDown);
        offerDropUp = itemView.findViewById(R.id.offersDropUp);
        mainOfferLayout = itemView.findViewById(R.id.mainOfferLayout);
        ussdLayout = itemView.findViewById(R.id.offerUssdLayout);
        paymentLayout = itemView.findViewById(R.id.offerPaymentLayout);
        dialLayout = itemView.findViewById(R.id.offerDialLayout);
        tillLayout = itemView.findViewById(R.id.offerTillLayout);
        amountDummy = itemView.findViewById(R.id.offerAmountDummy);
    }
}


package com.ujuzi.bingwasokonibot.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.ujuzi.bingwasokonibot.R;
import com.ujuzi.bingwasokonibot.models.TransactionPOJO;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder>{
    private Context context;
    private List<TransactionPOJO> pojoList;

    public TransactionAdapter(Context context) {
        this.context = context;
    }

    public void setPojoList(List<TransactionPOJO> pojoList) {
        this.pojoList = pojoList;
        notifyDataSetChanged();
    }
    public void setFilteredList(List<TransactionPOJO> filteredList) {
        this.pojoList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transactions_list_item,parent,false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        String response = pojoList.get(position).getUssdResponse();
        if (response.equalsIgnoreCase("-1")){
            holder.txtUssdResponse.setText("Connection problem or invalid MMI code.");
        }else {
            holder.txtUssdResponse.setText(response);
        }
        holder.txtTransactionAmount.setText(pojoList.get(position).getTransactionAmount());
        holder.txtTransactionTimeStamp.setText(pojoList.get(position).getTimeStamp());
        String status = pojoList.get(position).getStatus();
        String ussdCode = pojoList.get(position).getUssd();
        if (status.equalsIgnoreCase("0")){
            holder.txtTransactionStatus.setText("Failed.");
        }else if (status.equalsIgnoreCase("1")){
            holder.txtTransactionStatus.setText("Successfull.");
        }
        holder.txtTransactionRecipient.setText(ussdCode);
        holder.txtTransactionRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + ussdCode));
                context.startActivity(intent);
            }
        });
        holder.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ussdDummy.getVisibility() == View.GONE){
                    holder.dropDown.setVisibility(View.GONE);
                    holder.dropUp.setVisibility(View.VISIBLE);
                    holder.ussdDummy.setVisibility(View.VISIBLE);
                    holder.responseLayout.setVisibility(View.VISIBLE);
                    holder.amountLayout.setVisibility(View.VISIBLE);
                    holder.timeLayout.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(holder.transactionPojoLayout,new AutoTransition());
                }
            }
        });
        holder.dropUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ussdDummy.getVisibility() == View.VISIBLE){
                    holder.dropDown.setVisibility(View.VISIBLE);
                    holder.dropUp.setVisibility(View.GONE);
                    holder.ussdDummy.setVisibility(View.GONE);
                    holder.responseLayout.setVisibility(View.GONE);
                    holder.amountLayout.setVisibility(View.GONE);
                    holder.timeLayout.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(holder.transactionPojoLayout,new AutoTransition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pojoList.size();
    }
}
class TransactionViewHolder extends RecyclerView.ViewHolder {
    CardView transactionCardView;
    TextView txtUssdResponse, txtTransactionAmount, txtTransactionTimeStamp, txtTransactionRecipient,txtTransactionStatus,ussdDummy;
    LinearLayout responseLayout,amountLayout,timeLayout,transactionPojoLayout;
    ImageView dropDown,dropUp;
    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        transactionCardView = itemView.findViewById(R.id.transactionCardView);
        txtUssdResponse = itemView.findViewById(R.id.txtUssdResponse);
        txtTransactionAmount = itemView.findViewById(R.id.txtTransactionAmount);
        txtTransactionTimeStamp = itemView.findViewById(R.id.txtTransactionTimeStamp);
        txtTransactionRecipient = itemView.findViewById(R.id.txtTransactionRecepient);
        txtTransactionStatus = itemView.findViewById(R.id.txtTransactionStatus);
        ussdDummy = itemView.findViewById(R.id.ussdDummy);
        responseLayout = itemView.findViewById(R.id.responseLayout);
        amountLayout = itemView.findViewById(R.id.amountLayout);
        transactionPojoLayout = itemView.findViewById(R.id.transactionPojoLayout);
        timeLayout = itemView.findViewById(R.id.timeLayout);
        dropDown = itemView.findViewById(R.id.dropDown);
        dropUp = itemView.findViewById(R.id.dropUp);
    }
}

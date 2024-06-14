package com.example.meisterbot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meisterbot.R;
import com.example.meisterbot.models.TransactionPOJO;

import java.util.ArrayList;
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

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transactions_list_item,parent,false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.transactionCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "to be added soon", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        holder.transactionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Long click on a transaction to resend it.", Toast.LENGTH_LONG).show();
            }
        });
        if (pojoList.get(position).getUssdResponse().equals("-1")){
            holder.txtUssdResponse.setText("Invalid MMI code");
        }else{
            holder.txtUssdResponse.setText(pojoList.get(position).getUssdResponse());
        }
        holder.txtTransactionAmount.setText(pojoList.get(position).getTransactionAmount());
        holder.txtTransactionTimeStamp.setText(pojoList.get(position).getTimeStamp());
        holder.txtTransactionRecipient.setText(pojoList.get(position).getRecipient());
    }

    @Override
    public int getItemCount() {
        return pojoList.size();
    }
}
class TransactionViewHolder extends RecyclerView.ViewHolder {
    LinearLayout transactionCardView;
    TextView txtUssdResponse, txtTransactionAmount, txtTransactionTimeStamp, txtTransactionRecipient;
    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        transactionCardView = itemView.findViewById(R.id.transactionCardView);
        txtUssdResponse = itemView.findViewById(R.id.txtUssdResponse);
        txtTransactionAmount = itemView.findViewById(R.id.txtTransactionAmount);
        txtTransactionTimeStamp = itemView.findViewById(R.id.txtTransactionTimeStamp);
        txtTransactionRecipient = itemView.findViewById(R.id.txtTransactionRecepient);
    }
}

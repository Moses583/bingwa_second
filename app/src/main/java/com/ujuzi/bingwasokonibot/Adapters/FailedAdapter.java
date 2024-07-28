package com.ujuzi.bingwasokonibot.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.ujuzi.bingwasokonibot.R;
import com.ujuzi.bingwasokonibot.models.TransactionPOJO;

import java.util.List;

public class FailedAdapter extends RecyclerView.Adapter<FailedViewHolder>{
    private Context context;
    private List<TransactionPOJO> pojoList;

    public FailedAdapter(Context context) {
        this.context = context;
    }

    public void setPojoList(List<TransactionPOJO> pojoList) {
        this.pojoList = pojoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FailedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delete_transactions_list_item,parent,false);
        return new FailedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FailedViewHolder holder, int position) {

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
                    holder.button.setVisibility(View.VISIBLE);
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
                    holder.button.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(holder.transactionPojoLayout,new AutoTransition());
                }
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(holder.getAdapterPosition(), pojoList.get(holder.getAdapterPosition()).getUssd());
            }
        });
    }
    public void delete(int itemId, String code){
        DBHelper helper = new DBHelper(context);
        boolean checkDelete = helper.deleteData(code);
        if (checkDelete){
            Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "unable to delete offer", Toast.LENGTH_SHORT).show();
        }
        pojoList.remove(itemId);
        notifyItemRemoved(itemId);
    }

    @Override
    public int getItemCount() {
        return pojoList.size();
    }
}
class FailedViewHolder extends RecyclerView.ViewHolder {
    CardView transactionCardView;
    TextView txtUssdResponse, txtTransactionAmount, txtTransactionTimeStamp, txtTransactionRecipient,txtTransactionStatus,ussdDummy;
    LinearLayout responseLayout,amountLayout,timeLayout,transactionPojoLayout;
    ImageView dropDown,dropUp;
    Button button;
    public FailedViewHolder(@NonNull View itemView) {
        super(itemView);
        transactionCardView = itemView.findViewById(R.id.failedTransactionCardView);
        txtUssdResponse = itemView.findViewById(R.id.failedtxtUssdResponse);
        txtTransactionAmount = itemView.findViewById(R.id.failedtxtTransactionAmount);
        txtTransactionTimeStamp = itemView.findViewById(R.id.failedtxtTransactionTimeStamp);
        txtTransactionRecipient = itemView.findViewById(R.id.failedtxtTransactionRecepient);
        txtTransactionStatus = itemView.findViewById(R.id.failedtxtTransactionStatus);
        ussdDummy = itemView.findViewById(R.id.failedussdDummy);
        responseLayout = itemView.findViewById(R.id.failedresponseLayout);
        amountLayout = itemView.findViewById(R.id.failedamountLayout);
        transactionPojoLayout = itemView.findViewById(R.id.failedtransactionPojoLayout);
        timeLayout = itemView.findViewById(R.id.failedtimeLayout);
        dropDown = itemView.findViewById(R.id.faileddropDown);
        dropUp = itemView.findViewById(R.id.faileddropUp);
        button = itemView.findViewById(R.id.btnDeleteFailed);
    }
}

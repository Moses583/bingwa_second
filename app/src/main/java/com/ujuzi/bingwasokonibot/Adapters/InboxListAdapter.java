package com.ujuzi.bingwasokonibot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ujuzi.bingwasokonibot.R;
import com.ujuzi.bingwasokonibot.models.InboxListPOJO;

import java.util.List;

public class InboxListAdapter extends RecyclerView.Adapter<InboxListViewHolder> {
    private Context context;

    public void setInboxListPOJOList(List<InboxListPOJO> inboxListPOJOList) {
        this.inboxListPOJOList = inboxListPOJOList;
        notifyDataSetChanged();
    }

    private List<InboxListPOJO> inboxListPOJOList;

    public InboxListAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public InboxListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inbox_list_item,parent,false);
        return new InboxListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxListViewHolder holder, int position) {
        holder.txtInboxMessage.setText(inboxListPOJOList.get(position).getMessage());
        holder.txtInboxTimeStamp.setText(inboxListPOJOList.get(position).getTimeStamp());
        holder.txtSender.setText(inboxListPOJOList.get(position).getSender());
    }

    @Override
    public int getItemCount() {
        return inboxListPOJOList.size();
    }
}
class InboxListViewHolder extends RecyclerView.ViewHolder {
    CardView layout;
    TextView txtInboxMessage;
    TextView txtInboxTimeStamp;
    TextView txtSender;
    public InboxListViewHolder(@NonNull View itemView) {
        super(itemView);
        txtInboxMessage = itemView.findViewById(R.id.txtInboxMessage);
        txtInboxTimeStamp = itemView.findViewById(R.id.txtInboxTimeStamp);
        txtSender = itemView.findViewById(R.id.txtInboxSender);
        layout = itemView.findViewById(R.id.selectInboxMessage);
    }
}

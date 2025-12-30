package com.example.handymen;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkerViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvEmail, tvPhone, tvInfo;

    public WorkerViewHolder(@NonNull View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.tvName);
        tvEmail = itemView.findViewById(R.id.tvEmail);
        tvPhone = itemView.findViewById(R.id.tvPhone);
        tvInfo = itemView.findViewById(R.id.tvInfo);
    }
}

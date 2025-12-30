package com.example.handymen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerViewHolder> {

    private Context context;
    private ArrayList<Worker> list;

    public WorkerAdapter(ArrayList<Worker> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_worker, parent, false);

        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {

        Worker w = list.get(position);

        holder.tvName.setText(w.name);
        holder.tvEmail.setText("Email: " + w.email);
        holder.tvPhone.setText("Phone: " + w.phone);
        holder.tvInfo.setText(
                w.location + " | Rate: " + w.rate + " | Exp: " + w.experience
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkerDetailsActivity.class);
            intent.putExtra("name", w.name);
            intent.putExtra("email", w.email);
            intent.putExtra("phone", w.phone);
            intent.putExtra("experience", w.experience);
            intent.putExtra("rate", w.rate);
            intent.putExtra("location", w.location);
            intent.putExtra("profession", w.profession);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

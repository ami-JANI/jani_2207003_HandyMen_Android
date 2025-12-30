package com.example.handymen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkerAdapter
        extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private final List<Worker> workerList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Worker worker);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public WorkerAdapter(List<Worker> workerList) {
        this.workerList = workerList;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_worker, parent, false);

        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WorkerViewHolder holder, int position) {

        Worker worker = workerList.get(position);

        holder.tvName.setText(worker.name);
        holder.tvPhone.setText(worker.phone);
        holder.tvLocation.setText(worker.location);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(worker);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    static class WorkerViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName, tvPhone, tvLocation;

        WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}

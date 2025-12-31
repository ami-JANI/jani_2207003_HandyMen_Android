package com.example.handymen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    List<BookingItem> list;
    Context context;

    public BookingAdapter(Context context, List<BookingItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_booking_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        BookingItem b = list.get(i);
        h.tvProfession.setText(b.profession);
        h.tvName.setText(b.name);
        h.tvEmail.setText(b.email);
        h.tvPhone.setText(b.phone);
        h.tvSlot.setText(b.slot);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProfession, tvName, tvEmail, tvPhone, tvSlot;

        ViewHolder(View v) {
            super(v);
            tvProfession = v.findViewById(R.id.tvProfession);
            tvName = v.findViewById(R.id.tvName);
            tvEmail = v.findViewById(R.id.tvEmail);
            tvPhone = v.findViewById(R.id.tvPhone);
            tvSlot = v.findViewById(R.id.tvSlot);
        }
    }
}

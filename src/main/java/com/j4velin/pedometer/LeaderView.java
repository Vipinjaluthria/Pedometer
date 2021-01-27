package com.j4velin.pedometer;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderView extends RecyclerView.ViewHolder {
    TextView textView;
    public LeaderView(@NonNull View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.textview);
    }
}

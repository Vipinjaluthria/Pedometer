package com.j4velin.pedometer;

import android.service.quickaccesswallet.WalletCard;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderView extends RecyclerView.ViewHolder {
    TextView textView,timeview, idview;
    RelativeLayout card;
    public LeaderView(@NonNull View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.textview);
        timeview=itemView.findViewById(R.id.timeview);
        idview=itemView.findViewById(R.id.idview);
        card = itemView.findViewById(R.id.card);
    }
}

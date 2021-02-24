package com.j4velin.pedometer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderView> {
    public LeaderAdapter(ArrayList<LeaderBoard> leaderBoards) {
        this.leaderBoards = leaderBoards;
    }

    ArrayList<LeaderBoard> leaderBoards;

    private Context context;

    @NonNull
    @Override
    public LeaderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboardscore, parent, false);
        context = parent.getContext();
        return new LeaderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderView holder, int position) {
        if (position == 0) {
            holder.card.setBackgroundResource(R.drawable.leaderboard_bg);
            ViewGroup.LayoutParams params = holder.card.getLayoutParams();
            ;
            params.height = 800;
            holder.card.setLayoutParams(params);
            int color = Integer.parseInt("ffffff", 16) + 0xFF000000;
            holder.textView.setTextColor(color);
            holder.timeview.setTextColor(color);
            holder.idview.setTextColor(color);
            holder.imageView.setImageResource(R.drawable.round_button);
        }
        try {
            Toast.makeText(context, leaderBoards.get(position).getTime(), Toast.LENGTH_SHORT).show();
            if (leaderBoards.get(position).getPhoto() != null || TextUtils.isEmpty(leaderBoards.get(position).getPhoto())) {
                Picasso.with(context).load(Uri.parse(leaderBoards.get(position).getPhoto())).into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.round_button);
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.round_button);
        }
        holder.textView.setText(leaderBoards.get(position).getName());
        holder.timeview.setText(leaderBoards.get(position).getTime());
        holder.idview.setText(String.valueOf(position + 1));

    }

    @Override
    public int getItemCount() {
        return leaderBoards.size();
    }
}

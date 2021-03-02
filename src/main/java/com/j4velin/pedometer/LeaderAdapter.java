package com.j4velin.pedometer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.j4velin.pedometer.PEDOMETER.ui.Fragment_Overview.LOGGEDINname;

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
        try{
        if (position == 0) {
            holder.card.setBackgroundResource(R.drawable.leaderboard_bg);
            ViewGroup.LayoutParams params = holder.card.getLayoutParams();
            params.height = 700;
            holder.card.setLayoutParams(params);
            int color = Integer.parseInt("ffffff", 16) + 0xFF000000;
            holder.textView.setTextColor(color);
            holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            holder.timeview.setTextColor(color);
            holder.idview.setTextColor(color);
            holder.idview.setTextSize(TypedValue.COMPLEX_UNIT_SP,44);
            holder.imageView.setImageResource(R.drawable.round_button);
            holder.imageView.getLayoutParams().height = 176;
            holder.imageView.getLayoutParams().width = 176;
            holder.imageView.requestLayout();

            holder.textView.setText("     "+leaderBoards.get(position).getName()+" \n     "+leaderBoards.get(position).getTime());
            holder.textView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.textView.requestLayout();

            holder.timeview.setVisibility(View.GONE);
            holder.idview.setText(String.valueOf(position + 1));

        }

            try {
//            Toast.makeText(context, leaderBoards.get(position).getTime(), Toast.LENGTH_SHORT).show();
                if (leaderBoards.get(position).getPhoto() != null || TextUtils.isEmpty(leaderBoards.get(position).getPhoto())) {
                    Picasso.with(context).load(Uri.parse(leaderBoards.get(position).getPhoto())).into(holder.imageView);
                } else {
                    holder.imageView.setImageResource(R.drawable.round_button);
                }



            } catch (Exception e) {
                holder.imageView.setImageResource(R.drawable.round_button);
            }

            LeaderboardView.shimmerFrameLayout.setVisibility(View.GONE);
        if(position != 0) {
            holder.textView.setText(leaderBoards.get(position).getName());
            holder.timeview.setText(leaderBoards.get(position).getTime());
            holder.idview.setText(String.valueOf(position + 1));
        }
        if(leaderBoards.get(position).getName().equals(LOGGEDINname)){
            int k=position+1;
            Toast.makeText(context,"You're at position "+k, Toast.LENGTH_SHORT).show();
        }}
        catch(Exception e){
            Toast.makeText(context, "FAILED TO FETCH! TRY AFTER SOMETIME", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return leaderBoards.size();
    }
}

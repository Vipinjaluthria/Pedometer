package com.j4velin.pedometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.j4velin.pedometer.PEDOMETER.ui.Fragment_Overview.LOGGEDINname;


public class LeaderboardView extends Fragment {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    String uuid;
    ArrayList<LeaderBoard> leaderBoardArrayList;
    LeaderAdapter leaderAdapter;
    GoogleSignInClient mGoogleSignInClient;
    public static String OWN_POSTION;
    RecyclerView recyclerView;
    Button button;
    public static ShimmerFrameLayout shimmerFrameLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.activity_leaderboard, container, false);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerview);
        shimmerFrameLayout=view.findViewById(R.id.shimmerFrameLayout);
        firebaseDatabase = FirebaseDatabase.getInstance();
        uuid = mAuth.getUid();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        leaderBoardArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getData();
        leaderAdapter = new LeaderAdapter(leaderBoardArrayList);
        recyclerView.setAdapter(leaderAdapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        return view;

    }

    private void getData() {
        leaderBoardArrayList.clear();
        Query DatabaseQuery = firebaseDatabase.getReference().child("USERS").orderByChild("Time").limitToLast(100);
        final String[] photo = {"null"};
        final String[] name={"test"};
        final String[] time={"0:0"};
        DatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaderBoardArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {

                        if (snapshot2.getKey().equals("Photo")) {
                            photo[0] = snapshot2.getValue().toString();
                            Log.d("vipin",photo[0]);
//                            leaderBoardArrayList.add(new LeaderBoard(photo[0],name, name, name, name));
                        }
                        if (snapshot2.getKey().equals("Name")) {
                            name[0] = snapshot2.getValue().toString();
                            Log.d("vipin",name[0]);
//                            leaderBoardArrayList.add(new LeaderBoard(photo[0],name[0], name, name, name));
                        }

                        if (snapshot2.getKey().equals("Time")) {
                            time[0] = snapshot2.getValue().toString();
                            Log.d("time",time[0]);
//                            Toast.makeText(getActivity(), photo[0]+ name[0] + time[0], Toast.LENGTH_SHORT).show();
                            leaderBoardArrayList.add(new LeaderBoard(photo[0],name[0], "", time[0], ""));
                        }

                    }
                    leaderAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}


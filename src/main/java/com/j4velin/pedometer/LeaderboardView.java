package com.j4velin.pedometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class LeaderboardView extends Fragment {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    String uuid,email;
    ArrayList<LeaderBoard> leaderBoardArrayList;
    LeaderAdapter leaderAdapter;
    GoogleSignInClient mGoogleSignInClient;
    RecyclerView recyclerView;
    Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.activity_leaderboard, container, false);
        mAuth=FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.recyclerview);
        firebaseDatabase=FirebaseDatabase.getInstance();
        uuid=mAuth.getUid();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        leaderBoardArrayList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        leaderAdapter=new LeaderAdapter(leaderBoardArrayList);
        recyclerView.setAdapter(leaderAdapter);
        getData();
        return view;

    }

    private void getData() {
        Query DatabaseQuery =firebaseDatabase.getReference().child("USERS").orderByChild("Time");
        DatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaderBoardArrayList.clear();
                for(DataSnapshot postsnapshot: snapshot.getChildren()) {
                    for(DataSnapshot snap : postsnapshot.getChildren())
                    {
                        if(snap.getKey().equals("Name")) {
                            Log.d("vipin", snap.getValue().toString());
                            leaderBoardArrayList.add(new LeaderBoard(snap.getValue().toString(),snap.getValue().toString(),snap.getValue().toString()));

                        }
                    }
                }
                leaderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}


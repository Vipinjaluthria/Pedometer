package com.j4velin.pedometer.PEDOMETER.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.PopupMenu;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.j4velin.pedometer.AboutActivity;
import com.j4velin.pedometer.BuildConfig;

import com.j4velin.pedometer.MainActivity;
import com.j4velin.pedometer.PEDOMETER.Database;
import com.j4velin.pedometer.PEDOMETER.Google_Sign_In;
import com.j4velin.pedometer.R;
import com.j4velin.pedometer.PEDOMETER.SensorListener;
import com.j4velin.pedometer.PEDOMETER.util.API26Wrapper;
import com.j4velin.pedometer.PEDOMETER.util.Logger;
import com.j4velin.pedometer.PEDOMETER.util.Util;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_Overview extends Fragment implements SensorEventListener {
    TextView unitid;

    TextView text_active;
    private TextView stepsView;
    private PieModel sliceGoal, sliceCurrent;
    private PieChart pg;
    public static String LOGGEDINname;
    File imagepath;
    TextView username;
    public static int toogle_run=0;
    File file = null;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    GoogleSignInClient mGoogleSignInClient;
    Button share;
    String Date;
    public static SharedPreferences.Editor chronoeditor;
    public static SharedPreferences prefchrono;
    Button button;
    private TextView progresstext;
    int steps = 0;
    boolean isChronometerRunning = false;
    String currentDateTimeString;
    public Chronometer mStopWatch;
    int perc;
    Dialog dialog;
    private String hh = "00", mm = "00", ss = "00";
    public static int todayOffset, total_start, goal, since_boot, total_days;
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private boolean showSteps = true;
    private Button stopbtn;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= 26) {
            API26Wrapper.startForegroundService(getActivity(),
                    new Intent(getActivity(), SensorListener.class));
        } else {
            getActivity().startService(new Intent(getActivity(), SensorListener.class));
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_overview, null);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        stepsView = (TextView) v.findViewById(R.id.steps);
        stepsView.setText("0");
        unitid = v.findViewById(R.id.unit);
        stopbtn = v.findViewById(R.id.stopbtn);
        pg = (PieChart) v.findViewById(R.id.graph);
        TextView btn = v.findViewById(R.id.rule_booklet);
        btn.setOnClickListener(v1 -> makedialogrule());
        ImageView img = (ImageView) v.findViewById(R.id.imageView);
        username = v.findViewById(R.id.username);
        username.setVisibility(View.VISIBLE);
        text_active = v.findViewById(R.id.text_active);
        LOGGEDINname = firebaseAuth.getCurrentUser().getDisplayName();
        mStopWatch = (Chronometer) v.findViewById(R.id.chronometer);
        mStopWatch.setBase(SystemClock.elapsedRealtime());
        mStopWatch.stop();

        currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        Date = currentDateTimeString.substring(4, 6);
        String[] Date1 = Date.split(",");
        Date = Date1[0];

        SharedPreferences run = getActivity().getSharedPreferences("Database", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = run.edit();
        prefchrono = getActivity().getSharedPreferences("CHRONODB", Context.MODE_PRIVATE);
        chronoeditor = prefchrono.edit();

        if (run.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            run.edit().putBoolean("firstrun", false).apply();
            editor.putBoolean("today", false).apply();
            editor.putBoolean("tomorrow", false).apply();
            editor.putBoolean("dayaftertomorrow", false).apply();
            editor.putLong("MinimumTime", 10000000).apply();
        }
        if(Date.equals("3")) {
            editor.putBoolean("today", true).apply();
        }else{editor.putBoolean("today", false).apply();}
        if(Date.equals("13")) {
            editor.putBoolean("tomorrow", true).apply();
        }else{editor.putBoolean("tomorrow", false).apply();}
        if(Date.equals("14")) {
            editor.putBoolean("dayaftertomorrow", true).apply();
        }else{editor.putBoolean("dayaftertomorrow", false).apply();}


        Log.d("vipin", currentDateTimeString + "/" + Date);
//        Toast.makeText(getActivity(), "TODAY's Date = " + currentDateTimeString, Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        String name = acct.getDisplayName();
        Uri photo = acct.getPhotoUrl();
//        Toast.makeText(getActivity(), photo.toString(), Toast.LENGTH_SHORT).show();
        CircleImageView profileimg = v.findViewById(R.id.profile_image);
        Picasso.with(getActivity()).load(photo).into(profileimg);
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        String[] words = name.split(" ");
//        Toast.makeText(getActivity(), "" + words[0], Toast.LENGTH_SHORT).show();
        username.setText(words[0]);
//        profileimg.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showdialog();
//            }
//        });


        button = v.findViewById(R.id.startstop);
        share = v.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
//                Bitmap bitmap=takescreen();
//                saveBitmap(bitmap);
//                shareit();
                View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bmp = getScreenShot(rootView);
                store(bmp, "file.png");
                shareImage(file);
            }
        });

        mStopWatch.setBase(SystemClock.elapsedRealtime());

//        mStopWatch.setBase(SystemClock.elapsedRealtime());
//         button  =  v.findViewById(R.id.startstop);
        button.setTag(1);
        button.setText("Start Run");
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddData("0","0");
                button.setVisibility(View.VISIBLE);
                text_active.setVisibility(View.VISIBLE);
                stopbtn.setVisibility(View.INVISIBLE);
                stepsView.setText("0");
                mStopWatch.setBase(SystemClock.elapsedRealtime());
                mStopWatch.stop();
                isChronometerRunning  = false;
                editor.putBoolean("today", false).apply();
                chronoeditor.putBoolean("CHRONO",false).apply();
                button.setText("Start Run");
                set_to_zero();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        set_to_zero();
//                        button.performClick();
//                        stopbtn.performClick();
                    }
                }, 20);
            }
        });
        ////checking values for shared prefrenece
        boolean today = run.getBoolean("today", true);
        boolean tomorrow = run.getBoolean("today", true);
        Toast.makeText(getActivity(), "TODAY=" + today, Toast.LENGTH_SHORT).show();
        boolean dayaftertomorrow = run.getBoolean("today", true);
        if (Date.equals("3") && today) {
            button.setEnabled(true);
            button.setClickable(true);

            stopbtn.setClickable(true);
            stopbtn.setEnabled(true);
        } else if (Date.equals("13") && tomorrow) {
            button.setEnabled(true);
            button.setClickable(true);
            stopbtn.setClickable(true);
            stopbtn.setEnabled(true);
        } else if (Date.equals("14") && dayaftertomorrow) {
            button.setEnabled(true);
            button.setClickable(true);
            stopbtn.setClickable(true);
            stopbtn.setEnabled(true);
        } else {
            button.setEnabled(false);
            button.setClickable(false);
            stopbtn.setClickable(false);
            stopbtn.setEnabled(false);
        }
        int stopbtn_visible = 0;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopbtn.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
                text_active.setVisibility(View.INVISIBLE);
                final int status = (Integer) v.getTag();
//                mStopWatch.setBase(SystemClock.elapsedRealtime());
//                mStopWatch.stop();
                isChronometerRunning  = false;
                chronoeditor.putBoolean("CHRONO",false).apply();
                if (Date.equals("3")) {
                    if (today) {
                        Toast.makeText(getActivity(), "I am here", Toast.LENGTH_SHORT).show();
                        chronoeditor.putBoolean("CHRONO",true).apply();
                        mStopWatch.setBase(SystemClock.elapsedRealtime());
                        mStopWatch.start();

                        mStopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer cArg) {
                                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                                int h = (int) (time / 3600000);

                                isChronometerRunning=true;
                                int m = (int) (time - h * 3600000) / 60000;
                                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                                hh = h < 10 ? "0" + h : h + "";
                                mm = m < 10 ? "0" + m : m + "";
                                ss = s < 10 ? "0" + s : s + "";
                                if (s > 20) {
                                    stopbtn.setEnabled(false);
                                    stopbtn.setBackgroundResource(R.drawable.round_button_grey);
                                    stopbtn.setClickable(false);
                                }
                                if (Integer.parseInt(stepsView.getText().toString()) >= 6666) {

                                    Toast.makeText(getActivity(), "You Completed Your Journey in " + hh + ":" + mm + ":" + ss + " time!", Toast.LENGTH_LONG).show();
                                    makedialog();
                                    button.setClickable(false);
                                    button.setEnabled(false);
                                    stopbtn.setClickable(false);
                                    stopbtn.setEnabled(false);
                                    mStopWatch.stop();
                                    isChronometerRunning  = false;
                                    long MinimumTime = run.getLong("MinimumTime", 1000000);
                                    MinimumTime = Math.min(MinimumTime, time);
                                    h = (int) (MinimumTime / 3600000);
                                    m = (int) (MinimumTime - h * 3600000) / 60000;
                                    s = (int) (MinimumTime - h * 3600000 - m * 60000) / 1000;
                                    hh = h < 10 ? "0" + h : h + "";
                                    mm = m < 10 ? "0" + m : m + "";
                                    ss = s < 10 ? "0" + s : s + "";
                                    if (m < 13 && h == 0) {
                                        Toast.makeText(getActivity(), "Security Reason TRY NEXT TIME!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addToDatabase(firebaseAuth.getUid(), "vipin", hh + ":" + mm + ":" + ss);
                                        Database db = Database.getInstance(getActivity());
                                        db.deleteAll();
                                        db.addToLastEntry(0);
                                        db.saveCurrentSteps(0);
                                        db.close();
                                    }
                                    if (Date.equals("3")) {
                                        editor.putBoolean("today", true).apply();

                                        editor.putLong("MinimumTime", MinimumTime);
                                    } else if (Date.equals("13")) {

                                        editor.putBoolean("tomorrow", true).apply();
                                        editor.putLong("MinimumTime", MinimumTime);
                                    } else if (Date.equals("14")) {
                                        editor.putLong("MinimumTime", MinimumTime);
                                        editor.putBoolean("dayaftertomorrow", true).apply();
                                    }
                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
//                Bitmap bitmap=takescreen();
//                saveBitmap(bitmap);
//                shareit();
//                    View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//                    Bitmap bmp=getScreenShot(rootView);
//                    store(bmp,"file.png");
//                    shareImage(file);
                                    //UPLOAD TO DATABASE TIME
                                    //String time=hh+":"+mm+":"+ss;
                                    cArg.setText(hh + ":" + mm + ":" + ss);
                                    set_to_zero();
                                    button.setText("Start Run");

                                }
                            }


                        });


                        isChronometerRunning  = true;
                        v.setTag(0);
//                        start_again();
                    }
                } else if (Date.equals("13")) {
                    if (tomorrow) {
                        mStopWatch.setBase(SystemClock.elapsedRealtime());
                        mStopWatch.start();
                        chronoeditor.putBoolean("CHRONO",true).apply();
                        isChronometerRunning  = true;

                        mStopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer cArg) {
                                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                                int h = (int) (time / 3600000);

                                isChronometerRunning=true;
                                int m = (int) (time - h * 3600000) / 60000;
                                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                                hh = h < 10 ? "0" + h : h + "";
                                mm = m < 10 ? "0" + m : m + "";
                                ss = s < 10 ? "0" + s : s + "";
                                if (s > 20) {
                                    stopbtn.setEnabled(false);
                                    stopbtn.setBackgroundResource(R.drawable.round_button_grey);
                                    stopbtn.setClickable(false);
                                }
                                if (Integer.parseInt(stepsView.getText().toString()) >= 6666) {

                                    Toast.makeText(getActivity(), "You Completed Your Journey in " + hh + ":" + mm + ":" + ss + " time!", Toast.LENGTH_LONG).show();
                                    makedialog();
                                    button.setClickable(false);
                                    button.setEnabled(false);
                                    stopbtn.setClickable(false);
                                    stopbtn.setEnabled(false);
                                    mStopWatch.stop();
                                    isChronometerRunning  = false;
                                    long MinimumTime = run.getLong("MinimumTime", 1000000);
                                    MinimumTime = Math.min(MinimumTime, time);
                                    h = (int) (MinimumTime / 3600000);
                                    m = (int) (MinimumTime - h * 3600000) / 60000;
                                    s = (int) (MinimumTime - h * 3600000 - m * 60000) / 1000;
                                    hh = h < 10 ? "0" + h : h + "";
                                    mm = m < 10 ? "0" + m : m + "";
                                    ss = s < 10 ? "0" + s : s + "";
                                    if (m < 13 && h == 0) {
                                        Toast.makeText(getActivity(), "Security Reason TRY NEXT TIME!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addToDatabase(firebaseAuth.getUid(), "vipin", hh + ":" + mm + ":" + ss);
                                    }
                                    if (Date.equals("3")) {
                                        editor.putBoolean("today", true).apply();

                                        editor.putLong("MinimumTime", MinimumTime);
                                    } else if (Date.equals("13")) {

                                        editor.putBoolean("tomorrow", true).apply();
                                        editor.putLong("MinimumTime", MinimumTime);
                                    } else if (Date.equals("14")) {
                                        editor.putLong("MinimumTime", MinimumTime);
                                        editor.putBoolean("dayaftertomorrow", true).apply();
                                    }
                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
//                Bitmap bitmap=takescreen();
//                saveBitmap(bitmap);
//                shareit();
//                    View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//                    Bitmap bmp=getScreenShot(rootView);
//                    store(bmp,"file.png");
//                    shareImage(file);
                                    //UPLOAD TO DATABASE TIME
                                    //String time=hh+":"+mm+":"+ss;
                                    cArg.setText(hh + ":" + mm + ":" + ss);
                                    set_to_zero();
                                    button.setText("Start Run");

                                }
                            }


                        });

                        v.setTag(0);
                        start_again();
                    }
                } else if (Date.equals("14")) {
                    if (dayaftertomorrow) {
                        mStopWatch.setBase(SystemClock.elapsedRealtime());
                        mStopWatch.start();
                        chronoeditor.putBoolean("CHRONO",true).apply();
                        mStopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer cArg) {
                                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                                int h = (int) (time / 3600000);

                                isChronometerRunning=true;
                                int m = (int) (time - h * 3600000) / 60000;
                                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                                hh = h < 10 ? "0" + h : h + "";
                                mm = m < 10 ? "0" + m : m + "";
                                ss = s < 10 ? "0" + s : s + "";
                                if (s > 20) {
                                    stopbtn.setEnabled(false);
                                    stopbtn.setBackgroundResource(R.drawable.round_button_grey);
                                    stopbtn.setClickable(false);
                                }
                                if (Integer.parseInt(stepsView.getText().toString()) >= 6666) {

                                    Toast.makeText(getActivity(), "You Completed Your Journey in " + hh + ":" + mm + ":" + ss + " time!", Toast.LENGTH_LONG).show();
                                    makedialog();
                                    button.setClickable(false);
                                    button.setEnabled(false);
                                    stopbtn.setClickable(false);
                                    stopbtn.setEnabled(false);
                                    mStopWatch.stop();
                                    isChronometerRunning  = false;
                                    long MinimumTime = run.getLong("MinimumTime", 1000000);
                                    MinimumTime = Math.min(MinimumTime, time);
                                    h = (int) (MinimumTime / 3600000);
                                    m = (int) (MinimumTime - h * 3600000) / 60000;
                                    s = (int) (MinimumTime - h * 3600000 - m * 60000) / 1000;
                                    hh = h < 10 ? "0" + h : h + "";
                                    mm = m < 10 ? "0" + m : m + "";
                                    ss = s < 10 ? "0" + s : s + "";
                                    if (m < 13 && h == 0) {
                                        Toast.makeText(getActivity(), "Security Reason TRY NEXT TIME!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addToDatabase(firebaseAuth.getUid(), "vipin", hh + ":" + mm + ":" + ss);
                                    }
                                    if (Date.equals("3")) {
                                        editor.putBoolean("today", true).apply();

                                        editor.putLong("MinimumTime", MinimumTime);
                                    } else if (Date.equals("13")) {

                                        editor.putBoolean("tomorrow", true).apply();
                                        editor.putLong("MinimumTime", MinimumTime);
                                    } else if (Date.equals("14")) {
                                        editor.putLong("MinimumTime", MinimumTime);
                                        editor.putBoolean("dayaftertomorrow", true).apply();
                                    }
                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
//                Bitmap bitmap=takescreen();
//                saveBitmap(bitmap);
//                shareit();
//                    View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//                    Bitmap bmp=getScreenShot(rootView);
//                    store(bmp,"file.png");
//                    shareImage(file);
                                    //UPLOAD TO DATABASE TIME
                                    //String time=hh+":"+mm+":"+ss;
                                    cArg.setText(hh + ":" + mm + ":" + ss);
                                    set_to_zero();
                                    button.setText("Start Run");

                                }
                            }


                        });

                        isChronometerRunning  = true;
                        v.setTag(0);
                        start_again();
                    }
                }else{
                    isChronometerRunning  = false;
                    mStopWatch.setBase(SystemClock.elapsedRealtime());
                    mStopWatch.stop();
                    chronoeditor.putBoolean("CHRONO",false).apply();
                    v.setTag(1);
                }
            }
        });
        // slice for the steps taken today
        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", Fragment_Settings.DEFAULT_GOAL, Color.parseColor("#F99E2B"));
        pg.addPieSlice(sliceGoal);
//        stepsDistanceChanged();
        pg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
//                showSteps = !showSteps;
//                stepsDistanceChanged();
            }
        });

        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();

        return v;
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.logout_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    private void showdialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setCancelable(false);
        builder1.setMessage("Logout");
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getActivity().getApplicationContext(), Google_Sign_In.class));
                        getActivity().finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void set_to_zero() {

//        stepsView.setText(String.valueOf(0));


        SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);

        try {
            Database db = Database.getInstance(getActivity());
            SensorListener.getNotification_to_Zero(getActivity());

            db.close();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "FAILED!!", Toast.LENGTH_SHORT).show();
        }

//        API26Wrapper.startForeground(NOTIFICATION_ID, SensorListener.getNotification_to_Zero(getActivity()));

    }

    public void start_again() {
        set_to_zero();

        Database db = Database.getInstance(getActivity());

        if (BuildConfig.DEBUG) db.logState();
        // read todays offset
        todayOffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);

        goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
        since_boot = db.getCurrentSteps();
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        // register a sensorlistener to live update the UI if a step is taken
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.no_sensor)
                    .setMessage(R.string.no_sensor_explain)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface dialogInterface) {
                            getActivity().finish();
                        }
                    }).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        }

        since_boot -= pauseDifference;

        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();

        db.close();

        stepsDistanceChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity(),"PASUED",Toast.LENGTH_SHORT).show();
//
//        mStopWatch.setBase(SystemClock.elapsedRealtime());
//        mStopWatch.stop();
//
        boolean running = prefchrono.getBoolean("CHRONO", false);
        if(running) {
            String w[]=getLastDB().split(",");
            Toast.makeText(getActivity(),"RUNNING "+w[0]+":"+w[1],Toast.LENGTH_SHORT).show();
//            try{stepsView.setText(since_boot);}catch(Exception e){stepsView.setText(w[0]);}
//            since_boot=Integer.parseInt(w[0]);
            stopbtn.setBackgroundResource(R.drawable.round_button_grey);
            stopbtn.setVisibility(View.VISIBLE);
            button.setVisibility(View.GONE);
            text_active.setVisibility(View.GONE);


        }else{
            Toast.makeText(getActivity(),"NOT RUNNING",Toast.LENGTH_SHORT).show();
            stopbtn.setBackgroundResource(R.drawable.round_button);
            stopbtn.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            text_active.setVisibility(View.VISIBLE);
            if(!stepsView.getText().toString().equals("0")){
                stopbtn.performClick();
                mStopWatch.stop();
            }
        }
        Database db = Database.getInstance(getActivity());

        if (BuildConfig.DEBUG) db.logState();
        // read todays offset
        todayOffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);

        goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
        since_boot = db.getCurrentSteps();
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        // register a sensorlistener to live update the UI if a step is taken
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.no_sensor)
                    .setMessage(R.string.no_sensor_explain)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface dialogInterface) {
                            getActivity().finish();
                        }
                    }).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        }

        since_boot -= pauseDifference;

        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();

        db.close();


        stepsDistanceChanged();
    }

    /**
     * Call this method if the Fragment should update the "steps"/"km" text in
     * the pie graph as well as the pie and the bars graphs.
     */
    private void stepsDistanceChanged() {
//        showSteps=!showSteps;
        if (showSteps) {
            unitid.setText(getString(R.string.steps));
        } else {
            String unit = "km";
            unitid.setText(unit);
        }

        updatePie();

    }
    public void AddData(String steps,String time){
//        if(myDB.insertData(steps,time)){
//            Toast.makeText(getActivity(),"DONE!!",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(getActivity(),"ERR DB!!",Toast.LENGTH_SHORT).show();
//        }
    }
    public String getLastDB(){
//        Cursor res=myDB.getLastData();
//        if(res.getCount()==0){
//            Toast.makeText(getActivity(),"FRESH RUN",Toast.LENGTH_SHORT).show();
//            return "0,0";
//        }
//        res.moveToPosition(res.getCount() - 1);
//
//        String step_from_db=res.getString(1);
//        String time_from_db=res.getString(2);
//        Toast.makeText(getActivity(),step_from_db+" "+time_from_db,Toast.LENGTH_SHORT).show();
//        return step_from_db+","+time_from_db;
        return "0,0";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AddData("0","0");
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getActivity(),"PASUED",Toast.LENGTH_SHORT).show();
        try {
            SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
        }

        if(isChronometerRunning){
            long elapsedMillis = SystemClock.elapsedRealtime() - mStopWatch.getBase();
            AddData(stepsView.getText().toString(),String.valueOf(elapsedMillis));
            Toast.makeText(getActivity(),"SENT on DB PAUSE",Toast.LENGTH_SHORT).show();
            chronoeditor.putBoolean("CHRONO",true).apply();
        }else{
            chronoeditor.putBoolean("CHRONO",false).apply();
            if(!stepsView.getText().toString().equals("0")){
                stopbtn.performClick();
                mStopWatch.stop();
            }
        }


        Database db = Database.getInstance(getActivity());
        db.saveCurrentSteps(since_boot);
        db.close();
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // won't happen
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (BuildConfig.DEBUG) Logger.log(
                "UI - sensorChanged | todayOffset: " + todayOffset + " since boot: " +
                        event.values[0]);
        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        if (todayOffset == Integer.MIN_VALUE) {
            // no values for today
            // we dont know when the reboot was, so set todays steps to 0 by
            // initializing them with -STEPS_SINCE_BOOT
            todayOffset = -(int) event.values[0];
            Database db = Database.getInstance(getActivity());
            db.insertNewDay(Util.getToday(), (int) event.values[0]);
            db.close();
        }
        since_boot = (int) event.values[0];
        updatePie();

    }

    private void updatePie() {
        if(!isChronometerRunning){return;}
        if (BuildConfig.DEBUG) Logger.log("UI - update steps: " + since_boot);
        // todayOffset might still be Integer.MIN_VALUE on first start
        int steps_today = Math.max(todayOffset + since_boot, 0);
        sliceCurrent.setValue(steps_today);
        if (goal - steps_today > 0) {
            // goal not reached yet
            if (pg.getData().size() == 1) {
                // can happen if the goal value was changed: old goal value was
                // reached but now there are some steps missing for the new goal
                pg.addPieSlice(sliceGoal);
            }
            sliceGoal.setValue(goal - steps_today);
        } else {
            // goal reached
            pg.clearChart();
            pg.addPieSlice(sliceCurrent);
        }
        pg.update();
        if (showSteps) {
            stepsView.setText(formatter.format(steps_today));
            Toast.makeText(getActivity(),"STEPS TODAY="+steps_today,Toast.LENGTH_SHORT).show();

        } else {
            // update only every 10 steps when displaying distance
            SharedPreferences prefs =
                    getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
            float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE);
            float distance_today = steps_today * stepsize;
            float distance_total = (total_start + steps_today) * stepsize;
            if (prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT)
                    .equals("cm")) {
                distance_today /= 100000;
                distance_total /= 100000;
            } else {
                distance_today /= 5280;
                distance_total /= 5280;
            }
//            stepsView.setText(formatter.format(distance_today));
        }
    }


    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;

    }

    public void store(Bitmap bm, String fileName) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();

            Toast toast = Toast.makeText(getActivity(), "FAILED", Toast.LENGTH_SHORT);
        }
    }

    private void shareImage(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public void makedialog2() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_dos, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    public void saveBitmap(Bitmap bitmap) {
        imagepath = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + "screenshot.jpg");
        FileOutputStream fos;
        String path;
        //File file=new File(path);
        try {
            fos = new FileOutputStream(imagepath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ignored) {
        }
    }

    public void shareit() {
        Uri path = FileProvider.getUriForFile(getActivity(), "com.alcheringa.circularprogressbar", imagepath);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = Uri.fromFile(imagepath);
        share.putExtra(Intent.EXTRA_STREAM, uri);
//        share.putExtra(Intent.EXTRA_STREAM,path);
        share.setType("image/*");
        startActivity(Intent.createChooser(share, "Share..."));
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.fromFile(imagepath);
//        intent.setDataAndType(uri,"image/jpeg");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);

    }

    private void makedialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_option, null);
        Button sharebtn;
        TextView final_time = mView.findViewById(R.id.time);
        final_time.setText(hh + ":" + mm + ":" + ss);

//        closebtn=mView.findViewById(R.id.sports);
        sharebtn = mView.findViewById(R.id.sharebutton);


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
//                Bitmap bitmap=takescreen();
//                saveBitmap(bitmap);
//                shareit();
//                AlertDialog dialog2 =AlertDialog.class.cast(dialog);
//                takeScreenshot(dialog2);

                View rootView = dialog.getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bmp = getScreenShot(rootView);
                store(bmp, "file.png");
                shareImage(file);
                dialog.cancel();
            }
        });
    }


    private void addToDatabase(String uuid, String steps, String time) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Photo", firebaseAuth.getCurrentUser().getPhotoUrl().toString());
        hashMap.put("Name", firebaseAuth.getCurrentUser().getDisplayName());
        hashMap.put("Steps", steps);
        hashMap.put("Time", time);
        hashMap.put("Email", firebaseAuth.getCurrentUser().getEmail());
        firebaseDatabase.getReference().child("USERS").child(uuid).updateChildren(hashMap).addOnSuccessListener(aVoid ->
                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show());

    }

    public void makedialogrule() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_pdf, null);
        mBuilder.setView(mView);
        PDFView pdfView;
        pdfView = mView.findViewById(R.id.pdfView);
        pdfView.fromAsset("rule.pdf")
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_logout:
                    firebaseAuth.signOut();
                    startActivity(new Intent(getActivity().getApplicationContext(), Google_Sign_In.class));
                    getActivity().finish();
                default:
            }
            return false;
        }
    }
}
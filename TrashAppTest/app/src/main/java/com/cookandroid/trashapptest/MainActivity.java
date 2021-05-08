package com.cookandroid.trashapptest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    Context context;
    ImageButton btn1, btn2, btn3;
    Button rank, Total_rank;
    LinearLayout linear;


    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentMap fragmentMap = new FragmentMap();
    private FragmentRanking fragmentRanking = new FragmentRanking();
    private FragmentMyInfo fragmentMyInfo = new FragmentMyInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout, fragmentMap).commitAllowingStateLoss();

        btn1 = (ImageButton) findViewById(R.id.btn1);
        btn2 = (ImageButton) findViewById(R.id.btn2);
        btn3 = (ImageButton) findViewById(R.id.btn3);
        rank = (Button) findViewById(R.id.rank);
        Total_rank = (Button) findViewById(R.id.Total_rank);

        linear = (LinearLayout) findViewById(R.id.linear);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.framelayout, fragmentMap).commitAllowingStateLoss();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.framelayout, fragmentRanking).commitAllowingStateLoss();

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.framelayout, fragmentMyInfo).commitAllowingStateLoss();
            }
        });

    }
}

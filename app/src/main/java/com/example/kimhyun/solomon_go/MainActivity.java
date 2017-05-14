package com.example.kimhyun.solomon_go;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

        Button btn_Main_Profile, btn_Main_Solostop, btn_Main_Nearsolomon, btn_Main_Bag, btn_Main_Setting;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            btn_Main_Profile = (Button) findViewById(R.id.btn_Main_Profile);
            btn_Main_Solostop = (Button) findViewById(R.id.btn_Main_Solostop);
            btn_Main_Nearsolomon = (Button) findViewById(R.id.btn_Main_Nearsolomon);
            btn_Main_Bag = (Button) findViewById(R.id.btn_Main_Bag);
            btn_Main_Setting = (Button) findViewById(R.id.btn_Main_Setting);

            btn_Main_Profile.setOnClickListener(this);
            btn_Main_Solostop.setOnClickListener(this);
            btn_Main_Nearsolomon.setOnClickListener(this);
            btn_Main_Bag.setOnClickListener(this);
            btn_Main_Setting.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case R.id.btn_Main_Profile:
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    break;
                case R.id.btn_Main_Solostop:
                    intent = new Intent(MainActivity.this, SolostopActivity.class);
                    break;
                case R.id.btn_Main_Setting:
                    intent = new Intent(MainActivity.this, SettingActivity.class);
                    break;
                case R.id.btn_Main_Nearsolomon:
                    intent = new Intent(MainActivity.this, NearsolomonActivity.class);
                    break;
                case R.id.btn_Main_Bag:
                    intent = new Intent(MainActivity.this, BagActivity.class);
                    break;
            }
            startActivity(intent);

        }
    }
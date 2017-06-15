package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class BagActivity extends AppCompatActivity {
    SharedPreferences sp_id;
    int item;
    TextView text_Item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);
        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        item = sp_id.getInt("item",0);
        text_Item = (TextView)findViewById(R.id.text_Item);
        text_Item.setText("현재");
    }
}

package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by LEDPEAR on 2017-06-03.
 */

public class DialogAcitivy extends Activity implements View.OnClickListener {
    Button btn_Close;
    ImageView profile_Image;
    TextView profile_Text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        profile_Image = (ImageView)findViewById(R.id.profile_Image);
        profile_Text = (TextView)findViewById(R.id.profile_text);
        btn_Close =(Button)findViewById(R.id.btn_Close);
        btn_Close.setOnClickListener(this);

        Intent intent = getIntent();

        profile_Text.setText(intent.getStringExtra("profile"));
        profile_Image.setImageResource(intent.getIntExtra("img",R.drawable.circle));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Close:
                this.finish();
                break;
        }
    }
}

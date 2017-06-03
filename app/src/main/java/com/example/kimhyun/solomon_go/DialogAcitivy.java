package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by LEDPEAR on 2017-06-03.
 */

public class DialogAcitivy extends Activity implements View.OnClickListener {
    Button btn_Close;
    ImageView profile_Image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        profile_Image = (ImageView)findViewById(R.id.profile_Image);
        profile_Image.setImageResource(R.drawable.circle);
        setContent();
    }

    private void setContent() {
        btn_Close =(Button)findViewById(R.id.btn_Close);
        btn_Close.setOnClickListener(this);
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

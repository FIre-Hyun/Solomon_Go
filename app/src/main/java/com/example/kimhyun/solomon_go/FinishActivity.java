package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by LEDPEAR on 2017-06-03.
 */

public class FinishActivity extends Activity implements View.OnClickListener {
    Button btn_Yes,btn_No;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_finish);
        setContent();
    }

    private void setContent() {
        btn_Yes = (Button)findViewById(R.id.btn_Close_Yes);
        btn_No = (Button)findViewById(R.id.btn_Close_No);
        btn_Yes.setOnClickListener(this);
        btn_No.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Close_Yes:
                finish();
                break;
            case R.id.btn_Close_No:
                this.finish();
                break;
        }

    }
}


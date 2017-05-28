package com.example.kimhyun.solomon_go;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by kimhyun on 2017. 5. 27..
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText et_id, et_password;

    CheckBox ckbox_autologin;

    ImageView btn_login, btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);

        ckbox_autologin = (CheckBox) findViewById(R.id.ckbox_autologin);

        btn_login = (ImageView) findViewById(R.id.btn_login);
        btn_register = (ImageView) findViewById(R.id.btn_register);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch(v.getId()){
            case R.id.btn_login:
                //db에 보내서 확인하고 맞으면
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case R.id.btn_register:
                intent = new Intent(getApplicationContext(), Login_RegisterActivity.class);
                break;
        }
        startActivity(intent);
    }
}
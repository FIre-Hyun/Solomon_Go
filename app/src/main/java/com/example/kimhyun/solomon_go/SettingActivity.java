package com.example.kimhyun.solomon_go;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingActivity extends AppCompatActivity {

    CheckBox ckbox_disable, ckbox_push, ckbox_picture, ckbox_name, ckbox_age, ckbox_hobby, ckbox_job, ckbox_home, ckbox_etc, ckbox_sex;

    Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ckbox_disable = (CheckBox) findViewById(R.id.ckbox_disable);
        ckbox_push = (CheckBox) findViewById(R.id.ckbox_push);
        ckbox_picture = (CheckBox) findViewById(R.id.ckbox_picture);
        ckbox_name = (CheckBox) findViewById(R.id.ckbox_name);
        ckbox_age = (CheckBox) findViewById(R.id.ckbox_age);
        ckbox_hobby = (CheckBox) findViewById(R.id.ckbox_hobby);
        ckbox_job = (CheckBox) findViewById(R.id.ckbox_job);
        ckbox_home = (CheckBox) findViewById(R.id.ckbox_home);
        ckbox_etc = (CheckBox) findViewById(R.id.ckbox_etc);
        ckbox_sex = (CheckBox) findViewById(R.id.ckbox_sex);

        btn_logout = (Button) findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 실행하고
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}

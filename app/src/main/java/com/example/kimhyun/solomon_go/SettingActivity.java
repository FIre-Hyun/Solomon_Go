package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    CheckBox ckbox_disable, ckbox_push, ckbox_picture, ckbox_name, ckbox_age, ckbox_hobby, ckbox_job, ckbox_home, ckbox_etc, ckbox_sex;

    Button btn_logout;

    SharedPreferences sp_id;
    SharedPreferences.Editor editor;


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

        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = sp_id.edit();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 실행하고
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                editor.clear();
                editor.commit();

                Toast.makeText(SettingActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                startActivity(intent);
            }
        });
    }
}

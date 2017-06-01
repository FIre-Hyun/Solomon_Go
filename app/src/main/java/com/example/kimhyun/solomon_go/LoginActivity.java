package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by kimhyun on 2017. 5. 27..
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_id, et_password;

    CheckBox ckbox_autologin;

    ImageView btn_login, btn_register;

    SharedPreferences auto_login;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);

        ckbox_autologin = (CheckBox) findViewById(R.id.ckbox_autologin);

        btn_login = (ImageView) findViewById(R.id.btn_login);
        btn_register = (ImageView) findViewById(R.id.btn_register);


        auto_login = getSharedPreferences("setting", 0);
        editor = auto_login.edit();


        if(auto_login.getBoolean("Auto_Login_enabled", false)){

            et_id.setText(auto_login.getString("ID", ""));
            et_password.setText(auto_login.getString("PW", ""));
            ckbox_autologin.setChecked(true);


            String id = et_id.getText().toString();
            String password = et_password.getText().toString();

            insertToDatabase(id, password);



        }



        ckbox_autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){

                    String ID = et_id.getText().toString();
                    String PW = et_password.getText().toString();

                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                }else{
//	        		editor.remove("ID");
//	        		editor.remove("PW");
//	        		editor.remove("Auto_Login_enabled");
                    editor.clear();
                    editor.commit();
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                String id = et_id.getText().toString();
                String password = et_password.getText().toString();

                insertToDatabase(id, password);


                break;

            case R.id.btn_register:
                Intent intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
                startActivity(intent);

                break;
        }
    }

    private void insertToDatabase(String id, String password) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(LoginActivity.this,
                        "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                loading.dismiss();

                try {

                    Log.d("Login", result);

                    JSONObject jsonObj = new JSONObject(result);

                    String name = jsonObj.getJSONArray("result").getJSONObject(0).getString("name");

                    if(name != "null"){       //로그인이 성공한다면

                        Toast.makeText(getApplicationContext(), name + "님 안녕하세요", Toast.LENGTH_SHORT).show();

                        auto_login = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editer = auto_login.edit();

                        editer.putString("id", et_id.getText().toString());
                        editer.putString("password", et_password.getText().toString());
                        editer.commit();



                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);

                        finish();

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "로그인 실패! ㅠㅠ ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String link = "http://jun123101.cafe24.com/login.php";

                try {
                    String id =  params[0];
                    String password = params[1];

                    String data = URLEncoder.encode("id", "UTF-8") + "="
                            + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                            + URLEncoder.encode(password, "UTF-8");

                    Log.d("Login", "Insert Data Complete");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.d("sbbb", sb.toString());
                    return sb.toString();
                } catch (Exception e) {

                    return new String("Exception: " + e.getMessage());
                }


            }
        }

        InsertData task = new InsertData();
        task.execute(id, password);
    }


}
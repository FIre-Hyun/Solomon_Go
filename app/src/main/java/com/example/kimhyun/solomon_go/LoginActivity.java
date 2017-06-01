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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by kimhyun on 2017. 5. 27..
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_id, et_password;
    Button btn_Main_Go;
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

        btn_Main_Go = (Button)findViewById(R.id.btn_Main_Go);

        auto_login = getSharedPreferences("setting", 0);
        editor = auto_login.edit();


        if(auto_login.getBoolean("Auto_Login_enabled", false)){
            et_id.setText(auto_login.getString("ID", ""));
            et_password.setText(auto_login.getString("PW", ""));
            ckbox_autologin.setChecked(true);


            String id = et_id.getText().toString();
            String password = et_password.getText().toString();

            insertToDatabase(id, password);


            GetData task = new GetData();
            task.execute("http://jun123101.cafe24.com/login.php");
            Log.d("Login", "onclick까지 먹음");



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
        btn_Main_Go.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_login:

                String id = et_id.getText().toString();
                String password = et_password.getText().toString();

                insertToDatabase(id, password);


                GetData task = new GetData();
                task.execute("http://jun123101.cafe24.com/login.php");
                Log.d("Login", "onclick까지 먹음");


                break;

            case R.id.btn_register:
                intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
                startActivity(intent);

                break;
            case R.id.btn_Main_Go:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String id =  params[0];
                    String password = params[1];

                    String link = "http://jun123101.cafe24.com/login.php";
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
                    return sb.toString();
                } catch (Exception e) {

                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(id, password);
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d("Login", "PostExecute까지 먹음");
            Log.d("result", result);


            try {

                JSONObject jsonObj = new JSONObject(result);
                Log.d("Login", "Json받자");
                String name = jsonObj.getJSONArray("result").getJSONObject(0).getString("name");

                Log.d("Login", "Json받았니??? ㅠㅠㅠㅠ ");
                Toast.makeText(getApplicationContext(), name + "님 ㅎㅇ", Toast.LENGTH_SHORT).show();

                if(name != null){       //로그인이 성공한다면

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

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

    }

}
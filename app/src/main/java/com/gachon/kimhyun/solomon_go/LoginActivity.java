package com.gachon.kimhyun.solomon_go;

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

        auto_login = getSharedPreferences("auto", Activity.MODE_PRIVATE);   // 프리퍼런스를 생성하고 auto라는 이름을 붙혀준다
        editor = auto_login.edit();


        if(auto_login.getBoolean("Auto_Login_enabled", false)){ // auto_login이  가지고있는 "Auto_Login_enable" 항목(자동로그인) 이 false이면 => (자동로그인이 체크돼있으면)

            et_id.setText(auto_login.getString("ID", ""));
            et_password.setText(auto_login.getString("PW", ""));    //id, password에 auto_login이 가지고 있는 프리퍼런스 값을 넣는다
            ckbox_autologin.setChecked(true);   //  체크박스도 체크해둔다


            String id = et_id.getText().toString();
            String password = et_password.getText().toString();

            insertToDatabase(id, password);     // id 체크



        }


        btn_Main_Go.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_login:        //로그인 버튼을 누르면

                String id = et_id.getText().toString();
                String password = et_password.getText().toString();

                insertToDatabase(id, password);     // id 체크


                break;

            case R.id.btn_register:
                intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
                intent.putExtra("before", "Login");
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
            protected void onPreExecute() {     //생명주기 첫번째,, ProgressDialog 시작하기
                super.onPreExecute();

                loading = ProgressDialog.show(LoginActivity.this,
                        "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {       // 생명주기 세번째,,
                super.onPostExecute(result);

                loading.dismiss();

                try {

                    Log.d("Login", result);

                    JSONObject jsonObj = new JSONObject(result);        // login.php에서 json값을 받아온다

                    String name = jsonObj.getJSONArray("result").getJSONObject(0).getString("name");    //result라는 이름의 json값에서 0번째 name 항목을 받아온다.

                    if(name != "null"){       //로그인이 성공한다면

                        Toast.makeText(getApplicationContext(), name + "님 안녕하세요", Toast.LENGTH_SHORT).show();

                        String ID = et_id.getText().toString();

                        if(ckbox_autologin.isChecked()){    //자동로그인이 눌려있으면

                            String PW = et_password.getText().toString();

                            editor.putString("PW", PW);     //preference에 password 값과 자동로그인을 넣어둔다
                            editor.putBoolean("Auto_Login_enabled", true);
                        }else{
                            editor.clear();     //자동로그인이 안눌려져있으면 자동로그인 정보들을 지운다.
                        }

                        Log.d("로그인 pre", ID);
                        editor.putString("ID", ID);
                        editor.commit();



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
                            + URLEncoder.encode(password, "UTF-8");     //data에 id값과 password 값을 넣는다.

                    Log.d("Login", "Insert Data Complete");

                    Log.d("lgdata", data);
                    Log.d("lgid", id);
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();  //서버에 있는 login.php에 연결

                    conn.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);     // OutputStreamWriter에 data값을 넣는다.
                    wr.flush();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);      //php문에 갔다와서 반환되는 값을 받는다
                        break;
                    }
                    Log.d("sbbb", sb.toString());
                    return sb.toString();   //php문에서 반환된 sb값을 return 한다.
                } catch (Exception e) {

                    return new String("Exception: " + e.getMessage());
                }


            }
        }

        InsertData task = new InsertData();
        task.execute(id, password);     //AsyncTask 실행행
    }

}
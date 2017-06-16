package com.gachon.kimhyun.solomon_go;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btn_Main_Profile, btn_Main_Solostop, btn_Main_Nearsolomon, btn_Main_Bag, btn_Main_Setting, btn_Main_Recently;

    Button btn_Main_Logout;

    String imgUrl = "http://jun123101.cafe24.com/picture/", id;

    picture task;

    int item,day,setdate;

    Bitmap bmImg;

    SharedPreferences sp_id;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_Main_Profile = (ImageView) findViewById(R.id.btn_Main_Profile);
        btn_Main_Solostop = (ImageView) findViewById(R.id.btn_Main_Solostop);
        btn_Main_Nearsolomon = (ImageView) findViewById(R.id.btn_Main_Nearsolomon);
        btn_Main_Bag = (ImageView) findViewById(R.id.btn_Main_Bag);
        btn_Main_Setting = (ImageView) findViewById(R.id.btn_Main_Setting);
        btn_Main_Recently = (ImageView) findViewById(R.id.btn_Main_Recently);
        btn_Main_Logout = (Button) findViewById(R.id.btn_Main_Logout);

        btn_Main_Profile.setOnClickListener(this);
        btn_Main_Solostop.setOnClickListener(this);
        btn_Main_Nearsolomon.setOnClickListener(this);
        btn_Main_Bag.setOnClickListener(this);
        btn_Main_Setting.setOnClickListener(this);
        btn_Main_Recently.setOnClickListener(this);
        btn_Main_Logout.setOnClickListener(this);

        checkDangerousPermissions();

        task = new picture();
        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = sp_id.edit();

        //하루에 3개까지 솔로포인트를 얻을 수 있도록 설정
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        day = date.getDate();
        setdate = sp_id.getInt("date",0);

        if(setdate!=day){
            setdate=day;
            editor.putInt("date",day);
            editor.putInt("num",0);
            editor.commit();
            Toast.makeText(getApplicationContext(), "오늘 첫 로그인!!\n솔로포인트를 하루 3개까지 얻을 수 있습니다!", Toast.LENGTH_LONG).show();
        }

        id = sp_id.getString("ID", "");
        Log.d("prefer", id);
        task.execute(imgUrl + "picture_" + id + ".png");    // 아이디를 이용해서 서버에서 사진을 받아옴

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //종료방지
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                String buttonMessage = "어플을 종료하시겠습니까?";
                String buttonYes = "Yes";
                String buttonNo = "No";

                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(buttonMessage)
                        .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                moveTaskToBack(true);
                                finish();
                            }
                        })
                        .setNegativeButton(buttonNo, null)
                        .show();
        }
        return true;
    }

    private void checkDangerousPermissions() {
        String[] permissions = {//import android.Manifest;
                Manifest.permission.ACCESS_FINE_LOCATION,   //GPS 이용권한
                Manifest.permission.ACCESS_COARSE_LOCATION, //네트워크/Wifi 이용 권한
                Manifest.permission.READ_EXTERNAL_STORAGE,  //읽기 권한
                Manifest.permission.WRITE_EXTERNAL_STORAGE  //쓰기 권한
        };

        //권한을 가지고 있는지 체크
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }//end of checkDangerousPermissions

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_Main_Profile:
                intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                intent.putExtra("before", "change");
                startActivity(intent);
                break;
            case R.id.btn_Main_Solostop:
                intent = new Intent(MainActivity.this, SolostopActivity.class);
                intent.putExtra("map",1);
                startActivity(intent);
                break;
            case R.id.btn_Main_Setting:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_Main_Nearsolomon:
                intent = new Intent(MainActivity.this, NearsolomonActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_Main_Bag:
                item = sp_id.getInt("item",0);
                Toast.makeText(getApplicationContext(),"현재 보유 솔로포인트는 " + item +" 입니다.", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_Main_Recently:
                intent = new Intent(MainActivity.this, DialogAcitivy.class);//여기서 이미지나 프로필내용을 보내주어야 한다
                intent.putExtra("before", "recently");
                startActivity(intent);
                break;
            case R.id.btn_Main_Logout:

                editor.clear();
                editor.commit();

                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }

    }

    private class picture extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {   //서버에서 이미지를 id에 맞는 이미지를 불러옴
            // TODO Auto-generated method stub
            Log.d("Main", "back 들어옴");
            try {
                Log.d("Main", "try 들어옴");
                URL myFileUrl = new URL(urls[0]);

                Log.d("Main", "들어옴");
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }

        protected void onPostExecute(Bitmap img) {
            btn_Main_Profile.setImageBitmap(getRoundedBitmap(bmImg));
        }

    }
    public static Bitmap getRoundedBitmap(Bitmap bitmap) {      //받아온 이미지를 동그랗게 만드는 메소드
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.GRAY;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }


}
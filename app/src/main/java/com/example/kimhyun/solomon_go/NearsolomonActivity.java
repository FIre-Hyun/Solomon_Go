package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;


public class NearsolomonActivity extends AppCompatActivity {
    private static String TAG = "Nearsolomon";

    private static final String TAG_JSON = "near";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_AGE = "age";

    NearMemberAdapter adapter;


    ListView mlistView;
    String mJsonString;

    ImageView btn_reload;

    Bitmap bmImg;

    Drawable drawable;

    String id;
    SharedPreferences sp_id;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearsolomon);

        mlistView = (ListView) findViewById(R.id.listView_main_list);
        btn_reload = (ImageView) findViewById(R.id.btn_reload);

        adapter = new NearMemberAdapter();

        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = sp_id.edit();

        Location lastLocation = starLocationService();  //GPS값 받아오기
        Double latitude = lastLocation.getLatitude();
        Double longitude = lastLocation.getLongitude();

        compareGPS(latitude.toString(), longitude.toString());      // 다른 솔로몬들과 GPS값을 비교한다


        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //리스트 항목 클릭시 자세한 프로필이 나옴

                NearMember item = (NearMember)adapter.getItem(position);
                NearMember item2 = new NearMember();
                Intent intent = new Intent(getApplicationContext(), DialogAcitivy.class);
                String profile = item.getId();
                intent.putExtra("profile",profile);
                editor.putString("lover", item.getId());

                intent.putExtra("before", "near");

                startActivity(intent);
            }
        });
        
        btn_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       // 새로고침, 갱신

                adapter = new NearMemberAdapter();

                Location lastLocation = starLocationService();
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                compareGPS(latitude.toString(), longitude.toString());

            }
        });
    }

    private void compareGPS(String latitude, String longitude) {

        class InsertGPS extends AsyncTask<String, Void, String> {
            ProgressDialog progressDialog;
            String errorString = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(NearsolomonActivity.this,
                        "Please Wait", null, true, true);
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                progressDialog.dismiss();
                Log.d(TAG, "response  - " + result);

                mJsonString = result;
                showResult();
            }


            @Override
            protected String doInBackground(String... params) {

                String serverURL ="http://jun123101.cafe24.com/near.php";   //near.php에 접속


               try {
                    String latitude =  params[0];
                    String longitude = params[1];

                    String data = URLEncoder.encode("latitude", "UTF-8") + "="
                            + URLEncoder.encode(latitude, "UTF-8");
                    data += "&" + URLEncoder.encode("longitude", "UTF-8") + "="
                            + URLEncoder.encode(longitude, "UTF-8");

                    URL url = new URL(serverURL);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data); //현재 위도, 경도값을 data에 넣고 near.php에 보낸다.
                    wr.flush();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }


                    reader.close();


                    return sb.toString().trim();


                } catch (Exception e) {

                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();

                    return null;
                }

            }
        }
        InsertGPS task = new InsertGPS();
        task.execute(latitude, longitude);
    }

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);        //json Array 형태로 받아온다
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            mlistView.setAdapter(adapter);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String age = item.getString(TAG_AGE);

                if(!id.equals(sp_id.getString("ID",""))){

                    GetImage imagetask = new GetImage();
                    bmImg = imagetask.execute("http://jun123101.cafe24.com/picture/picture_" + id + ".png").get();  //서버에서 이미지를 받아오기위해 imagetask 실행

                    drawable = new BitmapDrawable(getResources(), bmImg);

                    if (drawable != null) {

                        Log.d("drawable2", drawable.toString());
                    }

                    adapter.addItem(drawable, id, name, age);
                }
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    private class GetImage extends AsyncTask<String, Integer, Bitmap> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(NearsolomonActivity.this,
                    "Please Wait", null, true, true);

        }

        @Override
        protected Bitmap doInBackground(String... urls) {   //서버에 접속한 후 이미지를 불러온다
            // TODO Auto-generated method stub
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
            progressDialog.dismiss();
        }

    }



    private void insertToDatabase(String latitude, String longitude) {

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String latitude = (String) params[0];
                    String longitude = (String) params[1];

                    String link = "http://jun123101.cafe24.com/insertgps.php";
                    String data = URLEncoder.encode("latitude", "UTF-8") + "="
                            + URLEncoder.encode(latitude, "UTF-8");
                    data += "&" + URLEncoder.encode("longitude", "UTF-8") + "="
                            + URLEncoder.encode(longitude, "UTF-8");
                    data += "&" + URLEncoder.encode("id", "UTF-8") + "="
                            + URLEncoder.encode(id, "UTF-8");

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
        task.execute(latitude, longitude);
    }


    //여기서부터 gps부분

    private Location starLocationService(){
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        GPSListener gpsListener = new GPSListener();

        long minTime = 1000;//GPS정보 전달 시간 지정 - 1초마다 위치정보 전달
        float minDistance = 0;//이동거리 지정 - 이동하면 무조건 갱신

        //showCurrentLocation((double)latitude_position,(double)longitude_position);//위치이동
        //위치정보는 위치 프로바이더(Location Provider)를 통해 얻는다
        try {
            // GPS를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, //위치 정보 확인 방법 설정
                    minTime, // 위치 정보 갱신 시간 설정
                    minDistance, //위치 정보 갱신을 위한 최소 이동거리 설정
                    gpsListener);//위치가 변동될 때마다 위치 정보 갱신을 위한 리스너 설정

            // 네트워크(기지국)를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();
                //Toast.makeText(getApplicationContext(), "Last Known Location : " + "Latitude : "
                //        + latitude + "\nLongitude:" + longitude, Toast.LENGTH_LONG).show();
                LatLng nowPoint = new LatLng(latitude,longitude);

                return lastLocation;
            }

        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private class GPSListener implements LocationListener {
        //위치 정보가 확인(수신)될 때마다 자동 호출되는 메소드
        public void onLocationChanged(Location location) {

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            if(location.getAccuracy()<1000){

                insertToDatabase(latitude.toString(), longitude.toString());
            }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }// end of GPSListener

}
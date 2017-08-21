package dgsw.hs.kr.gpsjungol;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    String xml;

    private GpsInfo gpsInfo;

    TextView tv;

    Button btn1;
    Button btn2;
    Button btn3;

    String cityName = "";

    int cityCode = 0;

    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    HashMap<Integer, ArrayList> nodeMap = new HashMap<Integer, ArrayList>();

    HashMap<String, Object> tempMap = new HashMap<String, Object>();
    HashMap<String, Object> destMap = new HashMap<String, Object>();         // Key : 목적지 한글명 Value : routeid

    StringBuilder sb;

    ArrayList<String> stationList = new ArrayList<String>();
    ArrayList<String> nodeIdList = new ArrayList<String>();
    ArrayList<String> busNoList = new ArrayList<String>();
    ArrayList<String> destList = new ArrayList<String>();

    ArrayAdapter arrayAdapter;
    ArrayAdapter arrayAdapter2;
    ArrayAdapter arrayAdapter3;

    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;

    String[][] stationString = new String[10][10];
    HashMap<String, String> routeMap = new HashMap<String, String>();

    private static final int MAX_STR = 300;

    int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, stationList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        arrayAdapter2 = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, busNoList);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1 = (Spinner)findViewById(R.id.stationSpinner);
        spinner1.setAdapter(arrayAdapter);

        spinner2 = (Spinner)findViewById(R.id.busSpinner);
        spinner2.setAdapter(arrayAdapter2);

        spinner3 = (Spinner)findViewById(R.id.destSpinner);
        spinner3.setAdapter(arrayAdapter3);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();

                            boolean isDup = false;

                            busNoList = new ArrayList<String>();

                            String stationName = stationString[position][0];
                            String[] tempString;

                            tempString = stationString[position];

                            for(int i = 1; stationString[position][i] != null; i++) {
                                BusNumberParser parser = new BusNumberParser();
                                ArrayList<HashMap<String, Object>> arrayList = parser.parser(cityCode, tempString[i]);

                                for(int j = 0; j < arrayList.size(); j++) {
                                    tempMap = arrayList.get(j);

                                    for(int k = 0; k < busNoList.size(); k++) {
                                        if(busNoList.get(k).equals(tempMap.get("routeno"))) {
                                            isDup = true;
                                            break;
                                        }
                                    }

                                    if(!isDup) {
                                        routeMap.put(tempMap.get("routeno").toString(), tempMap.get("routeid").toString());
                                        busNoList.add(tempMap.get("routeno").toString());
                                    }
                                }
                            }

                            arrayAdapter2 = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, busNoList);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    spinner2.setAdapter(arrayAdapter2);
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "인터넷이나 GPS 문제이거나, 지원하지 않는 장소입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();

                            destList = new ArrayList<String>();

                            BusRouteParser parser = new BusRouteParser();
                            ArrayList<HashMap<String, Object>> arrayList = parser.parser(cityCode, routeMap.get(spinner2.getSelectedItem()).toString());

                            HashMap<String, Object> dummyMap = new HashMap<String, Object>();

                            for(int i = 0; i < arrayList.size(); i++) {
                                dummyMap = arrayList.get(i);

                                destMap.put(dummyMap.get("nodenm").toString(), dummyMap.get("nodeid"));
                                destList.add(dummyMap.get("nodenm").toString());
                            }

                            arrayAdapter3 = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, destList);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    spinner3.setAdapter(arrayAdapter3);
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "인터넷이나 GPS 문제이거나, 지원하지 않는 장소입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else {
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
            //곧바로 OnRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else {
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
            //곧바로 OnRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else {
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
            //곧바로 OnRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);

        }


        // Location 제공자에서 정보를 얻어오기(GPS)
        // 1. Location을 사용하기 위한 권한을 얻어와야한다 AndroidManifest.xml
        //     ACCESS_FINE_LOCATION : NETWORK_PROVIDER, GPS_PROVIDER
        //     ACCESS_COARSE_LOCATION : NETWORK_PROVIDER
        // 2. LocationManager 를 통해서 원하는 제공자의 리스너 등록
        // 3. GPS 는 에뮬레이터에서는 기본적으로 동작하지 않는다
        // 4. 실내에서는 GPS_PROVIDER 를 요청해도 응답이 없다.  특별한 처리를 안하면 아무리 시간이 지나도
        //    응답이 없다.
        //    해결방법은
        //     ① 타이머를 설정하여 GPS_PROVIDER 에서 일정시간 응답이 없는 경우 NETWORK_PROVIDER로 전환
        //     ② 혹은, 둘다 한꺼번헤 호출하여 들어오는 값을 사용하는 방식.

        tv = (TextView) findViewById(R.id.textView2);
        tv.setText("위치정보 미수신중");

        btn1 = (Button) findViewById(R.id.toggle1);
        btn2 = (Button) findViewById(R.id.toggle2);
        btn3 = (Button) findViewById(R.id.toggle3);

        // LocationManager 객체를 얻어온다
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    gpsInfo = new GpsInfo(MainActivity.this);
                    //if(tb.isChecked()){
                    tv.setText("위도 : " + gpsInfo.getLatitude() + "  경도 : " + gpsInfo.getLongitude());

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "ERROR!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cityName = "";
                            double latitude;
                            double longitude;

                            Looper.prepare();

                            gpsInfo = new GpsInfo(MainActivity.this);

                            if(gpsInfo.isGetLocation()) {
                                latitude = gpsInfo.getLatitude();
                                longitude = gpsInfo.getLongitude();

                                BusStopParser parser = new BusStopParser();
                                ArrayList<HashMap<String, Object>> arrayList = parser.parser(latitude, longitude);

                                if(arrayList.isEmpty()) {
                                    Toast.makeText(MainActivity.this, "근처에 정류장이 없습니다!", Toast.LENGTH_SHORT).show();
                                } else {

                                    String dupCheck = "";

                                    Geocoder mGeoCoder = new Geocoder(MainActivity.this, Locale.KOREA);

                                    List<Address> addrs = mGeoCoder.getFromLocation(latitude, longitude, 1);
                                    final String city = addrs.get(0).getLocality();

                                    boolean isDup = false;

                                    cityCode = transformCityName(city);

                                    stationList = new ArrayList<String>();
                                    nodeIdList = new ArrayList<String>();

                                    ArrayList<String> tempArray = new ArrayList<String>();
                                    int tempIdx = 0;

                                    for (int i = 0; i < arrayList.size(); i++) {
                                        hashMap = arrayList.get(i);

                                        if(!(hashMap.get("citycode").toString().equals(cityCode + ""))) {
                                            continue;
                                        }

                                        boolean isDone = false;

                                        String nodenm = hashMap.get("nodenm").toString();
                                        String nodeid = hashMap.get("nodeid").toString();

                                        for(int j = 0; ; j++) {
                                            if(stationString[j][0] == null) {
                                                for(int k = 0; ; k++) {
                                                    if(stationString[k][0] == null) {
                                                        stationList.add(nodenm);
                                                        stationString[k][0] = nodenm;
                                                        stationString[k][1] = nodeid;
                                                        break;
                                                    }
                                                }

                                                break;
                                            }

                                            if(stationString[j][0].equals(nodenm)) {
                                                for(int k = 1; ; k++) {
                                                    if(stationString[j][k] == null) {
                                                        stationString[j][k] = nodeid;
                                                        isDone = true;
                                                        break;
                                                    }
                                                }
                                            }

                                            if(isDone)
                                                break;
                                        }
                                    }

                                    for(int i = 0; stationString[i][0] != null; i++) {
                                        for(int j = 0; stationString[i][j] != null; j++) {
                                            System.out.println(stationString[i][j]);
                                        }
                                    }

                                    arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, stationList);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            spinner1.setAdapter(arrayAdapter);

                                            tv.setText(city);
                                        }
                                    });
                                }
                            } else {
                                gpsInfo.showSettingsAlert();
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "인터넷이나 GPS 문제이거나, 지원하지 않는 장소입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int transformCityName(String cityName) {
        switch(cityName) {
            case City.NAME_AHSAN:
                return City.CODE_AHSAN;

            case City.NAME_CHANGWON:
                return City.CODE_CHANGWON;

            case City.NAME_CHEONAN:
                return City.CODE_CHEONAN;

            case City.NAME_CHUNCHEON:
                return City.CODE_CHUNCHEON;

            case City.NAME_CHUNGJOO:
                return City.CODE_CHUNGJOO;

            case City.NAME_DAEGU:
                return City.CODE_DAEGU;

            case City.NAME_DAEJUN:
                return City.CODE_DAEJUN;

            case City.NAME_GEOJAE:
                return City.CODE_GEOJAE;

            case City.NAME_GIMHAE:
                return City.CODE_GIMHAE;

            case City.NAME_GOONSAN:
                return City.CODE_GOONSAN;

            case City.NAME_GWANGJOO:
                return City.CODE_GWANGJOO;

            case City.NAME_GWANGYANG:
                return City.CODE_GWANGYANG;

            case City.NAME_GYEONGSAN:
                return City.CODE_GYEONGSAN;

            case City.NAME_SEOUL:
            case City.NAME_INCHEON:
                return City.CODE_INCHEON;

            case City.NAME_JEJU:
                return City.CODE_JEJU;

            case City.NAME_JEONJOO:
                return City.CODE_JEONJOO;

            case City.NAME_JINJOO:
                return City.CODE_JINJOO;

            case City.NAME_MILYANG:
                return City.CODE_MILYANG;

            case City.NAME_POHANG:
                return City.CODE_POHANG;

            case City.NAME_SOONCHEON:
                return City.CODE_SOONCHEON;

            case City.NAME_TONGYEONG:
                return City.CODE_TONGYEONG;

            case City.NAME_ULSAN:
                return City.CODE_ULSAN;

            case City.NAME_WONJOO:
                return City.CODE_WONJOO;

            case City.NAME_YANGSAN:
                return City.CODE_YANGSAN;

            case City.NAME_YEOSOO:
                return City.CODE_YEOSOO;

            default:
                return -1;
        }
    }
}

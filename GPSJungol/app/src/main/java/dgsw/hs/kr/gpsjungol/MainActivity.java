package dgsw.hs.kr.gpsjungol;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    Button btn1;
    Button btn2;
    Button btn3;

    StringBuilder sb;

    String xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                try{
                    //if(tb.isChecked()){
                        tv.setText("수신중..");
                        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                        lm.requestLocationUpdates((String)LocationManager.GPS_PROVIDER, (long)100, (float)1, mLocationListener);
                    //Toast.makeText(getApplicationContext(), "1" + (String)LocationManager.GPS_PROVIDER, Toast.LENGTH_SHORT).show();
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);

                    //Toast.makeText(getApplicationContext(), "2" + LocationManager.NETWORK_PROVIDER, Toast.LENGTH_SHORT).show();
                    /*}else{
                        Toast.makeText(getApplicationContext(), "??", Toast.LENGTH_SHORT).show();
                        tv.setText("위치정보 미수신중");
                        Toast.makeText(getApplicationContext(), "5", Toast.LENGTH_SHORT).show();
                        lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                        Toast.makeText(getApplicationContext(), "6", Toast.LENGTH_SHORT).show();
                    }*/
                }catch(SecurityException ex){

                }catch(Exception e) {

                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 내용

                                }
                            }, 0);

                            StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList"); /*URL*/
                            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=EbPZIVMOvAUeHygZz6sAApvBW7YLcSxn3jy%2F195I1Gk9ilGJLGxch4csWd9Ha6aGHZ7qSzBpeOAx1SwJ5RPNAw%3D%3D"); /*Service Key*/
                            urlBuilder.append("&" + URLEncoder.encode("gpsLati", "UTF-8") + "=" + URLEncoder.encode("36.3", "UTF-8")); /*파라미터설명*/
                            urlBuilder.append("&" + URLEncoder.encode("gpsLong", "UTF-8") + "=" + URLEncoder.encode("127.3", "UTF-8")); /*파라미터설명*/

                            URL url = new URL(urlBuilder.toString());
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("Content-type", "application/json");
                            System.out.println("Response code: " + conn.getResponseCode());
                            BufferedReader rd;
                            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            } else {
                                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                            }
                            sb = new StringBuilder();
                            String line;
                            while ((line = rd.readLine()) != null) {
                                sb.append(line);
                            }
                            rd.close();
                            conn.disconnect();

                            apiParserSearch();


                        } catch(Exception e) {
                            Toast.makeText(getApplicationContext(), "ERROR!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.start();

                //Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    } // end of onCreate

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            //Toast.makeText(getApplicationContext(), "3" + location, Toast.LENGTH_SHORT).show();

            //Toast.makeText(getApplicationContext(), "onLocationChanged, location:" + location, Toast.LENGTH_SHORT).show();
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            tv.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Toast.makeText(getApplicationContext(), "onProviderDisabled, provider:" + provider, Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Toast.makeText(getApplicationContext(), "onProviderEnabled, provider:" + provider, Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Toast.makeText(getApplicationContext(), "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras, Toast.LENGTH_SHORT).show();
        }
    };

    public void positionCheck(Location location) {
        final double longitude = location.getLongitude(); //경도
        final double latitude = location.getLatitude();   //위도
        double altitude = location.getAltitude();   //고도
        float accuracy = location.getAccuracy();    //정확도
        String provider = location.getProvider();   //위치제공자
        //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
        //Network 위치제공자에 의한 위치변화
        //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
        tv.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);

        new Thread() {
            public void run() {
                try {
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 내용

                        }
                    }, 0);

                    StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList"); /*URL*/
                    urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=EbPZIVMOvAUeHygZz6sAApvBW7YLcSxn3jy%2F195I1Gk9ilGJLGxch4csWd9Ha6aGHZ7qSzBpeOAx1SwJ5RPNAw%3D%3D"); /*Service Key*/
                    urlBuilder.append("&" + URLEncoder.encode("gpsLati", "UTF-8") + "=" + URLEncoder.encode(latitude + "", "UTF-8")); /*파라미터설명*/
                    urlBuilder.append("&" + URLEncoder.encode("gpsLong", "UTF-8") + "=" + URLEncoder.encode(longitude + "", "UTF-8")); /*파라미터설명*/

                    URL url = new URL(urlBuilder.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");
                    System.out.println("Response code: " + conn.getResponseCode());
                    BufferedReader rd;
                    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }
                    sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                    conn.disconnect();
                } catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "ERROR!", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();

        xml = sb.toString();
        Toast.makeText(getApplicationContext(), xml, Toast.LENGTH_SHORT).show();
    }

    public void apiParserSearch() throws Exception {
        ArrayList<String> arrayList = new ArrayList<String>();

        URL url = new URL("http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList?ServiceKey=EbPZIVMOvAUeHygZz6sAApvBW7YLcSxn3jy%2F195I1Gk9ilGJLGxch4csWd9Ha6aGHZ7qSzBpeOAx1SwJ5RPNAw%3D%3D&gpsLati=36.3&gpsLong=127.3");

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");

        String tag = null;
        int event_type = xpp.getEventType();

        ArrayList<String> list = new ArrayList<String>();

        String addr = null;
        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
            } else if (event_type == XmlPullParser.TEXT) {
                if(tag.equals("nodenm")) {
                    arrayList.add(xpp.getText());
                }
            } else if (event_type == XmlPullParser.END_TAG) {
                tag = xpp.getName();
                if (tag.equals("item")) {
                    list.add(addr);
                }
            }

            event_type = xpp.next();
        }
    }

} // end of class

package dgsw.hs.kr.gpsjungol;

import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static dgsw.hs.kr.gpsjungol.BusStopParser.API_KEY;
import static dgsw.hs.kr.gpsjungol.BusStopParser.URL_HEAD;

/**
 * Created by Developer on 2017-08-17.
 */

class BusStopParser {

    public static final String URL_HEAD = "http://openapi.tago.go.kr/openapi/service/";
    public static final String API_KEY = "EbPZIVMOvAUeHygZz6sAApvBW7YLcSxn3jy%2F195I1Gk9ilGJLGxch4csWd9Ha6aGHZ7qSzBpeOAx1SwJ5RPNAw%3D%3D";

    public ArrayList<HashMap<String, Object>> parser(double latitude, double longitude) throws ParserConfigurationException, UnsupportedEncodingException {

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

        String url = URL_HEAD;
        String parameter = "&gpsLati=" + latitude + "&gpsLong=" + longitude + "&numOfRows=99&pageSize=99";

        url = url + "BusSttnInfoInqireService/getCrdntPrxmtSttnList" + "?ServiceKey=" + API_KEY + parameter;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document d = null;
        String parse = url;
        try {
            d = db.parse(parse);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        Element e = d.getDocumentElement();

        int len = e.getElementsByTagName("citycode").getLength();

        if(len == 0) {
            Log.i("LEN", "0");
        }

        for(int i = 0; i < len; i++) {
            Node cityCode = e.getElementsByTagName("citycode").item(i);
            Node gpsLati = e.getElementsByTagName("gpslati").item(i);
            Node gpsLong = e.getElementsByTagName("gpslong").item(i);
            Node nodeId = e.getElementsByTagName("nodeid").item(i);
            Node nodeNm = e.getElementsByTagName("nodenm").item(i);

            HashMap<String, Object> hashMap = new HashMap<String, Object>();

            hashMap.put("citycode", cityCode.getTextContent());
            hashMap.put("gpslati", gpsLati.getTextContent());
            hashMap.put("gpslong", gpsLong.getTextContent());
            hashMap.put("nodeid", nodeId.getTextContent());
            hashMap.put("nodenm", nodeNm.getTextContent());

            arrayList.add(hashMap);
        };

        return arrayList;
    }
}

class BusNumberParser {

    public ArrayList<HashMap<String, Object>> parser(int cityCode, String nodeId) throws ParserConfigurationException, UnsupportedEncodingException {

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

        String url = URL_HEAD;
        String parameter = "&cityCode=" + cityCode + "&nodeId=" + nodeId + "&numOfRows=99&pageSize=99";

        url = url + "ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList" + "?ServiceKey=" + API_KEY + parameter;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document d = null;
        String parse = url;
        try {
            d = db.parse(parse);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        Element e = d.getDocumentElement();

        int len = e.getElementsByTagName("routeno").getLength();

        if(len == 0) {
            Log.i("LEN", "0");
        }

        for(int i = 0; i < len; i++) {
            Node arrTime = e.getElementsByTagName("arrtime").item(i);
            Node routeId = e.getElementsByTagName("routeid").item(i);
            Node routeNo = e.getElementsByTagName("routeno").item(i);

            HashMap<String, Object> hashMap = new HashMap<String, Object>();

            hashMap.put("arrtime", arrTime.getTextContent());
            hashMap.put("routeid", routeId.getTextContent());
            hashMap.put("routeno", routeNo.getTextContent());

            arrayList.add(hashMap);
        };

        return arrayList;
    }

    public void debug() {
        Log.i("AA", "debug: HELLO");
    }
}

class BusRouteParser {

    public static final String URL_HEAD = "http://openapi.tago.go.kr/openapi/service/";
    public static final String API_KEY = "EbPZIVMOvAUeHygZz6sAApvBW7YLcSxn3jy%2F195I1Gk9ilGJLGxch4csWd9Ha6aGHZ7qSzBpeOAx1SwJ5RPNAw%3D%3D";

    public ArrayList<HashMap<String, Object>> parser(int cityCode, String routeId) throws ParserConfigurationException, UnsupportedEncodingException {

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

        String url = URL_HEAD;
        String parameter = "&cityCode=" + cityCode + "&routeId=" + routeId + "&numOfRows=200&pageSize=200";

        url = url + "BusRouteInfoInqireService/getRouteAcctoThrghSttnList" + "?ServiceKey=" + API_KEY + parameter;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document d = null;
        String parse = url;
        try {
            d = db.parse(parse);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        Element e = d.getDocumentElement();

        int len = e.getElementsByTagName("nodenm").getLength();

        if (len == 0) {
            Log.i("LEN", "0");
        }

        for (int i = 0; i < len; i++) {
            Node nodeNm = e.getElementsByTagName("nodenm").item(i);
            Node nodeId = e.getElementsByTagName("nodeid").item(i);

            HashMap<String, Object> hashMap = new HashMap<String, Object>();

            hashMap.put("nodenm", nodeNm.getTextContent());
            hashMap.put("nodeid", nodeId.getTextContent());

            arrayList.add(hashMap);
        }
        ;

        return arrayList;
    }
}

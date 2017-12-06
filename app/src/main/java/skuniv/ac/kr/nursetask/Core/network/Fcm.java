package skuniv.ac.kr.nursetask.Core.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import skuniv.ac.kr.nursetask.UI.Admin.AdminTodayScheduleActivity;

/**
 * Created by gunyoungkim on 2017-11-28.
 */

public class Fcm {
    private String sender;
    private String msg;
    private String token;
    private String action;

    public Fcm(String sender,String msg,String token,String action){
        this.msg=msg;
        this.sender=sender;
        this.token=token;
        this.action=action;
    }
    public void start(){


        new AsyncTask<Void, Void, String>() {


            @Override
            protected String doInBackground(Void... params) {

                String result = "";
//                try {
//            msg=URLEncoder.encode(msg,"UTF-8");
//            Log.d("msg",msg);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
                // 1. 내 서버정보 세팅
                String server_url = "http://117.17.142.135:8080/nurse/fcm";
                // 2. 서버로 전송할 POST message 세팅
                String post_data = "to_token=" + token + "&msg=" + msg + "&sender=" + sender+ "&action="+action;

                try {
                    // 3. HttpUrlConnection 을 사용해서 내 서버로 메시지를 전송한다
                    //     a.서버연결
                    URL url = new URL(server_url);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    //     b.header 설정
                    con.setRequestMethod("POST");
                    //     c.POST데이터(body) 전송
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    os.write(post_data.getBytes());
                    os.flush();
                    os.close();
                    //     d.전송후 결과처리
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // code 200
                        // 결과처리후 내 서버에서 발송된 결과메시지를 꺼낸다.
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String dataLine = "";
                        // 메시지를 한줄씩 읽어서 result 변수에 담아두고
                        while ((dataLine = br.readLine()) != null) {
                            result = result + dataLine;
                        }
                        br.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // 결과처리된 메시지를 화면에 보여준다

            }
        }.execute();
    }
}

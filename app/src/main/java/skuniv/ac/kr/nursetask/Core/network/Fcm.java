package skuniv.ac.kr.nursetask.Core.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import skuniv.ac.kr.nursetask.UI.Admin.AdminTodayScheduleActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

/**
 * Created by gunyoungkim on 2017-11-28.
 */

    public class Fcm extends SafeAsyncTask<String> {
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
        @Override
        public String call() throws Exception {
            Log.d("msg",msg);
            String url="http://117.17.142.133:8080/nurse/fcm";
            String post_data = "to_token=" + token + "&msg=" + msg + "&sender=" + sender+ "&action="+action;
            HttpRequest request=HttpRequest.post(url);
            request.accept( HttpRequest.CONTENT_TYPE_JSON );
            request.connectTimeout( 1000 );
            request.readTimeout( 3000 );
            request.send(post_data);
            int responseCode = request.code();
            if ( responseCode != HttpURLConnection.HTTP_OK  ) {
                    /* 에러 처리 */
                System.out.println("---------------------ERROR");
                return null;
            }
            return null;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(String str) throws Exception {
            super.onSuccess(str);

        }
    }


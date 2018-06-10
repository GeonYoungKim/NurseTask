package skuniv.ac.kr.nursetask.Core.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.UI.Admin.AdminTodayScheduleActivity;
import skuniv.ac.kr.nursetask.UI.Admin.UpdateNurseRoomToken;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

/**
 * Created by gunyoungkim on 2017-11-28.
 */

public class Fcm extends AsyncTask<Void, Void, String> {
    private String answer;
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
    protected String doInBackground(Void... params) {

        OkHttpClient client = new OkHttpClient();
        Response response;
        RequestBody requestBody = null;

        requestBody = new FormBody.Builder().add("to_token",token).add("msg",msg).add("sender",sender).add("action",action).build();

        Request request = new Request.Builder()
                .url("http://117.17.142.133:8080/nurse/fcm")
                .post(requestBody)
                .build();
        try {
            response = client.newCall(request).execute();
            answer = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("answer", ""+answer);
        return answer;
    }

    protected void onPostExecute(Patient patient) {

    }
}



package skuniv.ac.kr.nursetask.UI.Admin;

import android.os.AsyncTask;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;

/**
 * Created by gunyoungkim on 2017-11-23.
 */

public class UpdateToken extends AsyncTask<Void, Void, String> {
    private String nurseId,token,answer;


    public UpdateToken(String nurseId,String token){
        this.nurseId=nurseId;
        this.token=token;
    }
    @Override
    protected String doInBackground(Void... params) {

        OkHttpClient client = new OkHttpClient();
        Response response;
        RequestBody requestBody = null;

        requestBody = new FormBody.Builder().add("nurseId",this.nurseId).add("token",this.token)
                .build();

        Request request = new Request.Builder()
                .url("http://117.17.142.133:8080/nurse/update-token")
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
        UpdateNurseRoomToken updateNurseRoomToken=new UpdateNurseRoomToken(this.nurseId,this.token);
        updateNurseRoomToken.execute();
    }
}
package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.Core.provider.NurseProvider;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.AdminListArrayAdapter;
import skuniv.ac.kr.nursetask.UI.Admin.AdminMainActivity;
import skuniv.ac.kr.nursetask.UI.Admin.AdminNursesListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.AdminPatientListArrayAdapter;
import skuniv.ac.kr.nursetask.UI.Admin.UpdateNurseRoomToken;
import skuniv.ac.kr.nursetask.UI.Admin.UpdateToken;

public class MainActivity extends AppCompatActivity {
    private EditText inputId;
    private EditText inputPassword;
    private String id;
    private String password;
    private String nurseId;
    private String nurseListToken="";
    private ChatActivity chatActivity;
    private ListArrayAdapter listArrayAdapter;
    private AdminListArrayAdapter adminListArrayAdapter;
    private AdminPatientListArrayAdapter adminPatientListArrayAdapter;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adminPatientListArrayAdapter=new AdminPatientListArrayAdapter(getApplicationContext());
        adminListArrayAdapter=new AdminListArrayAdapter(getApplicationContext());
        listArrayAdapter=new ListArrayAdapter(getApplicationContext());
        chatActivity=new ChatActivity();
        if(getNurse()!=null){
            if(getNurse().getnurseId().equals("admin")){
                admin_Login(getNurse());
            }else{
                nurse_Login(getNurse());
            }
        }

        inputId=(EditText)findViewById(R.id.nurseId);
        inputPassword=(EditText)findViewById(R.id.nursePassword);

        findViewById(R.id.memberShip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MemberShipActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=inputId.getText()+"";
                password=inputPassword.getText()+"";
                new LoginNurse().execute();

                inputId.setText("");
                inputPassword.setText("");
            }
        });
    }



    public class LoginNurse extends AsyncTask<Void, Void, Nurse> {
        Nurse answer;

        @Override
        protected Nurse doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            requestBody = new FormBody.Builder().add("id",id).add("password",password)
                    .build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/login")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                Gson gson=new Gson();
                answer = gson.fromJson(response.body().string(),Nurse.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        protected void onPostExecute(Nurse nurse) {
            if(nurse==null){
                Toast.makeText(getApplicationContext(),"로그인에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
            }else{
                if("admin".equals(nurse.getnurseId()+"")&&"admin".equals(nurse.getPassword()+"")){
                    storeNurse(nurse);
                    admin_Login(nurse);
                }else{
                    storeNurse(nurse);
                    nurse_Login(nurse);
                }
                new FatchNurseListAsyncTask().execute();
            }
        }
    }

    public class FatchNurseListAsyncTask extends SafeAsyncTask<List<Nurse>> {

        @Override
        public List<Nurse> call() throws Exception {
            List<Nurse> Nurses=new NurseProvider().FatchNurseList();
            return Nurses;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","arror"+e);
        }

        @Override
        protected void onSuccess(List<Nurse> Nurses) throws Exception {
            super.onSuccess(Nurses);
            for(Nurse nurse:Nurses){
                if(nurseListToken.equals("")){
                    nurseListToken+=nurse.getToken();
                }else{
                    nurseListToken+=","+nurse.getToken();
                }
            }
            Fcm fcm=new Fcm(getNurse().getName(),"updated_nurse",nurseListToken,getNurse().getnurseId());
            fcm.execute();
        }
    }


    private void storeNurse(Nurse nurse){
        SharedPreferences mSharedPreferences=getSharedPreferences("Text_number_store",MODE_PRIVATE);
        SharedPreferences.Editor mEditor=mSharedPreferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(nurse);
        mEditor.putString("nurse",json);
        mEditor.apply();

    }
    private Nurse getNurse(){
        Gson gson=new Gson();
        SharedPreferences mSharedPreferences=getSharedPreferences("Text_number_store",MODE_PRIVATE);
        String json=mSharedPreferences.getString("nurse","");
        Nurse nurse =gson.fromJson(json,Nurse.class);
        return nurse;
    }

    public void admin_Login(Nurse nurse){
        adminListArrayAdapter.setOwnNurse(nurse);

        nurseId=nurse.getnurseId();
        if(nurse.getToken().equals("0")){
            UpdateToken updateNurseToken=new UpdateToken(nurseId,getToken()+"");
            updateNurseToken.execute();
        }
        chatActivity.setMyNurseId(nurseId);
        chatActivity.setMyNurseName(nurse.getName());
        adminPatientListArrayAdapter.setNurse(nurse);
        Toast.makeText(getApplicationContext(),nurse.getName()+"",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,AdminMainActivity.class);
        intent.putExtra("nurseId",nurseId);
        startActivity(intent);
        finish();
    }
    public void nurse_Login(Nurse nurse){
        listArrayAdapter.setOwnNurse(nurse);
        nurseId=nurse.getnurseId();

        if(nurse.getToken().equals("0")){
            UpdateToken updateNurseToken=new UpdateToken(nurseId,getToken()+"");
            updateNurseToken.execute();
        }
        chatActivity.setMyNurseId(nurseId);
        chatActivity.setMyNurseName(nurse.getName());
        Toast.makeText(getApplicationContext(),nurse.getName()+"님 환영합니다.",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,ChoiceRoomActivity.class);
        intent.putExtra("nurseId",nurseId);
        startActivity(intent);
        finish();
    }

    public String getToken(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token===",token);
        return token;
    }


}

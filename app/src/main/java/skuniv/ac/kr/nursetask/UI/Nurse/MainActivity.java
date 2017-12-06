package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.NurseRoom;
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
import skuniv.ac.kr.nursetask.UI.Admin.AlarmNotificationReceiver;
import skuniv.ac.kr.nursetask.UI.Admin.GetSet;
import skuniv.ac.kr.nursetask.UI.Admin.UpdateToken;

public class MainActivity extends AppCompatActivity {
    public ChatRoomListFragment chatRoomListFragment;
    public AdminChatRoomListFragment adminChatRoomListFragment;
    public NurseListFragment nurseListFragment;
    public AdminNursesListFragment adminNursesListFragment;
    Intent alarmintent;
    PendingIntent pendingIntent;
    ChatActivity chatActivity;
    //Socket mSock=null;
    static BufferedReader mReader;
    static BufferedWriter mWriter;
    String mRecvData="";
    //CheckRecv mCheckRecv;
    private EditText inputid;
    private EditText inputpassword;
    private String id;
    private String password;
    String nurseid;
    String nurse_list_token="";
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getNurse()!=null){
            if(getNurse().getNurseid().equals("admin")){
                admin_Login(getNurse());
            }else{
                nurse_Login(getNurse());
            }
        }

        inputid=(EditText)findViewById(R.id.nurseId);
        inputpassword=(EditText)findViewById(R.id.nursePassword);

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
                id=inputid.getText()+"";
                password=inputpassword.getText()+"";
                new LoginNurse().execute();

                inputid.setText("");
                inputpassword.setText("");
            }
        });
    }
    private class LoginNurse extends SafeAsyncTask<Nurse> {
        @Override
        public Nurse call() throws Exception {

            System.out.println("---------------------------------"+id);
            System.out.println("---------------------------------"+password);
            String url="http://117.17.142.135:8080/nurse/login";
            String query="id="+id+"&password="+password;
            HttpRequest request=HttpRequest.post(url);
            request.accept( HttpRequest.CONTENT_TYPE_JSON );
            request.connectTimeout( 1000 );
            request.readTimeout( 3000 );
            request.send(query);
            int responseCode = request.code();
            if ( responseCode != HttpURLConnection.HTTP_OK  ) {
                    /* 에러 처리 */
                System.out.println("---------------------ERROR");
                return null;
            }
            JSONResultFatchNurse result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchNurse.class);
            Nurse nurse=result.getData();
            return nurse;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(Nurse nurse) throws Exception {
            super.onSuccess(nurse);
            if(nurse==null){
                Toast.makeText(getApplicationContext(),"로그인에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
            }else{
                if("admin".equals(nurse.getNurseid()+"")&&"admin".equals(nurse.getPassword()+"")){
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
    private class JSONResultFatchNurse extends JsonResult<Nurse> {}

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
                if(nurse_list_token.equals("")){
                    nurse_list_token+=nurse.getToken();
                }else{
                    nurse_list_token+=","+nurse.getToken();
                }
            }
            Fcm fcm=new Fcm(getNurse().getName(),"updated_nurse",nurse_list_token,getNurse().getNurseid());
            fcm.start();
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
        AdminListArrayAdapter.ownnurse=nurse;

        nurseid=nurse.getNurseid();
        if(nurse.getToken().equals("0")){
            UpdateToken updateNurseToken=new UpdateToken(nurseid,getToken()+"");
            updateNurseToken.execute();
        }
        ChatActivity.my_nurseid=nurseid;
        ChatActivity.my_nursename=nurse.getName();
        AdminPatientListArrayAdapter.nurse=nurse;
        Toast.makeText(getApplicationContext(),nurse.getName()+"",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,AdminMainActivity.class);
        intent.putExtra("nurseid",nurseid);
        startActivity(intent);
        finish();
    }
    public void nurse_Login(Nurse nurse){
        ListArrayAdapter.ownnurse=nurse;
        nurseid=nurse.getNurseid();

        if(nurse.getToken().equals("0")){
            UpdateToken updateNurseToken=new UpdateToken(nurseid,getToken()+"");
            updateNurseToken.execute();
        }
        ChatActivity.my_nurseid=nurseid;
        ChatActivity.my_nursename=nurse.getName();
        Toast.makeText(getApplicationContext(),nurse.getName()+"님 환영합니다.",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,ChoiceRoomActivity.class);
        intent.putExtra("nurseid",nurseid);
        startActivity(intent);
        finish();
    }

    public String getToken(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token===",token);
        return token;
    }


}

package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

import static java.security.AccessController.getContext;

public class AdminTodayScheduleActivity extends AppCompatActivity {
    private LinearLayout container;
    private EditText timeEdit,contentEdit;
    private int i;
    private Nurse nurse;
    private Map<EditText,EditText> todayScheduleMap;
    private String todayScheduleResult="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_today_schedule);

        Intent intent=getIntent();
        nurse=(Nurse)intent.getExtras().get("nurse");
        System.out.println(nurse.getToken());
        i=1;
        todayScheduleMap=new HashMap<EditText, EditText>();

        container=(LinearLayout) findViewById(R.id.today_parent_layout);
        timeEdit=(EditText)findViewById(R.id.today_schedule_time_1);
        contentEdit=(EditText)findViewById(R.id.today_schedule_content_1);
        todayScheduleMap.put(timeEdit,contentEdit);

        findViewById(R.id.today_schedule_addbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout linearLayout=new LinearLayout(AdminTodayScheduleActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams linear_params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(linear_params);

                LinearLayout.LayoutParams time_params=(LinearLayout.LayoutParams)timeEdit.getLayoutParams();

                EditText time_edit=new EditText(AdminTodayScheduleActivity.this);
                time_edit.setLayoutParams(time_params);
                time_edit.setHint("18:00");

                LinearLayout.LayoutParams content_param=(LinearLayout.LayoutParams)contentEdit.getLayoutParams();

                EditText content_edit=new EditText(AdminTodayScheduleActivity.this);
                content_edit.setLayoutParams(content_param);

                linearLayout.addView(time_edit);
                linearLayout.addView(content_edit);
                //부모 뷰에 추가
                container.addView(linearLayout);
                todayScheduleMap.put(time_edit,content_edit);
            }
        });

        findViewById(R.id.today_schedule_confirmbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(EditText time:todayScheduleMap.keySet()){
                    todayScheduleResult+=time.getText()+""+"-"+todayScheduleMap.get(time).getText()+"";
                    if(i!=todayScheduleMap.size()) {
                        todayScheduleResult += ",";
                    }
                    i++;
                }
                System.out.println(todayScheduleResult);
                new TodayScheduleUpdate().execute();
            }
        });

    }

    private class TodayScheduleUpdate extends AsyncTask<Void, Void, String> {
        String answer;
        @Override
        protected String doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            requestBody = new FormBody.Builder().add("todayScheduleResult",todayScheduleResult).add("nurseId",nurse.getnurseId())
                  .build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/today-schedule-update")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                answer = response.body().toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        protected void onPostExecute(Patient patient) {
            finish();

            Fcm fcm=new Fcm("update_schedule-"+nurse.getnurseId(),"confirm_schedule",nurse.getToken()+"","");
            fcm.execute();
        }
    }

}

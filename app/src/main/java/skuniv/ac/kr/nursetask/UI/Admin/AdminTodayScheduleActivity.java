package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

import static java.security.AccessController.getContext;

public class AdminTodayScheduleActivity extends AppCompatActivity {
    LinearLayout container;
    EditText timeEdit;
    EditText contentEdit;
    int i;
    Nurse nurse;
    Map<EditText,EditText> todayScheduleMap;
    String todayScheduleResult="";
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
    private class TodayScheduleUpdate extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            String url="http://117.17.142.133:8080/nurse/today-schedule-update";
            String query="todayScheduleResult="+todayScheduleResult+"&nurseId="+nurse.getnurseId();

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
            finish();

            Fcm fcm=new Fcm("update_schedule-"+nurse.getnurseId(),"confirm_schedule",nurse.getToken()+"","");
            fcm.execute();
        }
    }
}

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
    EditText time_et;
    EditText content_et;
    int i;
    Nurse nurse;
    Map<EditText,EditText> today_schedule_map;
    String today_schedule_result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_today_schedule);

        Intent intent=getIntent();
        nurse=(Nurse)intent.getExtras().get("nurse");
        System.out.println(nurse.getToken());
        i=1;
        today_schedule_map=new HashMap<EditText, EditText>();

        container=(LinearLayout) findViewById(R.id.today_parent_layout);
        time_et=(EditText)findViewById(R.id.today_schedule_time_1);
        content_et=(EditText)findViewById(R.id.today_schedule_content_1);
        today_schedule_map.put(time_et,content_et);

        findViewById(R.id.today_schedule_addbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout linearLayout=new LinearLayout(AdminTodayScheduleActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams linear_params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(linear_params);

                LinearLayout.LayoutParams time_params=(LinearLayout.LayoutParams)time_et.getLayoutParams();

                EditText time_edit=new EditText(AdminTodayScheduleActivity.this);
                time_edit.setLayoutParams(time_params);
                time_edit.setHint("18:00");

                LinearLayout.LayoutParams content_param=(LinearLayout.LayoutParams)content_et.getLayoutParams();

                EditText content_edit=new EditText(AdminTodayScheduleActivity.this);
                content_edit.setLayoutParams(content_param);

                linearLayout.addView(time_edit);
                linearLayout.addView(content_edit);
                //부모 뷰에 추가
                container.addView(linearLayout);
                today_schedule_map.put(time_edit,content_edit);
            }
        });

        findViewById(R.id.today_schedule_confirmbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(EditText time:today_schedule_map.keySet()){
                    today_schedule_result+=time.getText()+""+"-"+today_schedule_map.get(time).getText()+"";
                    if(i!=today_schedule_map.size()) {
                        today_schedule_result += ",";
                    }
                    i++;
                }
                System.out.println(today_schedule_result);
                new today_schedule_update().execute();
            }
        });

    }
    private class today_schedule_update extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            String url="http://117.17.142.135:8080/nurse/today_schedule_update";
            String query="today_schedule_result="+today_schedule_result+"&nurseid="+nurse.getNurseid();

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

            Fcm fcm=new Fcm("update_schedule-"+nurse.getNurseid(),"confirm_schedule",nurse.getToken()+"","");
            fcm.start();
        }
    }
}

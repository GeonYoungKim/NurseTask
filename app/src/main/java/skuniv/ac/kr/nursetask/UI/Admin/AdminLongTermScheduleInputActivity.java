package skuniv.ac.kr.nursetask.UI.Admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

public class AdminLongTermScheduleInputActivity extends AppCompatActivity {

    private Button startBtn,endBtn;
    private EditText contentEdit;
    private Nurse nurse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_long_term_schedule_input);

        Intent intent=getIntent();
        nurse=(Nurse) intent.getExtras().get("nurse");

        startBtn =(Button)findViewById(R.id.long_term_schedule_start_btn);
        endBtn =(Button)findViewById(R.id.long_term_schedule_end_btn);
        contentEdit =(EditText)findViewById(R.id.long_term_schedule_contentEdit);
        contentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"내용을 입력해주세요",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.long_term_schedule_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.long_term_schedule_store_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongTermScheduleInsert().execute();
                Toast.makeText(getApplicationContext(),"스케쥴을 부여 하였습니다.",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private class LongTermScheduleInsert extends AsyncTask<Void, Void, String> {
        String answer;
        @Override
        protected String doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;
            requestBody = new FormBody.Builder().add("startDay",startBtn.getText().toString()).add("endDay",endBtn.getText().toString()).add("content",contentEdit.getText().toString()).add("nurseId",nurse.getnurseId()).build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/long-term-schedule-insert")
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

        protected void onPostExecute(Room room) {
            finish();
            Fcm fcm=new Fcm("update_schedule-"+nurse.getnurseId(),"confirm_schedule",nurse.getToken()+"","");
            fcm.execute();
        }
    }

    public void dialogStartDatePicker(View view){
        Calendar calendar=Calendar.getInstance();
        DatePickerDialog dpd=new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ((Button)findViewById(R.id.long_term_schedule_start_btn)).setText(year+"년"+(monthOfYear+1)+"월"+dayOfMonth+"일");
            }
        },calendar.get(calendar.YEAR),calendar.get(calendar.MONTH),calendar.get(calendar.DATE)
        );
        dpd.show();
    }
    public void dialogEndDatePicker(View view){
        Calendar calendar=Calendar.getInstance();
        DatePickerDialog dpd=new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ((Button)findViewById(R.id.long_term_schedule_end_btn)).setText(year+"년"+(monthOfYear+1)+"월"+dayOfMonth+"일");
            }
        },calendar.get(calendar.YEAR),calendar.get(calendar.MONTH),calendar.get(calendar.DATE)
        );
        dpd.show();
    }
}

package skuniv.ac.kr.nursetask.UI.Admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.Calendar;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.MainActivity;

public class AdminLongTermScheduleInputActivity extends AppCompatActivity {

    Button start_btn;
    Button end_btn;
    EditText content_et;
    String nurseid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_long_term_schedule_input);

        Intent intent=getIntent();
        nurseid=(String)intent.getExtras().get("nurseid");

        start_btn=(Button)findViewById(R.id.long_term_schedule_start_btn);
        end_btn=(Button)findViewById(R.id.long_term_schedule_end_btn);
        content_et=(EditText)findViewById(R.id.long_term_schedule_content_et);
        content_et.setOnClickListener(new View.OnClickListener() {
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
                new long_term_schedule_insert().execute();
                Toast.makeText(getApplicationContext(),"스케쥴을 부여 하였습니다.",Toast.LENGTH_SHORT).show();

            }
        });


    }
    private class long_term_schedule_insert extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            String url="http://117.17.142.135:8080/controller/Nurse?a=long_term_schedule_insert";
            String query="startday="+start_btn.getText()+"&endday="+end_btn.getText()+"&content="+content_et.getText()+"&nurseid="+nurseid;

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
        }
    }
    public void dialog_start_date_picker(View view){
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
    public void dialog_end_date_picker(View view){
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

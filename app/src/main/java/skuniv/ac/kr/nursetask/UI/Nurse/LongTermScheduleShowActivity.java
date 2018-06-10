package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.LongTermScheduleVo;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;

public class LongTermScheduleShowActivity extends ListActivity {

    private String nurseId;

    private ListView lv;
    private List<LongTermScheduleVo> longTermScheduleVoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_term_schedule_show);

        Intent intent=getIntent();
        nurseId=(String)intent.getExtras().get("nurseId");

        lv= (ListView) findViewById(android.R.id.list);

        findViewById(R.id.long_term_schedule_show_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        new FatchLongTermScheduleShowAsyncTask().execute();


    }
    class CustomAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return longTermScheduleVoList.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView=getLayoutInflater().inflate(R.layout.row_long_term_schedule_show_list,null);
            ((TextView)convertView.findViewById(R.id.long_term_schedule_startday)).setText(longTermScheduleVoList.get(position).getStartDay());
            ((TextView)convertView.findViewById(R.id.long_term_schedule_endday)).setText(longTermScheduleVoList.get(position).getEndDay());

            ((Button)convertView.findViewById(R.id.long_term_schedule_contentBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new android.app.AlertDialog.Builder(LongTermScheduleShowActivity.this).
                            setTitle( "Schedule" ).
                            setIcon( android.R.drawable.ic_dialog_alert ).
                            setMessage( "content : "+ longTermScheduleVoList.get(position).getContent()+"\n"

                            ).
                            setPositiveButton( "예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println( "------> 예 clicked" );
                                }
                            } ).
                            show();
                }

            });
            return convertView;
        }
    }
    public class FatchLongTermScheduleShowAsyncTask extends SafeAsyncTask<List<LongTermScheduleVo>> {

        @Override
        public List<LongTermScheduleVo> call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/long-term-schedule-show";
            String query="nurseId="+nurseId;

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
            JSONResultFatchLongTermScheduleShow result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchLongTermScheduleShow.class);
            List<LongTermScheduleVo> longTermScheduleVos =result.getData();
            return longTermScheduleVos;

        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","error"+e);
        }
        @Override
        protected void onSuccess(List<LongTermScheduleVo> longTermScheduleVos) throws Exception {
            super.onSuccess(longTermScheduleVos);
            longTermScheduleVoList = longTermScheduleVos;
            CustomAdapter customAdapter = new CustomAdapter();
            lv.setAdapter(customAdapter);
        }
    }
    private class JSONResultFatchLongTermScheduleShow extends JsonResult<List<LongTermScheduleVo>> {}
}

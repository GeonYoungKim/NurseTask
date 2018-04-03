package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;

public class TodayScheduleShowActivity extends ListActivity {
    String nurseId;
    ListView lv;
    HashMap<String,String> todaySchduleMap;
    String[] todaySchduleStr;
    TreeMap<String,String> treeMap;
    Iterator<String> iterator;
    List<String> treeMapKeyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_schedule_show);

        todaySchduleMap=new HashMap<String, String>();
        Intent intent=getIntent();
        lv= (ListView) findViewById(android.R.id.list);
        nurseId=(String)intent.getExtras().get("nurseId");
        new FatchgetNurseTodayScheduleShowAsyncTask().execute();

        findViewById(R.id.today_schedule_show_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    class CustomAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return treeMap.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView=getLayoutInflater().inflate(R.layout.row_today_schedule_show_list,null);
            ((TextView)convertView.findViewById(R.id.today_schedule_show_time)).setText(treeMapKeyList.get(position));
            ((TextView)convertView.findViewById(R.id.today_schedule_show_content)).setText(treeMap.get(treeMapKeyList.get(position)));
            return convertView;
        }
    }
    public class FatchgetNurseTodayScheduleShowAsyncTask extends SafeAsyncTask<Nurse> {

        @Override
        public Nurse call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/today-schedule-show";
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
            JSONResultFatchgetNurseTodaySceduleShow result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchgetNurseTodaySceduleShow.class);
             Nurse nurse=result.getData();
            return nurse;

        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","error"+e);
        }
        @Override
        protected void onSuccess(Nurse nurse) throws Exception {
            super.onSuccess(nurse);
            Log.d("today",nurse.getTodaySchedule());
            todaySchduleStr=nurse.getTodaySchedule().split(",");


            for(int i=0;i<todaySchduleStr.length;i++){
                String[] today_schedule_unit_str=todaySchduleStr[i].split("-");
                todaySchduleMap.put(today_schedule_unit_str[0],today_schedule_unit_str[1]);
            }

            treeMap=new TreeMap<String,String>(todaySchduleMap);
            iterator=treeMap.keySet().iterator();
            treeMapKeyList=new ArrayList<String>();
            while(iterator.hasNext()){
                String key=iterator.next();
                treeMapKeyList.add(key);
            }

            CustomAdapter customAdapter = new CustomAdapter();
            lv.setAdapter(customAdapter);
        }
    }
    private class JSONResultFatchgetNurseTodaySceduleShow extends JsonResult<Nurse> {}
}

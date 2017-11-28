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
    String nurseid;
    ListView lv;
    HashMap<String,String> today_schdule_map;
    String[] today_schdule_str;
    TreeMap<String,String> treemap;
    Iterator<String> iterator;
    List<String> treemap_key_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_schedule_show);

        today_schdule_map=new HashMap<String, String>();
        Intent intent=getIntent();
        lv= (ListView) findViewById(android.R.id.list);
        nurseid=(String)intent.getExtras().get("nurseid");

        findViewById(R.id.today_schedule_show_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new FatchgetNurseTodayScheduleShowAsyncTask().execute();


    }
    class CustomAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return treemap.size();
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
            ((TextView)convertView.findViewById(R.id.today_schedule_show_time)).setText(treemap_key_list.get(position));
            ((TextView)convertView.findViewById(R.id.today_schedule_show_content)).setText(treemap.get(treemap_key_list.get(position)));
            return convertView;
        }
    }
    public class FatchgetNurseTodayScheduleShowAsyncTask extends SafeAsyncTask<Nurse> {

        @Override
        public Nurse call() throws Exception {
            String url="http://117.17.142.135:8080/controller/Nurse?a=today_schedule_show";
            String query="nurseid="+nurseid;

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
            today_schdule_str=nurse.getTodayschedule().split(",");
            for(int i=0;i<today_schdule_str.length;i++){
                String[] today_schedule_unit_str=today_schdule_str[i].split("-");
                today_schdule_map.put(today_schedule_unit_str[0],today_schedule_unit_str[1]);
            }

            treemap=new TreeMap<String,String>(today_schdule_map);
            iterator=treemap.keySet().iterator();
            treemap_key_list=new ArrayList<String>();
            while(iterator.hasNext()){
                String key=iterator.next();
                treemap_key_list.add(key);
            }

            CustomAdapter customAdapter = new CustomAdapter();
            lv.setAdapter(customAdapter);
        }
    }
    private class JSONResultFatchgetNurseTodaySceduleShow extends JsonResult<Nurse> {}
}

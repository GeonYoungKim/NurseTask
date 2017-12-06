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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.Core.provider.PatientProvider;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminInChargePatientSelectActivity;
import skuniv.ac.kr.nursetask.UI.Admin.AdminListArrayAdapter;

public class InchargePatientShowActivity extends ListActivity {
    String nurseid;
    ListView lv;
    List<Patient> patients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incharge_patient_show);


        Intent intent=getIntent();
        lv= (ListView) findViewById(android.R.id.list);
        nurseid=(String)intent.getExtras().get("nurseid");

        System.out.println(nurseid+"gggggggggggggggggggggggggggggggggggggggggg");

        findViewById(R.id.incharge_patient_show_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new FatchInchargePatientListShowAsyncTask().execute();
    }

    class CustomAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return patients.size();
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

            convertView=getLayoutInflater().inflate(R.layout.row_incharge_patient_show_list,null);
            ((TextView)convertView.findViewById(R.id.incharge_patient_show_name)).setText(patients.get(position).getName());
            return convertView;
        }
    }
    public class FatchInchargePatientListShowAsyncTask extends SafeAsyncTask<List<Patient>> {

        @Override
        public List<Patient> call() throws Exception {
            String url="http://117.17.142.135:8080/nurse/incharge_patient_show";
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
            JSONResultFatchInchargePatientListShow result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchInchargePatientListShow.class);
            List<Patient> patients=result.getData();
            return patients;

        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","error"+e);
        }
        @Override
        protected void onSuccess(List<Patient> Patients) throws Exception {
            super.onSuccess(Patients);
            System.out.println(Patients);
            patients=Patients;
            CustomAdapter customAdapter = new CustomAdapter();
            lv.setAdapter(customAdapter);
        }
    }
    private class JSONResultFatchInchargePatientListShow extends JsonResult<List<Patient>> {}
}

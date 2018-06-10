package skuniv.ac.kr.nursetask.UI.Admin;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
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
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.NurseProvider;
import skuniv.ac.kr.nursetask.Core.provider.PatientProvider;
import skuniv.ac.kr.nursetask.R;

public class AdminInChargePatientSelectActivity extends ListActivity {

    private Map<String,CheckBox> patientsCheckMap;
    private String checkedPatient;
    private Nurse nurse;
    private List<Patient> patients;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_in_charge_patient_select);

        patientsCheckMap=new HashMap<String,CheckBox>();
        checkedPatient="";
        Intent intent=getIntent();
        nurse=(Nurse) intent.getExtras().get("Nurse");
        new FatchAdminPatientListAsyncTask().execute();
        lv= (ListView) findViewById(android.R.id.list);

        findViewById(R.id.OKInChargePatientBtn).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for( String key : patientsCheckMap.keySet() ){
                    //System.out.println( String.format("환자 아이디 : %s, 해당 체크 되었는지 : %s", key, patientsCheckMap.get(key).isChecked()) );
                    if(patientsCheckMap.get(key).isChecked()==true){
                        if(checkedPatient.equals("")){
                            checkedPatient+=key;
                        }else{
                            checkedPatient+="-"+key;
                        }
                    }
                }
                new InsertInChargePatient().execute();
            }
        });
    }
    private class InsertInChargePatient extends AsyncTask<Void, Void, String> {
        String answer;
        @Override
        protected String doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;
            requestBody = new FormBody.Builder().add("nurseId",nurse.getnurseId()).add("patientCode",checkedPatient).build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/insert-incharge-patient")
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

        @Override
        protected void onPostExecute(String s) {
            if("good".equals(s)){
                Toast.makeText(getApplicationContext(),nurse.getnurseId()+"님에게 담당환자를 부여하였습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
            Fcm fcm=new Fcm("admin","incharge_patient_update",nurse.getToken()+"","");
            fcm.execute();
        }
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

            convertView=getLayoutInflater().inflate(R.layout.row_admin_incharge_patient_list,null);
            patientsCheckMap.put(patients.get(position).getPatientCode(),((CheckBox)convertView.findViewById(R.id.InchargeSelectedCheckBox)));
            ((TextView)convertView.findViewById(R.id.inchargePatientName)).setText(patients.get(position).getName());
            return convertView;
        }
    }

    public class FatchAdminPatientListAsyncTask extends SafeAsyncTask<List<Patient>> {

        @Override
        public List<Patient> call() throws Exception {
            List<Patient> Patients=new PatientProvider().FatchPatientList();
            return Patients;
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
}
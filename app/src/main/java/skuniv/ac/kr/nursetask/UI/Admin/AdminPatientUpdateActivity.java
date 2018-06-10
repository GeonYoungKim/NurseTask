package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

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
import skuniv.ac.kr.nursetask.Core.provider.NurseProvider;
import skuniv.ac.kr.nursetask.R;

public class AdminPatientUpdateActivity extends AppCompatActivity {

    private AdminPatientsListFragment adminPatientsListFragment;
    private Patient patient;
    private EditText[] patientUpdateContents;
    private String[] getpatientUpdateContents;
    private String nurseListToken="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patient_update);

        Intent intent=getIntent();

        patient=(Patient)intent.getExtras().get("patient");
        patientUpdateContents=new EditText[]{(EditText)findViewById(R.id.patientUpdate_patientName),(EditText)findViewById(R.id.patientUpdate_patientBirth),
                (EditText)findViewById(R.id.patientUpdate_patientSex),(EditText)findViewById(R.id.patientUpdate_patientDisease),(EditText)findViewById(R.id.patientUpdate_patientPeriod),
                (EditText)findViewById(R.id.patientUpdate_patientNote),(EditText)findViewById(R.id.patientUpdate_patientRoom)};


        getpatientUpdateContents=new String[]{patient.getName(),patient.getBirth(),patient.getSex(),patient.getDisease(),patient.getPeriod(),patient.getNote(),patient.getRoom()};
        for(int i=0;i<patientUpdateContents.length;i++){
            patientUpdateContents[i].setHint(getpatientUpdateContents[i]);
        }

        findViewById(R.id.patientUpdate_updateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<patientUpdateContents.length;i++){
                    getpatientUpdateContents[i]=patientUpdateContents[i].getText()+"";
                }
                new UpdatePatient().execute();
            }
        });
        findViewById(R.id.patientUpdate_cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class UpdatePatient extends AsyncTask<Void, Void, Patient> {
        Patient answer;
        @Override
        protected Patient doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            requestBody = new FormBody.Builder().add("patientCode",patient.getPatientCode()).add("name",getpatientUpdateContents[0])
                    .add("birth",getpatientUpdateContents[1]).add("sex",getpatientUpdateContents[2])
                    .add("disease",getpatientUpdateContents[3]).add("period",getpatientUpdateContents[4])
                    .add("note",getpatientUpdateContents[5]).add("room",getpatientUpdateContents[6]).build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/update-patient")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                Gson gson=new Gson();
                answer = gson.fromJson(response.body().toString(),Patient.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        protected void onPostExecute(Patient patient) {
            if(patient==null){
                Toast.makeText(getApplicationContext(),"환자수정에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"환자수정에 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                adminPatientsListFragment=AdminPatientsListFragment.getInstance();
                adminPatientsListFragment.realTimeUpdate();

                new FatchNurseListAsyncTask().execute();
                finish();
            }
        }
    }


    public class FatchNurseListAsyncTask extends SafeAsyncTask<List<Nurse>> {
        @Override
        public List<Nurse> call() throws Exception {
            List<Nurse> Nurses=new NurseProvider().FatchNurseList();
            return Nurses;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","arror"+e);
        }
        @Override
        protected void onSuccess(List<Nurse> Nurses) throws Exception {
            super.onSuccess(Nurses);
            for(Nurse nurse:Nurses){
                if(nurseListToken.equals("")){
                    nurseListToken+=nurse.getToken();
                }else{
                    nurseListToken+=","+nurse.getToken();
                }
            }
            Fcm fcm=new Fcm(getNurse().getName(),"Patient_update - > "+patient.getName(),nurseListToken,getNurse().getnurseId());
            fcm.execute();
        }
    }
    private Nurse getNurse(){
        Gson gson=new Gson();
        SharedPreferences mSharedPreferences=getSharedPreferences("Text_number_store",MODE_PRIVATE);
        String json=mSharedPreferences.getString("nurse","");
        Nurse nurse =gson.fromJson(json,Nurse.class);
        return nurse;
    }
}

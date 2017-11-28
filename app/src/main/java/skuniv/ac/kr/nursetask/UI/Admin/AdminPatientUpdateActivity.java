package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;

import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;

public class AdminPatientUpdateActivity extends AppCompatActivity {
    public AdminPatientsListFragment adminPatientsListFragment;
    Patient patient;
    EditText[] PatientUpdateContents;
    String[] getPatientUpdateContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patient_update);

        Intent intent=getIntent();
        patient=(Patient)intent.getExtras().get("patient");
        PatientUpdateContents=new EditText[]{(EditText)findViewById(R.id.patientUpdate_patientName),(EditText)findViewById(R.id.patientUpdate_patientBirth),
                (EditText)findViewById(R.id.patientUpdate_patientSex),(EditText)findViewById(R.id.patientUpdate_patientDisease),(EditText)findViewById(R.id.patientUpdate_patientPeriod),
                (EditText)findViewById(R.id.patientUpdate_patientNote),(EditText)findViewById(R.id.patientUpdate_patientRoom)};


        getPatientUpdateContents=new String[]{patient.getName(),patient.getBirth(),patient.getSex(),patient.getDisease(),patient.getPeriod(),patient.getNote(),patient.getRoom()};
        for(int i=0;i<PatientUpdateContents.length;i++){
            PatientUpdateContents[i].setHint(getPatientUpdateContents[i]);
        }

        findViewById(R.id.patientUpdate_updateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<PatientUpdateContents.length;i++){
                    getPatientUpdateContents[i]=PatientUpdateContents[i].getText()+"";
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
    private class UpdatePatient extends SafeAsyncTask<Patient> {
        @Override
        public Patient call() throws Exception {

            String url="http://117.17.142.135:8080/controller/Nurse?a=updatePatient";
            String query="patientcode="+patient.getPatientcode()+"&name="+getPatientUpdateContents[0]+"&birth="+getPatientUpdateContents[1]+
                    "&sex="+getPatientUpdateContents[2]+"&disease="+getPatientUpdateContents[3]+"&period="+getPatientUpdateContents[4]+"&note="+getPatientUpdateContents[5]
                    +"&room="+getPatientUpdateContents[6];

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
            JSONResultFatchUpdatePatient result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchUpdatePatient.class);
            Patient patient=result.getData();
            return patient;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(Patient patient) throws Exception {
            super.onSuccess(patient);

            if(patient==null){
                Toast.makeText(getApplicationContext(),"환자수정에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"환자수정에 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                adminPatientsListFragment=GetSet.getAdminPatientsListFragment();
                adminPatientsListFragment.realTimeupdate();
                finish();
            }
        }
    }
    private class JSONResultFatchUpdatePatient extends JsonResult<Patient> {}
}
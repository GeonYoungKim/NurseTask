package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.NurseProvider;
import skuniv.ac.kr.nursetask.Core.provider.back;
import skuniv.ac.kr.nursetask.R;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class AdminPatientListArrayAdapter extends ArrayAdapter<Patient> {
    private AdminPatientsListFragment adminPatientsListFragment;
    private String imageUrl = "http://117.17.142.133:8080/img/";
    private Bitmap bmImg;
    private String nurseListToken="";
    private Patient fcmPatinet;
    private Nurse nurse;

    public Nurse getNurse() {
        return nurse;
    }

    public void setNurse(Nurse nurse) {
        this.nurse = nurse;
    }

    private LayoutInflater layoutInflater;
    public AdminPatientListArrayAdapter(@NonNull Context context) {
        super(context, R.layout.row_admin_patient_list);
        layoutInflater=LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_admin_patient_list, parent, false);
        }
        final Patient patient=getItem(position);
        ((TextView)view.findViewById(R.id.patientName)).setText(patient.getName());
        ImageView bmImage = (ImageView) view.findViewById(R.id.imagebtn_patient);
        new back(bmImage,bmImg).execute(imageUrl + patient.getImage() + "");
        view.findViewById(R.id.updatePatientBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //환자 수정 소스
                Intent intent=new Intent(getContext(),AdminPatientUpdateActivity.class);
                intent.putExtra("patient",(Parcelable) patient);
                getContext().startActivity(intent);
            }
        });
        view.findViewById(R.id.deletePatientBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fcmPatinet=patient;
                DeletePatient deletePatient=new DeletePatient(patient.getPatientCode());
                deletePatient.execute();
            }
        });

        return view;
    }
    public  void add(List<Patient> list){
        if(list==null){
            return;
        }
        for(Patient patient:list){
            add(patient);
        }
    }
    private class DeletePatient extends SafeAsyncTask<String> {
        String patientCode="";
        public DeletePatient(String patientCode){
            this.patientCode=patientCode;
        }
        @Override
        public String call() throws Exception {

            String url="http://117.17.142.133:8080/nurse/delete-patient";
            String query="patientCode="+patientCode;

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
            String result=new GsonBuilder().create().fromJson(request.bufferedReader(),String.class);
            return null;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(String result) throws Exception {
            super.onSuccess(result);
            adminPatientsListFragment=AdminPatientsListFragment.getInstance();
            adminPatientsListFragment.realTimeUpdate();
            new FatchNurseListAsyncTask().execute();

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
            Fcm fcm=new Fcm(nurse.getnurseId(),"Patient_delete - > "+fcmPatinet.getName(),nurseListToken,nurse.getName());
            fcm.execute();
        }
    }
}

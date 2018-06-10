package skuniv.ac.kr.nursetask.UI.Admin;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import skuniv.ac.kr.nursetask.UI.Nurse.AsyncResponse;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.MemberShipActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.UploadFile;

public class AdminPatientInsesrtActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_CAMERA=1111;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP=4444;

    private EditText[] patientInsertContents;
    private String[] getPatientInsertContents;
    private String imageFileName;
    private String imgPath="";
    private String nurseListToken="";
    private ImageView imageView;
    private Uri photoUri, albumUri;
    private Patient insertPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patient_insesrt);

        imageView=(ImageView)findViewById(R.id.patientInsert_Image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPremission();
                getAlbum();
            }
        });
        patientInsertContents=new EditText[]{(EditText)findViewById(R.id.patientinsertPatientCode),(EditText)findViewById(R.id.patientinsertPatientName),(EditText)findViewById(R.id.patientinsertPatientBirth),
                (EditText)findViewById(R.id.patientinsertPatientSex),(EditText)findViewById(R.id.patientinsertPatientDisease),(EditText)findViewById(R.id.patientinsertPatientPeriod),
                (EditText)findViewById(R.id.patientinsertPatientNote),(EditText)findViewById(R.id.patientinsertPatientRoom)};

        findViewById(R.id.patientInsert_insertBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPatientInsertContents=new String[]{patientInsertContents[0].getText()+"",patientInsertContents[1].getText()+"",patientInsertContents[2].getText()+"",
                        patientInsertContents[3].getText()+"",patientInsertContents[4].getText()+"",patientInsertContents[5].getText()+"",patientInsertContents[6].getText()+"",
                        patientInsertContents[7].getText()+""};

                new InsertPatient().execute();
            }
        });
        findViewById(R.id.patientInsert_cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private class InsertPatient extends AsyncTask<Void, Void, Patient> {
        Patient answer;
        @Override
        protected Patient doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            requestBody = new FormBody.Builder().add("patientCode",getPatientInsertContents[0]).add("name",getPatientInsertContents[1])
                    .add("birth",getPatientInsertContents[2]).add("sex",getPatientInsertContents[3])
                    .add("disease",getPatientInsertContents[4]).add("period",getPatientInsertContents[5])
                    .add("note",getPatientInsertContents[6]).add("room",getPatientInsertContents[7])
                    .add("image",imageFileName).build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/insert-patient")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                Gson gson=new Gson();
                answer = gson.fromJson(response.body().string(),Patient.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        @Override
        protected void onPostExecute(Patient patient) {
            if(patient==null){
                Toast.makeText(getApplicationContext(),"환자추가에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"환자추가에 성공하셨습니다."+patient.getName()+"님",Toast.LENGTH_SHORT).show();
                insertPatient=patient;
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                new FatchNurseListAsyncTask().execute();
                finish();

            }
        }
    }

    private void checkPremission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if((ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))){
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한 거부")
                        .setNegativeButton("설정",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i =new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                i.setData(Uri.parse("package"+getPackageName()));
                                startActivity(i);
                            }
                        })
                        .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();

            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},MY_PERMISSION_CAMERA);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_CAMERA:
                for(int i=0; i<grantResults.length;i++){
                    if(grantResults[i]<0){
                        Toast.makeText(AdminPatientInsesrtActivity.this,"해당 권한을 활성화 해야합니다.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    public File createImageFile()throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPGE_"+timeStamp+".jpg";

        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/Pictures","WIT");

        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);
        imgPath = imageFile.getAbsolutePath();

        Log.d("-------storageDir","create file path : "+imgPath);
        return imageFile;
    }
    private void getAlbum(){
        Log.i("getAlbum","Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,REQUEST_TAKE_ALBUM);
    }

    public void cropImage(){
        Log.i("cropImage","Call");
        Log.i("cropImage","photoURI:"+photoUri+" /albumURI : "+albumUri);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri,"image/*");
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("output",albumUri);
        startActivityForResult(cropIntent,REQUEST_IMAGE_CROP);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_TAKE_ALBUM:
                if(resultCode== Activity.RESULT_OK){
                    if(data.getData()!=null){
                        try{
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoUri=data.getData();
                            albumUri=Uri.fromFile(albumFile);
                            cropImage();
                        }catch (Exception e){
                            Log.e("Take_ALBUM_SINGLE ERROR",e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if(resultCode==Activity.RESULT_OK){
                    // savePicture();
                    imageView.setImageURI(albumUri);
                    uploadFile(imgPath);
                    System.out.println(imgPath+"asdfasdfasdfasdfasdfasdfasdfasdfasfd");
                }
                break;
        }
    }
    public void uploadFile(String filePath){
        String url = "http://117.17.142.133:8080/nurse/photo";
        try {
            UploadFile uploadFile = new UploadFile(AdminPatientInsesrtActivity.this, new AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    Toast.makeText(getApplicationContext(),"사진 저장되었습니다.",Toast.LENGTH_SHORT).show();
                }
            });
            uploadFile.setPath(filePath);
            uploadFile.execute(url);
            uploadFile.getStatus();
        } catch (Exception e){

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
            Fcm fcm=new Fcm(getNurse().getName(),"Patient_insert - > "+insertPatient.getRoom()+"환자",nurseListToken,getNurse().getnurseId());
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

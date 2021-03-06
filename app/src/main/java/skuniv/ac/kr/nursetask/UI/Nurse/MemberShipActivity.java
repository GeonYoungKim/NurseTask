package skuniv.ac.kr.nursetask.UI.Nurse;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.UpdateNurseRoomToken;

public class MemberShipActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_CAMERA=1111;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP=4444;
    String imageFileName;
    private String imgPath="";

    Uri photoUri, albumUri;
    EditText[] membershipContents;
    ImageView imageView;
    String[] getMemberShipContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_ship);

        imageView=(ImageView)findViewById(R.id.memberShip_Image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPremission();
                getAlbum();
            }
        });

        membershipContents=new EditText[]{(EditText)findViewById(R.id.memberShip_nurseId),(EditText)findViewById(R.id.memberShip_nursePassword),(EditText)findViewById(R.id.memberShip_nurseName),
                (EditText)findViewById(R.id.memberShip_nurseBirth),(EditText)findViewById(R.id.memberShip_nursePhone),(EditText)findViewById(R.id.memberShip_Address)};


        findViewById(R.id.memberShip_memberShipbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMemberShipContents=new String[]{membershipContents[0].getText()+"",membershipContents[1].getText()+"",membershipContents[2].getText()+"",
                        membershipContents[3].getText()+"",membershipContents[4].getText()+"",membershipContents[5].getText()+""};

                new InsertNurse().execute();

            }
        });
    }

    public class InsertNurse extends AsyncTask<Void, Void, Nurse> {
        Nurse answer;

        @Override
        protected Nurse doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            requestBody = new FormBody.Builder().add("id",getMemberShipContents[0]).add("password",getMemberShipContents[1])
                    .add("name",getMemberShipContents[2]).add("birth",getMemberShipContents[3])
                    .add("phone",getMemberShipContents[4]).add("address",getMemberShipContents[5])
                    .add("image",imageFileName)
                    .build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/insert-nurse")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                Gson gson=new Gson();
                answer = gson.fromJson(response.body().string(),Nurse.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        protected void onPostExecute(Nurse nurse) {
            if(nurse==null){
                Toast.makeText(getApplicationContext(),"회원가입에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"회원가입에 성공하셨습니다."+nurse.getnurseId()+"님",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MemberShipActivity.this,"해당 권한을 활성화 해야합니다.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    public File createImageFile()throws IOException{
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
            UploadFile uploadFile = new UploadFile(MemberShipActivity.this, new AsyncResponse() {
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

}

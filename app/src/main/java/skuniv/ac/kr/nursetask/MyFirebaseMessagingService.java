package skuniv.ac.kr.nursetask;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.UI.Admin.AdminChatRoomListFragment;

import skuniv.ac.kr.nursetask.UI.Admin.AdminNursesListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.AlarmNotificationReceiver;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Nurse.MainActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.NurseListFragment;


/**
 * Created by gunyoungkim on 2017-11-22.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG="MyFirebaseMsgService";
    Intent alarmintent;
    PendingIntent pendingIntent;
    ChatActivity chatActivity;
    NurseListFragment nurseListFragment;
    AdminNursesListFragment adminNursesListFragment;
    AdminChatRoomListFragment adminChatRoomListFragment;
    ChatRoomListFragment chatRoomListFragment;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.d(TAG,"Message body:"+remoteMessage.getNotification().getBody());
        Log.d(TAG,"Message body:"+remoteMessage.getNotification().getBody());

        ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
        Log.d(TAG,"From:"+remoteMessage.getMessageId());
        Log.d(TAG,"Who:"+remoteMessage.getNotification().getClickAction());
        if(remoteMessage.getData().size()>0){
            Log.d(TAG,"Message data"+remoteMessage.getData());
        }
        if(remoteMessage.getNotification()!=null){
            Log.d(TAG,"Message body:"+remoteMessage.getNotification().getBody());
            chatActivity= ChatActivity.getInstance();
            adminChatRoomListFragment=AdminChatRoomListFragment.getInstance();
            chatRoomListFragment=ChatRoomListFragment.getInstance();

            String msg=remoteMessage.getNotification().getBody();
            try {
                msg=URLDecoder.decode(msg,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try{
                adminChatRoomListFragment.realTimeUpdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }try{
                chatRoomListFragment.realTimeUpdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            try{
                chatActivity.new FatchAdminChatListAsyncTask().execute();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            if(msg.equals("confirm_schedule")){
                sendNotification(remoteMessage.getNotification().getTitle(),msg);
                String[] schedules=remoteMessage.getNotification().getTitle().split("-");
                Log.d("today_schedule",schedules[1]);
                GetScheduleNurse getScheduleNurse=new GetScheduleNurse(schedules[1]);
                getScheduleNurse.execute();
            }else if(remoteMessage.getNotification().getBody().equals("updated_nurse")){
                if(remoteMessage.getNotification().getClickAction().equals(getNurse().getnurseId())){
                    System.out.println("내가 로그인한 것");
                }else{
                    sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
                }
                adminNursesListFragment=AdminNursesListFragment.getInstance();
                nurseListFragment=NurseListFragment.getInstance();
                nurseListFragment.realTimeUpdate();
                adminNursesListFragment.realTimeUpdate();

            }else if("incharge_patient_update".equals(msg)) {
                sendNotification(remoteMessage.getNotification().getTitle(),"incharge_patient_update");
            }else if("confirm_long_schedule".equals(msg)){
                sendNotification(remoteMessage.getNotification().getTitle(),msg);
            }else{
                    if(remoteMessage.getNotification().getClickAction().equals(getNurse().getnurseId())){
                        Log.d("my message","내가 보낸 메세지");
                    }else {
                        Log.d("emulator","emulator");
                        if(".UI.Nurse.ChatActivity".equals(taskInfo.get(0).topActivity.getShortClassName())){
                            Log.d("chatActivity","현재 채팅창임.");
                        }else{
                            String sp[]=msg.split("-");
                            sendNotification(remoteMessage.getNotification().getTitle(),sp[1]);
                            getRoomFlag getRoomFlag=new getRoomFlag(sp[0],getNurse().getnurseId());
                            getRoomFlag.execute();
                        }

                    }

                }
            }
        }

    private Nurse getNurse(){
        Gson gson=new Gson();
        SharedPreferences mSharedPreferences=getSharedPreferences("Text_number_store",MODE_PRIVATE);
        String json=mSharedPreferences.getString("nurse","");
        Nurse nurse =gson.fromJson(json,Nurse.class);
        return nurse;
    }
    private void sendNotification(String sender,String body) {


        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notibuilder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(sender)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notibuilder.build());
    }
    private class GetScheduleNurse extends SafeAsyncTask<Nurse> {
        private long triggerTime=0;
        private String id;
        public GetScheduleNurse(String id){
            this.id=id;
        }
        @Override
        public Nurse call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/today-schedule-show";
            String query="id="+id;
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
            JSONResultFatchNurse result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchNurse.class);
            Nurse nurse=result.getData();
            return nurse;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(Nurse nurse) throws Exception {
            super.onSuccess(nurse);
            String[] alarm_schedule_item=nurse.getTodaySchedule().split(",");
            Arrays.sort(alarm_schedule_item, String.CASE_INSENSITIVE_ORDER);

            AlarmNotificationReceiver.getInstance().setI(0);
            AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmintent=new Intent(getApplicationContext(),AlarmNotificationReceiver.class);

            for(int i=0;i<alarm_schedule_item.length;i++){
                String[] time_and_content=alarm_schedule_item[i].split("-");

                AlarmNotificationReceiver.getInstance().getContent().add(time_and_content[1]);

                String[] hour_and_minute=time_and_content[0].split(":");

                pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),i,alarmintent,0);
                triggerTime = setTriggerTime(Integer.parseInt(hour_and_minute[0]),Integer.parseInt(hour_and_minute[1]));
                manager.set(AlarmManager.RTC_WAKEUP, triggerTime,pendingIntent);
            }
        }
    }
    private class JSONResultFatchNurse extends JsonResult<Nurse> {}
    private long setTriggerTime(int hour, int minute)
    {
        // timepicker
        Calendar curTime = Calendar.getInstance();
        curTime.set(Calendar.HOUR_OF_DAY,hour);
        curTime.set(Calendar.MINUTE,minute);
        curTime.set(Calendar.SECOND, 0);
        long btime = curTime.getTimeInMillis();
        long triggerTime = btime;
        return triggerTime;
    }

   private class getRoomFlag extends SafeAsyncTask<String> {
        String roomNo;
       String nurseId;
       public getRoomFlag(String roomNo,String nurseId){
           this.roomNo=roomNo;
           this.nurseId=nurseId;
       }
        @Override
        public String call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/get-room-flag";
            String query="roomNo="+roomNo+"&nurseId="+nurseId;
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
            return null;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(String room) throws Exception {
            super.onSuccess(room);
            adminChatRoomListFragment=AdminChatRoomListFragment.getInstance();
            adminChatRoomListFragment.realTimeUpdate();
            chatRoomListFragment=ChatRoomListFragment.getInstance();
            chatRoomListFragment.realTimeUpdate();

        }
    }
}

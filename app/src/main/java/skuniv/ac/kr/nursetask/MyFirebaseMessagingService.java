package skuniv.ac.kr.nursetask;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminChatRoomListFragment;

import skuniv.ac.kr.nursetask.UI.Admin.AdminNursesListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.AdminRoomsListArrayAdapter;
import skuniv.ac.kr.nursetask.UI.Admin.AlarmNotificationReceiver;
import skuniv.ac.kr.nursetask.UI.Admin.GetSet;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Nurse.InviteActivity;
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

        Log.d(TAG,"From:"+remoteMessage.getMessageId());
        Log.d(TAG,"Who:"+remoteMessage.getNotification().getClickAction());
        if(remoteMessage.getData().size()>0){
            Log.d(TAG,"Message data"+remoteMessage.getData());
        }
        if(remoteMessage.getNotification()!=null){
            Log.d(TAG,"Message body:"+remoteMessage.getNotification().getBody());
            chatActivity= GetSet.getChatActivity();
            adminChatRoomListFragment=GetSet.getAdminChatRoomListFragment();
            chatRoomListFragment=GetSet.getChatRoomListFragment();
            try{
                adminChatRoomListFragment.realTimeupdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }try{
                chatRoomListFragment.realTimeupdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            try{
                chatActivity.new FatchAdminChatListAsyncTask().execute();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            if(remoteMessage.getNotification().getBody().equals("confirm_schedule")){
                sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
                String[] schedules=remoteMessage.getNotification().getTitle().split("-");
                GetScheduleNurse getScheduleNurse=new GetScheduleNurse(schedules[1]);
                getScheduleNurse.execute();
            }else if(remoteMessage.getNotification().getBody().equals("updated_nurse")){
                if(remoteMessage.getNotification().getClickAction().equals(getNurse().getNurseid())){
                    System.out.println("내가 로그인한 것");
                }else{
                    sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
                }
                adminNursesListFragment=GetSet.getAdminNursesListFragment();
                nurseListFragment=GetSet.getNurseListFragment();
                try{
                    nurseListFragment.realTimeupdate();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                try{
                    adminNursesListFragment.realTimeupdate();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }else {
                if(remoteMessage.getNotification().getClickAction().equals(getNurse().getNurseid())){

                }else {
                    String sp[]=remoteMessage.getNotification().getBody().split("-");
                    sendNotification(remoteMessage.getNotification().getTitle(),sp[1]);
                    getRoomFlag getRoomFlag=new getRoomFlag(sp[0],getNurse().getNurseid());
                    getRoomFlag.execute();
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
            String url="http://117.17.142.135:8080/controller/Nurse?a=get_schedule_nurse";
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
            String[] alarm_schedule_item=nurse.getTodayschedule().split(",");
            Arrays.sort(alarm_schedule_item, String.CASE_INSENSITIVE_ORDER);

            AlarmNotificationReceiver.i=0;
            AlarmNotificationReceiver.content=new ArrayList<String>();
            AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmintent=new Intent(getApplicationContext(),AlarmNotificationReceiver.class);

            for(int i=0;i<alarm_schedule_item.length;i++){
                String[] time_and_content=alarm_schedule_item[i].split("-");

                AlarmNotificationReceiver.content.add(time_and_content[1]);

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
        String roomno;
       String nurseid;
       public getRoomFlag(String roomno,String nurseid){
           this.roomno=roomno;
           this.nurseid=nurseid;
       }
        @Override
        public String call() throws Exception {
            String url="http://117.17.142.135:8080/controller/Nurse?a=getRoomFlag";
            String query="roomno="+roomno+"&nurseid="+nurseid;
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
            try{
                adminChatRoomListFragment.realTimeupdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }try{
                chatRoomListFragment.realTimeupdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }

    }

}

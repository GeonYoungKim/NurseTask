package skuniv.ac.kr.nursetask.UI.Admin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.List;

import skuniv.ac.kr.nursetask.MyFirebaseMessagingService;
import skuniv.ac.kr.nursetask.R;

/**
 * Created by gunyoungkim on 2017-10-16.
 */

public class AlarmNotificationReceiver extends BroadcastReceiver {

    static public List<String> content;
    static public int i=0;
    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고


        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MyFirebaseMessagingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("스케쥴 알림").setContentText(content.get(i))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());
        i++;
    }
}

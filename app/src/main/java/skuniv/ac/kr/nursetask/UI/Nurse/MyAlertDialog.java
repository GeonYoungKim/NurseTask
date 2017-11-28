package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * Created by gunyoungkim on 2017-08-31.
 */

public class MyAlertDialog extends RoomActivity{
    public MyAlertDialog(Activity activity,int num){
        System.out.println(this);
        new android.app.AlertDialog.Builder( activity).
                setTitle( "환자 사항" ).
                setIcon( android.R.drawable.ic_dialog_alert ).
                setMessage( "이름 : "+seats.get(num).getName()+"\n"+
                        "생년월일 : "+seats.get(num).getBirth()+"\n"+
                        "성별 : "+seats.get(num).getSex()+"\n"+
                        "병 명 : "+seats.get(num).getDisease()+"\n"+
                        "입원 기간 : "+seats.get(num).getPeriod()+"\n"+
                        "특이 사항 : "+seats.get(num).getNote()+"\n"
                ).
                setPositiveButton( "예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println( "------> 예 clicked" );
                    }
                } ).show();
    }
}

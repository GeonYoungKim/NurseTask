package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
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

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.Core.provider.NurseProvider;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.GetSet;

/**
 * Created by gunyoungkim on 2017-10-09.
 */

public class InviteActivity extends ListActivity {
    public ChatRoomListFragment chatRoomListFragment;
    public AdminChatRoomListFragment adminChatRoomListFragment;
    Map<Nurse,CheckBox> nursesCheckMap;
    String checkedNurse;
    String checkedNurseId;
    int roomno;
    List<Nurse> nurses;
    ListView lv;
    String roomname;
    String[] roomnames;
    boolean exist;
    int count;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_in_charge_patient_select);

        nursesCheckMap=new HashMap<Nurse,CheckBox>();
        checkedNurse="";
        checkedNurseId="";
        intent=getIntent();
        roomno=(int)intent.getExtras().get("roomno");
        System.out.println(roomno);
        new getRoom().execute();

        lv= (ListView) findViewById(android.R.id.list);

        findViewById(R.id.OKInChargePatientBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=0;
                for(Nurse key:nursesCheckMap.keySet()){
                    if(nursesCheckMap.get(key).isChecked()==true){
                        count++;
                        if(checkedNurse.equals("")){
                            checkedNurseId+=key.getNurseid();
                            checkedNurse+=key.getName()+"님";
                        }else{
                            checkedNurseId+=","+key.getNurseid();
                            checkedNurse+=","+key.getName()+"님";
                        }
                    }
                }
                new UpdateRoom().execute();
            }
        });
    }
    private class UpdateRoom extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {
            String url="http://117.17.142.135:8080/nurse/updateRoom";
            String query="roomno="+roomno+"&roomname="+checkedNurse+"&count="+count+"&strNurseId="+checkedNurseId;
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
        protected void onSuccess(String str) throws Exception {
            super.onSuccess(str);
            chatRoomListFragment= GetSet.getChatRoomListFragment();
            adminChatRoomListFragment=GetSet.getAdminChatRoomListFragment();
            if(chatRoomListFragment!=null){
                chatRoomListFragment.realTimeupdate();
            }
            if(adminChatRoomListFragment!=null){
                adminChatRoomListFragment.realTimeupdate();
            }
            ChatActivity.rooms_id=checkedNurseId;
            checkedNurseId=checkedNurseId.replaceAll(",","-");
            intent.putExtra("rooms_id",checkedNurseId);
            setResult(Activity.RESULT_OK,intent);

            System.out.println("----------------------"+ChatActivity.rooms_id);
            finish();
        }
    }
    private class getRoom extends SafeAsyncTask<Room> {
        @Override
        public Room call() throws Exception {

            String url="http://117.17.142.135:8080/nurse/getRoom2";
            String query="roomno="+roomno;
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
            JSONResultFatchRoom result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchRoom.class);
            Room room=result.getData();
            return room;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(Room room) throws Exception {
            super.onSuccess(room);
            roomname=room.getRoomname();
            roomname=roomname.replaceAll("님","");
            roomnames=roomname.split(",");
            for(int i=0;i<roomnames.length;i++){
                System.out.println(roomnames[i]);
            }
            new FatchInviteListAsyncTask().execute();
        }
        private class JSONResultFatchRoom extends JsonResult<Room> {}
    }

    class CustomAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return nurses.size();
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
            nursesCheckMap.put(nurses.get(position),((CheckBox)convertView.findViewById(R.id.InchargeSelectedCheckBox)));
            ((TextView)convertView.findViewById(R.id.inchargePatientName)).setText(nurses.get(position).getName());
            exist=true;
            for(int i=0;i<roomnames.length;i++){
                if(nurses.get(position).getName().equals(roomnames[i])){
                    exist=false;
                    break;
                }
            }
            if(exist==true){
                ((CheckBox)convertView.findViewById(R.id.InchargeSelectedCheckBox)).setChecked(false);
            }else{
                ((CheckBox)convertView.findViewById(R.id.InchargeSelectedCheckBox)).setChecked(true);
            }
            return convertView;
        }
    }
    public class FatchInviteListAsyncTask extends SafeAsyncTask<List<Nurse>> {

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
            System.out.println(Nurses);
            nurses=Nurses;
            CustomAdapter customAdapter = new CustomAdapter();
            lv.setAdapter(customAdapter);
        }
    }
    private class JSONResultFatchInviteList extends JsonResult<List<Nurse>> {}
}

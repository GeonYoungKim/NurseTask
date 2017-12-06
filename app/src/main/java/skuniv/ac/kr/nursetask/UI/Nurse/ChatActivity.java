package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Chat;
import skuniv.ac.kr.nursetask.Core.domain.NurseRoom;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.ChatProvider;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.AdminNursesListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.GetSet;

public class ChatActivity extends ListActivity {
    String mRecvData="";
    BufferedReader mReader;
    BufferedWriter mWriter;
    Button submitChat;
    Button inviteBtn;
    ListView lv;
    EditText charEditText;
    List<Chat> chats;
    int roomno;
    CustomAdapter customAdapter;
    static String my_nurseid;
    static String my_nursename;

    String content;
    String realContent;
    static String rooms_id;
    AdminChatRoomListFragment adminChatRoomListFragment;
    ChatRoomListFragment chatRoomListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rooms_id="";

        GetSet.setChatActivity(this);

        lv= (ListView) findViewById(android.R.id.list);

        Intent intent=getIntent();
        roomno=(int)intent.getExtras().get("roomno");
        submitChat=(Button)findViewById(R.id.submitChat);
        charEditText=(EditText)findViewById(R.id.charEditText);
        new FatchAdminChatListAsyncTask().execute();


        inviteBtn=(Button)findViewById(R.id.inviteBtn);
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,InviteActivity.class);
                intent.putExtra("roomno",roomno);
                startActivityForResult(intent,1);
            }
        });
        submitChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getNurseRoom().execute();
                content=charEditText.getText().toString();
                realContent=charEditText.getText().toString();
                content=rooms_id+"-"+content+"-"+my_nurseid;
                charEditText.setText("");
                new InsertChat().execute();
            }
        });
        getRoomFlag getRoomFlag=new getRoomFlag(roomno+"",my_nurseid);
        getRoomFlag.execute();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("rooms_id");
                this.rooms_id=result;
            }
        }
    }
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return chats.size();
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

            if(my_nurseid.equals(chats.get(position).getNurseid2())){
                convertView=getLayoutInflater().inflate(R.layout.row_chat_list,null);
                ((TextView)convertView.findViewById(R.id.chatText)).setText(chats.get(position).getChatcontent()+"");
            }else{
                convertView=getLayoutInflater().inflate(R.layout.row__chat_left_list,null);
                ((TextView)convertView.findViewById(R.id.chatText)).setText(chats.get(position).getChatcontent()+"");
                ((TextView)convertView.findViewById(R.id.chatName)).setText(chats.get(position).getNurseid2()+"");
            }
            return convertView;
        }
    }
    public class FatchAdminChatListAsyncTask extends SafeAsyncTask<List<Chat>> {

        @Override
        public List<Chat> call() throws Exception {
            ChatProvider chatProvider=new ChatProvider(roomno);
            List<Chat> Chats=chatProvider.FatchChatList();
            return Chats;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","error"+e);
        }
        @Override
        protected void onSuccess(List<Chat> Chats) throws Exception {
            super.onSuccess(Chats);
            System.out.println(Chats);
            chats=Chats;
            customAdapter = new CustomAdapter();
            lv.setAdapter(customAdapter);
            lv.post(new Runnable(){ public void run() { lv.setSelection(lv.getCount() - 1); }});
        }
    }

    private class InsertChat extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {
            String url="http://117.17.142.135:8080/controller/Nurse?a=insertChat";
            String query="roomno="+roomno+"&nurseid2="+my_nurseid+"&chatcontent="+realContent;
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
            new FatchAdminChatListAsyncTask().execute();


            Fcm fcm=new Fcm(my_nursename,(roomno+"-"+realContent),rooms_id,my_nurseid);
            fcm.start();
            rooms_id="";
        }
    }
    private class getNurseRoom extends SafeAsyncTask<List<NurseRoom>> {
        @Override
        public List<NurseRoom> call() throws Exception {
            String url="http://117.17.142.135:8080/controller/Nurse?a=getNurseRoom";
            String query="roomno="+roomno;
            HttpRequest request=HttpRequest.post(url);
            request.accept( HttpRequest.CONTENT_TYPE_JSON );
            request.connectTimeout( 1000 );
            request.readTimeout( 3000 );
            request.send(query);
            int responseCode = request.code();
            if ( responseCode != HttpURLConnection.HTTP_OK  ) {
                    /* 에러 처리 */
                System.out.println("---------------------ERRORRoom");
                return null;
            }
            JSONResultFatchListNurseRoom result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchListNurseRoom.class);
            List<NurseRoom> nurseRooms=  result.getData();
            return nurseRooms;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(List<NurseRoom> nurseRooms) throws Exception {
            super.onSuccess(nurseRooms);

            for(NurseRoom nurseRoom:nurseRooms){
                if(rooms_id.equals("")){
                    rooms_id+=nurseRoom.getToken();
                }else{
                    rooms_id+=","+nurseRoom.getToken();
                }
            }
        }
    }
    private class JSONResultFatchListNurseRoom extends JsonResult<List<NurseRoom>> {}

    private class getRoomFlag extends SafeAsyncTask<String> {
        String roomno;
        String nurseid;
        public getRoomFlag(String roomno,String nurseid){
            this.roomno=roomno;
            this.nurseid=nurseid;
        }
        @Override
        public String call() throws Exception {
            String url="http://117.17.142.135:8080/controller/Nurse?a=getRoomFlag2";
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
        }

    }

}

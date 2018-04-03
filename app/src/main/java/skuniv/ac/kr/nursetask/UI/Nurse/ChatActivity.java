package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.ChatVo;
import skuniv.ac.kr.nursetask.Core.domain.NurseRoomVo;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.ChatProvider;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.GetSet;

public class ChatActivity extends ListActivity {
    String mRecvData="";
    BufferedReader mReader;
    BufferedWriter mWriter;
    Button submitChat;
    Button inviteBtn;
    ListView lv;
    EditText charEditText;
    List<ChatVo> chatVos;
    int roomNo;
    CustomAdapter customAdapter;
    static String myNurseId;
    static String myNurseName;

    String content;
    String realContent;
    static String roomsId;
    AdminChatRoomListFragment adminChatRoomListFragment;
    ChatRoomListFragment chatRoomListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        roomsId="";

        GetSet.setChatActivity(this);

        lv= (ListView) findViewById(android.R.id.list);

        Intent intent=getIntent();
        roomNo=(int)intent.getExtras().get("roomNo");
        submitChat=(Button)findViewById(R.id.submitChat);
        charEditText=(EditText)findViewById(R.id.charEditText);
        new FatchAdminChatListAsyncTask().execute();


        inviteBtn=(Button)findViewById(R.id.inviteBtn);
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,InviteActivity.class);
                intent.putExtra("roomNo",roomNo);
                startActivityForResult(intent,1);
            }
        });
        submitChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetNurseRoom().execute();
                content=charEditText.getText().toString();
                realContent=charEditText.getText().toString();
                content=roomsId+"-"+content+"-"+myNurseId;
                charEditText.setText("");
                new InsertChat().execute();
            }
        });
        getRoomFlag getRoomFlag=new getRoomFlag(roomNo+"",myNurseId);
        getRoomFlag.execute();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("roomsId");
                this.roomsId=result;
            }
        }
    }
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return chatVos.size();
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

            if(myNurseId.equals(chatVos.get(position).getnurseId2())){
                convertView=getLayoutInflater().inflate(R.layout.row_chat_list,null);
                ((TextView)convertView.findViewById(R.id.chatText)).setText(chatVos.get(position).getChatContent()+"");
            }else{
                convertView=getLayoutInflater().inflate(R.layout.row__chat_left_list,null);
                ((TextView)convertView.findViewById(R.id.chatText)).setText(chatVos.get(position).getChatContent()+"");
                ((TextView)convertView.findViewById(R.id.chatName)).setText(chatVos.get(position).getnurseId2()+"");
            }
            return convertView;
        }
    }
    public class FatchAdminChatListAsyncTask extends SafeAsyncTask<List<ChatVo>> {

        @Override
        public List<ChatVo> call() throws Exception {
            ChatProvider chatProvider=new ChatProvider(roomNo);
            List<ChatVo> chatVos =chatProvider.FatchChatList();
            return chatVos;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","error"+e);
        }
        @Override
        protected void onSuccess(List<ChatVo> chatVos) throws Exception {
            super.onSuccess(chatVos);
            System.out.println(chatVos);
            ChatActivity.this.chatVos = chatVos;
            customAdapter = new CustomAdapter();
            lv.setAdapter(customAdapter);
            lv.post(new Runnable(){ public void run() { lv.setSelection(lv.getCount() - 1); }});
        }
    }

    private class InsertChat extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/insert-chat";
            String query="roomNo="+roomNo+"&nurseId2="+myNurseId+"&chatContent="+realContent;
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


            Fcm fcm=new Fcm(myNurseName,(roomNo+"-"+realContent),roomsId,myNurseId);
            fcm.execute();
            roomsId="";
        }
    }
    private class GetNurseRoom extends SafeAsyncTask<List<NurseRoomVo>> {
        @Override
        public List<NurseRoomVo> call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/get-nurse-room";
            String query="roomNo="+roomNo;
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
            List<NurseRoomVo> nurseRoomVos =  result.getData();
            return nurseRoomVos;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(List<NurseRoomVo> nurseRoomVos) throws Exception {
            super.onSuccess(nurseRoomVos);

            for(NurseRoomVo nurseRoomVo : nurseRoomVos){
                if(roomsId.equals("")){
                    roomsId+= nurseRoomVo.getToken();
                }else{
                    roomsId+=","+ nurseRoomVo.getToken();
                }
            }
        }
    }
    private class JSONResultFatchListNurseRoom extends JsonResult<List<NurseRoomVo>> {}

    private class getRoomFlag extends SafeAsyncTask<String> {
        String roomNo;
        String nurseId;
        public getRoomFlag(String roomNo,String nurseId){
            this.roomNo=roomNo;
            this.nurseId=nurseId;
        }
        @Override
        public String call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/get-room-flag2";
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
            adminChatRoomListFragment=GetSet.getAdminChatRoomListFragment();
            chatRoomListFragment=GetSet.getChatRoomListFragment();
            try{
                adminChatRoomListFragment.realTimeUpdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }try{
                chatRoomListFragment.realTimeUpdate();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

    }

}

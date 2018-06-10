package skuniv.ac.kr.nursetask.UI.Nurse;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.ChatVo;
import skuniv.ac.kr.nursetask.Core.domain.NurseRoomVo;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.Fcm;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.ChatProvider;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Admin.UpdateNurseRoomToken;

public class ChatActivity extends ListActivity {
    private Button submitChat,inviteBtn;
    private ListView lv;
    private EditText charEditText;
    private List<ChatVo> chatVos;
    private int roomNo;
    private CustomAdapter customAdapter;
    private String myNurseId,myNurseName;

    private static ChatActivity chatActivity = null;
    public static synchronized ChatActivity getInstance() {
        if (chatActivity == null) {
            chatActivity = new ChatActivity();
        }
        return chatActivity;
    }

    public String getMyNurseId() {
        return myNurseId;
    }

    public void setMyNurseId(String myNurseId) {
        this.myNurseId = myNurseId;
    }

    public String getMyNurseName() {
        return myNurseName;
    }

    public void setMyNurseName(String myNurseName) {
        this.myNurseName = myNurseName;
    }

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

        getInstance();

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


    public class InsertChat extends AsyncTask<Void, Void, String> {
        String answer;

        @Override
        protected String doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            requestBody = new FormBody.Builder().add("roomNo",String.valueOf(roomNo)).add("nurseId2",myNurseId).add("chatContent",realContent)
                    .build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/insert-chat")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                answer = response.body().toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        protected void onPostExecute(Patient patient) {
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




    public class getRoomFlag extends AsyncTask<Void, Void, String> {
        String answer;
        String roomNo;
        String nurseId;
        public getRoomFlag(String roomNo,String nurseId){
            this.roomNo=roomNo;
            this.nurseId=nurseId;
        }

        @Override
        protected String doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;
            requestBody = new FormBody.Builder().add("roomNo",String.valueOf(roomNo)).add("nurseId",nurseId)
                    .build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/get-room-flag2")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                answer = response.body().toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        protected void onPostExecute(Patient patient) {
            adminChatRoomListFragment=AdminChatRoomListFragment.getInstance();
            chatRoomListFragment=ChatRoomListFragment.getInstance();
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

package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.back;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class AdminListArrayAdapter extends ArrayAdapter<Nurse> {


    public AdminChatRoomListFragment adminChatRoomListFragment;
    private LayoutInflater layoutInflater;
    public static Nurse ownnurse;

    private String data1;
    private String data2;
    private String roomname;
    private int count;
    String imageUrl = "http://117.17.142.135:8080/img/";
    Bitmap bmImg;
    public AdminListArrayAdapter(@NonNull Context context) {
        super(context, R.layout.row_admin_nurse_list);
        layoutInflater=LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_admin_nurse_list, parent, false);
        }
        final Nurse nurse=getItem(position);
        ((TextView)view.findViewById(R.id.nurseName)).setText(nurse.getName());
        ImageView bmImage = (ImageView) view.findViewById(R.id.imagebtn);
        new back(bmImage,bmImg).execute(imageUrl + nurse.getImage() + "");

        view.findViewById(R.id.chatbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //대화하기
                data1=ownnurse.getNurseid();
                data2=nurse.getNurseid();
                roomname=ownnurse.getName()+"님,"+nurse.getName()+"님";
                count=2;
                new InsertRoom().execute();

            }
        });
        view.findViewById(R.id.responbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //담당 환자 선택해서 정해줌
                Intent intent=new Intent(getContext(),AdminInChargePatientSelectActivity.class);
                intent.putExtra("NurseId",nurse.getNurseid());
                getContext().startActivity(intent);

            }
        });
        view.findViewById(R.id.schedulebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //스케쥴 정해주기
                Intent intent=new Intent(getContext(),AdminScheduleChoiceActivity.class);
                intent.putExtra("Nurse", (Parcelable) nurse);
                getContext().startActivity(intent);
            }
        });
        return view;
    }
    public  void add(List<Nurse> list){
        if(list==null){
            return;
        }
        for(Nurse nurse:list){
            add(nurse);
        }
    }

    private class InsertRoom extends SafeAsyncTask<Room> {
        @Override
        public Room call() throws Exception {

            String url="http://117.17.142.135:8080/controller/Nurse?a=inserChatRoom";
            String query="data1="+data1+"&data2="+data2+"&roomname="+roomname+"&count="+count;

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
            JSONResultFatchInsertChatRoom result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchInsertChatRoom.class);
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
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+room.getRoomno());
            Intent intent=new Intent(getContext(),ChatActivity.class);
            intent.putExtra("roomno",room.getRoomno());
            getContext().startActivity(intent);
            adminChatRoomListFragment= GetSet.getAdminChatRoomListFragment();
            adminChatRoomListFragment.realTimeupdate();
        }
    }
    private class JSONResultFatchInsertChatRoom extends JsonResult<Room> {}

}
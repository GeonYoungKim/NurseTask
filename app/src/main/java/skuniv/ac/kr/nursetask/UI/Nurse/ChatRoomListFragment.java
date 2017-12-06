package skuniv.ac.kr.nursetask.UI.Nurse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.Core.provider.RoomProvider;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminRoomsListArrayAdapter;
import skuniv.ac.kr.nursetask.UI.Admin.GetSet;

/**
 * Created by gunyoungkim on 2017-09-04.
 */

public class ChatRoomListFragment extends ListFragment {
    String receivemessage;
    public AdminRoomsListArrayAdapter adminRoomsListArrayAdapter;
    Map<Integer,Room> roommap;
    int roomno;
    public List<Room> Rooms;

    public AdminRoomsListArrayAdapter getAdminRoomsListArrayAdapter() {
        return adminRoomsListArrayAdapter;
    }

    public void setAdminRoomsListArrayAdapter(AdminRoomsListArrayAdapter adminRoomsListArrayAdapter) {
        this.adminRoomsListArrayAdapter = adminRoomsListArrayAdapter;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("create adapter","adapter");
        adminRoomsListArrayAdapter=new AdminRoomsListArrayAdapter(getActivity());


    }

    @Nullable


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        GetSet.setChatRoomListFragment(this);
        return inflater.inflate(R.layout.fragment_chatroom_list,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv=((ListView)getView().findViewById(android.R.id.list));

        roommap=new HashMap<Integer,Room>();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomno=Rooms.get(position).getRoomno();
                Intent intent=new Intent(getContext(),ChatActivity.class);
                intent.putExtra("roomno",roomno);
                getContext().startActivity(intent);
            }
        });
        new FatchAdminRoomListAsyncTask().execute();
    }
    public void realTimeupdate(){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.detach(ChatRoomListFragment.this).attach(ChatRoomListFragment.this).commitAllowingStateLoss();
    }
    public class FatchAdminRoomListAsyncTask extends SafeAsyncTask<List<Room>> {

        @Override
        public List<Room> call() throws Exception {
            RoomProvider roomProvider=new RoomProvider(ListArrayAdapter.ownnurse.getNurseid());
            Rooms=roomProvider.FatchRoomList();
            return Rooms;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","arror"+e);
        }
        @Override
        protected void onSuccess(List<Room> Rooms) throws Exception {
            super.onSuccess(Rooms);

            for(Room room:Rooms){
                roommap.put(room.getRoomno(),room);

                System.out.println(room);
            }
            System.out.println(Rooms);
            adminRoomsListArrayAdapter.add(Rooms); // 무조건 처음에 한번만
            setListAdapter(adminRoomsListArrayAdapter);
            getView().findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

}

package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.Core.provider.RoomProvider;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatRoomListFragment;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class AdminChatRoomListFragment extends ListFragment {
    private AdminRoomsListArrayAdapter adminRoomsListArrayAdapter;
    private Map<Integer, Room> roomMap;
    private int roomNo;
    private List<Room> Rooms;
    private AdminListArrayAdapter adminListArrayAdapter;
    private static AdminChatRoomListFragment adminChatRoomListFragment = null;

    public static synchronized AdminChatRoomListFragment getInstance() {
        if (adminChatRoomListFragment == null) {
            adminChatRoomListFragment = new AdminChatRoomListFragment();
        }
        return adminChatRoomListFragment;
    }

    public AdminRoomsListArrayAdapter getAdminRoomsListArrayAdapter() {
        return adminRoomsListArrayAdapter;
    }

    public void setAdminRoomsListArrayAdapter(AdminRoomsListArrayAdapter adminRoomsListArrayAdapter) {
        this.adminRoomsListArrayAdapter = adminRoomsListArrayAdapter;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("create adapter", "adapter");
        adminListArrayAdapter=new AdminListArrayAdapter(getActivity());
        adminRoomsListArrayAdapter = new AdminRoomsListArrayAdapter(getActivity());
    }


    @Nullable


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getInstance();
        return inflater.inflate(R.layout.fragment_chatroom_list, container, false);
    }

    public void realTimeUpdate() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(AdminChatRoomListFragment.this).attach(AdminChatRoomListFragment.this).commitAllowingStateLoss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = ((ListView) getView().findViewById(android.R.id.list));
        roomMap = new HashMap<Integer, Room>();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomNo = Rooms.get(position).getRoomNo();
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("roomNo", roomNo);
                getContext().startActivity(intent);
            }
        });
        new FatchAdminRoomListAsyncTask().execute();

    }

    public class FatchAdminRoomListAsyncTask extends SafeAsyncTask<List<Room>> {
        @Override
        public List<Room> call() throws Exception {
            RoomProvider roomProvider = new RoomProvider(adminListArrayAdapter.getOwnNurse().getnurseId());
            Rooms = roomProvider.FatchRoomList();
            return Rooms;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask", "arror" + e);
        }

        @Override
        protected void onSuccess(List<Room> Rooms) throws Exception {
            super.onSuccess(Rooms);
            for (Room room : Rooms) {
                roomMap.put(room.getRoomNo(), room);
                System.out.println(room);
            }
            adminRoomsListArrayAdapter.add(Rooms);
            setListAdapter(adminRoomsListArrayAdapter);
            getView().findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
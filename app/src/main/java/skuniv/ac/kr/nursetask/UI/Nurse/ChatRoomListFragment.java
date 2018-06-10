package skuniv.ac.kr.nursetask.UI.Nurse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.RoomProvider;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.AdminRoomsListArrayAdapter;

/**
 * Created by gunyoungkim on 2017-09-04.
 */

public class ChatRoomListFragment extends ListFragment {
    private AdminRoomsListArrayAdapter adminRoomsListArrayAdapter;
    private Map<Integer,Room> roomMap;
    private int roomNo;
    private List<Room> Rooms;
    private ListArrayAdapter listArrayAdapter;

    private static ChatRoomListFragment chatRoomListFragment = null;
    public static synchronized ChatRoomListFragment getInstance() {
        if (chatRoomListFragment == null) {
            chatRoomListFragment = new ChatRoomListFragment();
        }
        return chatRoomListFragment;
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
        Log.d("create adapter","adapter");
        listArrayAdapter=new ListArrayAdapter(getActivity());
        adminRoomsListArrayAdapter=new AdminRoomsListArrayAdapter(getActivity());


    }

    @Nullable


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getInstance();
        return inflater.inflate(R.layout.fragment_chatroom_list,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv=((ListView)getView().findViewById(android.R.id.list));

        roomMap=new HashMap<Integer,Room>();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomNo=Rooms.get(position).getRoomNo();
                Intent intent=new Intent(getContext(),ChatActivity.class);
                intent.putExtra("roomNo",roomNo);
                getContext().startActivity(intent);
            }
        });
        new FatchAdminRoomListAsyncTask().execute();
    }
    public void realTimeUpdate(){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.detach(ChatRoomListFragment.this).attach(ChatRoomListFragment.this).commitAllowingStateLoss();
    }
    public class FatchAdminRoomListAsyncTask extends SafeAsyncTask<List<Room>> {

        @Override
        public List<Room> call() throws Exception {
            RoomProvider roomProvider=new RoomProvider(listArrayAdapter.getOwnNurse().getnurseId());
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
                roomMap.put(room.getRoomNo(),room);

                System.out.println(room);
            }
            System.out.println(Rooms);
            adminRoomsListArrayAdapter.add(Rooms); // 무조건 처음에 한번만
            setListAdapter(adminRoomsListArrayAdapter);
            getView().findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

}

package skuniv.ac.kr.nursetask.UI.Admin;

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
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class AdminChatRoomListFragment extends ListFragment {
    private AdminRoomsListArrayAdapter adminRoomsListArrayAdapter;
    Map<Integer,Integer> roommap;
    int roomno;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        adminRoomsListArrayAdapter=new AdminRoomsListArrayAdapter(getActivity());
        setListAdapter(adminRoomsListArrayAdapter);
        GetSet.setAdminChatRoomListFragment(this);
        return inflater.inflate(R.layout.fragment_chatroom_list,container,false);
    }
    public void realTimeupdate(){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.detach(AdminChatRoomListFragment.this).attach(AdminChatRoomListFragment.this).commitAllowingStateLoss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv=((ListView)getView().findViewById(android.R.id.list));
        roommap=new HashMap<Integer,Integer>();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomno=roommap.get(position);
                Intent intent=new Intent(getContext(),ChatActivity.class);
                intent.putExtra("roomno",roomno);
                getContext().startActivity(intent);
            }
        });
        new FatchAdminRoomListAsyncTask().execute();


    }

    public class FatchAdminRoomListAsyncTask extends SafeAsyncTask<List<Room>> {
        @Override
        public List<Room> call() throws Exception {
            RoomProvider roomProvider=new RoomProvider(AdminListArrayAdapter.ownnurse.getNurseid());
             List<Room> Rooms=roomProvider.FatchRoomList();
            return Rooms;
         }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","arror"+e);        }

        @Override
        protected void onSuccess(List<Room> Rooms) throws Exception {
            super.onSuccess(Rooms);
            int i=0;
            for(Room room:Rooms){
                roommap.put(i,room.getRoomno());
                i++;
                System.out.println(room);
            }
            adminRoomsListArrayAdapter.add(Rooms);
            getView().findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

}


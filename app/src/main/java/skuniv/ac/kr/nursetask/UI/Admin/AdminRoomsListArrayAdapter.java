package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.R;

/**
 * Created by gunyoungkim on 2017-09-27.
 */

public class AdminRoomsListArrayAdapter extends ArrayAdapter<Room> {
    private LayoutInflater layoutInflater;

    public AdminRoomsListArrayAdapter(@NonNull Context context) {
        super(context, R.layout.row_room_list);
        layoutInflater=LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_room_list, parent, false);
        }
        Room room=getItem(position);
        ((TextView)view.findViewById(R.id.roomName)).setText(room.getRoomname());
        ((TextView)view.findViewById(R.id.roomCount)).setText(room.getCount()+"명");
        return view;
    }
    public  void add(List<Room> list){
        if(list==null){
            return;
        }
        for(Room room:list){
            add(room);
        }
    }
}

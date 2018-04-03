package skuniv.ac.kr.nursetask.UI.Nurse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;

/**
 * Created by gunyoungkim on 2017-09-04.
 */

public class ChoiceRoomFragment extends Fragment implements View.OnClickListener {

    private Button buttons[];
    private String room;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View myView=inflater.inflate(R.layout.fragment_choice_room,container,false);
        buttons= new Button[]{((Button) myView.findViewById(R.id.room_101)),((Button) myView.findViewById(R.id.room_102)),((Button) myView.findViewById(R.id.room_103))
                ,((Button) myView.findViewById(R.id.room_201)),((Button) myView.findViewById(R.id.room_202)),((Button) myView.findViewById(R.id.room_203))
                ,((Button) myView.findViewById(R.id.room_301)),((Button) myView.findViewById(R.id.room_302)),((Button) myView.findViewById(R.id.room_303))};

        for(int i=0;i<buttons.length;i++){
            buttons[i].setOnClickListener(this);
        }
        return myView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.room_101:
                room="101- ";
                new roomPatientList().execute();
                break;
            case R.id.room_102:
                room="102- ";
                new roomPatientList().execute();
                break;
            case R.id.room_103:
                room="103- ";
                new roomPatientList().execute();
                break;
            case R.id.room_201:
                room="201- ";
                new roomPatientList().execute();
                break;
            case R.id.room_202:
                room="202- ";
                new roomPatientList().execute();
                break;
            case R.id.room_203:
                room="203- ";
                new roomPatientList().execute();
                break;
            case R.id.room_301:
                room="301- ";
                new roomPatientList().execute();
                break;
            case R.id.room_302:
                room="302- ";
                new roomPatientList().execute();
                break;
            case R.id.room_303:
                room="303- ";
                new roomPatientList().execute();
                break;
        }
    }
    private class roomPatientList extends SafeAsyncTask<List<Patient>> {

        @Override
        public List<Patient> call() throws Exception {
            String url="http://117.17.142.133:8080/nurse/room-patient-list";
            String query="room="+room;
            System.out.println("**************************************"+room);
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
            JSONResultFatchRoomePatientList result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchRoomePatientList.class);
            List<Patient> roomPatientList=result.getData();
            return roomPatientList;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------->exception: "+e);
        }
        @Override
        protected void onSuccess(List<Patient> roomPatientList) throws Exception {
            super.onSuccess(roomPatientList);
            System.out.println(roomPatientList);
            Intent intent=new Intent(getActivity(),RoomActivity.class);
            intent.putExtra("roomPatientList",(Serializable)roomPatientList);
            startActivity(intent);
        }
    }
    private class JSONResultFatchRoomePatientList extends JsonResult<List<Patient>> {}
}
package skuniv.ac.kr.nursetask.UI.Nurse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.domain.Room;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.JsonResult;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.Core.provider.back;
import skuniv.ac.kr.nursetask.UI.Admin.UpdateNurseRoomToken;

/**
 * Created by gunyoungkim on 2017-08-08.
 */

public class ListArrayAdapter extends ArrayAdapter<Nurse> {

    private Bitmap bmImg;
    private ChatRoomListFragment chatRoomListFragment;
    private LayoutInflater layoutInflater;
    private Nurse ownNurse;
    private String data1,data2,roomName;
    private int count;
    private String imageUrl = "http://117.17.142.133:8080/img/";

    public Nurse getOwnNurse() {
        return ownNurse;
    }

    public void setOwnNurse(Nurse ownNurse) {
        this.ownNurse = ownNurse;
    }
    public ListArrayAdapter(@NonNull Context context) {
        super(context, R.layout.row_nurse_list);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_nurse_list, parent, false);
        }
        final Nurse nurse = getItem(position);
        ((TextView) view.findViewById(R.id.nurseName)).setText(nurse.getName());
        ImageView bmImage = (ImageView) view.findViewById(R.id.imagebtn);

        new back(bmImage,bmImg).execute(imageUrl + nurse.getImage() + "");
        bmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),InformationActivity.class);
                intent.putExtra("nurse", (Parcelable) nurse);
                getContext().startActivity(intent);
            }
        });
        view.findViewById(R.id.chatbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //대화하기
                data1 = ownNurse.getnurseId();
                data2 = nurse.getnurseId();
                roomName = ownNurse.getName() + "님," + nurse.getName() + "님";
                count = 2;
                new InsertRoom().execute();
            }
        });
        return view;
    }

    public void add(List<Nurse> list) {
        if (list == null) {
            return;
        }
        for (Nurse nurse : list) {
            add(nurse);
        }
    }


    public class InsertRoom extends AsyncTask<Void, Void, Room> {
        Room answer;

        @Override
        protected Room doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            requestBody = new FormBody.Builder().add("data1",data1).add("data2",data2)
                    .add("roomName",roomName).add("count",String.valueOf(count))
                    .build();

            Request request = new Request.Builder()
                    .url("http://117.17.142.133:8080/nurse/insert-chat-room")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                Gson gson=new Gson();
                answer = gson.fromJson(response.body().string(),Room.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("answer", ""+answer);
            return answer;
        }

        protected void onPostExecute(Room room) {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + room.getRoomNo());
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("roomNo", room.getRoomNo());
            getContext().startActivity(intent);
            chatRoomListFragment = ChatRoomListFragment.getInstance();
            chatRoomListFragment.realTimeUpdate();
        }
    }
}


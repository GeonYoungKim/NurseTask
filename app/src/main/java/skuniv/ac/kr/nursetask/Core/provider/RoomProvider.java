package skuniv.ac.kr.nursetask.Core.provider;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Chat;
import skuniv.ac.kr.nursetask.Core.domain.Room;

/**
 * Created by gunyoungkim on 2017-09-27.
 */

public class RoomProvider {
    String nurseid;
    public RoomProvider(String nurseid){
        this.nurseid=nurseid;
    }
    public List<Room> FatchRoomList(){
        String url="http://117.17.142.135:8080/nurse/roomList";
        String query="nurseid="+nurseid;
        HttpRequest request=HttpRequest.post(url);
        request.accept( HttpRequest.CONTENT_TYPE_JSON );
        request.connectTimeout( 1000 );
        request.readTimeout( 3000 );
        request.send(query);
        int responseCode = request.code();
        if ( responseCode != HttpURLConnection.HTTP_OK  ) {
            throw new RuntimeException("HTTP Response Exception : "+responseCode);
        }
        JSONResultFatchRoomList result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchRoomList.class);
        if("success".equals(result.getResult())==false){
            throw new RuntimeException("JSONResultFatchUserList Response Exception: "+result.getMessage());
        }
        return result.getData();
    }
    private class JSONResultFatchRoomList extends JsonResult<List<Room>>{}
}

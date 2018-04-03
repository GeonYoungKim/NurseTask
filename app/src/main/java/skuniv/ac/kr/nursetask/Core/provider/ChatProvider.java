package skuniv.ac.kr.nursetask.Core.provider;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.ChatVo;

/**
 * Created by gunyoungkim on 2017-09-27.
 */
public class ChatProvider {
    int roomNo;
    public ChatProvider(int roomNo){
        this.roomNo=roomNo;
    }
    public List<ChatVo> FatchChatList(){
        String url="http://117.17.142.133:8080/nurse/chat-list";
        String query="roomNo="+roomNo;
        HttpRequest request=HttpRequest.post(url);
        request.accept( HttpRequest.CONTENT_TYPE_JSON );
        request.connectTimeout( 1000 );
        request.readTimeout( 3000 );
        request.send(query);
        int responseCode = request.code();
        if ( responseCode != HttpURLConnection.HTTP_OK  ) {
            throw new RuntimeException("HTTP Response Exception : "+responseCode);
        }
        JSONResultFatchChatList result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchChatList.class);
        if("success".equals(result.getResult())==false){
            throw new RuntimeException("JSONResultFatchUserList Response Exception: "+result.getMessage());
        }
        return result.getData();
    }
    private class JSONResultFatchChatList extends JsonResult<List<ChatVo>>{}
}

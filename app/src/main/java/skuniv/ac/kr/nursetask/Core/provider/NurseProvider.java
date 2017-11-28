package skuniv.ac.kr.nursetask.Core.provider;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class NurseProvider {
    public List<Nurse> FatchNurseList(){
        String url="http://117.17.142.135:8080/controller/Nurse?a=nurseList";
        HttpRequest request=HttpRequest.get(url);
        request.contentType( HttpRequest.CONTENT_TYPE_JSON );
        request.accept( HttpRequest.CONTENT_TYPE_JSON );
        request.connectTimeout( 1000 );
        request.readTimeout( 3000 );
        int responseCode = request.code();
        if ( responseCode != HttpURLConnection.HTTP_OK  ) {
            throw new RuntimeException("HTTP Response Exception : "+responseCode);
        }
        JSONResultFatchNurseList result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchNurseList.class);
        if("success".equals(result.getResult())==false){
            throw new RuntimeException("JSONResultFatchUserList Response Exception: "+result.getMessage());
        }
        return result.getData();
    }
    private class JSONResultFatchNurseList extends JsonResult<List<Nurse>>{}
}

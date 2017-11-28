package skuniv.ac.kr.nursetask.Core.provider;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.domain.Patient;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class PatientProvider {
    public List<Patient> FatchPatientList(){
        String url="http://117.17.142.135:8080/controller/Nurse?a=patientList";
        HttpRequest request=HttpRequest.get(url);
        request.contentType( HttpRequest.CONTENT_TYPE_JSON );
        request.accept( HttpRequest.CONTENT_TYPE_JSON );
        request.connectTimeout( 1000 );
        request.readTimeout( 3000 );
        int responseCode = request.code();
        if ( responseCode != HttpURLConnection.HTTP_OK  ) {
            throw new RuntimeException("HTTP Response Exception : "+responseCode);
        }
        JSONResultFatchPatientList result=new GsonBuilder().create().fromJson(request.bufferedReader(),JSONResultFatchPatientList.class);
        if("success".equals(result.getResult())==false){
            throw new RuntimeException("JSONResultFatchUserList Response Exception: "+result.getMessage());
        }
        return result.getData();
    }
    private class JSONResultFatchPatientList extends JsonResult<List<Patient>>{}
}



package skuniv.ac.kr.nursetask.UI.Admin;

import com.github.kevinsawicki.http.HttpRequest;

import java.net.HttpURLConnection;

import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;

/**
 * Created by gunyoungkim on 2017-11-23.
 */

public class UpdateToken extends SafeAsyncTask<String> {
    String nurseid;
    String token;

    public UpdateToken(String nurseid,String token){
        this.nurseid=nurseid;
        this.token=token;
    }
    @Override
    public String call() throws Exception {


        String url="http://117.17.142.135:8080/controller/Nurse?a=update_token";
        String query="nurseid="+this.nurseid+"&token="+this.token;
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
        return null;
    }
    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);
        System.out.println("----------->exception: "+e);
    }
    @Override
    protected void onSuccess(String str) throws Exception {
        super.onSuccess(str);

    }
}
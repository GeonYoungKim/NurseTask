package skuniv.ac.kr.nursetask;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by gunyoungkim on 2017-11-22.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    private static final String TAG="MyFirebaseInsIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"New Token : "+refreshedToken);

    }
}

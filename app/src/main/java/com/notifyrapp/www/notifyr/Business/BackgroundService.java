package com.notifyrapp.www.notifyr.Business;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by K on 12/29/2016.
 */

public class BackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Business business = new Business(this);
        business.getUserArticlesFromServer(0, 20, "", -1, new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {
                Log.d("BACKGROUND_SERVICE","GOT ARTICLES IN SERVICE");
            }
        });

        return BackgroundService.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

}

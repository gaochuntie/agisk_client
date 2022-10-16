/*
 * Copyright 2021 John "topjohnwu" Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package atms.app.my_application_c.Tools;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.ipc.RootService;

import java.util.UUID;

// Demonstrate root service using Messengers
public class MSGService extends RootService implements Handler.Callback {

    static {
        System.loadLibrary("agisk-dev111111111111");
    }

    native int nativeGetUid();


    public static String TAG = "MSGService";
    public static final int MSG_GETINFO = 1;
    public static final int MSG_STOP = 2;
    public static final int MSG_DOWORK = 4;
    public static final String UUID_KEY = "uuid";
    public static final String RETURNCODE_KEY = "return_code";


    private final int return_code = 0;
    private String uuid;

    @Override
    public void onCreate() {
        uuid = UUID.randomUUID().toString();
        GlobalMsg.addMsg("MSGService: onCreate, " + uuid);
        Log.d(TAG, "MSGService: onCreate, " + uuid);
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        GlobalMsg.addMsg("MSGService: onBind");
        Log.d(TAG, "MSGService: onBind");
        Handler h = new Handler(Looper.getMainLooper(), this);
        Messenger m = new Messenger(h);
        return m.getBinder();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        //there cant show log.d
        Log.d(TAG, String.valueOf(msg.what));
        if (msg.what == MSG_DOWORK) {
            //true run tasks
            Log.d(TAG, "Recieve task msg");
        }
        if (msg.what == MSG_STOP) {
            stopSelf();
        }
        if (msg.what == MSG_GETINFO) {
            Log.d(TAG, "Recieve get info");
            Message reply = Message.obtain();
            reply.what = msg.what;
            //
            reply.arg1 = Process.myPid();
            reply.arg2 = nativeGetUid();
            Bundle data = new Bundle();
            //return code and msg
            data.putString(UUID_KEY, uuid);
            data.putInt(RETURNCODE_KEY, return_code);
            reply.setData(data);
            try {
                msg.replyTo.send(reply);
            } catch (RemoteException e) {
                GlobalMsg.addErrorMsg("Remote error:" + e.getMessage());
                Log.e(TAG, "Remote error", e);
            }
        }
        return false;

    }

    @Override
    public boolean onUnbind(@NonNull Intent intent) {
        GlobalMsg.addMsg("MSGService: onUnbind, client process unbound");
        Log.d(TAG, "MSGService: onUnbind, client process unbound");
        // Default returns false, which means onRebind will not be called
        return false;
    }

}

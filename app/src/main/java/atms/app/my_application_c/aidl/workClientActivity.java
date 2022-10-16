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
package atms.app.my_application_c.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.CallbackList;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ipc.RootService;
import com.topjohnwu.superuser.nio.FileSystemManager;

import java.io.InputStream;
import java.util.List;

import atms.app.my_application_c.R;
import atms.app.my_application_c.Tools.MSGService;

public class workClientActivity implements Handler.Callback {
    private final Messenger me = new Messenger(new Handler(Looper.getMainLooper(), this));
    public static final String TAG = "EXAMPLE";

    private final List<String> consoleList = new AppendCallbackList();

    // Demonstrate Shell.Initializer
    static class ExampleInitializer extends Shell.Initializer {

        @Override
        public boolean onInit(@NonNull Context context, @NonNull Shell shell) {
            // Load our init script
            InputStream bashrc = context.getResources().openRawResource(R.raw.bashrc);
            shell.newJob().add(bashrc).exec();
            return true;
        }
    }

    private AIDLConnection aidlConn;
    private AIDLConnection daemonConn;
    private FileSystemManager remoteFS;

    class AIDLConnection implements ServiceConnection {

        private final boolean isDaemon;

        AIDLConnection(boolean b) {
            isDaemon = b;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "AIDL onServiceConnected");
            if (isDaemon) {
                daemonConn = this;
            } else {
                aidlConn = this;
            }

            IWorkServicelInterface ipc = IWorkServicelInterface.Stub.asInterface(service);
            try {

                consoleList.add("AIDL PID : " + ipc.getPid());
                consoleList.add("AIDL UID : " + ipc.getUid());
                consoleList.add("AIDL UUID: " + ipc.getUUID());
                if (!isDaemon) {
                    // Get the remote file system service proxy through AIDL
                    IBinder binder = ipc.getFileSystemService();
                    // Create a fs manager with the binder proxy.
                    // We will use this fs manager in our stress test.
                    remoteFS = FileSystemManager.getRemote(binder);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Remote error", e);
            }
            refreshUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "AIDL onServiceDisconnected");
            if (isDaemon) {
                daemonConn = null;
            } else {
                aidlConn = null;
                remoteFS = null;
            }
            refreshUI();
        }
    }

    private MSGConnection msgConn;

    class MSGConnection implements ServiceConnection {

        private Messenger m;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "MSG onServiceConnected");
            m = new Messenger(service);
            msgConn = this;
            refreshUI();

            Message msg = Message.obtain(null, MSGService.MSG_GETINFO);
            msg.replyTo = me;
            try {
                m.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Remote error", e);
            } finally {
                msg.recycle();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "MSG onServiceDisconnected");
            msgConn = null;
            refreshUI();
        }

        void stop() {
            if (m == null)
                return;
            Message msg = Message.obtain(null, MSGService.MSG_STOP);
            try {
                m.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Remote error", e);
            } finally {
                msg.recycle();
            }
        }
    }


    private void refreshUI() {
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        consoleList.add("MSG PID  : " + msg.arg1);
        consoleList.add("MSG UID  : " + msg.arg2);
        String uuid = msg.getData().getString(MSGService.UUID_KEY);
        consoleList.add("MSG UUID : " + uuid);
        return false;
    }

    public workClientActivity(Context context) {

        if (aidlConn == null) {
            Intent intent = new Intent(context, AIDLService.class);
            RootService.bind(intent, new AIDLConnection(false));
        }
        Intent intent = new Intent(context, MSGService.class);
        RootService.bind(intent, new MSGConnection());

        // To verify whether the daemon actually works, kill the app and try testing the
        // daemon again. You should get the same PID as last time (as it was re-using the
        // previous daemon process), and in AIDLService, onRebind should be called.
        // Note: re-running the app in Android Studio is not the same as kill + relaunch.
        // The root process will kill itself when it detects the client APK has updated.

        // Bind to a daemon root service
    }

    /**
     * This List does not store the output anywhere. It is used only as an
     * callback API every time a new output is created by the root shell.
     */
    class AppendCallbackList extends CallbackList<String> {
        @Override
        public void onAddElement(String s) {
            Log.d(TAG, "onAddElement :" + s);
        }
    }
}

package atms.app.agiskclient.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.CallbackList;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ipc.RootService;
import com.topjohnwu.superuser.nio.FileSystemManager;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import atms.app.agiskclient.MainActivity;
import atms.app.agiskclient.R;

public class workClient {
    static String TAG = "workClient";
    //    static {
//        Shell.enableVerboseLogging = BuildConfig.DEBUG;
//        Shell.setDefaultBuilder(Shell.Builder.create()
//                .setFlags(Shell.FLAG_REDIRECT_STDERR)
//                .setInitializers(ExampleInitializer.class)
//        );
//    }
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
    private IWorkServicelInterface ipc;

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

            ipc = IWorkServicelInterface.Stub.asInterface(service);
            try {
                consoleList.add("AIDL PID : " + ipc.getPid());
                consoleList.add("AIDL UID : " + ipc.getUid());
                consoleList.add("AIDL UUID: " + ipc.getUUID());

                //just dont need this
//                if (!isDaemon) {
//                    // Get the remote file system service proxy through AIDL
//                    IBinder binder = ipc.getFileSystemService();
//                    // Create a fs manager with the binder proxy.
//                    // We will use this fs manager in our stress test.
//                    remoteFS = FileSystemManager.getRemote(binder);
//                }
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
            // service dead

            refreshUI();

        }
    }

    ////////////////////////////////////
    Context context = null;
    String clientUUID = UUID.randomUUID().toString();

    AtomicInteger taskNum = new AtomicInteger(0);


    public String getClientUUID() {
        return clientUUID;
    }

    private void refreshUI() {
        Log.d(TAG, "reFreshUI");
    }

    public workClient(Context context, String xml_content) {
        this.context = context;
        this.xmlcontent = xml_content;
    }

    public int getTaskNum() {
        if (ipc == null) {
            return -1;
        }
        try {
            return ipc.getTaskNum();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -2;
    }

    /**
     * force stop root service
     */
    public void terminateAllTask() {
        if (ipc != null) {
            try {
                ipc.terminateSelf();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * updateProgress to ui handler
     */
    private void updateProgress(String log) {
        String outlog = "[" + getClientUUID() + "] " + log;
        Log.d(TAG, outlog);
    }


    /**
     * submit work to root service via aidl ipc
     * this may failed because ipc is null
     * xml is always the identy of action
     *
     * @param xml
     * @return
     */

    String xmlcontent = null;
    private static int ScanRatForRemoteMsg = 500;

    /**
     * set the scan rate for remote msg (ms/once)
     *
     * @param scanRatForRemoteMsg
     */
    public static void setScanRatForRemoteMsg(int scanRatForRemoteMsg) {
        ScanRatForRemoteMsg = scanRatForRemoteMsg;
    }

    public static int getScanRatForRemoteMsg() {
        return ScanRatForRemoteMsg;
    }


    /**
     * submit work and wait until it's finished
     * sync function
     */
    public boolean submitWork() {
        // Bind to a root service; IPC via AIDL
        /**
         * a workClient only handle a rootService
         */

        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, AIDLService.class);
                RootService.bind(intent, new AIDLConnection(false));
            }
        });

        boolean loopflag = true;
        boolean isCheckedServiceState = false;
        boolean result = false;

        while (loopflag) {
            if (ipc == null) {
                if (!isCheckedServiceState) {
                    updateProgress("Waiting rootService to connect.");
                    isCheckedServiceState = true;
                }
                continue;
            }

            loopflag = false;
            try {
                Thread.sleep(getScanRatForRemoteMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //rootService connected
        //do work


        try {
            if (xmlcontent == null) {
                consoleList.add("AIDL : xmlcontent null");
                updateProgress("[CLIENT " + getClientUUID() + "] xml content is null.");
                return false;
            } else {
                result = ipc.doWork(xmlcontent);
                consoleList.add("[CLIENT  " + getClientUUID() + "] result : " + result);
                return result;
            }
        } catch (RemoteException e) {

            e.printStackTrace();
            updateProgress(e.getMessage());
            updateProgress("[[ERROR]] stop!");
            return false;
        }
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

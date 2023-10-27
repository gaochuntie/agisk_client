package atms.app.agiskclient.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

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


    /**
     * AIDLConnection
     */
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
            ipc = null;
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
    /**
     * WorkClient
     */

    static String TAG = "workClient";
    //    static {
//        Shell.enableVerboseLogging = BuildConfig.DEBUG;
//        Shell.setDefaultBuilder(Shell.Builder.create()
//                .setFlags(Shell.FLAG_REDIRECT_STDERR)
//                .setInitializers(ExampleInitializer.class)
//        );
//    }
    //////////////////
    private AIDLConnection aidlConn;
    private AIDLConnection daemonConn;
    private FileSystemManager remoteFS;
    private IWorkServicelInterface ipc;
    //Set in constructor
    private IWorkListener iWorkListener = null;
    ///////////////////////
    /**
     * WorkClient states related variables
     */
    public static enum TASKSTATE{TASK_READY,TASK_RUNNING,TASK_SUCCESS,TASK_FAILED};

    private int finished = 0;

    private int total = 0;
    TASKSTATE task_state=TASKSTATE.TASK_READY;

    public int getFinished() {
        return finished;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public TASKSTATE getTask_state() {
        return task_state;
    }

    public int getProgressPercentage(){
        return (int)(finished*100/total);
    }
    //////////////////////////////////////////

    private final List<String> consoleList = new AppendCallbackList();
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
        iWorkListener = new IWorkListener.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

            }

            @Override
            public void onProgress(int finished, int total) throws RemoteException {
                /**
                 * Update progress bar
                 */
                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, finished + "/" + total, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCompleted(boolean success) throws RemoteException {
                /**
                 * Stop tasks
                 */
            }

            @Override
            public void onThrowInfo(String info) throws RemoteException {
                /**
                 * store info under client item,touch such item in client list to see
                 */
            }

            @Override
            public void onThrowWarning(String warn) throws RemoteException {
                /**
                 * Show warning in the head of client list
                 * the tasks may be still processing normally
                 * just some warnings
                 */
            }

            @Override
            public void OnThrowError(String error) throws RemoteException {
                /**
                 * Show error in the head of client(first priority)
                 * The tasks were terminated already
                 * Some error occured and disturbed the task processing
                 */
            }
        };
        //stub end
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
     *
     * return can be casted ,just check client_type
     */
    public Object submitWork() {
        // Bind to a root service; IPC via AIDL
        /**
         * a workClient only handle a rootService
         * This function runs out of ui thread
         */

        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, AIDLService.class);
                aidlConn = new AIDLConnection(false);
                RootService.bind(intent, aidlConn);
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

                /**
                 * Switch client_type
                 */
                switch (client_type) {
                    case COMMON:
                        result = ipc.doWork(xmlcontent, iWorkListener);
                        consoleList.add("[CLIENT  " + getClientUUID() + "] result : " + result);
                        return new Boolean(result);
                    case DIRECT1_PART_DUMP:
                        String results = ipc.direct_GetPartListString(xmlcontent);

                        consoleList.add("[CLIENT  " + getClientUUID() + "] result : "
                                // @Deprecated
                                //+ result //This is always too large,just for debug
                        );
                        return results;
                    case DIRECT2_PART_DELETE:
                        boolean delete_result
                                = ipc.direct_DeletePart(xmlcontent, direct2_number);
                        consoleList.add("[CLIENT  " + getClientUUID() + "] result : "
                                // @Deprecated
                                + delete_result //just for debug
                        );
                        return delete_result;
                    case DIRECT3_PART_NEW:
                        boolean new_result
                                = ipc.direct_NewPart(xmlcontent, direct3_start_byte, direct3_end_byte, direct3_code, direct3_name);
                        consoleList.add("[CLIENT  " + getClientUUID() + "] result : "
                                // @Deprecated
                                + new_result//just for debug
                        );
                        return new_result;
                    case DIRECT4_FILE_FORCEWRITE:
                        boolean file_write_result
                                = ipc.direct_File_ForceWrite(direct4_content, direct4_dest);
                        consoleList.add("[CLIENT  " + getClientUUID() + "] result : "
                                // @Deprecated
                                + file_write_result//just for debug
                        );
                        return file_write_result;
                    case DIRECT5_FILE_FORCEREAD:
                        String file_read_result = ipc.direct_File_ForceRead(direct5_path);

                        consoleList.add("[CLIENT  " + getClientUUID() + "] result : "
                                // @Deprecated
                                //+ result //This is always too large,just for debug
                        );
                        return file_read_result;

                    default:
                        return new Boolean(false);
                }

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



    //////////////////////////////////////////////////////////////////
    /**
     * Extra part
     * Direct function
     */

    public enum CLIENT_TYPE{
        COMMON,// return Boolean
        DIRECT1_PART_DUMP, //return String
        DIRECT2_PART_DELETE ,//return boolean
        DIRECT3_PART_NEW, //return boolean
        DIRECT4_FILE_FORCEWRITE ,//return boolean
        DIRECT5_FILE_FORCEREAD //return String
    };
    public CLIENT_TYPE client_type=CLIENT_TYPE.COMMON;

    /**
     * 1 Part list dumper
     */
    public void setDirect1_PART_DUMPER(){
        this.client_type = CLIENT_TYPE.DIRECT1_PART_DUMP;
    }

    /**
     * set number
     */
    int direct2_number = 0;
    public void setDirect2_PART_DELETE(int number) {
        direct2_number = number;
        this.client_type = CLIENT_TYPE.DIRECT2_PART_DELETE;
    }

    /**
     * part new
     */
    long direct3_start_byte=-1;
    long direct3_end_byte=-1;
    String direct3_code = null;
    String direct3_name = null;
    public void setDirect3_PART_NEW(long start_byte, long end_byte, String code,String name) {
        direct3_start_byte = start_byte;
        direct3_end_byte = end_byte;
        direct3_code = code;
        direct3_name = name;
        this.client_type = CLIENT_TYPE.DIRECT3_PART_NEW;
    }


    /**
     * file forcely write with root
     */
    String direct4_content=null;
    String direct4_dest=null;
    public void setDirect4_FILE_FORCEWRITE(String content, String dest) {
        this.direct4_content=content;
        this.direct4_dest=dest;
        this.client_type = CLIENT_TYPE.DIRECT4_FILE_FORCEWRITE;
    }


    /**
     * file forcely read with root
     * @param path
     */
    String direct5_path=null;
    public void setDirect5_FILE_FORCEREAD(String path) {
        this.direct5_path=path;
        this.client_type=CLIENT_TYPE.DIRECT5_FILE_FORCEREAD;
    }
}

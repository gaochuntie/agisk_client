package atms.app.agiskclient.Tools;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.CallbackList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GlobalMsg {
    public static String DISKACTION_LOG = "/sdcard/Android/data/atms.app.agiskclient/files/log/disk.log";

    public static String PARTITION_DUMP_LOG = "/sdcard/Android/data/atms.app.agiskclient/files/log/partdump.log";

    public static String PARTITION_LOG = "/sdcard/Android/data/atms.app.agiskclient/files/log/partition.log";

    public static String GLOBAL_LOG = "/sdcard/Android/data/atms.app.agiskclient/files/log/main.log";

    //            #define PARTITION_DUMP_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/partdump.log"
//            #define PARTITION_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/partition.log"
//            #define GLOBAL_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/main.log"
    public static StringBuilder errormsg = new StringBuilder();

    public static void addLog(String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        globalMsg.add(msg + "[" + date + "]\n");
    }

    public static String getLog() {
        return globalMsg.toArray().toString();
    }

    public static void clearLog() {
        globalMsg.clear();
    }

    public static void addErrorMsg(String msg) {
        errormsg.append(msg + "\n");
        globalMsg.add(msg);
    }

    public static native void appendLog(String log, String path);

    public static native String readLog(String path);

    public static String getErrorMsg() {
        return errormsg.toString();
    }

    public static void clearErrorMsg() {
        errormsg = new StringBuilder();
    }

    synchronized public static void addMsg(String msg) {
        globalMsg.add(msg + "\n");
    }

    public static String getMsg() {
        return globalMsg.toArray().toString();
    }

    public static void clearMsg() {
        globalMsg.clear();
    }

    /////////////////////////////////////////////////////
    private static final List<String> globalMsg = new MyCallBackList();
    private static globalMsgOnAddCallback callback = null;

    public static void setCallback(globalMsgOnAddCallback callback) {
        GlobalMsg.callback = callback;
    }

    static class MyCallBackList extends CallbackList<String> {
        @Override
        public void onAddElement(String s) {
            if (callback != null) {
                callback.onMsgAdd(s);
            }
        }
    }

    @NonNull
    public static String getContinusDataString() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        return (formatter.format(date));
    }
}


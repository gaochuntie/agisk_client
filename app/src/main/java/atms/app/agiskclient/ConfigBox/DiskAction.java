package atms.app.agiskclient.ConfigBox;

/**
 * disk action,all action implement by jni
 */
public class DiskAction extends ActionBase {
    public enum Disk_Action_Type {
        DISK_ACTION_TYPE_RESERVE, DISK_ACTION_TYPE_WRITE, DISK_ACTION_TYPE_FORMAT0, DISK_ACTION_TYPE_CLONE, DISK_ACTION_TYPE_BACKUP, DISK_ACTION_TYPE_SPACE
    }

    //

    public Disk_Action_Type type;
    public String driver;
    public long sector;
    public ReservedChunks reservedChunks = null;
    public String[] argv = null;
    int argc = 0;

    //public function

    public DiskAction(int _argc, Disk_Action_Type _type, String[] args, String driver) {
        argc = _argc;
        this.argv = args;
        this.type = _type;
        this.actiontype = ActionType.ACTION_TYPE_DISK;
        this.driver = driver;
    }

    /**
     * do action auto,just call this in workThread
     *
     * @return
     */
    @Override
    public boolean doAction() {

        //Thread.sleep(2000);

        //GlobalMsg.appendLog("Action ..type "+type,logpath);
        //GlobalMsg.appendLog("Debug",logpath);
        switch (type) {
            case DISK_ACTION_TYPE_CLONE:
                //(JNIEnv *env, jobject thiz,
                //                                                            jstring driver, jlong s_start,
                //                                                            jlong length, jstring t_driver,
                //                                                            jlong t_start)
                //              element3.getAttribute("s_driver")
                //                                                        , element3.getAttribute("s_start")
                // , element3.getAttribute("s_length")
                // , element3.getAttribute("t_start")}
                //
                // , element3.getAttribute("t_driver")));
                clone(driver, Long.valueOf(argv[1]), Long.valueOf(argv[2]), argv[4], Long.valueOf(argv[3]));
                break;
            case DISK_ACTION_TYPE_SPACE:
                spare(driver, Long.parseLong(argv[0]));
                break;
            case DISK_ACTION_TYPE_WRITE:
                write(driver, Long.valueOf(argv[0]), Long.valueOf(argv[1]), argv[2], Long.valueOf(argv[3]));
                break;
            case DISK_ACTION_TYPE_BACKUP:
                //GlobalMsg.appendLog(argv[0],logpath);
                backup(driver, Long.valueOf(argv[0]), Long.valueOf(argv[1]), argv[2]);
                break;
            case DISK_ACTION_TYPE_FORMAT0:
                format(driver, Long.valueOf(argv[0]), Long.valueOf(argv[1]));
                break;
            case DISK_ACTION_TYPE_RESERVE:
                addReservedChunks(new ReservedChunks(argv[2],argv[0], argv[1]));
                break;
            default:
                break;

        }


        return false;
    }


    public static class ReservedChunks {

        public long start;
        public long length;
        public String driver;

        public ReservedChunks(String driver,String start, String length) {
            this.start = Long.valueOf(start);
            this.length = Long.valueOf(length);
            this.driver = driver;
        }


    }

    public ReservedChunks getReservedChunks() {
        return reservedChunks;
    }

    public void addReservedChunks(ReservedChunks reservedChunks) {
        this.reservedChunks = reservedChunks;
    }

    public void clearReservedChunks() {
        this.reservedChunks = null;
    }


    private native int write(String driver, long start, long length
            , String raw_file_path, long offset_raw);

    private native int format(String driver, long start, long length);

    //specially clone to current disk
    //(JNIEnv *env, jobject thiz,
    //                                                            jstring driver, jlong s_start,
    //                                                            jlong length, jstring t_driver,
    //                                                            jlong t_start)
    private native int clone(String driver,  long s_start, long s_length,String s_driver, long t_start);

    private native int backup(String driver, long start, long length, String backupto);

    /**
     * find the last usable chunks' first lba
     *
     * @param driver
     * @return
     */
    private native long spare(String driver, long length);
}

package atms.app.agiskclient.ConfigBox;

import android.util.Log;

import atms.app.agiskclient.Tools.GlobalMsg;
import atms.app.agiskclient.Tools.TAG;

/**
 * partition action,all action implement by jni
 */
public class PartitionAction extends ActionBase {
    public enum PARTITION_ACTION_TYPE {
        PARTITION_ACTION_TYPE_NEW
        , PARTITION_ACTION_TYPE_DELETE
        , PARTITION_ACTION_TYPE_CLONE
        , PARTITION_ACTION_TYPE_FORMAT
        , PARTITION_ACTION_TYPE_COEXIST_MOVE
        , PARTITION_ACTION_TYPE_HIDE_MOVE
        , PARTITION_ACTION_TYPE_RESIZE
        , PARTITION_ACTION_TYPE_MOUNT
        , PARTITION_ACTION_TYPE_RENAME
        , PARTITION_ACTION_TYPE_RESIZE_TABLE
        , PARTITION_ACTION_TYPE_UMOUNT
        , PARTITION_ACTION_TYPE_READ
        , PARTITION_ACTION_TYPE_BACKUP_TABLE
        , PARTITION_ACTION_TYPE_RESTORE_TABLE
    }


    public PARTITION_ACTION_TYPE pt_type;
    public String driver;
    public int argc = 0;
    public String[] argv;


    public PartitionAction(int argc, String[] args, PARTITION_ACTION_TYPE pt_type, String driver) {
        this.actiontype = ActionType.ACTION_TYPE_PARTITION;
        this.pt_type = pt_type;
        this.argc = argc;
        this.argv = args;
        this.driver = driver;


    }

    /**
     * do action auto,just call this in workThread
     *
     * @return
     */
    @Override
    public boolean doAction() {
        //TODO doAction
        Log.d(TAG.Partition_TAG, "PartitionAction called.");

        switch (pt_type) {
            case PARTITION_ACTION_TYPE_NEW:
                /**
                 * "name"
                 * "start"
                 * "size"
                 * "number"
                 */
                if (argv[1].isEmpty() && argv[3].isEmpty()) {
                    newPart(driver, argv[0], Long.valueOf(argv[2]));
                    break;
                }
                if (argv[3].isEmpty()) {
                    newPart(driver, argv[0], Long.valueOf(argv[1]), Long.valueOf(argv[2]));
                    break;
                }
                newPart(driver, Integer.valueOf(argv[3]), argv[0], Long.valueOf(argv[1]), Long.valueOf(argv[2]));

                break;
            case PARTITION_ACTION_TYPE_DELETE:
                if (!argv[0].isEmpty()) {
                    delete(driver, argv[0]);
                    break;
                }
                if (Integer.valueOf(argv[1]) >= 0) {
                    delete(driver, Integer.valueOf(argv[1]));
                    break;
                }
                GlobalMsg.addMsg("Task[" + taskID + "] delete error ");
                break;
            case PARTITION_ACTION_TYPE_CLONE:
                clone(driver, argv[0], Integer.valueOf(argv[1]), Long.valueOf(argv[2]));
                break;
            case PARTITION_ACTION_TYPE_FORMAT:
                format(driver, Integer.valueOf(argv[0]), argv[1]);
                break;
            case PARTITION_ACTION_TYPE_MOUNT:
                mount(driver, Integer.valueOf(argv[0]), argv[1], argv[2]);
                break;
            case PARTITION_ACTION_TYPE_READ:
                readInfo(driver);
                break;
            case PARTITION_ACTION_TYPE_RENAME:
                if (!argv[0].isEmpty()) {
                    if (Integer.valueOf(argv[0]) < 0) {
                        return false;
                    }
                    //use pt_number priority
                    rename(driver, Integer.valueOf(argv[0]), argv[2]);
                    break;
                }
                if (!argv[1].isEmpty()) {
                    rename(driver, argv[1], argv[2]);
                    break;
                }
                break;
            case PARTITION_ACTION_TYPE_RESIZE_TABLE:
                if (!argv[0].isEmpty()) {
                    resize_table(driver, Integer.valueOf(argv[0]));
                    break;
                }
                break;
            case PARTITION_ACTION_TYPE_UMOUNT:
                if (!argv[0].isEmpty()) {
                    if (Integer.valueOf(argv[0]) < 0) {
                        return false;
                    }
                    //use pt_number priority
                    umount(driver, Integer.valueOf(argv[0]));
                    break;
                }
                if (!argv[1].isEmpty()) {
                    umount(driver, argv[1]);
                    break;
                }
                break;
            case PARTITION_ACTION_TYPE_BACKUP_TABLE:
                if (!argv[0].isEmpty()) {
                    backup_table(driver, argv[0]);
                    break;
                }
                break;
            case PARTITION_ACTION_TYPE_RESTORE_TABLE:
                if (!argv[0].isEmpty()) {
                    restore_table(driver, argv[0]);
                    break;
                }
                break;

        }
        //Thread.sleep(2000);
        return false;
    }
//

    /**
     * fully tailored
     *
     * @param driver
     * @param number
     * @param name
     * @param start
     * @param length
     * @return
     */
    private native int newPart(String driver, int number, String name, long start, long length);
//

    /**
     * number auto
     *
     * @param driver
     * @param name
     * @param start
     * @param length
     * @return
     */
    private native int newPart(String driver, String name, long start, long length);
//

    /**
     * number  location auto
     *
     * @param driver
     * @param name
     * @param length
     * @return
     */
    private native int newPart(String driver, String name, long length);


    /**
     * delete by name
     *
     * @param driver
     * @param name
     * @return
     */
    private native int delete(String driver, String name);

    /**
     * delete by number
     *
     * @param driver
     * @param number
     * @return
     */
    private native int delete(String driver, int number);

    /**
     * clone a partition from a driver to this driver
     *
     * @param driver
     * @param from_driver
     * @param from_number
     * @param t_start
     * @return
     */
    private native int clone(String driver, String from_driver, int from_number, long t_start);

    /**
     * format a partition
     * the filesystem type support is specialized on certain system
     *
     * @param driver
     * @param filesystem    support ext4 f2fs fat fat32 ...add here
     * @param partition_num
     * @return
     */
    private  int format(String driver, int partition_num, String filesystem){

        return 0;
    }


    /**
     * mount a partition
     *
     * @param driver
     * @param number
     * @param filesystem
     * @param mountPoint
     * @return
     */
    private native int mount(String driver, int number, String filesystem, String mountPoint);

    private native int readInfo(String driver);


    /**
     * rename a partition by number (1)
     * @param driver
     * @param partition_num
     * @param new_name
     * @return
     */
    private native int rename(String driver, int partition_num, String new_name);

    /**
     * rename a partition by name(2)
     * @param driver
     * @param old_name
     * @param new_name
     * @return
     */
    private native int rename(String driver, String old_name, String new_name);
    private static int umount(String driver, int number){
        //TODO
        return 1;
    }
    private  static int umount(String driver, String name){
        //TODO
        return 1;
    }
    private native int resize_table(String driver, int new_size);
    private native int backup_table(String driver, String path);
    private native int restore_table(String driver, String path);
}

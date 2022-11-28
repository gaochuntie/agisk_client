package atms.app.agiskclient.ConfigBox;

import java.util.UUID;

import atms.app.agiskclient.Native.jniRes;

public class ActionBase {
    public enum ActionType {
        ACTION_TYPE_DISK, ACTION_TYPE_PARTITION, ACTION_TYPE_REBOOT, ACTION_TYPE_REBOOT_RECOVERY, ACTION_TYPE_REBOOT_BOOTLOADER
        ,ACTION_TYPE_FILE
    }

    //

    String taskName = "Anyous task";
    String taskID = UUID.randomUUID().toString();

    public String getTaskID() {
        return taskID;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public ActionType actiontype;

    public boolean doAction() {
        switch (actiontype) {
            case ACTION_TYPE_REBOOT:
                jniRes.reboot();
                break;
            case ACTION_TYPE_REBOOT_RECOVERY:
                jniRes.rebootRec();
                break;
            case ACTION_TYPE_REBOOT_BOOTLOADER:
                jniRes.rebootBootloader();
                break;
            default:
                break;
        }

        return false;
    }

}

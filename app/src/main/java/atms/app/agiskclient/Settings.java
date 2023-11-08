package atms.app.agiskclient;

import java.util.concurrent.atomic.AtomicBoolean;

public class Settings {
    private static final AtomicBoolean rootAccess = new AtomicBoolean(false);
    private static String serial_number="";
    private static final AtomicBoolean storageChipType_UFS = new AtomicBoolean(false);

    public static void setRootAccess(boolean rootaccess) {
        rootAccess.set(rootaccess);

    }

    public static void setUFS() {
        storageChipType_UFS.set(true);
    }
    public static void setEMMC(){
        storageChipType_UFS.set(false);
    }
    public static boolean isUFS(){
        return storageChipType_UFS.get();
    }

    public static void setSerial_number(String _serial_number) {
        serial_number=_serial_number;
    }
    public static String getSerial_number(){
        return serial_number;
    }

    /**
     * Check root permission
     * @return 1 granted 0 rejected
     */
    public static boolean getRootAccess() {
        return rootAccess.get();

    }

    /**
     * homwFragment settings
     */
    public static boolean openFileUsingThirdPartyLib = true;
}

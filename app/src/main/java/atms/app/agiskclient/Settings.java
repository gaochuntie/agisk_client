package atms.app.agiskclient;

import java.util.concurrent.atomic.AtomicBoolean;

public class Settings {
    private static final AtomicBoolean rootAccess = new AtomicBoolean(false);
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

    /**
     * Check root permission
     * @return 1 granted 0 rejected
     */
    public static boolean getRootAccess() {
        return rootAccess.get();

    }
}

package atms.app.agiskclient;

import java.util.concurrent.atomic.AtomicBoolean;

public class Settings {
    private static final AtomicBoolean rootAccess = new AtomicBoolean(false);

    public static void setRootAccess(boolean rootaccess) {
        rootAccess.set(rootaccess);

    }

    /**
     * Check root permission
     * @return 1 granted 0 rejected
     */
    public static boolean getRootAccess() {
        return rootAccess.get();

    }
}

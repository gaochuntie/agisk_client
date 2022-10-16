package atms.app.my_application_c;

import java.util.concurrent.atomic.AtomicBoolean;

public class Settings {
    private static final AtomicBoolean rootAccess = new AtomicBoolean(false);

    public static void setRootAccess(boolean rootaccess) {
        rootAccess.set(rootaccess);

    }

    public static boolean getRootAccess() {
        return rootAccess.get();

    }
}

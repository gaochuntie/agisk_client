package atms.app.agiskclient.Tools;

import android.util.Log;

public class DebugUtils {
    public static void functionHit(String functionName) {
        Log.d(TAG.FUNCTION_DUMP, functionName + " hit");
    }
}

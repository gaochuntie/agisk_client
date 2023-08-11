package atms.app.agiskclient.Tools;

import android.content.Context;

import atms.app.agiskclient.aidl.workClient;

public class DirectFunctionUtils {
    public static boolean Direct2_PART_DELETE(Context context, String driver, int num) {
        workClient client = new workClient(context, driver);
        client.setDirect2_PART_DELETE(num);
        boolean result = (Boolean) client.submitWork();
        return result;
    }

    public static String Direct1_PART_DUMPER(Context context, String driver) {
        workClient client = new workClient(context, driver);
        client.setDirect1_PART_DUMPER();
        String result = (String) client.submitWork();
        return result;
    }

    public static boolean Direct3_PART_NEW(Context context, String driver, long start_byte, long end_byte, String code) {
        //TODO
        return true;
    }
}

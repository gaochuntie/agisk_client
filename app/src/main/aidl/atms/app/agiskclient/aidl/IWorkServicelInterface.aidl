// IWorkServicelInterface.aidl
package atms.app.agiskclient.aidl;

import atms.app.agiskclient.aidl.IWorkListener;
// Declare any non-default types here with import statements

interface IWorkServicelInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
            int getPid();
                int getUid();
                String getUUID();
                IBinder getFileSystemService();
                boolean doWork(String xml,IWorkListener listener);
                int getTaskNum();
                void terminateSelf();
                String getTasksState();
                String getConclusionMsg();
}

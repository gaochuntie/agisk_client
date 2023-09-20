// IWorkListener.aidl
package atms.app.agiskclient.aidl;

// Declare any non-default types here with import statements

interface IWorkListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void onProgress(int finished,int total);
    void onCompleted(boolean success);
    void onThrowInfo(String info);
    void onThrowWarning(String warn);
    void OnThrowError(String error);
}
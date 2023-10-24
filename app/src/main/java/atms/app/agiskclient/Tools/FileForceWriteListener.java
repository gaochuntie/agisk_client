package atms.app.agiskclient.Tools;

public interface FileForceWriteListener {
    void onWriteFailed(String reson);
    void onWriteSuccess();
    //void onProgress(int per);
}

package atms.app.agiskclient;


public class JNI {


    public native int add(int x, int y);

    public native boolean checkPasswd(String passwd);
}

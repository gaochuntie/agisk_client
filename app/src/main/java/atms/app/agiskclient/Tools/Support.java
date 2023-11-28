package atms.app.agiskclient.Tools;

public class Support {
    public static native String BytesToIeee(long sectors,long sector_size);
    public static native String DES_EncryptString(String str,String key);
    public static native String DES_DecryptString(String str,String key);
}

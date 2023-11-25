package atms.app.agiskclient.ConfigBox;

import android.content.res.AssetManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atms.app.agiskclient.ReservedAreaKits.ReservedAreaRepository;
import atms.app.agiskclient.Tools.TAG;

public class OrigConfig {
    enum FILE_TYPE {FILE_TYPE_INNER, FILE_TYPE_EXTRA, FILE_TYPE_CONTENT}

    //
    FILE_TYPE file_type;
    public static String TAG = "OrigConfig";
    String file_path;
    Map<String, String> attributions;
    List<ActionBase> actionList = new ArrayList<>();
    XmlProcessor m_xmlProcessor;
    private boolean isEncrypted = false;

    public boolean isDecrypted(){
        return m_xmlProcessor.isDecrypted();
    }

    public boolean isParseSuccess() {
        return m_xmlProcessor.isParseSuccess();
    }

    public FILE_TYPE getFile_type() {
        return file_type;
    }

    public List<ActionBase> getActionList() {
        return actionList;
    }

    public int getActionNum() {
        return actionList.size();
    }


    public Map<String, String> getAttributions() {
        return attributions;
    }

    public String getFile_path() {
        if (getFile_type() == FILE_TYPE.FILE_TYPE_INNER) {
            return "assets/" + file_path;
        }
        return file_path;
    }


    /**
     * decrypt xml if need,this only support extra_path xml
     * for xml_content ,it must be decrypted
     * so there is no mean to decrypt it
     * @param key
     * @param flag
     * @return 1 decrypt failed - wrong key
     *         2 parse failed
     *         0 ok
     */
    public int doDecrypt(String key,int flag){
        if (!isEncrypted) {
            Log.d(TAG, "Not encrypted");
            return 0;
        }
        XmlProcessor xmlProcessor = new XmlProcessor(m_xmlProcessor.filename, key, flag);
        if (!xmlProcessor.isDecrypted()){
            //Decrypt failed
            //TODO
            return 1;
        }
        if (!xmlProcessor.isParseSuccess()){
            //parse failed
            //TODO
            return 2;
        }
        //decrypt ok
        attributions = xmlProcessor.getAttributes();
        m_xmlProcessor = xmlProcessor;
        isEncrypted = xmlProcessor.isEncrypted();
        return 0;
    }

    /**
     * extra file constructor
     *
     * @param extra_path
     */
    public OrigConfig(String extra_path) {
        file_path = extra_path;
        file_type = FILE_TYPE.FILE_TYPE_EXTRA;
        //parse config
        XmlProcessor xmlProcessor = new XmlProcessor(extra_path);
        m_xmlProcessor = xmlProcessor;
        isEncrypted = xmlProcessor.isEncrypted();
        attributions = xmlProcessor.getAttributes();
    }

    /**
     * xml content constructor
     *
     * @param xml_content
     * @param encode
     */
    public OrigConfig(String xml_content, String encode) {
        //parse config
        //TODO other encode support
        file_path = "";
        file_type = FILE_TYPE.FILE_TYPE_CONTENT;
        XmlProcessor xmlProcessor = new XmlProcessor(xml_content, "UTF-8");
        attributions = xmlProcessor.getAttributes();
        m_xmlProcessor = xmlProcessor;
        //isEncrypted = xmlProcessor.isEncrypted();
    }


    public void setActionList() {
        if (actionList.size() != 0) {
            return;
        }
        m_xmlProcessor.setActions(actionList);
    }




    /**
     * inner constructor without set action list
     *
     * @param assetManager
     * @param inner_path
     */
    public OrigConfig(AssetManager assetManager, String inner_path) {
        file_path = inner_path;
        file_type = FILE_TYPE.FILE_TYPE_INNER;
        //parse config
        XmlProcessor xmlProcessor = new XmlProcessor(assetManager, inner_path);
        attributions = xmlProcessor.getAttributes();
        m_xmlProcessor = xmlProcessor;

    }

    /**
     * return xml content as string
     *
     * @return
     */
    public String getXmlString() {
        return m_xmlProcessor.getDocString();
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }
}

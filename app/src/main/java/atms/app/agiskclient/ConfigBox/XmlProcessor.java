package atms.app.agiskclient.ConfigBox;

import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import atms.app.agiskclient.Tools.GlobalMsg;


public class XmlProcessor {
    static {
        System.loadLibrary("agisk-cli");
    }

    public static String TAG = "XML : ";
    /**
     * give a file to construct the Xml Object
     */

    AssetManager assetManager;
    String filename;
    Document doc = null;
    private boolean isEncrypted = false;
    private boolean isParseSuccess = false;

    public boolean isParseSuccess() {
        return isParseSuccess;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    /**
     * simply check file suffix, .enxml is encrypted others false
     * @param extra_path
     * @return
     */
    private boolean checkIsEncrypted(String extra_path) {
        //TODO check is encrypted
        boolean result = false;
        if (extra_path.endsWith(".enxml")) {
            result = true;
        }
        isEncrypted = result;
        return result;
    }

    private native String decryptXml(String extra_path);

    /**
     * default constructor
     *
     * @param __assetManager
     * @param __filename
     */
    public XmlProcessor(AssetManager __assetManager, String __filename) {
        this.assetManager = __assetManager;
        this.filename = __filename;

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        InputStream inputStream;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            inputStream = assetManager.open(filename);
            //creat a doc
            doc = builder.parse(inputStream);
            isParseSuccess = true;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            GlobalMsg.addMsg(e.getMessage());
            e.printStackTrace();
        }

    }

    public XmlProcessor(String extra_path) {
        this.filename = extra_path;
        if (checkIsEncrypted(extra_path)) {
            String xmlStr = decryptXml(extra_path);
            //Log.d(TAG, "de xml : " + xmlStr);
            StringReader sr = new StringReader(xmlStr);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                doc = builder.parse(is);
                isParseSuccess = true;
            } catch (ParserConfigurationException | IOException | SAXException e) {
                GlobalMsg.addMsg(e.getMessage());
                e.printStackTrace();
            }
        } else {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            InputStream inputStream;
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                inputStream = new FileInputStream(extra_path);
                //creat a doc
                doc = builder.parse(inputStream);
                isParseSuccess = true;
            } catch (ParserConfigurationException | IOException | SAXException e) {
                GlobalMsg.addMsg(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    /**
     * string constructor
     *
     * @param xml_content_utf8
     */
    public XmlProcessor(String xml_content_utf8, String encode) {
        String xmlStr = xml_content_utf8;
        StringReader sr = new StringReader(xmlStr);
        InputSource is = new InputSource(sr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            isParseSuccess = true;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            GlobalMsg.addMsg(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * get xml string
     *
     * @return
     */
    @Nullable
    public String getDocString() {
        // 方法1：将xml文件转化为String

        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            String result = sw.toString();
            return result;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * map.put("id",id);
     * map.put("name",name);
     * map.put("uuid",uuid);
     * map.put("author", author);
     * map.put("description", description);
     * map.put("mark", mark);
     *
     * @return
     */
    public Map<String, String> getAttributes() {
        doc.getDocumentElement().normalize();
        Map<String, String> map = new HashMap<>();
        //parse attribute
        NodeList attrNodeList = doc.getElementsByTagName("attribute");

        for (int i = 0; i < attrNodeList.getLength(); i++) {
            Node node = attrNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String author = element.getElementsByTagName("author")
                        .item(0).getTextContent();
                Log.d(TAG, "author : " + author);
                String description = element.getElementsByTagName("description")
                        .item(0).getTextContent();
                Log.d(TAG, "description : " + description);

                String mark = element.getElementsByTagName("mark")
                        .item(0).getTextContent();
                Log.d(TAG, "mark : " + mark);

                String id = element.getElementsByTagName("id")
                        .item(0).getTextContent();
                Log.d(TAG, "id : " + id);

                String name = element.getElementsByTagName("name")
                        .item(0).getTextContent();
                Log.d(TAG, "name : " + name);

                String uuid = element.getElementsByTagName("uuid")
                        .item(0).getTextContent();
                Log.d(TAG, "uuid : " + uuid);

                String rom_name = element.getElementsByTagName("rom_name").item(0).getTextContent();

                String android_version = element.getElementsByTagName("android_version").item(0).getTextContent();

                map.put("id", id);
                map.put("name", name);
                map.put("uuid", uuid);
                map.put("author", author);
                map.put("description", description);
                map.put("mark", mark);
                map.put("rom_name", rom_name);
                map.put("android_version", android_version);
            }
        }
        if (map.isEmpty()) {
            GlobalMsg.addMsg("XML : get attributions failed. size 0");
        }
        return map;
    }


    /**
     * set action list and get reserved chunks
     *
     * @param actions
     */
    public void setActions(List<ActionBase> actions) {

        NodeList nodeList = doc.getElementsByTagName("Action");

        //extra Action
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                //Extra 2st action list

                NodeList nodeList2 = element.getChildNodes();
                for (int i2 = 0; i2 < nodeList2.getLength(); i2++) {
                    Node node2 = nodeList2.item(i2);
                    if (node2.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node2;

                        //extra 3rd action list
                        NodeList nodeList3 = element2.getChildNodes();
                        for (int i3 = 0; i3 < nodeList3.getLength(); i3++) {
                            Node node3 = nodeList3.item(i3);
                            if (node3.getNodeType() == Node.ELEMENT_NODE) {
                                Element element3 = (Element) node3;

                                //read action
                                switch (element2.getTagName()) {

                                    /**
                                     * argc include source driver!!!! add 1
                                     */
                                    case "PartitionModification":

                                        switch (element3.getTagName()) {
                                            case "new":
                                                actions.add(new PartitionAction(5
                                                        , new String[]{
                                                        element3.getAttribute("name")
                                                        , element3.getAttribute("start")
                                                        , element3.getAttribute("size")
                                                        ,element3.getAttribute("pt_number")}

                                                        , PartitionAction.PARTITION_ACTION_TYPE.PARTITION_ACTION_TYPE_NEW
                                                        , element3.getAttribute("driver"))

                                                );
                                                break;
                                            case "delete":
                                                actions.add(new PartitionAction(3
                                                        , new String[]{
                                                        element3.getAttribute("name")
                                                        , element3.getAttribute("pt_number")}

                                                        , PartitionAction.PARTITION_ACTION_TYPE.PARTITION_ACTION_TYPE_DELETE
                                                        , element3.getAttribute("driver"))

                                                );
                                                break;

                                            /**
                                             * clone to current device
                                             */
                                            case "clone":
                                                actions.add(new PartitionAction(4
                                                        , new String[]{
                                                        element3.getAttribute("s_driver")
                                                        , element3.getAttribute("s_number")
                                                        , element3.getAttribute("t_start")}

                                                        , PartitionAction.PARTITION_ACTION_TYPE.PARTITION_ACTION_TYPE_CLONE
                                                        , element3.getAttribute("t_driver"))

                                                );
                                                break;
                                            case "format":
                                                actions.add(new PartitionAction(3
                                                                , new String[]{
                                                                element3.getAttribute("pt_number")
                                                                , element3.getAttribute("filesystem")
                                                        }

                                                                , PartitionAction.PARTITION_ACTION_TYPE.PARTITION_ACTION_TYPE_FORMAT
                                                                , element3.getAttribute("driver"))
                                                );

                                                break;
                                            case "coexist_move":
                                                //TODO coexist_move
                                                break;
                                            case "hide_move":
                                                //TODO hide_move
                                                break;
                                            case "mount":
                                                actions.add(new PartitionAction(4
                                                                , new String[]{
                                                                element3.getAttribute("pt_number")
                                                                , element3.getAttribute("filesystem")
                                                                , element3.getAttribute("mount_point")
                                                        }

                                                                , PartitionAction.PARTITION_ACTION_TYPE.PARTITION_ACTION_TYPE_MOUNT
                                                                , element3.getAttribute("driver"))
                                                );
                                                break;
                                            case "read":
                                                actions.add(new PartitionAction(1
                                                        , new String[]{""}
                                                        , PartitionAction.PARTITION_ACTION_TYPE.PARTITION_ACTION_TYPE_READ
                                                        , element3.getAttribute("driver")));
                                                break;
                                        }

                                        break;
                                    case "DiskModification":

                                        switch (element3.getTagName()) {

                                            /**
                                             * reserve will be perform when app obtain root access
                                             * reserved area will be protected only in this app's actions
                                             */
                                            case "reserve":
                                                DiskAction diskAction = new DiskAction(3
                                                        , DiskAction.Disk_Action_Type.DISK_ACTION_TYPE_RESERVE
                                                        , new String[]{
                                                        element3.getAttribute("start")
                                                        , element3.getAttribute("length")}

                                                        , element3.getAttribute("driver"));
                                                diskAction.addReservedChunks(new DiskAction.ReservedChunks(
                                                        element3.getAttribute("start")
                                                        , element3.getAttribute("length")));
                                                actions.add(diskAction);
                                                break;
                                            case "write":
                                                actions.add(new DiskAction(5
                                                        , DiskAction.Disk_Action_Type.DISK_ACTION_TYPE_WRITE
                                                        , new String[]{
                                                        element3.getAttribute("start")
                                                        , element3.getAttribute("length")
                                                        , element3.getAttribute("raw_file")
                                                        , element3.getAttribute("offset_raw")}

                                                        , element3.getAttribute("driver")));
                                                break;
                                            case "format":
                                                actions.add(new DiskAction(3
                                                        , DiskAction.Disk_Action_Type.DISK_ACTION_TYPE_FORMAT0
                                                        , new String[]{
                                                        element3.getAttribute("start")
                                                        , element3.getAttribute("length")}

                                                        , element3.getAttribute("driver")));
                                                break;
                                            case "clone":
                                                actions.add(new DiskAction(5
                                                        , DiskAction.Disk_Action_Type.DISK_ACTION_TYPE_CLONE
                                                        , new String[]{
                                                        element3.getAttribute("s_driver")
                                                        , element3.getAttribute("s_start")
                                                        , element3.getAttribute("s_length")
                                                        , element3.getAttribute("t_start")}
                                                        , element3.getAttribute("driver")));
                                                break;
                                            case "backup":
                                                //GlobalMsg.appendLog(element.getAttribute("length"),"/sdcard/test1");
                                                actions.add(new DiskAction(4
                                                        , DiskAction.Disk_Action_Type.DISK_ACTION_TYPE_BACKUP
                                                        , new String[]{
                                                        element3.getAttribute("start")
                                                        , element3.getAttribute("length")
                                                        , element3.getAttribute("destfile")}
                                                        , element3.getAttribute("driver")));
                                                break;
                                            case "spare":
                                                actions.add(new DiskAction(2
                                                        , DiskAction.Disk_Action_Type.DISK_ACTION_TYPE_SPACE
                                                        , new String[]{
                                                        element3.getAttribute("length")}
                                                        , element3.getAttribute("driver")));
                                                break;
                                        }
                                        break;
                                    default:
                                        break;
                                }

                            }
                        }
                    }
                }

            }
        }

    }

    public void parseXml() {

        Log.d(TAG, "Root element :" + doc.getDocumentElement().getNodeName());


    }
}

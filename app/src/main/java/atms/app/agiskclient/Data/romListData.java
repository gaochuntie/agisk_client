package atms.app.agiskclient.Data;
/**
 * 1 rom class
 * 2 staticly restore a rom instance list
 * <p>
 * so call getRom/addRom to get/new a rom in order to avoid multiple define
 * id means the id from recyclerview item,originately from your getRomList func,\
 * so the id means a index of a element in a arry
 * func name mean func,dont need comment in fact
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atms.app.agiskclient.ConfigBox.ActionBase;
import atms.app.agiskclient.ConfigBox.DiskAction;
import atms.app.agiskclient.ConfigBox.OrigConfig;
import atms.app.agiskclient.ReservedAreaKits.ReservedAreaRepository;
import atms.app.agiskclient.Tools.TAG;

public class romListData {

    private static final Map<Integer, romListData> romList = new HashMap<>();

    public static romListData addRom(int id) {

        if (romList.get(id) == null) {
            romListData i = new romListData();
            romList.put(id, i);
            return i;
        } else {
            return romList.get(id);
        }
    }

    public static void clearRomList() {
        romList.clear();
    }

    @Nullable
    public static romListData getRom(int id) {
        return romList.get(id);
    }


    ////////////////////////
    //attribute

    //orig
    String id;
    String author;
    String uuid;
    String mark;
    String description;
    String xml_path;
    boolean isReservedProtected=false;

    public boolean isReservedProtected() {
        return isReservedProtected;
    }

    public void setIsReservedProtected() {
        //TODO implement is protected
        //check all disk related actions
        Log.d(TAG.ROM_LIST_DATA, "Checking reserved protection : " + uuid);
    }

    //parsed
    String romname;
    String systemSize;
    String userdataSize;

    Map<String, String> modifiedPartitionsMap = new HashMap<>();
    OrigConfig origConfig;

    //rom specific
    @Nullable
    private RomItemPicture rompicture = null;

    //////////////////////////////////


    public void setRompicture(@Nullable RomItemPicture rompicture) {
        this.rompicture = rompicture;
    }

    @Nullable
    public RomItemPicture getRompicture() {
        return rompicture;
    }

    /**
     * Must call this to set needed values from origconfig
     */
    public void initRomFromOrigConfig() {
        setId(origConfig.getAttributions().get("id"));
        setRomname(origConfig.getAttributions().get("name"));
        setAuthor(origConfig.getAttributions().get("author"));
        setUuid(origConfig.getAttributions().get("uuid"));
        setDescription(origConfig.getAttributions().get("description"));
        setXml_path(origConfig.getFile_path());
        setMark(origConfig.getAttributions().get("mark"));
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getXml_path() {
        return xml_path;
    }

    public void setXml_path(String xml_path) {
        this.xml_path = xml_path;
    }

    //TODO
    public Map<String, String> getModifiedPartitionsMap() {
        return modifiedPartitionsMap;
    }

    //TODO
    public void addModifiedPartitions(String pt_name, String dev_node) {
        modifiedPartitionsMap.put(pt_name, dev_node);
    }

    //TODO
    public void clearModifiedPartitionList() {
        modifiedPartitionsMap.clear();
    }

    public void setRomname(String romname) {
        this.romname = romname;
    }

    public String getRomname() {
        return romname;
    }

    public void setOrigConfig(OrigConfig origConfig) {
        this.origConfig = origConfig;
    }

    public OrigConfig getOrigConfig() {
        return origConfig;
    }

    //TODO
    public String getSystemSize() {
        return systemSize;
    }

    //TODO
    public void setSystemSize(String systemSize) {
        this.systemSize = systemSize;
    }

    //TODO
    public String getUserdataSize() {
        return userdataSize;
    }

    //TODO
    public void setUserdataSize(String userdataSize) {
        this.userdataSize = userdataSize;
    }

    private romListData() {

    }

    public static class RomItemPicture {
        enum PicType {PIC_TYPE_INNER, PIC_TYPE_EXTRA,}

        PicType type = PicType.PIC_TYPE_INNER;
        int inpic = 0;
        public Bitmap srcbitmap;

        public RomItemPicture(Resources resources, int pic_source) {
            type = PicType.PIC_TYPE_INNER;
            inpic = pic_source;
            srcbitmap = BitmapFactory.decodeResource(resources, pic_source);

        }

        public RomItemPicture(String picpath) {
            //TODO set rom pic
            type = PicType.PIC_TYPE_EXTRA;
        }

    }

}

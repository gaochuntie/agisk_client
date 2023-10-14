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
import atms.app.agiskclient.ConfigBox.PartitionAction;
import atms.app.agiskclient.ReservedAreaKits.ReservedAreaRepository;
import atms.app.agiskclient.ReservedAreaKits.ReservedAreaTools;
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
    List<String> filter=new ArrayList<>();
    String uuid;
    String mark;
    String description;
    String xml_path;
    boolean isReservedProtected=false;

    public boolean isReservedProtected() {
        return isReservedProtected;
    }


    /**
     * set isreservedprotected flag
     * true protected,so such action will be forbidden
     * Check DiskAction : format clone write
     * Check PartitionAction : new
     *
     * Warning : you should better not reserve area already used by some else
     * For example : you reserve a exist partition area
     * but The protection won't work because it's you break other's law
     *
     * SO I only implement PartitionAction new's reserve protection
     */
    public void setIsReservedProtected() {
        Log.d(TAG.ROM_LIST_DATA, "SetIsReservedProtected "+uuid);
        //check all disk related actions
        origConfig.setActionList();
        for (ActionBase actionBase : getOrigConfig().getActionList()) {
            Log.d(TAG.ROM_LIST_DATA, "Check 1");


            //DiskAction
            if (actionBase.actiontype == ActionBase.ActionType.ACTION_TYPE_DISK) {
                DiskAction diskAction = (DiskAction) actionBase;
                switch (diskAction.type) {
                    case DISK_ACTION_TYPE_FORMAT0:
                        Log.d(TAG.ROM_LIST_DATA, "Check format");
                        isReservedProtected =
                                ! ReservedAreaTools.checkArea(diskAction.driver
                                ,Long.valueOf(diskAction.argv[0])
                                ,Long.valueOf(diskAction.argv[1])
                                ,id);
                        break;
                    case DISK_ACTION_TYPE_WRITE:
                        Log.d(TAG.ROM_LIST_DATA, "Check write");
                        isReservedProtected =
                                ! ReservedAreaTools.checkArea(diskAction.driver
                                        ,Long.valueOf(diskAction.argv[0])
                                        ,Long.valueOf(diskAction.argv[1])
                                        ,id);
                        break;
                    case DISK_ACTION_TYPE_CLONE:
                        Log.d(TAG.ROM_LIST_DATA, "Check clone");
                        isReservedProtected =
                                ! ReservedAreaTools.checkArea(diskAction.driver
                                        ,Long.valueOf(diskAction.argv[3])
                                        ,Long.valueOf(diskAction.argv[2])
                                        ,id);
                        break;
                }
                continue;
            }

            //PartitionAction
            PartitionAction partitionAction = (PartitionAction) actionBase;

            switch (partitionAction.pt_type) {
                case PARTITION_ACTION_TYPE_NEW:
                    Log.d(TAG.ROM_LIST_DATA, "Check new part");
                    isReservedProtected =
                            !ReservedAreaTools.checkArea(partitionAction.driver
                                    , Long.valueOf(partitionAction.argv[1])
                                    , Long.valueOf(partitionAction.argv[2])
                                    , id);
                    break;
            }
        }
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
     * only attributions
     */
    public void initRomFromOrigConfig() {
        setId(origConfig.getAttributions().get("id"));
        setRomname(origConfig.getAttributions().get("name"));
        setAuthor(origConfig.getAttributions().get("author"));
        setUuid(origConfig.getAttributions().get("uuid"));
        setDescription(origConfig.getAttributions().get("description"));
        setXml_path(origConfig.getFile_path());
        setMark(origConfig.getAttributions().get("mark"));
        setFilter(origConfig.getAttributions().get("filter"));
    }

    private void setFilter(String filter_list) {
        String[] list = filter_list.split(";");
        for (String item : list) {
            if (!item.isEmpty()){
                filter.add(item.toLowerCase());
            }
        }
    }

    public List<String> getFilter() {
        return filter;
    }

    public boolean isMatchFilter(String _filter) {
        for (String item : filter) {
            if (_filter.toLowerCase().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMatchFilter(String filter_list_string, String _filter) {
        String[] m = filter_list_string.split(";");
        if (m != null) {
            for (String item : m) {
                if (item.toLowerCase().equals(_filter)) {
                    return true;
                }
            }
        }
        return false;
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
        public Bitmap srcbitmap=null;

        public RomItemPicture(Resources resources, int pic_source) {
            type = PicType.PIC_TYPE_INNER;
            inpic = pic_source;
            srcbitmap = BitmapFactory.decodeResource(resources, pic_source);

        }

        public RomItemPicture(String picpath) {
            //TODO set rom pic
            type = PicType.PIC_TYPE_EXTRA;
        }

        @Nullable
        public Bitmap getSrcbitmap() {
            return srcbitmap;
        }
    }

}

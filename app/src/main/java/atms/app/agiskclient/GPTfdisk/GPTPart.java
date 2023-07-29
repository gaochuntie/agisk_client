package atms.app.agiskclient.GPTfdisk;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.io.FilePermission;
import java.util.ArrayList;
import java.util.List;

import atms.app.agiskclient.Tools.TAG;

public class GPTPart extends DiskChunk{
    private int number;
    private String code;

    @NonNull
    private String name="";


    /**
     * Mount info
     * All mount_point are located in /mnt/
     * And have the same regx "apart_[part_num]_[part_name]"
     */
    private boolean isMounted=false;


    @Nullable
    private PartAttribute attribute;


    /**
     * Constructor
     *
     * @return
     */
    public GPTPart(String _driver, int _number, long _start_sector, long _end_sector) {
        super(_driver, _start_sector, _end_sector);
        super.driver=_driver;
        this.number=_number;
        super.startSector=_start_sector;
        super.endSector=_end_sector;
        setSize_sector(endSector-startSector+1);
    }
    public int getNumber() {
        return number;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getStartSector() {
        return startSector;
    }

    public void setStartSector(long startSector) {
        super.startSector = startSector;
    }

    public long getEndSector() {
        return endSector;
    }

    public void setEndSector(long endSector) {
        super.endSector = endSector;
    }

    public long getSize_sector() {
        return size_sector;
    }

    public void setSize_sector(long size_sector) {
        super.size_sector = size_sector;
    }

    @Nullable
    public PartType getPart_type() {
        return part_type;
    }

    public void setPart_type(PartType part_type) {
        super.part_type = part_type;
    }

    @Nullable
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setMounted(boolean mounted) {
        isMounted = mounted;
    }

    public boolean isMounted() {
        return isMounted;
    }

    public boolean checIskMounted() {

        List<String> resultList = new ArrayList<>();
        String mountPoint_t = "/mnt/agisk_" + number + "_" + name;
        Shell.cmd("grep -e \" "+mountPoint_t+" \" /proc/mounts >>/dev/null  && echo \"Y\" || echo \"N\"")
                .to(resultList).exec();
        String rts = resultList.get(resultList.size() - 1);
        if (rts.equals("Y")) {
            //mounted
            isMounted = true;
            Log.d(TAG.GPTPart_TAG, "Part mounted "+number+":"+name);
            return true;
        }
        isMounted = false;
        return false;
    }


    public String getMountPointString() {
        return "/mnt/agisk_" + number + "_" + name;
    }

    public void setAttribute(@Nullable PartAttribute attribute) {
        this.attribute = attribute;
    }

    @Nullable
    public PartAttribute getAttribute() {
        return attribute;
    }
}

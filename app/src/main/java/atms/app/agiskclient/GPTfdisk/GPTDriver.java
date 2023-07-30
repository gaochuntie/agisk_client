package atms.app.agiskclient.GPTfdisk;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GPTDriver {
    private List<DiskChunk> partList;
    private long sector_size;
    private long block_size;
    private String path;
    private int partLimit;
    private String GUID;
    private long total_size_sector=0;
    private long free_size_sector=0;

    public GPTDriver(String path, long sector_size, long block_size, int partLimit, String GUID) {
        this.path=path;
        this.sector_size=sector_size;
        this.block_size=block_size;
        this.partLimit=partLimit;
        this.GUID=GUID;
    }

    public void setTotal_size_sector(long total_size_sector) {
        this.total_size_sector = total_size_sector;
    }

    public long getTotal_size_sector() {
        return total_size_sector;
    }

    public void setFree_size_sector(long free_size_sector) {
        this.free_size_sector = free_size_sector;
    }

    public long getFree_size_sector() {
        return free_size_sector;
    }

    public String getGUID() {
        return GUID;
    }

    public int getPartLimit() {
        return partLimit;
    }

    public String getPath() {
        return path;
    }

    public long getSector_size() {
        return sector_size;
    }

    public long getBlock_size() {
        return block_size;
    }

    public void setPartList(List<DiskChunk> partList) {
        this.partList = partList;
    }

    @Nullable
    public List<DiskChunk> getPartList() {
        return partList;
    }

    public void addPart(DiskChunk part) {
        if (partList==null) {
            partList = new ArrayList<>();
        }
        //check ismounted
        if (part.getPart_type().getPartType() != PartType.Part_Type.TYPE_FREESPACE) {
            ((GPTPart) (part)).checIsMountedInner();
        }
        partList.add(part);
    }

    public boolean removePart(DiskChunk part) {
        if (partList == null) {
            return false;
        }
        return partList.remove(part);
    }
}

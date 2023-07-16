package atms.app.agiskclient.GPTfdisk;

import androidx.annotation.Nullable;

public class DiskChunk {
    protected long startSector;
    protected long endSector;
    protected long size_sector;
    protected PartType part_type;

    protected String driver;


    /**
     * Constructor
     *
     * @return
     */
    public DiskChunk(String _driver, long _start_sector, long _end_sector) {
        this.driver=_driver;
        this.startSector=_start_sector;
        this.endSector=_end_sector;
        setSize_sector(endSector-startSector+1);
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }


    public long getStartSector() {
        return startSector;
    }

    public void setStartSector(long startSector) {
        this.startSector = startSector;
    }

    public long getEndSector() {
        return endSector;
    }

    public void setEndSector(long endSector) {
        this.endSector = endSector;
    }

    public long getSize_sector() {
        return size_sector;
    }

    public void setSize_sector(long size_sector) {
        this.size_sector = size_sector;
    }

    @Nullable
    public PartType getPart_type() {
        return part_type;
    }

    public void setPart_type(PartType part_type) {
        this.part_type = part_type;
    }

}

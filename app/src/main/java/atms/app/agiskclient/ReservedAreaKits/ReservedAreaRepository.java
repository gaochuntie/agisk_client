package atms.app.agiskclient.ReservedAreaKits;

import java.util.ArrayList;
import java.util.List;

public class ReservedAreaRepository {
    //init at application
    public static List<Driver> driverList=null;

    public static class Driver{
        public String dev = null;
        List<Chunk> chunkList = null;
        List<Chunk> chunkListOrig = null;

        public String getDev() {
            return dev;
        }

        public Driver(String dev) {
            this.dev = dev;
            chunkList = new ArrayList<>();
            chunkListOrig = new ArrayList<>();
        }

        public void putChunk(Chunk chunk) {
            //TODO implement putChunk

        }

        public void putArea(long start, long length,String id) {
            Chunk chunk = new Chunk(start, length, id);
            putChunk(chunk);
        }

        /**
         *
         * @param addr
         * @param id
         * @return true allow
         */
        public boolean checkByte(long addr,String id) {

            for (Chunk chunk : chunkList) {
                if (addr >= chunk.startByte && addr <= (chunk.startByte + chunk.lengthByte - 1)) {
                    if (id.equals(chunk.id)) {
                        return true;
                    }else{
                        //protect hit
                        //TODO Hit
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean checkArea(long start, long length,String id) {
            for (Chunk chunk : chunkList) {

                //sense1 inside
                if (start >= chunk.startByte) {
                    if ((start + length - 1) <= chunk.endByte) {
                        if (id.equals(chunk.id)) {
                            return true;
                        }else{
                            //protect hit
                            return false;
                        }
                    }
                }

                //sense2 front overlap
                if (chunk.startByte >= start && chunk.startByte <= (start + length - 1)) {
                    if (id.equals(chunk.id)) {
                        return true;
                    }else{
                        //hit
                        return false;
                    }
                }

                //sense3 tail overlap
                if (chunk.endByte >= start && chunk.endByte <= (start + length - 1)) {
                    if (id.equals(chunk.id)) {
                        return true;
                    }else{
                        //hit
                        return false;
                    }
                }

                //sense4 hole overlap
                if (chunk.startByte >= start && chunk.endByte <= (start + length - 1)) {
                    if (id.equals(chunk.id)) {
                        return true;
                    } else {
                        //hit
                        return false;
                    }
                }
            }
            return true;
        }

    }
    public static class Chunk{
        public long startByte=0;
        public long lengthByte=0;
        public long endByte = 0;
        public long startSector = 0;
        public long lengthSector = 0;
        public String id="";

        public Chunk(long startByte, long lengthByte,String id) {
            this.startByte = startByte;
            this.lengthByte = lengthByte;
            this.endByte = startByte + lengthByte - 1;
            this.id = id;
        }
        public Chunk(long startByte, long lengthByte,String id,long sector_size) {
            this.startByte = startByte;
            this.lengthByte = lengthByte;
            this.endByte = startByte + lengthByte - 1;
            this.id = id;
            this.startSector = (long) startByte / sector_size;
            this.lengthSector = (long) ((lengthByte + sector_size - 1) / sector_size);
        }
    }
}


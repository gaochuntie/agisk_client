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


        public void putChunk(Chunk chunke) {
            chunkListOrig.add(chunke);

        }

        public void putChunk1(Chunk chunke) {
            //TODO implement putChunk
            chunkListOrig.add(chunke);

            //sense6 at tail
            Chunk tail = chunkList.get(chunkList.size() - 1);
            if (chunke.startByte >= tail.startByte) {
                //no overlap
                if (chunke.startByte > tail.endByte) {
                    chunkList.add(chunke);
                    return;
                }
                //inside
                if (chunke.endByte <= tail.endByte) {
                    return;
                }
                //overlap
                tail.endByte = chunke.endByte;
                return;
            }

            //sense7 at head
            Chunk head = chunkList.get(0);
            if (chunke.endByte <= head.endByte) {
                //no overlap
                if (chunke.endByte < head.startByte) {
                    chunkList.add(0,chunke);
                    return;
                }
                //inside
                if (chunke.startByte >= head.startByte) {
                    return;
                }
                //overlap
                head.startByte = chunke.startByte;
                return;
            }

            //

            for (int i = 0; i < chunkList.size()-1; i++) {
                Chunk former = chunkList.get(i);
                Chunk later = chunkList.get(i + 1);
                //sense1 no overlap
                if (chunke.startByte > former.endByte && chunke.endByte < later.startByte) {
                    chunkList.add(i + 1, chunke);
                    return;
                }

                Chunk chunk = former;
                //sense1 inside
                if (chunke.startByte >= chunk.startByte) {
                    if (chunke.endByte <= chunk.endByte) {
                        //inside
                        return;
                    }
                }

                //sense2 former overlap
                if (chunke.startByte>= chunk.startByte && chunke.startByte<=chunk.endByte) {
                    if (chunke.endByte > chunk.endByte && chunke.endByte < later.startByte) {
                        //combine
                        former.endByte = chunke.endByte;
                        return;
                    }
                }

                //sense3 later overlap
                if (chunke.startByte> former.endByte && chunke.startByte<later.startByte) {
                    if (chunke.endByte >= later.startByte && chunke.endByte <= later.endByte) {
                        //combine
                        later.startByte = chunke.startByte;
                        return;
                    }
                }

                //sense4 former and later overlap
                if (chunke.startByte>= former.startByte && chunke.startByte<=former.endByte) {
                    if (chunke.endByte >= later.startByte && chunke.endByte <= later.endByte) {
                        //combine
                        former.endByte= later.endByte;
                        chunkList.remove(i + 1);
                        return;
                    }
                }

                //sense5 overlap multiple chunks


            }

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
            List<Chunk> overlaped = new ArrayList<>();

            for (Chunk chunk : chunkListOrig) {
                if (addr >= chunk.startByte && addr <= chunk.endByte) {
                    if (id.equals(chunk.id)) {
                        return true;
                    }else{
                        //protect hit
                        //TODO Hit
                        overlaped.add(chunk);
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean checkArea(long start, long length,String id) {
            List<Chunk> overlaped = new ArrayList<>();
            for (Chunk chunk : chunkListOrig) {

                //sense1 inside
                if (start >= chunk.startByte) {
                    if ((start + length - 1) <= chunk.endByte) {
                        if (id.equals(chunk.id)) {
                            return true;
                        }else{
                            //protect hit
                            overlaped.add(chunk);
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
                        overlaped.add(chunk);
                        return false;
                    }
                }

                //sense3 tail overlap
                if (chunk.endByte >= start && chunk.endByte <= (start + length - 1)) {
                    if (id.equals(chunk.id)) {
                        return true;
                    }else{
                        //hit
                        overlaped.add(chunk);
                        return false;
                    }
                }

                //sense4 hole overlap
                if (chunk.startByte >= start && chunk.endByte <= (start + length - 1)) {
                    if (id.equals(chunk.id)) {
                        return true;
                    } else {
                        //hit
                        overlaped.add(chunk);
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

        //linked list
        public Chunk next = null;
        public Chunk prev = null;

        public Chunk getNext() {
            return next;
        }

        public void setNext(Chunk next) {
            this.next = next;
        }

        public Chunk getPrev() {
            return prev;
        }

        public void setPrev(Chunk prev) {
            this.prev = prev;
        }

        public Chunk(long startByte, long lengthByte, String id) {
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


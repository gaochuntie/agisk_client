package atms.app.agiskclient.ReservedAreaKits;

import android.util.Log;

import java.util.ArrayList;

import atms.app.agiskclient.ReservedAreaKits.ReservedAreaRepository.Driver;
import atms.app.agiskclient.Tools.TAG;

public class ReservedAreaTools {
    //init at application
    public static void initRepository() {
        if (ReservedAreaRepository.driverList == null) {
            ReservedAreaRepository.driverList = new ArrayList<>();
            return;
        }
        ReservedAreaRepository.driverList.clear();
    }

    //release static list when application exit
    public static void releaseRespository() {
        if (ReservedAreaRepository.driverList != null) {
            ReservedAreaRepository.driverList = null;
            return;
        }
    }

    //blank list
    public static void blankRepository() {
        releaseRespository();
        initRepository();
    }
    //Debug

    public static void dPrintAllReserved() {
        if (ReservedAreaRepository.driverList != null) {
            for (Driver driver_item : ReservedAreaRepository.driverList) {
                if (driver_item.chunkListOrig != null) {
                    for (ReservedAreaRepository.Chunk chunk : driver_item.chunkListOrig) {
                        Log.d(TAG.RESERVED_AREA_TOOLS, "RESERVED:\n"
                                + "id " + chunk.id + "\n"
                                + "driver " + driver_item.getDev() + "\n"
                                + "start " + chunk.startByte + "\n"
                                + "end " + chunk.endByte + "\n"
                                + "length " + chunk.lengthByte+"\n");
                    }
                }
            }
        }
    }

    public static boolean putArea(String driver, long start, long length, String id) {

        if (ReservedAreaRepository.driverList == null) {
            return false;
        }
        for (Driver driver_item : ReservedAreaRepository.driverList) {
            if (driver_item.getDev().equals(driver)) {
                driver_item.putArea(start, length, id);
                return true;
            }
        }
        //newly add driver
        Driver driver_new = new Driver(driver);
        ReservedAreaRepository.driverList.add(driver_new);
        driver_new.putArea(start, length, id);

        return true;
    }

    /**
     * Check if you can edit the byte
     * @param driver
     * @param addr
     * @param id
     * @return true allow,false denied
     */
    public static boolean checkByte(String driver, long addr,String id) {
        if (ReservedAreaRepository.driverList == null) {
            //Before init,refuse all
            return false;
        }
        for (Driver driver_item : ReservedAreaRepository.driverList) {
            if (driver_item.getDev().equals(driver)) {
                return driver_item.checkByte(addr, id);
            }
        }
        return true;
    }

    public static boolean checkArea(String driver, long start, long length,String id) {
        if (ReservedAreaRepository.driverList == null) {
            //Before init,refuse all
            return false;
        }
        for (Driver driver_item : ReservedAreaRepository.driverList) {
            if (driver_item.getDev().equals(driver)) {
                return driver_item.checkArea(start, length, id);
            }
        }
        return true;
    }
}

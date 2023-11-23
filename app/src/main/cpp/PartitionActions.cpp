//
// Created by jackmaxpale on 2022/10/15.
//

#include "PartitionActions.h"
#include <jni.h>
#include "include/gpt/gpt.h"
#include <android/log.h>
#include "MyLog.h"
#include "SingleTools.h"
#include <sys/mount.h>
#include <errno.h>
#include <dirent.h>
#include <sstream>

/**
 * partition create1
 * tested
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2ILjava_lang_String_2JJ(
        JNIEnv *env, jobject thiz, jstring driver, jint number, jstring name, jlong start,
        jlong length) {
    appendLogCutLine(PARTITION_LOG, "PARTITION CREATE1");
    const char *name_c = env->GetStringUTFChars(name, nullptr);
    string name_s(name_c);
    env->ReleaseStringUTFChars(name, name_c);

    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);

    uint32_t usedPartNum = -1;

    /**
     *  sgdisk use number starting from 1 not 0
     * but gpt lib start from 0
     * we take gpt lib
     */

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }
    if (!gptData.IsFreePartNum(number)) {
        appendBaseLog(PARTITION_LOG, "Partition num " + to_string(number) + " is used");
        return 1;
    }
    uint64_t start_sector = (uint64_t) (start / gptData.GetBlockSize());
    uint64_t last_sector = 0;

    /**
     * 0 to take the largest chunk
     */
    if (length == 0) {
        appendBaseLog(PARTITION_LOG, "Length is 0 , take the largest chunk");
        appendBaseLog(PARTITION_LOG, "Length " + to_string(
                (last_sector - start_sector + 1) * gptData.GetBlockSize()));
        last_sector = gptData.FindLastInFree(start_sector);
    } else {
        last_sector = (uint64_t) (start + length - 1) / gptData.GetBlockSize();
    }

    for (uint64_t i = start_sector; i <= last_sector; ++i) {
        if (!gptData.IsFree(i, &usedPartNum)) {
            appendBaseLog(PARTITION_LOG,
                          "Required area is used by other partition " + to_string(usedPartNum));
            return 1;
        }
    }
    if (gptData.CreatePartition(number, start_sector, last_sector)) {
        if (!gptData.SetName(number, name_s)) {
            appendBaseLog(PARTITION_LOG, "Unable to set name");
            return 1;
        }
        gptData.JustLooking(0);
        if (!gptData.SaveGPTData(1)) {
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        appendBaseLog(PARTITION_LOG, "Done");
        return 0;
    }
    appendBaseLog(PARTITION_LOG, "Create partition failed.");
    return 1;

}
/**
 * partition create2
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2Ljava_lang_String_2JJ(
        JNIEnv *env, jobject thiz, jstring driver, jstring name, jlong start, jlong length) {
    appendLogCutLine(PARTITION_LOG, "PARTITION CREATE2");
    const char *name_c = env->GetStringUTFChars(name, nullptr);
    string name_s(name_c);
    env->ReleaseStringUTFChars(name, name_c);

    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);
    uint32_t usedPartNum = -1;


    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }

    uint64_t number = gptData.FindFirstFreePart();
    if (number == -1) {
        appendBaseLog(PARTITION_LOG, "All partition num " + to_string(number) + " is used");
        return 1;
    }
    uint64_t start_sector = (uint64_t) (start / gptData.GetBlockSize());
    uint64_t last_sector = 0;

    /**
     * 0 to take the largest chunk
     */
    if (length == 0) {
        appendBaseLog(PARTITION_LOG, "Length is 0 , take the largest chunk");
        appendBaseLog(PARTITION_LOG, "Length " + to_string(
                (last_sector - start_sector + 1) * gptData.GetBlockSize()));
        last_sector = gptData.FindLastInFree(start_sector);
    } else {
        last_sector = (uint64_t) (start + length - 1) / gptData.GetBlockSize();
    }

    for (uint64_t i = start_sector; i <= last_sector; ++i) {
        if (!gptData.IsFree(i, &usedPartNum)) {
            appendBaseLog(PARTITION_LOG,
                          "Required area is used by other partition " + to_string(usedPartNum));
            return 1;
        }
    }


    if (gptData.CreatePartition(number, start_sector, last_sector)) {
        if (!gptData.SetName(number, name_s)) {
            appendBaseLog(PARTITION_LOG, "Unable to set name");
            return 1;
        }
        gptData.JustLooking(0);
        if (!gptData.SaveGPTData(1)) {
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        appendBaseLog(PARTITION_LOG, "Done");
        return 0;
    }

    appendBaseLog(PARTITION_LOG, "Create partition failed.");
    return 1;
}

/**
 * partition create3
 * tested
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2Ljava_lang_String_2J(
        JNIEnv *env, jobject thiz, jstring driver, jstring name, jlong length) {
    appendLogCutLine(PARTITION_LOG, "PARTITION CREATE3");
    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);

    const char *name_c = env->GetStringUTFChars(name, nullptr);
    string name_s(name_c);
    env->ReleaseStringUTFChars(name, name_c);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }
    uint64_t number = gptData.FindFirstFreePart();
    if (number == -1) {
        appendBaseLog(PARTITION_LOG, "All partition num " + to_string(number) + " is used");
        return 1;
    }

    ////////////////////////////////////////
    /**
     * find available segement for required length
     * from gpt lib
     */
    uint64_t diskSize = gptData.GetLastUsableLBA();


    //calculate requiredBlocks
    //This is a correct compulation
    uint64_t requiredBlocks = ((uint64_t) (length + gptData.GetBlockSize() - 1) /
                               gptData.GetBlockSize());

    appendBaseLog(PARTITION_LOG, "Required blocks : " + to_string(requiredBlocks));

    uint64_t start = UINT64_C(0); // starting point for each search
    uint64_t totalFound = UINT64_C(0); // running total
    uint64_t firstBlock = 0; // first block in a segment
    uint64_t lastBlock = 0; // last block in a segment
    uint64_t segmentSize = 0; // size of segment in blocks
    uint32_t num = 0;
    uint32_t numSegments = 0;
    uint64_t largestSegment = 0;
    uint64_t smallestSegement = 0;
    uint32_t foundNum = 0;
    uint64_t smallest_start = 0;
    uint64_t smallest_end = 0;
    uint64_t largest_start = 0;
    uint64_t largest_end = 0;

    if (diskSize > 0) {
        do {
            firstBlock = gptData.FindFirstAvailable(start);
            if (firstBlock != UINT64_C(0)) { // something's free...
                lastBlock = gptData.FindLastInFree(firstBlock);

                segmentSize = lastBlock - firstBlock + UINT64_C(1);

                if (segmentSize >= requiredBlocks) {
                    //ok find one

                    //largest
                    if (segmentSize > largestSegment) {
                        largestSegment = segmentSize;
                        largest_start = firstBlock;
                        largest_end = lastBlock;
                    }

                    foundNum++;
                    appendBaseLog(PARTITION_LOG,
                                  "Find available segement(sector) : " + to_string(firstBlock) +
                                  " to " + to_string(lastBlock) + " [" + to_string(segmentSize) +
                                  "]");
                    if (smallestSegement == 0) {
                        smallestSegement = segmentSize;
                        smallest_start = firstBlock;
                        smallest_end = lastBlock;
                    } else if (segmentSize < smallestSegement) {
                        smallestSegement = segmentSize;
                        smallest_start = firstBlock;
                        smallest_end = lastBlock;
                    }
                } // if
                totalFound += segmentSize;
                num++;
                start = lastBlock + 1;
            } // if
        } while (firstBlock != 0);
    } // if
    numSegments = num;

    if (foundNum < 1) {
        appendBaseLog(PARTITION_LOG, "No free segement match");
        return 1;
    }

    if (length != 0) {
        appendBaseLog(PARTITION_LOG, "Smallest free segement is " + to_string(smallestSegement));
        appendBaseLog(PARTITION_LOG,
                      "From " + to_string(smallest_start) + " to " +
                      to_string(smallest_start + requiredBlocks - 1));
        appendLogCutLine(PARTITION_LOG, "Take the smallest free segement");
    } else {
        appendBaseLog(PARTITION_LOG, "Length is 0 , take the largest chunk");
        appendBaseLog(PARTITION_LOG,
                      "Start " + to_string(largest_start) + " Length " + to_string(largestSegment));
    }

    //create partition
    int result = 1;

    if (length == 0) {
        result = gptData.CreatePartition(number, largest_start,
                                         largest_end);
    } else {
        result = gptData.CreatePartition(number, smallest_start,
                                         smallest_start + requiredBlocks - 1);
    }
    if (result) {
        if (!gptData.SetName(number, name_s)) {
            appendBaseLog(PARTITION_LOG, "Unable to set name");
            return 1;
        }
        gptData.JustLooking(0);
        if (!gptData.SaveGPTData(1)) {
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        appendBaseLog(PARTITION_LOG, "Done");
        return 0;
    }
    appendBaseLog(PARTITION_LOG, "Create partition failed.");
    return 1;
}

/**
 * delete1
 * tested
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_delete__Ljava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env, jobject thiz, jstring driver, jstring name) {
    appendLogCutLine(PARTITION_LOG, "PARTITION DELETE1");
    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);

    const char *name_c = env->GetStringUTFChars(name, nullptr);
    string name_s(name_c);
    env->ReleaseStringUTFChars(name, name_c);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }

    uint32_t foundedNumPart = 0;
    uint32_t foundedPart = 0;

    for (uint32_t i = 0; i < gptData.GetNumParts(); ++i) {
        GPTPart part = gptData[i];
        if (part.IsUsed() && part.GetDescription() == name_s) {
            foundedNumPart++;
            foundedPart = i;
            appendBaseLog(PARTITION_LOG, "Found " + to_string(i));
        }
    }
    if (foundedNumPart == 0) {
        appendBaseLog(PARTITION_LOG, "No such partition " + name_s);
        return 1;
    }
    if (foundedNumPart > 1) {
        appendBaseLog(PARTITION_LOG,
                      "More than one partition called " + name_s + " | " +
                      to_string(foundedNumPart));
        return 1;
    }

    gptData.DeletePartition(foundedPart);
    if (((GPTPart) gptData[foundedPart]).IsUsed()) {
        appendBaseLog(PARTITION_LOG, "Delete partition failed");
        return 1;
    }
    gptData.JustLooking(0);
    if (!gptData.SaveGPTData(1)) {
        appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
        return 1;
    }
    appendBaseLog(PARTITION_LOG, "Done");
    return 0;
}
/**
 * delete2
 * tested
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_delete__Ljava_lang_String_2I(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jstring driver,
                                                                                 jint number) {
    appendLogCutLine(PARTITION_LOG, "PARTITION DELETE2");
    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }

    if (gptData.IsUsedPartNum(number)) {
        if (!gptData.DeletePartition(number)) {
            appendBaseLog(PARTITION_LOG, "Delete failed " + to_string(number));
            return 1;
        }
        gptData.JustLooking(0);
        if (!gptData.SaveGPTData(1)) {
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        appendBaseLog(PARTITION_LOG, "Done");
        return 0;
    }
    appendBaseLog(PARTITION_LOG, "Partition not exist. " + to_string(number));
    return 1;


}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_clone(JNIEnv *env, jobject thiz,
                                                          jstring driver,
                                                          jstring from_driver,
                                                          jint from_number, jlong t_start) {
    // TODO: implement clone()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_mount(JNIEnv *env, jobject thiz,
                                                          jstring driver, jint number,
                                                          jstring filesystem,
                                                          jstring mount_point) {
    appendLogCutLine(PARTITION_LOG, "PARTITION MOUNT");
    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    const char *filesystem_c = env->GetStringUTFChars(filesystem, nullptr);
    const char *dir_c = env->GetStringUTFChars(mount_point, nullptr);
    string driver_s(driver_c);
    string fs_s(filesystem_c);
    string dir_s(dir_c);

    env->ReleaseStringUTFChars(driver, driver_c);
    env->ReleaseStringUTFChars(filesystem, filesystem_c);
    env->ReleaseStringUTFChars(mount_point, dir_c);

    //get special device
    driver_s = driver_s + to_string(number);
    appendBaseLog(PARTITION_LOG, "Mounting device " + driver_s);

    DIR *dir = opendir(dir_s.c_str());
    if (dir) {
        /* Directory exists. */
        closedir(dir);
    } else if (ENOENT == errno) {
        string command = string("mkdir -p ") + dir_s.c_str();
        system(command.c_str());
    } else {
        /* opendir() failed for some other reason. */
    }
    int ret = mount(driver_s.c_str(), dir_s.c_str(), fs_s.c_str(), MS_MGC_VAL, "");
    if (!ret) {
        appendBaseLog(PARTITION_LOG, "Mount " + driver_s + " to " + dir_s + " Successfully");
        return 0;
    }
    switch (errno) {
        case EPERM:
            appendBaseLog(PARTITION_LOG, "Permission denied");
            break;
        case ENODEV:
            appendBaseLog(PARTITION_LOG, "Unsupported filesystem type " + fs_s);
            break;
        case ENOTBLK:
            appendBaseLog(PARTITION_LOG, "Not block device");
            break;
        case EBUSY:
            appendBaseLog(PARTITION_LOG, "Device or mount point busy");
            break;
        case EINVAL:
            appendBaseLog(PARTITION_LOG, "EINVAL");
            break;
        case EACCES:
            appendBaseLog(PARTITION_LOG, "EACCESS");
            break;
        case EMFILE:
            appendBaseLog(PARTITION_LOG, "EMFILE");
            break;
    }
    return -1;

}


extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_readInfo(JNIEnv *env, jobject thiz,
                                                             jstring device) {
    appendLogCutLine(PARTITION_DUMP_LOG, "PARTITION DUMP");

    const char *devname_C = env->GetStringUTFChars(device, nullptr);
    string devname_s(devname_C);
    env->ReleaseStringUTFChars(device, devname_C);
    GPTData gptdata(devname_s);
    ////////////////////////////////////////////
    gptdata.JustLooking();

    uint32_t MainHeaderLBA = gptdata.GetMainHeaderLBA();
    uint32_t BlockSize = gptdata.GetBlockSize();

    uint32_t num = gptdata.GetNumParts();

    //Declare datas

    uint32_t low, high;
    uint32_t NumParts = gptdata.GetNumParts();
    uint32_t CountParts = gptdata.CountParts();
    uint32_t sector_size = gptdata.GetBlockSize();

    // A 4mib area reserved for atms
    // this area take up a partnum if possible
    // If you want to have some partitions cloned more than 1 time,this area is necessary
    //or it is not requested.

    // Because each clone will hide orig partitions,so We can lost cloned partitions' situation without a recorde area!!!
    //SO it comes with atms metadata
    //Actions about atms metadata are all defined in Action.hpp,You can modify it to your format
    // Also you can resize this area at line below...
    // atmsMetedataSizeBlock defined in Utils.hpp -extern

    gptdata.GetPartRange(&low, &high);
    appendBaseLog(PARTITION_DUMP_LOG, "Driver " + devname_s);
    appendBaseLog(PARTITION_DUMP_LOG, "Sector size : " + to_string(gptdata.GetBlockSize()));

    appendBaseLog(PARTITION_DUMP_LOG, "[BEGIN]");
    appendBaseLog(PARTITION_DUMP_LOG, "Number:Name:StartLBA:EndLBA:SizeInMib");
    for (uint32_t i = low; i <= high; i++) {
        /* code */
        if (gptdata.IsUsedPartNum(i)) {
            //cout<<"Operatoring "<<i<<"      ";
            GPTPart part = gptdata[i];
            appendBaseLog(PARTITION_DUMP_LOG, to_string(i) + " : "
                                              + part.GetDescription() + " : "
                                              + to_string(part.GetFirstLBA()) + " : "
                                              + to_string(part.GetLastLBA()) + " : "
                                              + to_string(
                    part.GetLengthLBA() * gptdata.GetBlockSize() / (1024 * 1024)) + "Mib");
        }

    }
    appendBaseLog(PARTITION_DUMP_LOG, "[END]");
    appendBaseLog(PARTITION_LOG, "Done");
    return 0;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_getPartListString(JNIEnv *env, jobject thiz,
                                                             jstring device) {
    appendLogCutLine(PARTITION_LOG, "PARTITION DUMP - Direct Function 1 ");

    const char *devname_C = env->GetStringUTFChars(device, nullptr);
    string devname_s(devname_C);
    env->ReleaseStringUTFChars(device, devname_C);
    GPTData gptdata(devname_s);
    GPTData &gptData = gptdata;
    ////////////////////////////////////////////
    gptdata.JustLooking();

    //get total free

    uint64_t start = UINT64_C(0); // starting point for each search
    uint64_t totalFound = UINT64_C(0); // running total
    uint64_t firstBlock = 0; // first block in a segment
    uint64_t lastBlock = 0; // last block in a segment
    uint64_t segmentSize = 0; // size of segment in blocks
    uint32_t num = 0;
    uint64_t largest_free=UINT64_C(0);
    uint64_t total_free = gptdata.FindFreeBlocks(&num, &largest_free);

    /**
   * return format
   * {phy_sector:logic_sector:GUID:partLimit:total:free:part_align}{number:name:start:end:code:typeGUID}
   */
    stringstream rts;
    rts << "{" << gptdata.GetDisk()->GetPhysBlockSize();
    rts << ":" << gptdata.GetBlockSize();
    rts << ":" << gptdata.GetDiskGUID().AsString();
    rts << ":" << gptdata.GetNumParts();
    rts << ":" << gptdata.GetLastUsableLBA() + 1;
    rts << ":" << total_free;
    rts << ":" << gptdata.GetAlignment();
    rts << "}";


    /**
     * add free segment
     */
    do {
        firstBlock = gptData.FindFirstAvailable(start);
        if (firstBlock != UINT64_C(0)) { // something's free...
            lastBlock = gptData.FindLastInFree(firstBlock);

            segmentSize = lastBlock - firstBlock + UINT64_C(1);

            /**
             * add free segment
             * USE parttype "FREESPACE"
             */
            rts << "{";
            rts << 0;
            rts << ":";
            rts << "*FREESPACE";
            rts << ":";
            rts << firstBlock;
            rts << ":";
            rts << lastBlock;
            rts << ":";
            rts << "0000";
            rts << ":";
            rts << "*FREESPACE";
            rts << "}";

            totalFound += segmentSize;
            num++;
            start = lastBlock + 1;
        } // if
    } while (firstBlock != 0);



    /**
     * add part
     */
    uint32_t low, high;
    gptdata.GetPartRange(&low, &high);

    //{number:name:start:end:code:typeGUID}
    for (uint32_t i = low; i <= high; i++) {
        if (gptdata.IsUsedPartNum(i)) {
            GPTPart part = gptdata[i];
            rts << "{";
            rts << i;
            rts << ":";
            rts << part.GetDescription();
            rts << ":";
            rts << part.GetFirstLBA();
            rts << ":";
            rts << part.GetLastLBA();
            rts << ":";
            rts << part.GetHexType();
            rts << ":";
            rts << part.GetUniqueGUID();
            rts << "}";
        }
    }
    string rt_value = rts.str();
    appendBaseLog(PARTITION_LOG, "GetPartList : " + rt_value);

    jstring jstring1 = env->NewStringUTF(rt_value.c_str());
    return jstring1;
}



/**
 * Notice ! In c/c++ 1 or upper = true, 0 = false
 */
extern "C"
JNIEXPORT jboolean JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_deletePart(JNIEnv *env, jobject thiz, jstring driver,
                                                      jint number) {
    appendLogCutLine(PARTITION_LOG, "PARTITION DELETE2 - Direct Function 2 ");
    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }

    if (gptData.IsUsedPartNum(number)) {
        if (!gptData.DeletePartition(number)) {
            appendBaseLog(PARTITION_LOG, "Delete failed " + to_string(number));
            return 1;
        }
        gptData.JustLooking(0);
        if (!gptData.SaveGPTData(1)) {
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        appendBaseLog(PARTITION_LOG, "Done");
        return 0;
    }
    appendBaseLog(PARTITION_LOG, "Partition not exist. " + to_string(number));
    return 1;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_newPart(JNIEnv *env, jobject thiz, jstring driver,
                                                   jlong start, jlong end, jstring code,
                                                   jstring name) {
    appendLogCutLine(PARTITION_LOG, "PARTITION NEW3 - Direct Function 3 ");
    const char *name_c = env->GetStringUTFChars(name, nullptr);
    string name_s(name_c);
    env->ReleaseStringUTFChars(name, name_c);

    const char *code_c = env->GetStringUTFChars(code, nullptr);
    string code_s(code_c);
    env->ReleaseStringUTFChars(code, code_c);

    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);
    uint32_t usedPartNum = -1;

    long length = end - start + 1;


    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }

    uint64_t number = gptData.FindFirstFreePart();
    if (number == -1) {
        appendBaseLog(PARTITION_LOG, "All partition num " + to_string(number) + " is used");
        return 1;
    }
    uint64_t start_sector = (uint64_t) (start / gptData.GetBlockSize());
    uint64_t last_sector = 0;

    /**
     * 0 to take the largest chunk
     */
    if (length == 0) {
        appendBaseLog(PARTITION_LOG, "Length is 0 , take the largest chunk");
        appendBaseLog(PARTITION_LOG, "Length " + to_string(
                (last_sector - start_sector + 1) * gptData.GetBlockSize()));
        last_sector = gptData.FindLastInFree(start_sector);
    } else {
        last_sector = (uint64_t) (start + length - 1) / gptData.GetBlockSize();
    }

    for (uint64_t i = start_sector; i <= last_sector; ++i) {
        if (!gptData.IsFree(i, &usedPartNum)) {
            appendBaseLog(PARTITION_LOG,
                          "Required area is used by other partition " + to_string(usedPartNum));
            return 1;
        }
    }


    if (gptData.CreatePartition(number, start_sector, last_sector)) {
        if (!gptData.SetName(number, name_s)) {
            appendBaseLog(PARTITION_LOG, "Unable to set name");
            return 1;
        }
        gptData.JustLooking(0);
        if (!gptData.SaveGPTData(1)) {
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        appendBaseLog(PARTITION_LOG, "Done");

        if (formatPartition(driver_s,code_s,name_s,number)){
            appendBaseLog(PARTITION_LOG, "Unable to format partition " + driver_s + to_string(number));
        }
        return 0;
    }

    appendBaseLog(PARTITION_LOG, "Create partition failed.");
    return 1;
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_rename__Ljava_lang_String_2ILjava_lang_String_2(
        JNIEnv *env, jobject thiz, jstring driver, jint partition_num, jstring new_name) {
    appendLogCutLine(PARTITION_LOG, "PARTITION RENAME1");
    const char *driver_s = env->GetStringUTFChars(driver, nullptr);
    const char *new_name_s = env->GetStringUTFChars(new_name, nullptr);
    string driver_ss(driver_s);
    string new_name_ss(new_name_s);
    env->ReleaseStringUTFChars(driver, driver_s);
    env->ReleaseStringUTFChars(new_name, new_name_s);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_ss);
        return 1;
    }
    if(!gptData.SetName(partition_num, new_name_ss)){
        appendBaseLog(PARTITION_LOG, "Failed to change name : " + partition_num);
        return 1;
    }
    gptData.JustLooking(0);
    if (!gptData.SaveGPTData(1)) {
        appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
        return 1;
    }
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_rename__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env, jobject thiz, jstring driver, jstring old_name, jstring new_name) {
    appendLogCutLine(PARTITION_LOG, "PARTITION RENAME2");
    const char *driver_s = env->GetStringUTFChars(driver, nullptr);
    const char *new_name_s = env->GetStringUTFChars(new_name, nullptr);
    const char *old_name_s = env->GetStringUTFChars(old_name, nullptr);

    string old_name_ss(old_name_s);
    string driver_ss(driver_s);
    string new_name_ss(new_name_s);
    env->ReleaseStringUTFChars(driver, driver_s);
    env->ReleaseStringUTFChars(new_name, new_name_s);
    env->ReleaseStringUTFChars(old_name, old_name_s);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_ss);
        return 1;
    }
    uint32_t part_num=0;
    uint32_t last_part=0;
    gptData.GetPartRange(&part_num, &last_part);
    for (; part_num <= last_part; part_num++) {
        if (gptData.IsUsedPartNum(part_num)) {
            GPTPart part = gptData.operator[](part_num);
            if (strcmp(part.GetDescription().c_str(), old_name_ss.c_str())) {
                if(!gptData.SetName(part_num, new_name_ss)){
                    appendBaseLog(PARTITION_LOG, "Failed to change name : " + part_num);
                    return 1;
                }
                gptData.JustLooking(0);
                if (!gptData.SaveGPTData(1)) {
                    appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
                    return 1;
                }
                return 0;
            }
        }
    }
    return 1;
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_resize_1table(JNIEnv *env, jobject thiz,
                                                                  jstring driver, jint new_size) {
    appendLogCutLine(PARTITION_LOG, "PARTITION RESIZE_TABLE");
    const char *driver_s = env->GetStringUTFChars(driver, nullptr);
    string driver_ss(driver_s);
    env->ReleaseStringUTFChars(driver, driver_s);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_ss);
        return 1;
    }

    uint32_t part_num=0;
    uint32_t last_part=0;
    gptData.GetPartRange(&part_num, &last_part);
    if (new_size <= last_part) {
        appendBaseLog(PARTITION_LOG, "Refused to resize table to such size : too small");
        return 1;
    }
    if (!gptData.SetGPTSize(new_size)) {
        appendBaseLog(PARTITION_LOG, "Failed to resize table");
        return 1;
    }
    gptData.JustLooking(0);
    if (!gptData.SaveGPTData(1)) {
        appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
        return 1;
    }
    return 0;

}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_backup_1table(JNIEnv *env, jobject thiz,
                                                                  jstring driver, jstring path) {

    appendLogCutLine(PARTITION_LOG, "PARTITION BACKUP_TABLE");
    const char* driver_s = env->GetStringUTFChars(driver, nullptr);
    const char* path_s = env->GetStringUTFChars(path, nullptr);
    string driver_ss(driver_s);
    string path_ss(path_s);
    env->ReleaseStringUTFChars(driver, driver_s);
    env->ReleaseStringUTFChars(path, path_s);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_ss)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_ss);
        return 1;
    }

    if (!gptData.SaveGPTBackup(path_ss)) {
        appendBaseLog(PARTITION_LOG, "Backup table successfully");
        return 0;
    }
    appendBaseLog(PARTITION_LOG, "Unable to backup table");
    return 1;
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_PartitionAction_restore_1table(JNIEnv *env, jobject thiz,
                                                                   jstring driver, jstring path) {

    appendLogCutLine(PARTITION_LOG, "PARTITION RESTORE_TABLE");
    const char* driver_s = env->GetStringUTFChars(driver, nullptr);
    const char* path_s = env->GetStringUTFChars(path, nullptr);
    string driver_ss(driver_s);
    string path_ss(path_s);
    env->ReleaseStringUTFChars(driver, driver_s);
    env->ReleaseStringUTFChars(path, path_s);

    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_ss)) {
        appendBaseLog(PARTITION_LOG, "Failed to load partition table : " + driver_ss);
        return 1;
    }

    gptData.JustLooking(0);
    if (gptData.LoadGPTBackup(path_ss)) {
        if (gptData.SaveGPTData(1)) {
            appendBaseLog(PARTITION_LOG, "Restore table successfully");
            return 0;
        }
    }
    appendBaseLog(PARTITION_LOG, "Unable to restore table");
    return 1;
}
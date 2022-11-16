//
// Created by jackmaxpale on 2022/10/15.
//

#include "PartitionActions.h"
#include <jni.h>
#include "include/gpt/gpt.h"
#include <android/log.h>
#include "MyLog.h"


/**
 * partition create1
 * tested
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2ILjava_lang_String_2JJ(
        JNIEnv *env, jobject thiz, jstring driver, jint number_or, jstring name, jlong start,
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
     * We and sgdisk use number starting from 1 not 0
     * but gpt lib start from 0
     */
    int number = number_or - 1;

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
    uint64_t last_sector = (uint64_t) (start + length - 1) / gptData.GetBlockSize();

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
        if (!gptData.SaveGPTData(1)){
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
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
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2Ljava_lang_String_2JJ(
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
    uint64_t last_sector = (uint64_t) (start + length - 1) / gptData.GetBlockSize();

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
        if (!gptData.SaveGPTData(1)){
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
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
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2Ljava_lang_String_2J(
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

    uint64_t requiredBlocks = ((uint64_t) length / gptData.GetBlockSize()) + 1;
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
    if (diskSize > 0) {
        do {
            firstBlock = gptData.FindFirstAvailable(start);
            if (firstBlock != UINT64_C(0)) { // something's free...
                lastBlock = gptData.FindLastInFree(firstBlock);

                segmentSize = lastBlock - firstBlock + UINT64_C(1);

                if (segmentSize >= requiredBlocks) {
                    //ok find one
                    largestSegment = segmentSize;
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
    appendBaseLog(PARTITION_LOG, "Smallest free segement is " + to_string(smallestSegement));
    appendBaseLog(PARTITION_LOG,
                  "From " + to_string(smallest_start) + " to " + to_string(smallest_end));
    appendLogCutLine(PARTITION_LOG, "Take the smallest free segement");
    //create partition

    if (gptData.CreatePartition(number, smallest_start, smallest_start+requiredBlocks-1)) {
        if (!gptData.SetName(number, name_s)) {
            appendBaseLog(PARTITION_LOG, "Unable to set name");
            return 1;
        }
        gptData.JustLooking(0);
        if (!gptData.SaveGPTData(1)){
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        return 0;
    }
    appendBaseLog(PARTITION_LOG, "Create partition failed.");
    return 1;
}

/**
 * delete1
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_delete__Ljava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env, jobject thiz, jstring driver, jstring name) {
    // TODO: test delete()
    appendLogCutLine(PARTITION_LOG, "PARTITION DELETE1");
    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);

    const char *name_c = env->GetStringUTFChars(name, nullptr);
    string name_s(driver_c);
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
    if (!gptData.SaveGPTData(1)){
        appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
        return 1;
    }
    return 0;
}
/**
 * delete2
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_delete__Ljava_lang_String_2I(JNIEnv *env,
                                                                                        jobject thiz,
                                                                                        jstring driver,
                                                                                        jint number) {
    // TODO: test delete()
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
        if (!gptData.SaveGPTData(1)){
            appendBaseLog(PARTITION_LOG, "Unable to save gpt table");
            return 1;
        }
        return 0;
    }
    appendBaseLog(PARTITION_LOG, "Partition not exist. " + to_string(number));
    return 1;


}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_clone(JNIEnv *env, jobject thiz,
                                                                 jstring driver,
                                                                 jstring from_driver,
                                                                 jint from_number, jlong t_start) {
    // TODO: implement clone()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_format(JNIEnv *env, jobject thiz,
                                                                  jstring driver,
                                                                  jint partition_num,
                                                                  jstring filesystem) {
    // TODO: implement format()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_mount(JNIEnv *env, jobject thiz,
                                                                 jstring driver, jint number,
                                                                 jstring filesystem,
                                                                 jstring mount_point) {
    // TODO: implement mount()
}


extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_readInfo(JNIEnv *env, jobject thiz,
                                                                    jstring device) {

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
    appendBaseLog(PARTITION_LOG, "Sector size : " + gptdata.GetBlockSize());
    appendBaseLog(PARTITION_DUMP_LOG, "[BEGIN]");
    for (uint32_t i = low; i <= high; i++) {
        /* code */
        if (gptdata.IsUsedPartNum(i)) {
            //cout<<"Operatoring "<<i<<"      ";
            GPTPart part = gptdata[i];
            appendBaseLog(PARTITION_DUMP_LOG, to_string(i) + ":"
                                              + part.GetDescription() + ":"
                                              + to_string(part.GetFirstLBA()) + ":"
                                              + to_string(part.GetLastLBA()));
        }

    }
    appendBaseLog(PARTITION_DUMP_LOG, "[END]");
    return 0;
}
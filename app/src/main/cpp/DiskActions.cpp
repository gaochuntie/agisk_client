#include <jni.h>
#include "android/log.h"
#include "Basic.h"
#include <fstream>
#include <error.h>
#include <errno.h>
#include "MyLog.h"

#include "include/gpt/gpt.h"
//
// Created by jackmaxpale on 2022/9/23.
//



/**
 * write a raw file into disk area
 * Tested ok!
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_DiskAction_write(JNIEnv *env, jobject thiz,
                                                            jstring driver, jlong start,
                                                            jlong length, jstring raw_file_path,
                                                            jlong offset_raw) {
    appendLogCutLine(DISKACTION_LOG, "DISK WRITE");

    const char *raw_file_pathc = env->GetStringUTFChars(raw_file_path, nullptr);
    ifstream raw_fi(raw_file_pathc,std::__ndk1::ios_base::binary);

    if (!raw_file_pathc) {
        appendBaseLog(DISKACTION_LOG, "RAW file unreachable : " + string(raw_file_pathc));
        return 1;
    }

    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    ofstream driver_of(driver_c, std::__ndk1::ios_base::binary | std::__ndk1::ios_base::out |
                                 std::__ndk1::ios_base::in);

    if (!driver_c) {
        appendBaseLog(DISKACTION_LOG, "Driver unreachable : " + string(driver_c));
        return 1;
    }
    appendBaseLog(DISKACTION_LOG,
                   string(raw_file_pathc)+" ("+to_string(offset_raw)+")" + " to " + string(driver_c)+" ("+to_string(start)+") length:"+to_string(length));

    raw_fi.seekg(0, std::__ndk1::ios_base::end);
    if (offset_raw + length - 1 > raw_fi.tellg()) {
        appendBaseLog(DISKACTION_LOG, "SIZE error : " + raw_fi.tellg());
        return 1;
    }
    raw_fi.seekg(offset_raw, std::__ndk1::ios_base::beg);
    driver_of.seekp(start, std::__ndk1::ios_base::beg);

    long leftNum=length;
    long block_size=512;
    GPTData gptData;
    gptData.JustLooking(1);
    if (gptData.LoadPartitions(driver_c)) {
        block_size=gptData.GetBlockSize();
    }//else failed to open gpt table,default block size 512

    char *buff = new char[block_size + 1];
    memset(buff, 0, sizeof(buff));
    for (; (long)(leftNum/block_size) >0; leftNum -= block_size) {
        raw_fi.read(buff, block_size);
        driver_of.write(buff, raw_fi.gcount());
    }
    raw_fi.read(buff, leftNum);
    driver_of.write(buff, raw_fi.gcount());
    delete[] buff;
    if (!driver_of.tellp() == start + length) {
        appendBaseLog(DISKACTION_LOG, "Write finished but with error!!! :" + string(driver_c));

    }
    driver_of.flush();
    driver_of.close();
    raw_fi.close();


    env->ReleaseStringUTFChars(raw_file_path, raw_file_pathc);
    env->ReleaseStringUTFChars(driver, driver_c);
    return 0;


}

/**
 * Tested ok!
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_DiskAction_format(JNIEnv *env, jobject thiz,
                                                             jstring driver, jlong start,
                                                             jlong length) {
    appendLogCutLine(DISKACTION_LOG, "DISK FORMAT");
    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    string driver_s(driver_c);
    env->ReleaseStringUTFChars(driver, driver_c);
    ofstream of(driver_s, std::__ndk1::ios_base::binary | std::__ndk1::ios_base::out |
                          std::__ndk1::ios_base::in);
    if (!of) {
        appendBaseLog(DISKACTION_LOG, "Device unreachable : " + driver_s);
        return 1;
    }
    if (!of.seekp(start)) {
        appendBaseLog(DISKACTION_LOG, "Device seek fault. : " + driver_s);
        return 1;
    }
/*    char zero[512];
    memset(zero, 0, sizeof(zero));
    long left=length;*/

    /**
     * !!!!!!!!!!!!!!!!!!
     * warning ! warning ! warning !
     * this is a very dangerous operation
     * This code now has confusion
     * !!!!!!!!!!!!!!!!!!
     */
     //TODO
/*    for (;  left%512 >0 ; left-=512) {
        of.write(zero, 512);
    }*/
/**
 * updated write function
 */
    of.write(nullptr, length);
    if (of.fail()) {
        appendBaseLog(DISKACTION_LOG, "Write failed. : " + driver_s);
        of.flush();
        of.close();
        return 1;
    }
    //failed
    if (of.tellp() != start + length) {
        appendBaseLog(DISKACTION_LOG, "Write finished but with error length!!! :" + driver_s);
        of.flush();
        of.close();
        return 1;
    }
    of.flush();
    of.close();
    appendBaseLog(DISKACTION_LOG, driver_s);
    appendBaseLog(DISKACTION_LOG, "Format success from " + to_string(start) + " to " +
                                  to_string((start + length - 1)));

    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_DiskAction_clone(JNIEnv *env, jobject thiz,
                                                            jstring driver, jlong s_start,
                                                            jlong length, jstring t_driver,
                                                            jlong t_start) {
    appendLogCutLine(DISKACTION_LOG, "DISK CLONE");

    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    const char *tdriver_c = env->GetStringUTFChars(t_driver, nullptr);

    string driver_s(driver_c);
    string t_driver_s(tdriver_c);
    env->ReleaseStringUTFChars(driver, driver_c);
    env->ReleaseStringUTFChars(t_driver, tdriver_c);

    ifstream fi(driver_s,std::__ndk1::ios_base::binary);

    if (!fi) {
        appendBaseLog(DISKACTION_LOG, "Driver unreachable : " + driver_s);
        return 1;
    }

    ofstream driver_of(driver_c, std::__ndk1::ios_base::binary | std::__ndk1::ios_base::out |
                                 std::__ndk1::ios_base::in);

    if (!driver_of) {
        appendBaseLog(DISKACTION_LOG, "Driver unreachable : " + t_driver_s);
        return 1;
    }

    fi.seekg(0, std::__ndk1::ios_base::end);
    if (s_start + length - 1 > fi.tellg()) {
        appendBaseLog(DISKACTION_LOG, "SIZE error : " + fi.tellg());
        return 1;
    }
    fi.seekg(s_start, std::__ndk1::ios_base::beg);
    driver_of.seekp(t_start, std::__ndk1::ios_base::beg);
    long leftNum=length;
    long block_size=512;
    GPTData gptData;
    gptData.JustLooking(1);
    if (gptData.LoadPartitions(driver_c)) {
        block_size=gptData.GetBlockSize();
    }//else failed to open gpt table,default block size 512

    char *buff = new char[block_size + 1];
    memset(buff, 0, sizeof(buff));
    for (; (long)(leftNum/block_size) >0; leftNum -= block_size) {
        fi.read(buff, block_size);
        driver_of.write(buff, fi.gcount());
    }
    fi.read(buff, leftNum);
    driver_of.write(buff, fi.gcount());
    delete[] buff;
    if (!driver_of.tellp() == t_start + length) {
        appendBaseLog(DISKACTION_LOG, "Write finished but with error!!! :" + string(driver_c));

    }
    driver_of.flush();
    driver_of.close();
    fi.close();
    return 0;
}


#define test_log "/sdcard/test.log"
/**
 * Tested ok!
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_ConfigBox_DiskAction_backup(JNIEnv *env, jobject thiz,
                                                             jstring driver, jlong start,
                                                             jlong length, jstring backupto) {
    using namespace std;
    const char *cdriver = env->GetStringUTFChars(driver, nullptr);
    const char *cdestfile = env->GetStringUTFChars(backupto, nullptr);
    //
    appendLogCutLine(DISKACTION_LOG,"DISK BACKUP");
    appendBaseLog(DISKACTION_LOG,cdriver);
    //
    ifstream inf(cdriver,std::__ndk1::ios_base::binary);
    ofstream ouf(cdestfile,std::__ndk1::ios_base::binary);
    if (inf) {
        if (ouf) {
            inf.seekg(start, std::__ndk1::ios_base::beg);
            char *buff = new char[length + 1];
            inf.read(buff, length);
            inf.close();
            ouf.write(buff, length);
            ouf.flush();
            ouf.close();
            delete[] buff;
        }
    } else {
        appendBaseLog(DISKACTION_LOG,"DiskAction backup failed");
    }

    env->ReleaseStringUTFChars(driver, cdestfile);
    env->ReleaseStringUTFChars(backupto, cdestfile);
    return 0;
}


/**
 * spare a segement for reserved area
 * will take the smallest available segement
 */
extern "C"
JNIEXPORT jlong JNICALL
Java_atms_app_agiskclient_ConfigBox_DiskAction_spare(JNIEnv *env, jobject thiz,
                                                            jstring driver, jlong length) {
    const char *cdriver = env->GetStringUTFChars(driver, nullptr);

    appendLogCutLine(DISKACTION_LOG, "SPARE");


    string driver_s(cdriver);
    env->ReleaseStringUTFChars(driver, cdriver);

    appendBaseLog(DISKACTION_LOG, "SPARE: " + driver_s);
    GPTData gptData;
    gptData.JustLooking(1);
    if (!gptData.LoadPartitions(driver_s)) {
        appendBaseLog(DISKACTION_LOG, "Failed to load partition table : " + driver_s);
        return 1;
    }
    uint64_t diskSize = gptData.GetLastUsableLBA();

    /**
     * find available segement for required length
     * from gpt lib
     */
     uint64_t requiredBlocks=((uint64_t)length/gptData.GetBlockSize()) +1;
    appendBaseLog(DISKACTION_LOG, "Required blocks : " + to_string(requiredBlocks));

    uint64_t start = UINT64_C(0); // starting point for each search
    uint64_t totalFound = UINT64_C(0); // running total
    uint64_t firstBlock =0; // first block in a segment
    uint64_t lastBlock=0; // last block in a segment
    uint64_t segmentSize=0; // size of segment in blocks
    uint32_t num = 0;
    uint32_t numSegments= 0;
    uint64_t largestSegment= 0;
    uint64_t smallestSegement=0;
    uint32_t foundNum=0;
    uint64_t smallest_start=0;
    uint64_t smallest_end=0;
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
                    appendBaseLog(DISKACTION_LOG,
                                  "Find available segement : " + to_string(firstBlock) +
                                  " to "+ to_string(lastBlock)+" ["+to_string(segmentSize)+"]");
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
        appendBaseLog(DISKACTION_LOG, "No free segement match");
        return 1;
    }
    appendBaseLog(DISKACTION_LOG, "Smallest free segement is " + to_string(smallestSegement));
    appendBaseLog(DISKACTION_LOG,
                  "From " + to_string(smallest_start) + " to " + to_string(smallest_end));
    //TODO create reserved xml for device_s from smallestStart to smallestEnd


    return 0;
}

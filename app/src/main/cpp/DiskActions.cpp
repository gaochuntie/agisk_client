#include <jni.h>
#include "android/log.h"
#include "Basic.h"
#include <fstream>
#include <error.h>
#include <errno.h>
#include "MyLog.h"
//
// Created by jackmaxpale on 2022/9/23.
//



/**
 * write a raw file into disk area
 */
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_DiskAction_write(JNIEnv *env, jobject thiz,
                                                            jstring driver, jlong start,
                                                            jlong length, jstring raw_file_path,
                                                            jlong offset_raw) {
    // TODO: implement write()
    appendBaseLog(DISKACTION_LOG, "DISK WRITE");

    const char *raw_file_pathc = env->GetStringUTFChars(raw_file_path, nullptr);
    ifstream raw_fi(raw_file_pathc);

    if (!raw_file_pathc) {
        appendBaseLog(DISKACTION_LOG, "RAW file unreachable : " + string(raw_file_pathc));
        return 1;
    }

    const char *driver_c = env->GetStringUTFChars(driver, nullptr);
    ofstream driver_of(driver_c);

    if (!driver_c) {
        appendBaseLog(DISKACTION_LOG, "Driver unreachable : " + string(driver_c));
        return 1;
    }
    appendBaseLog(DISKACTION_LOG,
                  "DISKACTION: " + string(raw_file_pathc) + " : " + string(driver_c));

    raw_fi.seekg(0, std::__ndk1::ios_base::end);
    if (offset_raw + length - 1 > raw_fi.tellg()) {
        appendBaseLog(DISKACTION_LOG, "SIZE error : " + raw_fi.tellg());
        return 1;
    }
    raw_fi.seekg(offset_raw, std::__ndk1::ios_base::beg);
    driver_of.seekp(start, std::__ndk1::ios_base::beg);
    char buff[512];

    long leftNum=length;

    for (; leftNum % 512 >0; leftNum -= 512) {
        raw_fi.read(buff, 512);
        driver_of.write(buff, raw_fi.gcount());
    }
    raw_fi.read(buff, leftNum);
    driver_of.write(buff, raw_fi.gcount());

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
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_DiskAction_format(JNIEnv *env, jobject thiz,
                                                             jstring driver, jlong start,
                                                             jlong length) {
    // TODO: implement format()
    return 1;
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_DiskAction_clone(JNIEnv *env, jobject thiz,
                                                            jstring driver, jlong s_start,
                                                            jlong length, jstring t_driver,
                                                            jlong t_start) {
    // TODO: implement clone()
    return 1;
}


#define test_log "/sdcard/test.log"
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_DiskAction_backup(JNIEnv *env, jobject thiz,
                                                             jstring driver, jlong start,
                                                             jlong length, jstring backupto) {
    // TODO: implement backup()
    using namespace std;
    const char *cdriver = env->GetStringUTFChars(driver, nullptr);
    long cstart = start;
    long clength = length;
    const char *cdestfile = env->GetStringUTFChars(backupto, nullptr);
    //
    appendBaseLog(DISKACTION_LOG,"DiskAction backup");
    appendBaseLog(DISKACTION_LOG,cdriver);
    //
    ifstream inf(cdriver);
    ofstream ouf(cdestfile);
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
extern "C"
JNIEXPORT jlong JNICALL
Java_atms_app_my_1application_1c_ConfigBox_DiskAction_spare(JNIEnv *env, jobject thiz,
                                                            jstring driver, jlong length) {
    // TODO: implement spare()
    return 0;
}
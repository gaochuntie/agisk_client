#include <jni.h>
#include "android/log.h"
#include "Basic.h"
#include <fstream>
#include <error.h>
#include <errno.h>
//
// Created by jackmaxpale on 2022/9/23.
//


extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_DiskAction_write(JNIEnv *env, jobject thiz,
                                                            jstring driver, jlong start,
                                                            jlong length, jstring raw_file_path,
                                                            jlong offset_raw) {
    // TODO: implement write()
    return 1;
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
    appendBaseLog("DiskAction backup", test_log);
    appendBaseLog(cdriver, test_log);
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
        appendBaseLog("DiskAction backup failed", test_log);
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
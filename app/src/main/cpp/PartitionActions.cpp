//
// Created by jackmaxpale on 2022/10/15.
//

#include "PartitionActions.h"
#include <jni.h>
#include "include/gpt/gpt.h"
#include <android/log.h>
#include "MyLog.h"

extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2ILjava_lang_String_2JJ(
        JNIEnv *env, jobject thiz, jstring driver, jint number, jstring name, jlong start,
        jlong length) {
    // TODO: implement newPart()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2Ljava_lang_String_2JJ(
        JNIEnv *env, jobject thiz, jstring driver, jstring name, jlong start, jlong length) {
    // TODO: implement newPart()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_newPart__Ljava_lang_String_2Ljava_lang_String_2J(
        JNIEnv *env, jobject thiz, jstring driver, jstring name, jlong length) {
    // TODO: implement newPart()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_delete__Ljava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env, jobject thiz, jstring driver, jstring name) {
    // TODO: implement delete()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_my_1application_1c_ConfigBox_PartitionAction_delete__Ljava_lang_String_2I(JNIEnv *env,
                                                                                        jobject thiz,
                                                                                        jstring driver,
                                                                                        jint number) {
    // TODO: implement delete()
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
    // TODO: implement readInfo()

    string dumpLog = "/sdcard/partdump.log";
    const char *devname_C = env->GetStringUTFChars(device, nullptr);
    appendBaseLog(devname_C, dumpLog);
    GPTData gptdata(devname_C);
    appendBaseLog("Success open device", dumpLog);
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

    for (uint32_t i = low; i <= high; i++) {
        /* code */
        if (gptdata.IsUsedPartNum(i)) {
            //cout<<"Operatoring "<<i<<"      ";
            GPTPart part = gptdata[i];
            appendBaseLog(to_string(i)+":"+part.GetDescription()+":"
                          + to_string(part.GetFirstLBA())+":"+ to_string(part.GetLastLBA()), dumpLog);
        }

    }
    env->ReleaseStringUTFChars(device, devname_C);
    return 0;
}
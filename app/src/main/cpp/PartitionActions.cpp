//
// Created by jackmaxpale on 2022/10/15.
//

#include "PartitionActions.h"
#include <jni.h>

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
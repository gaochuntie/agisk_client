//
// Created by jackmaxpale on 2023/11/23.
//

#include "Support.h"
#include <jni.h>
#include <string>
#include "SingleTools.h"
#include "support/support.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_Tools_Support_BytesToIeee(JNIEnv *env, jclass clazz, jlong sectors,
                                                    jlong sector_size) {
    string str = BytesToIeee(sectors, sector_size);
    return env->NewStringUTF(str.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_Tools_Support_DES_1EncryptString(JNIEnv *env, jclass clazz, jstring str,
                                                           jstring key) {
    const char *str_c = env->GetStringUTFChars(str, 0);
    const char *key_c = env->GetStringUTFChars(key, 0);
    string str_s = str_c;
    string key_s = key_c;
    string ens = doEncryptString(str_s, key_s);
    env->ReleaseStringUTFChars(str, str_c);
    env->ReleaseStringUTFChars(key, key_c);
    return env->NewStringUTF(ens.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_Tools_Support_DES_1DecryptString(JNIEnv *env, jclass clazz, jstring str,
                                                           jstring key) {
    const char *str_c = env->GetStringUTFChars(str, 0);
    const char *key_c = env->GetStringUTFChars(key, 0);
    string str_s = str_c;
    string key_s = key_c;
    string des = doDecryptString(str_s, key_s);
    env->ReleaseStringUTFChars(str, str_c);
    env->ReleaseStringUTFChars(key, key_c);
    return env->NewStringUTF(des.c_str());
}
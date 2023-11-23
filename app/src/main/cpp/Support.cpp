//
// Created by jackmaxpale on 2023/11/23.
//

#include "Support.h"
#include <jni.h>
#include "support/support.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_Tools_Support_BytesToIeee(JNIEnv *env, jclass clazz, jlong sectors,
                                                    jlong sector_size) {
    string str = BytesToIeee(sectors, sector_size);
    return env->NewStringUTF(str.c_str());
}
//
// Created by jackmaxpale on 2022/8/15.
//
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <cstring>
#include "atms_app_my_application_c_JNI.h"
#include "android/log.h"
#include <iostream>
#include <fstream>
#include "Basic.h"
#include "MyLog.h"

#define BASIC_TAG "Basic.cpp"

extern "C" JNIEXPORT jint JNICALL Java_atms_app_agiskclient_JNI_add
        (JNIEnv *env, jobject jobj, jint x, jint y) {
    return x + y;
}

extern "C" JNIEXPORT jboolean JNICALL Java_atms_app_agiskclient_JNI_checkPasswd
        (JNIEnv *env, jobject jobj, jstring pass) {
    jboolean iscopy = JNI_TRUE;
    const char *password = (char *) env->GetStringChars(pass, &iscopy);

    env->ReleaseStringChars(pass, (jchar *) password);

    return true;
}


extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_nativeGetUid(JNIEnv *env, jobject thiz) {
    return 1515;
    return getuid();
    // TODO: implement nativeGetUid()
}
extern "C"
JNIEXPORT jint JNICALL
Java_atms_app_agiskclient_Tools_MSGService_nativeGetUid(JNIEnv *env, jobject thiz) {
    return getuid();
}
extern "C"
JNIEXPORT void JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_writelog(JNIEnv *env, jobject thiz, jstring log) {
    // TODO: implement writelog()
    using namespace std;
    const char *log_file = env->GetStringUTFChars(log, nullptr);
    string ss(log_file);
    __android_log_print(ANDROID_LOG_DEBUG, "Basic.cpp ", "%s", log_file);

    ofstream of;
    of.open(GLOBAL_LOG, ios_base::app);


    of <<" ["<<NowTime<<"] "<< log_file << endl;

    return;

}
extern "C"
JNIEXPORT void JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_writelogTag(JNIEnv *env, jobject thiz,
                                                              jstring tag, jstring log) {
    using namespace std;
    const char *log_file = env->GetStringUTFChars(log, nullptr);
    string ss(log_file);
    string tagss(env->GetStringUTFChars(tag, nullptr));
    // __android_log_print(ANDROID_LOG_DEBUG,"Basic.cpp ","%s", log_file);

    ofstream of;
    of.open(GLOBAL_LOG, ios_base::app);

    of <<" ["<<NowTime<<"] "<<tagss << "::" << log_file << endl;
    return;
}


jstring str2jstring(JNIEnv *env, const char *pat) {

    //??????java String??? strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //??????String(byte[],String)????????????,???????????????byte[]????????????????????????String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //??????byte??????
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    //???char* ?????????byte??????
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte *) pat);
    // ??????String, ??????????????????,??????byte???????????????String????????????
    jstring encoding = (env)->NewStringUTF("utf-8");
    //???byte???????????????java String,?????????
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_ConfigBox_XmlProcessor_decryptXml(JNIEnv *env, jobject thiz,
                                                                   jstring extra_path) {
    // TODO: implement decryptXml()
    using namespace std;
    const char *filepath = env->GetStringUTFChars(extra_path, nullptr);
    __android_log_print(ANDROID_LOG_DEBUG, BASIC_TAG, "Xml path %s", filepath);

    char *xmlcontent = nullptr;
    ifstream ifstream1(filepath, ios_base::in | ios_base::binary);
    env->ReleaseStringUTFChars(extra_path, filepath);
    if (!ifstream1) {
        __android_log_print(ANDROID_LOG_DEBUG, BASIC_TAG, "Open xml failed. %s", errno);
        return nullptr;
    }

    int length = 0;
    // open input file
    ifstream1.seekg(0, std::ios::end);    // go to the end
    length = ifstream1.tellg();           // report location (this is the length)
    __android_log_print(ANDROID_LOG_DEBUG, BASIC_TAG, "Xml length %d", length);
    ifstream1.seekg(0, std::ios::beg);    // go back to the beginning
    xmlcontent = new char[length];    // allocate memory for a buffer of appropriate dimension
    ifstream1.read(xmlcontent, length);       // read the whole file into the buffer
    ifstream1.close();                    // close file handle

// ... do stuff with buffer here ...

    char *tmp = new char[strlen(xmlcontent) + 1];
    strcpy(tmp, xmlcontent);
    delete[] xmlcontent;
    //do decryption
    //TODO decrypt xml

    tmp[length] = '\0';
    //
    //__android_log_print(ANDROID_LOG_DEBUG,BASIC_TAG,"Xml %s", tmp);

    jstring rt = str2jstring(env, tmp);
    delete[] tmp;
    return rt;


}

extern "C"
JNIEXPORT void JNICALL
Java_atms_app_agiskclient_Tools_GlobalMsg_appendLog(JNIEnv *env, jclass clazz, jstring log,
                                                           jstring path) {
    using namespace std;
    const char *log_file = env->GetStringUTFChars(log, nullptr);
    string ss(log_file);
    env->ReleaseStringUTFChars(log, log_file);

    const char *pathc = env->GetStringUTFChars(path, nullptr);
    string pathSs(pathc);
    env->ReleaseStringUTFChars(path, pathc);
    // __android_log_print(ANDROID_LOG_DEBUG,"Basic.cpp ","%s", log_file);

    appendBaseLog(ss, pathSs);
    return;
}




extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_Tools_GlobalMsg_readLog(JNIEnv *env, jclass clazz, jstring path) {
    using namespace std;
    const char *log_file = env->GetStringUTFChars(path, nullptr);
    string ss(log_file);
    env->ReleaseStringUTFChars(path, log_file);

    // __android_log_print(ANDROID_LOG_DEBUG,"Basic.cpp ","%s", log_file);

    ifstream ifstream1(ss);

    int length = 0;
    // open input file
    ifstream1.seekg(0, std::ios::end);    // go to the end
    length = ifstream1.tellg();           // report location (this is the length)
    ifstream1.seekg(0, std::ios::beg);    // go back to the beginning
    char *logcontent = new char[length];    // allocate memory for a buffer of appropriate dimension
    ifstream1.read(logcontent, length);       // read the whole file into the buffer
    ifstream1.close();                    // close file handle

    string ll(logcontent);
    delete[] logcontent;

    return env->NewStringUTF(ll.c_str());
}
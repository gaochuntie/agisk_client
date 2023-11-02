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
#include "SingleTools.h"

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

    //定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte *) pat);
    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("utf-8");
    //将byte数组转换为java String,并输出
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_ConfigBox_XmlProcessor_decryptXml(JNIEnv *env, jobject thiz,
                                                                   jstring extra_path,jstring key,jint flag,jstring sn) {
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
    xmlcontent = new char[length+1];
    xmlcontent[length]='\0';// allocate memory for a buffer of appropriate dimension
    ifstream1.read(xmlcontent, length);       // read the whole file into the buffer
    ifstream1.close();                    // close file handle

// ... do stuff with buffer here ...

    char *tmp = new char[strlen(xmlcontent) + 1];
    strcpy(tmp, xmlcontent);
    delete[] xmlcontent;
    //do decryption
    tmp[length] = '\0';
    const char *key_s = env->GetStringUTFChars(key, nullptr);
    const char *sn_s = env->GetStringUTFChars(sn, nullptr);
    string de_xml = doDecryptAgiskSubXml(tmp, key_s, flag, sn_s);
    //
    //__android_log_print(ANDROID_LOG_DEBUG,BASIC_TAG,"Xml %s", tmp);

    jstring rt = str2jstring(env, de_xml.c_str());
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
extern "C"
JNIEXPORT jboolean JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_forceWriteToFileWithRoot(JNIEnv *env, jobject thiz,
                                                                    jstring content, jstring dest) {
    const char *content_s = env->GetStringUTFChars(content, nullptr);
    string content_ss(content_s);
    env->ReleaseStringUTFChars(content, content_s);

    const char *dest_s = env->GetStringUTFChars(dest, nullptr);
    string dest_ss(dest_s);
    env->ReleaseStringUTFChars(dest, dest_s);

    if (WriteToFile(content_ss, dest_ss)!=0){
        return false;
    }
    return true;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_ConfigBox_XmlProcessor_encryptXml(JNIEnv *env, jclass clazz, jstring orig,
                                                            jstring key, jint flag, jstring sn) {
    using namespace std;
    const char *orig_s = env->GetStringUTFChars(orig, nullptr);
    string orig_ss(orig_s);
    env->ReleaseStringUTFChars(orig, orig_s);

    const char *sn_s = env->GetStringUTFChars(sn, nullptr);
    string sn_ss(sn_s);
    env->ReleaseStringUTFChars(sn, sn_s);

    const char *key_s = env->GetStringUTFChars(key, nullptr);
    string key_ss(key_s);
    env->ReleaseStringUTFChars(key, key_s);

    string en_xml = doEncryptAgiskSubXml(orig_ss, key_ss, flag, sn_ss);
    //
    //__android_log_print(ANDROID_LOG_DEBUG,BASIC_TAG,"Xml %s", tmp);

    return env->NewStringUTF(en_xml.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_atms_app_agiskclient_aidl_AIDLService_forceReadWithRoot(JNIEnv *env, jobject thiz,
                                                             jstring filePath) {
    const char *path = env->GetStringUTFChars(filePath, nullptr);

    // Check if the file exists
    std::ifstream file(path);
    if (!file.good()) {
        env->ReleaseStringUTFChars(filePath, path);
        return env->NewStringUTF(""); // Return an empty jstring
    }
    // Read the file content into a C++ string
    std::string fileContent((std::istreambuf_iterator<char>(file)), std::istreambuf_iterator<char>());
    file.close();
    env->ReleaseStringUTFChars(filePath, path);

    // Convert the C++ string to a jstring
    return env->NewStringUTF(fileContent.c_str());
}
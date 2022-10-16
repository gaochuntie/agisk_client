#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_atms_app_my_1application_1c_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
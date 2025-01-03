/* Header for class nativehelper_NativeGpio */
#include "gpio.h"

#define LOG_TAG "GPIO"

#ifndef HOST
#include <android/log.h>
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#else
#define LOGD(...) printf(">==< %s >==< ",LOG_TAG),printf(__VA_ARGS__),printf("\n")
#endif

#ifdef __cplusplus
extern "C" {
#endif

static void on_new_value(int val);

static jmethodID cb_method_id;
static jclass cb_class;
static jobject cb_object;
static JNIEnv *cb_save_env;

/*
 * Class:     nativehelper_NativeGpio
 * Method:    readGpio
 * Signature: (Ljava/lang/String;Lnativehelper/NativeGpio/GpioInterruptCallback;)V
 */
JNIEXPORT void JNICALL Java_com_quectel_multicamera_NativeGpio_readGpio
  (JNIEnv *env, jclass cls, jstring path, jobject callback){
    cb_class = (*env)->GetObjectClass(env, callback);
    if(cb_class == NULL){
        LOGD("callback interface not found");
        return;
    }

    cb_method_id = (*env)->GetMethodID(env, cb_class, "onNewValue", "(I)V");
    if(cb_method_id == NULL){
        LOGD("could not find callback method");
        return;
    }

    cb_object = callback;
    cb_save_env = env;

    const char *fname = (*env)->GetStringUTFChars(env, path, NULL);
    LOGD("path is %s", fname);
    int a = read_gpio((char *)fname, on_new_value);
    (*env)->ReleaseStringUTFChars(env, path, fname);
}


static void on_new_value(int val){
//    LOGD("interrupt received, val: %d", val);
    (*cb_save_env)->CallVoidMethod(cb_save_env, cb_object, cb_method_id, (jint)val);
}
#ifdef __cplusplus
}
#endif

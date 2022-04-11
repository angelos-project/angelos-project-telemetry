/**
 * Copyright (c) 2022 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
#include <jni.h>
#include <stdlib.h>

#ifndef _Included_angelos_telemetry_TelemetryCore
#define _Included_angelos_telemetry_TelemetryCore
#ifdef __cplusplus
extern "C" {
#endif

static const char *JNIT_CLASS = "angelos/telemetry/TelemetryCore";

/*
 * Class:     angelos_telemetry_TelemetryCore
 * Method:    do_start_usage
 * Signature: ()J
 */
static jlong do_start_usage(JNIEnv *env, jclass thisClass) {
    struct rusage* usage = malloc(sizeof(struct rusage));
    getrusage(RUSAGE_SELF, usage);
    return (jlong) usage;
}

/*
 * Class:     angelos_telemetry_TelemetryCore
 * Method:    do_end_usage
 * Signature: (J)Langelos/telemetry/Benchmark;
 */
static jobject do_end_usage(JNIEnv *env, jclass thisClass, jlong start) {
    struct rusage* end_usage = malloc(sizeof(struct rusage));
    getrusage(RUSAGE_SELF, end_usage);

    struct rusage* start_usage = (struct rusage*) start;

    jclass local_cls = (*env)->FindClass(env, "angelos/telemetry/Benchmark");
    if (local_cls == NULL) // Quit program if Java class can't be found
        exit(1);

    jclass global_cls = (*env)->NewGlobalRef(env, local_cls);
    jmethodID cls_init = (*env)->GetMethodID(env, global_cls, "<init>", "(JJJJ)V");
    if (cls_init == NULL) // Quit program if Java class constructor can't be found
        exit(1);

    jobject bm = (*env)->NewObject(env, global_cls, cls_init,
        (((end_usage->ru_utime.tv_sec * 1000000 + end_usage->ru_utime.tv_usec) - (
        start_usage->ru_utime.tv_sec * 1000000 + start_usage->ru_utime.tv_usec)) + (
        (end_usage->ru_stime.tv_sec * 1000000 + end_usage->ru_stime.tv_usec) - (
         start_usage->ru_stime.tv_sec * 1000000 + start_usage->ru_stime.tv_usec))),
        end_usage->ru_maxrss - start_usage->ru_maxrss,
        end_usage->ru_inblock - start_usage->ru_inblock,
        end_usage->ru_oublock - start_usage->ru_oublock
    );

    free(start_usage);
    free(end_usage);
    return bm;
}

static JNINativeMethod funcs[] = {
        {"start_usage", "()J", (void *) &do_start_usage},
        {"end_usage", "(J)Langelos/telemetry/Benchmark;", (void *) &do_end_usage},
};

#define CURRENT_JNI JNI_VERSION_1_6

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    jclass cls;
    jint res;

    (void) reserved;

    if ((*vm)->GetEnv(vm, (void **) &env, CURRENT_JNI) != JNI_OK)
        return -1;

    cls = (*env)->FindClass(env, JNIT_CLASS);
    if (cls == NULL)
        return -1;

    res = (*env)->RegisterNatives(env, cls, funcs, sizeof(funcs) / sizeof(*funcs));
    if (res != 0)
        return -1;

    return CURRENT_JNI;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    jclass cls;

    (void)reserved;

    if ((*vm)->GetEnv(vm,(void **)&env, CURRENT_JNI) != JNI_OK)
        return;

    cls = (*env)->FindClass(env, JNIT_CLASS);
    if (cls == NULL)
        return;

    (*env)->UnregisterNatives(env, cls);
}


#ifdef __cplusplus
}
#endif
#endif

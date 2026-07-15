#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <fcntl.h>
#include <dirent.h>
#include <thread>
#include <atomic>
#include <cstring>

#define LOG_TAG "LUBV"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

static std::atomic<bool> g_attached{false};
static std::atomic<bool> g_running{true};
static pid_t g_mcPid = 0;

pid_t findMCPE() {
    DIR* d = opendir("/proc");
    if (!d) return -1;
    struct dirent* e;
    while ((e = readdir(d))) {
        if (e->d_type != DT_DIR) continue;
        pid_t p = atoi(e->d_name);
        if (p <= 1) continue;
        char path[256]; snprintf(path, sizeof(path), "/proc/%d/cmdline", p);
        int fd = open(path, O_RDONLY);
        if (fd < 0) continue;
        char buf[512] = {0}; read(fd, buf, sizeof(buf)-1); close(fd);
        if (strstr(buf, "minecraftpe")) { closedir(d); return p; }
    }
    closedir(d); return -1;
}

void attachLoop() {
    while (g_running) {
        if (!g_attached) {
            g_mcPid = findMCPE();
            if (g_mcPid > 0) { g_attached = true; LOGD("MCPE baglandi! PID: %d", g_mcPid); }
        } else {
            char p[256]; snprintf(p, sizeof(p), "/proc/%d", g_mcPid);
            if (access(p, F_OK) != 0) { g_attached = false; LOGD("MCPE kapandi."); }
        }
        std::this_thread::sleep_for(std::chrono::seconds(2));
    }
}

extern "C" {

JNIEXPORT void JNICALL
Java_com_lubv_mobile_NativeHacks_init(JNIEnv* env, jobject) {
    LOGD("LUBV Native 1.21 baslatildi!");
    std::thread(attachLoop).detach();
}

JNIEXPORT void JNICALL
Java_com_lubv_mobile_NativeHacks_enableHack(JNIEnv* env, jobject, jstring name) {
    const char* n = env->GetStringUTFChars(name, nullptr);
    LOGD("[ACIK] %s", n);
    env->ReleaseStringUTFChars(name, n);
}

JNIEXPORT void JNICALL
Java_com_lubv_mobile_NativeHacks_disableHack(JNIEnv* env, jobject, jstring name) {
    const char* n = env->GetStringUTFChars(name, nullptr);
    LOGD("[KAPALI] %s", n);
    env->ReleaseStringUTFChars(name, n);
}

JNIEXPORT jboolean JNICALL
Java_com_lubv_mobile_NativeHacks_isMinecraftRunning(JNIEnv* env, jobject) {
    return g_attached;
}

}

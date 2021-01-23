#include <jni.h>
#include <string>
#include <unistd.h>
#include <android/log.h>

uint32_t update_adler32(unsigned adler, const uint8_t *data, uint32_t len) {
    unsigned s1 = adler & 0xffffu;
    unsigned s2 = (adler >> 16u) & 0xffffu;

    while (len > 0) {
        /*at least 5550 sums can be done before the sums overflow, saving a lot of module divisions*/
        unsigned amount = len > 5550 ? 5550 : len;
        len -= amount;
        while (amount > 0) {
            s1 += (*data++);
            s2 += s1;
            --amount;
        }
        s1 %= 65521;
        s2 %= 65521;
    }
    return (s2 << 16u) | s1;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_tsihen_me_qscript_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "QScript: 一个 Xposed 模块";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_tsihen_me_qscript_util_Natives_getpagesize(JNIEnv *env, jobject thiz) {
    return getpagesize();
}

typedef struct tm timeStruct;

/**
 * @brief getDateFromMacro
 * @param time __DATE__
 * @return
 */
static timeStruct getDateFromMacro(char const *time) {
    char s_month[5];
    int month, day, year;
    timeStruct t = {0};
    static const char month_names[] = "JanFebMarAprMayJunJulAugSepOctNovDec";

    sscanf(time, "%s %d %d", s_month, &day, &year);

    month = (strstr(month_names, s_month) - month_names) / 3;

    t.tm_mon = month;
    t.tm_mday = day;
    t.tm_year = year - 1900;
    t.tm_isdst = -1;

    return t;
}

/**
 * @brief getTimeFromMacro
 * @param time __TIME__
 * @return
 */
static timeStruct getTimeFromMacro(char const *time) {
    int hour, min, sec;
    timeStruct t = {0};

    sscanf(time, "%d:%d:%d", &hour, &min, &sec);

    t.tm_hour = hour;
    t.tm_min = min;
    t.tm_sec = sec;

    return t;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_tsihen_me_qscript_util_Utils_ntGetBuildTimestamp(JNIEnv *env, jclass clazz) {
    timeStruct date = getDateFromMacro(__DATE__);
    timeStruct time = getTimeFromMacro(__TIME__);
    timeStruct t = {0};

    t.tm_sec = time.tm_sec;
    t.tm_min = time.tm_min;
    t.tm_hour = time.tm_hour;
    t.tm_mon = date.tm_mon;
    t.tm_mday = date.tm_mday;
    t.tm_year = date.tm_year;
    t.tm_isdst = date.tm_isdst;

    long long finalTime = ((long long) mktime(&t)) * 1000;

    return finalTime;
//    return doGetBuildTimestamp(env, clazz);
}

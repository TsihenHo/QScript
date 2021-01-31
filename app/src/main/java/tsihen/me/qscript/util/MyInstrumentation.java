package tsihen.me.qscript.util;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import tsihen.me.qscript.MainHook;

import static tsihen.me.qscript.util.JavaUtil.injectModuleResources;
import static tsihen.me.qscript.util.Utils.log;
import static tsihen.me.qscript.util.Utils.logd;

public class MyInstrumentation extends Instrumentation {
    private final Instrumentation mBase;

    public MyInstrumentation(Instrumentation instrumentation) {
        this.mBase = instrumentation;
    }

//    @SuppressWarnings("UNUSED")
//    public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Intent newIntent = new Intent(intent);
//        String stubPackage = "com.tencent.mobileqq";
//        newIntent.setComponent(new ComponentName(stubPackage, "com.tencent.mobileqq.activity.SplashActivity"));
//        newIntent.putExtra(JavaUtil.KEY_EXTRA_TARGET_INTENT, intent);
//        logi("JumpActivity : ExecStartActivity : newIntent is to " + Utils.getObject(intent.getComponent(), "mClass", String.class));
//        Class<?>[] classes = {Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class};
//        Object[] objects = {who, contextThread, token, target, newIntent, requestCode, options};
//        Method m;
//        try {
//            m = instrumentation.getClass().getDeclaredMethod("execStartActivity", classes);
//        } catch (NoSuchMethodException e) {
//            // 兼容 QN
//            m = instrumentation.getClass().getSuperclass().getDeclaredMethod("execStartActivity", classes);
//        } catch (Exception e) {
//            log(e);
//            return null;
//        }
//        m.setAccessible(true);
//        return (ActivityResult) m.invoke(instrumentation, objects);
//    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (className.startsWith("tsihen.me.qscript.")) {
            return (Activity) Initiator.class.getClassLoader().loadClass(className).newInstance();
        }
        return mBase.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        try {
            injectModuleResources(activity.getResources());
            mBase.callActivityOnCreate(activity, icicle);
        } catch (Exception e) {
            if (Pattern.matches("[\\W]tsihen\\.|me\\.qscript", Log.getStackTraceString(e).replace("tsihen.me.qscript.util.MyInstrumentation.callActivityOnStart",""))) {
                throw e;
            }
            //else ignore
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        try {
            injectModuleResources(activity.getResources());
            mBase.callActivityOnCreate(activity, icicle, persistentState);
        } catch (Exception e) {
            if (Pattern.matches("[\\W]me\\.|nil\\.nadph", Log.getStackTraceString(e).replace("nil.nadph.qnotified.MainHook$MyInstrumentation.callActivityOnStart",""))) {
                throw e;
            }
            log(e);
            //else ignore
        }
    }

    @Override
    public void onCreate(Bundle arguments) {
        mBase.onCreate(arguments);
    }

    @Override
    public void start() {
        mBase.start();
    }

    @Override
    public void onStart() {
        mBase.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        return mBase.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
        mBase.sendStatus(resultCode, results);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        mBase.finish(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        mBase.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        mBase.startPerformanceSnapshot();
    }

    @Override
    public void endPerformanceSnapshot() {
        mBase.endPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {
        mBase.onDestroy();
    }

    @Override
    public Context getContext() {
        return mBase.getContext();
    }

    @Override
    public ComponentName getComponentName() {
        return mBase.getComponentName();
    }

    @Override
    public Context getTargetContext() {
        return mBase.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
        return mBase.isProfiling();
    }

    @Override
    public void startProfiling() {
        mBase.startProfiling();
    }

    @Override
    public void stopProfiling() {
        mBase.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {
        mBase.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
        mBase.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
        mBase.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
        mBase.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return mBase.startActivitySync(intent);
    }

    @Override
    public Activity startActivitySync(Intent intent, Bundle options) {
        return super.startActivitySync(intent, options);
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        mBase.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return mBase.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return mBase.addMonitor(cls, result, block);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return mBase.checkMonitorHit(monitor, minHits);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return mBase.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return mBase.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        mBase.removeMonitor(monitor);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return mBase.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return mBase.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void sendStringSync(String text) {
        mBase.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
        mBase.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
        mBase.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
        mBase.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
        mBase.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
        mBase.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return mBase.newApplication(cl, className, context);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        mBase.callApplicationOnCreate(app);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws IllegalAccessException, InstantiationException {
        return mBase.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        mBase.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }


    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
        mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle savedInstanceState) {
        mBase.callActivityOnPostCreate(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, @Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        mBase.callActivityOnPostCreate(activity, savedInstanceState, persistentState);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        mBase.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        mBase.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        mBase.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        mBase.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        mBase.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        mBase.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
        mBase.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        mBase.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        mBase.callActivityOnUserLeaving(activity);
    }

    @Override
    public void startAllocCounting() {
        mBase.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
        mBase.stopAllocCounting();
    }

    @Override
    public Bundle getAllocCounts() {
        return mBase.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
        return mBase.getBinderCounts();
    }

    @Override
    public UiAutomation getUiAutomation() {
        return mBase.getUiAutomation();
    }
}

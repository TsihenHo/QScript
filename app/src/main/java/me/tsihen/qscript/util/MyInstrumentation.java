package me.tsihen.qscript.util;

/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
// This file is copy from QNotified.

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

@FromQNotified
public class MyInstrumentation extends Instrumentation {
    private final Instrumentation mBase;

    public MyInstrumentation(Instrumentation instrumentation) {
        this.mBase = instrumentation;
    }

    //    @SuppressWarnings("UNUSED")
//    public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Intent newIntent = new Intent(intent);
//        String stubPackage = "com.tencent.mobileqq";
//        newIntent.setComponent(new ComponentName(stubPackage, ConstsKt.STUB_DEFAULT_ACTIVITY));
//        newIntent.putExtra(ConstsKt.ACTIVITY_PROXY_INTENT, intent);
//        Utils.logi("JumpActivity : ExecStartActivity : newIntent is to " + ClassUtils.getObject(intent.getComponent(), "mClass", String.class));
//        Class<?>[] classes = {Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class};
//        Object[] objects = {who, contextThread, token, target, newIntent, requestCode, options};
//        Method m;
//        try {
//            m = mBase.getClass().getDeclaredMethod("execStartActivity", classes);
//        } catch (NoSuchMethodException e) {
//             兼容 QN
//            m = mBase.getClass().getSuperclass().getDeclaredMethod("execStartActivity", classes);
//        } catch (Exception e) {
//            Utils.log(e);
//            return null;
//        }
//        m.setAccessible(true);
//        return (ActivityResult) m.invoke(mBase, objects);
//    }
//
@Override
public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    if (className.startsWith("me.tsihen.qscript.activity.")) {
        return (Activity) Initiator.class.getClassLoader().loadClass(className).newInstance();
    }
    return mBase.newActivity(cl, className, intent);
}
/*

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        try {
            injectModuleResources(activity.getResources());
            mBase.callActivityOnCreate(activity, icicle);
        } catch (Exception e) {
            if (Pattern.matches("[\\W]me\\.|tsihen\\.qscript", Log.getStackTraceString(e).replace("me.tsihen.qscript.util.MyInstrumentation.callActivityOnStart", ""))) {
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
            if (Pattern.matches("[\\W]me\\.|tsihen\\.qscript", Log.getStackTraceString(e).replace("me.tsihen.qscript.util.MyInstrumentation.callActivityOnStart", ""))) {
                throw e;
            }
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

 */
}

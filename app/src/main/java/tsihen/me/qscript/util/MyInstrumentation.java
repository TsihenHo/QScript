package tsihen.me.qscript.util;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static tsihen.me.qscript.util.Utils.logd;

public class MyInstrumentation extends Instrumentation {
    private Instrumentation instrumentation;

    public MyInstrumentation(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @SuppressWarnings("UNUSED")
    public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logd("我是hook进来的!(exec)");
        Intent newIntent = new Intent(intent);
        String stubPackage = "com.tencent.mobileqq";
        newIntent.setComponent(new ComponentName(stubPackage, "com.tencent.mobileqq.activity.SplashActivity"));
        newIntent.putExtra(JavaUtil.KEY_EXTRA_TARGET_INTENT, intent);
        Class[] classes = {Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class};
        Object[] objects = {who, contextThread, token, target, newIntent, requestCode, options};
        Method m = instrumentation.getClass().getDeclaredMethod("execStartActivity", classes);
        m.setAccessible(true);
        return (ActivityResult) m.invoke(instrumentation, objects);
    }
}

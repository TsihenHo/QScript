package tsihen.me.qscript.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

import static de.robv.android.xposed.XposedHelpers.findField;
import static tsihen.me.qscript.util.Utils.log;
import static tsihen.me.qscript.util.Utils.logd;
import static tsihen.me.qscript.util.Utils.loge;

@SuppressLint("PrivateApi")
public class JavaUtil {
    public static final String KEY_EXTRA_TARGET_INTENT = "EXTRA_TARGET_INTENT";
    private static final String TAG = "APP";

    /**
     * 根据包名构建目标Context,并调用getPackageCodePath()来定位apk
     *
     * @param context           context参数
     * @param modulePackageName 当前模块包名
     * @return return apk file
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File findApkFile(Context context, String modulePackageName) {
        if (context == null) {
            return null;
        }
        try {
            Context moudleContext = context.createPackageContext(modulePackageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            if (moudleContext == null) {
                loge("Module Context is null");
            }
            String apkPath = moudleContext.getPackageCodePath();
            logd(apkPath);
            File f = new File(apkPath);
            if (f == null) {
                loge("File is null");
            }
            return f;
        } catch (PackageManager.NameNotFoundException e) {
            log(e);
        }
        loge("Return null");
        return null;
    }

    public static String findApkPath(Context context, String modulePackageName) {
        if (context == null) {
            return null;
        }
        try {
            Context moudleContext = context.createPackageContext(modulePackageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            if (moudleContext == null) {
                loge("Module Context is null");
            }
            String apkPath = moudleContext.getPackageCodePath();
            logd(apkPath);
            return apkPath;
        } catch (PackageManager.NameNotFoundException e) {
            log(e);
        }
        loge("Return null");
        return null;
    }

    public static void loadPlugin(DexClassLoader dexClassLoader, Context ctx) throws IllegalAccessException {
        //获取自己的dexElements
        PathClassLoader pathClassLoader = (PathClassLoader) ctx.getClassLoader();

        Field pathListField = findField(pathClassLoader.getClass(), "pathList");
        Object pathListObject = pathListField.get(pathClassLoader);

        Field dexElementsField = findField(pathListObject.getClass(), "dexElements");
        Object[] dexElementsObject = (Object[]) dexElementsField.get(pathListObject);

        Field pluginPathListField = findField(dexClassLoader.getClass(), "pathList");
        Object pluginPathListObject = pluginPathListField.get(dexClassLoader);

        Field pluginDexElementsField = findField(pluginPathListObject.getClass(), "dexElements");
        Object[] pluginDexElementsObject = (Object[]) pluginDexElementsField.get(pluginPathListObject);

        Class<?> elementClazz = dexElementsObject.getClass().getComponentType();
        Object newDexElements = Array.newInstance(elementClazz, pluginDexElementsObject.length + dexElementsObject.length);
        System.arraycopy(pluginDexElementsObject, 0, newDexElements, 0, pluginDexElementsObject.length);
        System.arraycopy(dexElementsObject, 0, newDexElements, pluginDexElementsObject.length, dexElementsObject.length);

        //设置
        dexElementsField.set(pathListObject, newDexElements);
    }
}



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

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import tsihen.me.qscript.MainHook;
import tsihen.me.qscript.R;

import static de.robv.android.xposed.XposedHelpers.findField;
import static tsihen.me.qscript.util.Utils.getObject;
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
                return null;
            }
            String apkPath = moudleContext.getPackageCodePath();
            File f = new File(apkPath);
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
                return null;
            }
            return moudleContext.getPackageCodePath();
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

    private static String sModulePath = null;

    @SuppressLint("PrivateApi")
    public static void injectModuleResources(Resources res) {
        if (res == null) return;
        try {
            res.getString(R.string.nothing);
            res.getLayout(R.layout.activity_setting);
            return;
        } catch (Resources.NotFoundException ignored) {
        }
        try {
            if (sModulePath == null) {
                String modulePath = null;
                BaseDexClassLoader pcl = (BaseDexClassLoader) MainHook.class.getClassLoader();
                Object pathList = getObject(pcl, "pathList", null);
                assert pathList != null;
                Object[] dexElements = (Object[]) getObject(pathList, "dexElements", null);
                assert dexElements != null;
                for (Object element : dexElements) {
                    File file = (File) getObject(element, "path", null);
                    if (file == null || file.isDirectory())
                        file = (File) getObject(element, "zip", null);
                    if (file == null || file.isDirectory())
                        file = (File) getObject(element, "file", null);
                    if (file != null && !file.isDirectory()) {
                        String path = file.getPath();
                        if (modulePath == null || !modulePath.contains("tsihen.me.qscript")) {
                            modulePath = path;
                        }
                    }
                }
                if (modulePath == null) {
                    throw new RuntimeException("get module path failed, loader=" + MainHook.class.getClassLoader());
                }
                sModulePath = modulePath;
            }
            AssetManager assets = res.getAssets();
            @SuppressLint("DiscouragedPrivateApi")
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            int cookie = (int) addAssetPath.invoke(assets, sModulePath);
            try {
                res.getString(R.string.nothing);
            } catch (Resources.NotFoundException e) {
                loge("Fatal: injectModuleResources: test injection failure!");
                loge("injectModuleResources: cookie=" + cookie + ", path=" + sModulePath + ", loader=" + MainHook.class.getClassLoader());
                long length = -1;
                boolean read = false;
                boolean exist = false;
                boolean isDir = false;
                try {
                    File f = new File(sModulePath);
                    exist = f.exists();
                    isDir = f.isDirectory();
                    length = f.length();
                    read = f.canRead();
                } catch (Throwable e2) {
                    log(e2);
                }
                loge("sModulePath: exists = " + exist + ", isDirectory = " + isDir + ", canRead = " + read + ", fileLength = " + length);
            }
        } catch (Exception e) {
            log(e);
        }
    }
}



package tsihen.me.qscript.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import tsihen.me.qscript.MainHook;
import tsihen.me.qscript.R;

import static de.robv.android.xposed.XposedHelpers.findField;
import static tsihen.me.qscript.util.ClassUtils.getObject;
import static tsihen.me.qscript.util.Utils.log;
import static tsihen.me.qscript.util.Utils.loge;
import static tsihen.me.qscript.util.Utils.logi;
import static tsihen.me.qscript.util.Utils.logw;

@SuppressLint("PrivateApi")
public class JavaUtil {
    private static boolean __stub_hooked = false;
    private static String sModulePath = null;

    /**
     * @param context           context参数
     * @param modulePackageName 当前模块包名
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

    @SuppressLint("PrivateApi")
    public static void initForStubActivity(Context ctx) {
        if (__stub_hooked) return;
        try {
            Class<?> clazz_ActivityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz_ActivityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object sCurrentActivityThread = currentActivityThread.invoke(null);
            Field mInstrumentation = clazz_ActivityThread.getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(sCurrentActivityThread);
            mInstrumentation.set(sCurrentActivityThread, new MyInstrumentation(instrumentation));
            //End of Instrumentation
            Field field_mH = clazz_ActivityThread.getDeclaredField("mH");
            field_mH.setAccessible(true);
            Handler oriHandler = (Handler) field_mH.get(sCurrentActivityThread);
            Field field_mCallback = Handler.class.getDeclaredField("mCallback");
            field_mCallback.setAccessible(true);
            Handler.Callback current = (Handler.Callback) field_mCallback.get(oriHandler);
            if (current == null || !current.getClass().getName().equals(MyH.class.getName())) {
                field_mCallback.set(oriHandler, new MyH(current));
            }
            //End of Handler
            Class activityManagerClass;
            Field gDefaultField;
            try {
                activityManagerClass = Class.forName("android.app.ActivityManagerNative");
                gDefaultField = activityManagerClass.getDeclaredField("gDefault");
            } catch (Exception err1) {
                try {
                    activityManagerClass = Class.forName("android.app.ActivityManager");
                    gDefaultField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
                } catch (Exception err2) {
                    loge("JavaUtil : FATAL : Unable to get IActivityManagerSingleton");
                    log(err1);
                    log(err2);
                    return;
                }
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance = mInstanceField.get(gDefault);
            Object proxy = Proxy.newProxyInstance(
                    Initiator.class.getClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new MyIActivityManager(mInstance));
            mInstanceField.set(gDefault, proxy);
            //End of IActivityManager
            try {
                Class activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");
                Field fIActivityTaskManagerSingleton = activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
                fIActivityTaskManagerSingleton.setAccessible(true);
                Object singleton = fIActivityTaskManagerSingleton.get(null);
                singletonClass.getMethod("get").invoke(singleton);
                Object mDefaultTaskMgr = mInstanceField.get(singleton);
                Object proxy2 = Proxy.newProxyInstance(
                        Initiator.class.getClassLoader(),
                        new Class[]{Class.forName("android.app.IActivityTaskManager")},
                        new MyIActivityManager(mDefaultTaskMgr));
                mInstanceField.set(singleton, proxy2);
            } catch (Exception err3) {
                //log(err3);
                //ignore
            }
            //End of IActivityTaskManager
            __stub_hooked = true;
        } catch (Exception e) {
            log(e);
        }
    }

    //    public static void loadPlugin(DexClassLoader dexClassLoader, Context ctx) {
//        try {
//            //获取自己的dexElements
//            PathClassLoader pathClassLoader = (PathClassLoader) ctx.getClassLoader();
//
//            Field pathListField = findField(pathClassLoader.getClass(), "pathList");
//            Object pathListObject = pathListField.get(pathClassLoader);
//
//            Field dexElementsField = findField(pathListObject.getClass(), "dexElements");
//            Object[] dexElementsObject = (Object[]) dexElementsField.get(pathListObject);
//
//            Field pluginPathListField = findField(dexClassLoader.getClass(), "pathList");
//            Object pluginPathListObject = pluginPathListField.get(dexClassLoader);
//
//            Field pluginDexElementsField = findField(pluginPathListObject.getClass(), "dexElements");
//            Object[] pluginDexElementsObject = (Object[]) pluginDexElementsField.get(pluginPathListObject);
//
//            Class<?> elementClazz = dexElementsObject.getClass().getComponentType();
//            Object newDexElements = Array.newInstance(elementClazz, pluginDexElementsObject.length + dexElementsObject.length);
//            System.arraycopy(pluginDexElementsObject, 0, newDexElements, 0, pluginDexElementsObject.length);
//            System.arraycopy(dexElementsObject, 0, newDexElements, pluginDexElementsObject.length, dexElementsObject.length);
//
//            //设置
//            dexElementsField.set(pathListObject, newDexElements);
//        } catch (Throwable e) {
//            log(e);
//        }
//    }
    public static void loadPlugin(Context context, String dexPath) throws IllegalAccessException, ClassNotFoundException {
        //判断dex是否存在
        File dex = new File(dexPath);
        if (!dex.exists()) {
            logw("JavaUtil : DexFile doesn't exist.");
            return;
        }

        //获取自己的dexElements
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();

        Field pathListField = findField(pathClassLoader.getClass(), "pathList");
        Object pathListObject = pathListField.get(pathClassLoader);

        Field dexElementsField = findField(pathListObject.getClass(), "dexElements");
        Object[] dexElementsObject = (Object[]) dexElementsField.get(pathListObject);

        //获取dex中的dexElements
        File odex = context.getDir("odex", Context.MODE_PRIVATE);
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, odex.getAbsolutePath(), null, Initiator.class.getClassLoader());
//        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, odex.getAbsolutePath(), null, pathClassLoader);

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
        try {
            dexClassLoader.loadClass("com.google.android.material.R");
        } catch (ClassNotFoundException e) {
            loge("JavaUtil : 致命 : 加载 AndroidX 失败");
            throw e;
        }
    }

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

    public static void replaceClassLoader(ClassLoader classLoader) throws Exception {
        // 1. 获取ActivityThread类对象
        // android.app.ActivityThread
        // 1.1 获取类类型
        Class<?> clzActivityThread = Class.forName("android.app.ActivityThread");
        // 1.2 获取类方法
        Method currentActivityThread = clzActivityThread.getMethod("currentActivityThread");
        // 1.3 调用方法
        currentActivityThread.setAccessible(true);
        Object objActivityThread = currentActivityThread.invoke(null);
        // 2. 通过类对象获取成员变量mBoundApplication
        //clzActivityThread.getDeclaredField()
        Field field = clzActivityThread.getDeclaredField("mBoundApplication");
        // AppBindData
        field.setAccessible(true);
        Object data = field.get(objActivityThread);
        // 3. 获取mBoundApplication对象中的成员变量info
        // 3.1 获取 AppBindData 类类型
        Class clzAppBindData = Class.forName("android.app.ActivityThread$AppBindData");
        // 3.2 获取成员变量info
        Field field1 = clzAppBindData.getDeclaredField("info");
        // 3.3 获取对应的值
        //LoadedApk
        field1.setAccessible(true);
        Object info = field1.get(data);
        // 4. 获取info对象中的mClassLoader
        // 4.1 获取 LoadedApk 类型
        Class<?> clzLoadedApk = Class.forName("android.app.LoadedApk");
        // 4.2 获取成员变量 mClassLoader
        Field field2 = clzLoadedApk.getDeclaredField("mClassLoader");
        field2.setAccessible(true);
        field2.set(info, classLoader);
    }
}



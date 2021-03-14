/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-2022 chinese.he.amber@gmail.com
 * https://github.com/GoldenHuaji/QScript
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
package me.tsihen.qscript.util;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.BaseDexClassLoader;
import me.tsihen.qscript.MainHook;
import me.tsihen.qscript.R;

import static me.tsihen.qscript.util.ReflexUtils.callMethod;
import static me.tsihen.qscript.util.ReflexUtils.getObject;
import static me.tsihen.qscript.util.Utils.log;
import static me.tsihen.qscript.util.Utils.loge;

@SuppressLint("PrivateApi")
public class JavaUtil {
    private static boolean __stub_hooked = false;
    private static String sModulePath = null;

    // From QNotified
    @FromQNotified
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

    // From QNotified
    @FromQNotified
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
                Object[] dexElements = getObject(pathList, "dexElements", null);
                assert dexElements != null;
                for (Object element : dexElements) {
                    File file = getObject(element, "path", null);
                    if (file == null || file.isDirectory())
                        file = getObject(element, "zip", null);
                    if (file == null || file.isDirectory())
                        file = getObject(element, "file", null);
                    if (file != null && !file.isDirectory()) {
                        String path = file.getPath();
                        if (modulePath == null || !modulePath.contains("me.tsihen.qscript")) {
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

    public static void replaceClassLoader(ClassLoader selfLoader, ClassLoader hostLoader, Context ctx) throws Exception {
        // 1. 获取ActivityThread类对象
//         android.app.ActivityThread
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
        field2.set(info, new androidxLoader(hostLoader));
    }

    /**
     * 使用这个类来解决一些莫名其妙的问题
     */
    private static class androidxLoader extends ClassLoader {
        private final ClassLoader hostLoader;

        public androidxLoader(ClassLoader hostLoader) {
            this.hostLoader = hostLoader;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            // 优先使用本模块的 ClassLoader 加载
            try {
                if (name.startsWith("androidx") || name.startsWith("com.google.android.material.") || name.startsWith("me.tsihen.qscript.ui."))
                    return Initiator.class.getClassLoader().loadClass(name);
            } catch (Exception ignored) {
            }
            return hostLoader.loadClass(name);
        }

        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return hostLoader.loadClass(name);
        }

        @Override
        public URL getResource(String name) {
            return hostLoader.getResource(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return hostLoader.getResources(name);
        }

        @Override
        protected URL findResource(String name) {
            return hostLoader.getResource(name);
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            return hostLoader.getResources(name);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return hostLoader.getResourceAsStream(name);
        }

        @Override
        protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
            try {
                Method m = ClassLoader.class.getDeclaredMethod("definePackage", String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class);
                m.setAccessible(true);
                try {
                    return (Package) m.invoke(hostLoader, name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        protected Package getPackage(String name) {
            return (Package) callMethod(hostLoader, "getPackage", name, String.class);
        }

        @Override
        protected Package[] getPackages() {
            return (Package[]) callMethod(hostLoader, "getPackages");
        }

        @Override
        protected String findLibrary(String libname) {
            return (String) callMethod(hostLoader, "findLibrary", libname, String.class);
        }

        @Override
        public void setDefaultAssertionStatus(boolean enabled) {
            hostLoader.setDefaultAssertionStatus(enabled);
        }

        @Override
        public void setPackageAssertionStatus(String packageName, boolean enabled) {
            hostLoader.setPackageAssertionStatus(packageName, enabled);
        }

        @Override
        public void setClassAssertionStatus(String className, boolean enabled) {
            hostLoader.setClassAssertionStatus(className, enabled);
        }

        @Override
        public void clearAssertionStatus() {
            hostLoader.clearAssertionStatus();
        }
    }
}



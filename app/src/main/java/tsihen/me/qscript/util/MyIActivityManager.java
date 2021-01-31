package tsihen.me.qscript.util;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyIActivityManager implements InvocationHandler {
    private final Object mOrigin;

    MyIActivityManager(Object origin) {
        mOrigin = origin;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            int index = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Intent raw = (Intent) args[index];
                ComponentName component = raw.getComponent();
                Context hostApp = Utils.getQqApplication();
                if (hostApp != null && component != null
                        && hostApp.getPackageName().equals(component.getPackageName())
                        && component.getClassName().startsWith("tsihen.me.qscript.activity.")) {
                    Intent wrapper = new Intent();
                    wrapper.setExtrasClassLoader(Initiator.class.getClassLoader());
                    wrapper.setClassName(component.getPackageName(), ConstsKt.STUB_DEFAULT_ACTIVITY);
                    wrapper.putExtra(ConstsKt.ACTIVITY_PROXY_INTENT, raw);
                    args[index] = wrapper;
                }
            }
        }
        try {
            return method.invoke(mOrigin, args);
        } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }
}
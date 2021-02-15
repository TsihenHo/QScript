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
package me.tsihen.qscript.util;
// This file is copy from QNotified.

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@FromQNotified
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
                Context hostApp = QQFields.getQQApplication();
                if (hostApp != null && component != null
                        && hostApp.getPackageName().equals(component.getPackageName())
                        && component.getClassName().startsWith("me.tsihen.qscript.activity.")) {
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
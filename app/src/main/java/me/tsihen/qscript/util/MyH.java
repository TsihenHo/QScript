/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-20222 chinese.he.amber@gmail.com
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


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static me.tsihen.qscript.util.ConstsKt.JUMP_ACTION_CHECK_ACTIVITY;
import static me.tsihen.qscript.util.ConstsKt.JUMP_ACTION_CMD;
import static me.tsihen.qscript.util.Utils.log;

public class MyH implements Handler.Callback {
    private final Handler.Callback mDefault;

    public MyH(Handler.Callback def) {
        mDefault = def;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 100) { // LAUNCH_ACTIVITY
            try {
                Object record = msg.obj;
                Field field_intent = record.getClass().getDeclaredField("intent");
                field_intent.setAccessible(true);
                Intent intent = (Intent) field_intent.get(record);
                //log("handleMessage/100: " + intent);
                Bundle bundle = null;
                try {
                    Field fExtras = Intent.class.getDeclaredField("mExtras");
                    fExtras.setAccessible(true);
                    bundle = (Bundle) fExtras.get(intent);
                } catch (Exception e) {
                    log(e);
                }
                if (bundle != null) {
                    bundle.setClassLoader(Initiator.getHostClassLoader());
                    if (intent.hasExtra(ConstsKt.ACTIVITY_PROXY_INTENT)) {
                        Intent realIntent = intent.getParcelableExtra(ConstsKt.ACTIVITY_PROXY_INTENT);
                        realIntent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_CHECK_ACTIVITY);
                        field_intent.set(record, realIntent);
                    }
                }
            } catch (Exception e) {
                log(e);
            }
        } else if (msg.what == 159) {
            // EXECUTE_TRANSACTION
            Object clientTransaction = msg.obj;
            try {
                if (clientTransaction != null) {
                    Method getCallbacks = Class.forName("android.app.servertransaction.ClientTransaction").getDeclaredMethod("getCallbacks");
                    getCallbacks.setAccessible(true);
                    List clientTransactionItems = (List) getCallbacks.invoke(clientTransaction);
                    if (clientTransactionItems != null && clientTransactionItems.size() > 0) {
                        for (Object item : clientTransactionItems) {
                            Class c = item.getClass();
                            if (c.getName().contains("LaunchActivityItem")) {
                                Field fmIntent = c.getDeclaredField("mIntent");
                                fmIntent.setAccessible(true);
                                Intent wrapper = (Intent) fmIntent.get(item);
                                Bundle bundle = null;
                                try {
                                    Field fExtras = Intent.class.getDeclaredField("mExtras");
                                    fExtras.setAccessible(true);
                                    bundle = (Bundle) fExtras.get(wrapper);
                                } catch (Exception e) {
                                    log(e);
                                }
                                if (bundle != null) {
                                    bundle.setClassLoader(Initiator.getHostClassLoader());
                                    if (wrapper.hasExtra(ConstsKt.ACTIVITY_PROXY_INTENT)) {
                                        Intent realIntent = wrapper.getParcelableExtra(ConstsKt.ACTIVITY_PROXY_INTENT);
                                        realIntent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_CHECK_ACTIVITY);
                                        fmIntent.set(item, realIntent);
                                        //log("unwrap, real=" + realIntent);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log(e);
            }
        }
        if (mDefault != null) {
            return mDefault.handleMessage(msg);
        }
        return false;
    }
}

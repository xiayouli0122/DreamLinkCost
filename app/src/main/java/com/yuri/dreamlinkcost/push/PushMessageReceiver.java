package com.yuri.dreamlinkcost.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yuri.dreamlinkcost.log.Log;

import cn.bmob.push.PushConstants;

public class PushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.d("客户端收到推送内容："+intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
        }
    }
}

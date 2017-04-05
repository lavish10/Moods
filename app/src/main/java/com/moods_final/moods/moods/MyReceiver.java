package com.moods_final.moods.moods;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.i("App", "called receiver method");
		try{
			Utils.generateNotification(context);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

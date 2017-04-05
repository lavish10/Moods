package com.moods_final.moods.moods;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

@SuppressWarnings("deprecation")
public class Utils {
    public static String quote="";
    public static String author="";
	public static NotificationManager mManager;
    public static Context context1;
	@SuppressWarnings("static-access")
	public static void generateNotification(Context context){

		context1=context;
		getData();
				}
	private static void getData() {
		Random rand = new Random();
		int n = rand.nextInt(90);
		String id= Integer.toString(n);

		String url = Config.DATA_URL+id;

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				showJSON(response);
			}
		},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});

		RequestQueue requestQueue = Volley.newRequestQueue(context1);
		requestQueue.add(stringRequest);
	}

	private static void showJSON(String response){

		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
			JSONObject collegeData = result.getJSONObject(0);
			quote = collegeData.getString(Config.KEY_QUOTE);
			author = collegeData.getString(Config.KEY_AUTHOR);

		} catch (JSONException e) {
			e.printStackTrace();
		}
          //   Intent intent1 = new Intent(context1,MainActivity.class);
        Intent intent1 = new Intent();
        intent1.setClassName(context1, BReceiver.class.getName());
        intent1.setAction("Test");

        PendingIntent pendingNotificationIntent = PendingIntent.getBroadcast(context1,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context1)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MOODS")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText("Quote:\t"+quote)
                .setAutoCancel(true)
                .setContentIntent(pendingNotificationIntent);
	/*	NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
		bigText.bigText("Quote of the day");
		bigText.setBigContentTitle("MOODS");
		bigText.setSummaryText("Quote:\t"+quote+"\nAuthor:\t" +author);
		notificationBuilder.setStyle(bigText);
		notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
      */  NotificationManager notificationManager =
                (NotificationManager) context1.getSystemService(context1.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

	}
}
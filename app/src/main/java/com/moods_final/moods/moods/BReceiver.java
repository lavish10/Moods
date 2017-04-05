package com.moods_final.moods.moods;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.moods_final.moods.moods.Utils.author;
import static com.moods_final.moods.moods.Utils.quote;


/**
 * Created by naincy on 7/12/16.
 */
public class BReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("Test", "########## intent action "+ intent.getAction());
        Toast.makeText(context, "Quote:\t"+quote+"\nAuthor:\t" +author, Toast.LENGTH_LONG).show();

     /*   new AlertDialog.Builder(context1).
                setCancelable(false).
                setTitle("Quote of the day").
                setMessage("Quote:\t"+quote+"\nAuthor:\t" +author).
                setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.exit(0);
                    }
                }).create().show();
*/
    }

}

package com.moods_final.moods.moods;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class GestureActivity extends AppCompatActivity {
    private GestureLibrary gLib;
    private static String mood="";
    // The URI of photo taken with camera
    private Uri mUriPhotoTaken;
    private ImageView imvSignature;
    private static final String TAG = "GestureActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        openOptionsMenu();
        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        gLib.load();

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(handleGestureListener);
        gestures.setGestureStrokeAngleThreshold(90.0f);
    }

    /**
     * our gesture listener
     */
    private OnGesturePerformedListener handleGestureListener;

    {
        handleGestureListener = new OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView gestureView,
                                           Gesture gesture) {

                ArrayList<Prediction> predictions = gLib.recognize(gesture);
                Log.d(TAG, "recognize");

                // one prediction needed
                // predictions.size() indicates the number of gestures that matched the path the user drew on the screen.
                if (predictions.size() > 0) {
                    //list is ranked in order from best match (at position 0)
                    Prediction prediction = predictions.get(0);
                    // checking prediction
                    //prediction.score :- level of confidence the framework has that a given Prediction is a match
                    if (prediction.score > 1.0) {
                        // and action
                        mood=prediction.name;
                        Toast.makeText(GestureActivity.this, prediction.name,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                try {


                    gestureView.setDrawingCacheEnabled(true);


                    Bitmap bm = Bitmap.createBitmap(gestureView.getDrawingCache());

                    ImageView imvSignature = (ImageView) findViewById(R.id.imvSignature);
                    imvSignature.setImageBitmap(bm);


                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "gesture.jpeg");


                    f.createNewFile();


                    FileOutputStream os;


                    os = new FileOutputStream(f);


                    //compress to specified format (PNG), quality - which is ignored for PNG, and out stream


                    bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    gestureView.setDrawingCacheEnabled(false);

                    os.close();


                } catch (Exception e) {


                    Log.v("Gestures", e.getMessage());


                    e.printStackTrace();


                }

                Intent intent = new Intent(getBaseContext(), RecognizeActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", 1);
                intent.putExtra("Mood", mood);
                startActivity(intent);
            }
        };
    }
}
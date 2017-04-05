//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Emotion-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.moods_final.moods.moods;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.moods_final.moods.entertainment.YouTubeAPIDemoActivity;
import com.moods_final.moods.feed.FeedActivity;
import com.moods_final.moods.moods.helper.ImageHelper;
import com.moods_final.moods.moods.helper.SelectImageActivity;
import com.moods_final.moods.recommend.AndroidFoursquare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecognizeActivity extends AppCompatActivity implements View.OnClickListener{


    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The button to select an image
    private ImageButton mButtonSelectImage;
    private Button proceed;

     private TextView textmood;
//The button for logout
    private Button buttonLogout;
    private Button places;
    private TextView textViewUserEmail;
    private TextView name;
    private FirebaseAuth firebaseAuth;
    private Button entertainment;
    private Button feedbtn;
    private Button chat;
    private Button musicbtn;
    private Button insta;
    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private EditText mEditText;

    private EmotionServiceClient client;
   // List<Map.Entry<String, Double>> collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_recognize);
        proceed=(Button)findViewById(R.id.button2);

        proceed.setOnClickListener(this);
        proceed.setEnabled(false);
        textmood=(TextView) findViewById(R.id.textmood);
        entertainment=(Button)findViewById(R.id.entertainment);
        entertainment.setOnClickListener(this);
        places=(Button)findViewById(R.id.places);
        places.setOnClickListener(this);
        chat=(Button)findViewById(R.id.feedbtn);
        chat.setOnClickListener(this);
        feedbtn=(Button)findViewById(R.id.button6);
        feedbtn.setOnClickListener(this);
        musicbtn=(Button)findViewById(R.id.button7);
        musicbtn.setOnClickListener(this);
        insta=(Button)findViewById(R.id.button8);
        insta.setOnClickListener(this);
        int value = getIntent().getIntExtra("EXTRA_SESSION_ID",0);

        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(value == 1) {
            String mood = getIntent().getStringExtra("Mood");
            setContentView(R.layout.activity_recognize);
            File imageFile = new File("/storage/emulated/0/gesture.jpeg");
            ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
            if (imageFile.exists()) {
                Toast.makeText(this, mood, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "image location correct", Toast.LENGTH_LONG).show();

                imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));


//                mEditText = (EditText) findViewById(R.id.editTextResult);
//                mEditText.setText(mood);
                textmood.setText(mood);
                Toast.makeText(getApplicationContext(),mood,Toast.LENGTH_SHORT).show();
                final_mood=mood;
                proceed.setEnabled(true);
//                Intent i=new Intent(RecognizeActivity.this,NextActivity)
            }



        }

        else{
            Intent intent;
            intent = new Intent(RecognizeActivity.this, com.moods_final.moods.moods.helper.SelectImageActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        }

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }


        mButtonSelectImage = (ImageButton) findViewById(R.id.buttonSelectImage);
//        mEditText = (EditText) findViewById(R.id.editTextResult);
        buttonLogout =(Button) findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }




        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        Intent i=new Intent(RecognizeActivity.this, SelectImageActivity.class);
        startActivity(i);
    }

    public void doRecognize() {
        mButtonSelectImage.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
//            new doRequest(false).execute();
        } catch (Exception e) {
            mEditText.append("Error encountered. Exception is: " + e.toString());
        }

        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        if (faceSubscriptionKey.equalsIgnoreCase("Please_add_the_face_subscription_key_here")) {
            mEditText.append("\n\nThere is no face subscription key in res/values/strings.xml. Skip the sample for detecting emotions using face rectangles\n");
        } else {
            // Do emotion detection using face rectangles provided by Face API.
            try {
                new doRequest(true).execute();
            } catch (Exception e) {
                mEditText.append("Error encountered. Exception is: " + e.toString());
            }
        }
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
//        mEditText.setText("");

        Intent intent;
        intent = new Intent(RecognizeActivity.this, com.moods_final.moods.moods.helper.SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }
    public void drawImage(View view){
       // finish();
        startActivity(new Intent(this, GestureActivity.class));
    }
    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("RecognizeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Log.d("RecognizeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());

                        doRecognize();
                    }
                }
                break;
            default:
                break;
        }
    }


    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long startTime = System.currentTimeMillis();
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE STARTS HERE
        // -----------------------------------------------------------------------

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE ENDS HERE
        // -----------------------------------------------------------------------
        return result;
    }

    private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long timeMark = System.currentTimeMillis();
        Log.d("emotion", "Start face detection using Face API");
        FaceRectangle[] faceRectangles = null;
        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
        Face faces[] = faceClient.detect(inputStream, false, false, null);
        Log.d("emotion", String.format("Face detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        if (faces != null) {
            faceRectangles = new FaceRectangle[faces.length];

            for (int i = 0; i < faceRectangles.length; i++) {
                // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
            }
        }

        List<RecognizeResult> result = null;
        if (faceRectangles != null) {
            inputStream.reset();

            timeMark = System.currentTimeMillis();
            Log.d("emotion", "Start emotion detection using Emotion API");
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE STARTS HERE
            // -----------------------------------------------------------------------
            result = this.client.recognizeImage(inputStream, faceRectangles);

            String json = gson.toJson(result);
            Log.d("result", json);
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE ENDS HERE
            // -----------------------------------------------------------------------
            Log.d("emotion", String.format("Emotion detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            LoginManager.getInstance().logOut();
           // if (savedInstanceState == null) {
                startActivity(new Intent(this, LoginActivity.class));
            //}
        }
        if(view==proceed)
        {
            Log.e("","Befor intent");
            Intent i= new Intent(RecognizeActivity.this,NextActivity.class);
            i.putExtra("mood",final_mood);
            Log.e("","Befor call");
            startActivity(i);
        }
        if(view==places)
        {
            Log.e("","Befor intent");
            Intent i= new Intent(RecognizeActivity.this,AndroidFoursquare.class);
            i.putExtra("mood",final_mood);
            Log.e("","Befor call");
            startActivity(i);
        }
        if(view==entertainment)
        {
            Intent i= new Intent(RecognizeActivity.this, YouTubeAPIDemoActivity.class);
            i.putExtra("mood",final_mood);
            startActivity(i);
        }
        if(view==chat)
        {
            Intent i= new Intent(RecognizeActivity.this, com.moods_final.moods.feed.FeedActivity.class);
            i.putExtra("mood",final_mood);
            startActivity(i);
        }
        if(view==feedbtn)
        {
            Intent i= new Intent(RecognizeActivity.this, com.moods_final.moods.feeds.AllFeed.class);
            i.putExtra("mood",final_mood);
            startActivity(i);
        }
        if(view==musicbtn)
        {
            Intent i= new Intent(RecognizeActivity.this, com.moods_final.moods.entertainment.VideoListSearchActivity.class);
            i.putExtra("mood",final_mood);
            startActivity(i);
        }
        if(view==insta)
        {
            Intent i= new Intent(RecognizeActivity.this, com.moods_final.moods.insta_mood.Insta.class);
            startActivity(i);

        }

    }
    String final_mood ="";
    Double d=0.0;

    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;
        private boolean useFaceRectangles = false;

        public doRequest(boolean useFaceRectangles) {
            this.useFaceRectangles = useFaceRectangles;
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (this.useFaceRectangles == false) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            } else {
                try {
                    return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Display based on error existence

            if (this.useFaceRectangles == false) {
               // mEditText.append("\n\nRecognizing emotions with auto-detected face rectangles...\n");
            } else {
//                mEditText.append("\n\nRecognizing emotions with existing face rectangles from Face API...\n");
            }
            if (e != null) {
                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                this.e = null;
            } else {
                if (result.size() == 0) {
//                    mEditText.append("No emotion detected :(");
                    Toast.makeText(getApplicationContext(),"No Mood Detected",Toast.LENGTH_LONG).show();
                } else {
                    Integer count = 0;
                    // Covert bitmap to a mutable bitmap by copying it
                    Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas faceCanvas = new Canvas(bitmapCopy);
                    faceCanvas.drawBitmap(mBitmap, 0, 0, null);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);
                    //
                    //

                    for (RecognizeResult r : result) {

//                        mEditText.append(String.format("\nFace #%1$d \n", count+1));

                        if(r.scores.anger>=d){  d=r.scores.anger;   final_mood="Anger";      }
//                        mEditText.append(String.format("\t anger: %1$.5f\n", r.scores.anger));
                        if(r.scores.contempt>=d){  d=r.scores.contempt;   final_mood="Contempt";      }
//                        mEditText.append(String.format("\t contempt: %1$.5f\n", r.scores.contempt));
                        if(r.scores.disgust>=d){  d=r.scores.disgust;   final_mood="Disgust";      }
//                        mEditText.append(String.format("\t disgust: %1$.5f\n", r.scores.disgust));
                        if(r.scores.fear>=d){  d=r.scores.fear;   final_mood="Fear";      }
//                        mEditText.append(String.format("\t fear: %1$.5f\n", r.scores.fear));
                        if(r.scores.happiness>=d){  d=r.scores.happiness;   final_mood="Happiness";      }
//                        mEditText.append(String.format("\t happiness: %1$.5f\n", r.scores.happiness));
                        if(r.scores.neutral>=d){  d=r.scores.neutral;   final_mood="Neutral";      }
//                        mEditText.append(String.format("\t neutral: %1$.5f\n", r.scores.neutral));
                        if(r.scores.sadness>=d){  d=r.scores.sadness;   final_mood="Sadness";      }
//                        mEditText.append(String.format("\t sadness: %1$.5f\n", r.scores.sadness));
                        if(r.scores.surprise>=d){  d=r.scores.surprise;   final_mood="Surprise";      }
//                        mEditText.append(String.format("\t surprise: %1$.5f\n", r.scores.surprise));

//                        mEditText.append(String.format("\t face rectangle: %d, %d, %d, %d", r.faceRectangle.left, r.faceRectangle.top, r.faceRectangle.width, r.faceRectangle.height));;

                        faceCanvas.drawRect(r.faceRectangle.left,
                                r.faceRectangle.top,
                                r.faceRectangle.left + r.faceRectangle.width,
                                r.faceRectangle.top + r.faceRectangle.height,
                                paint);
                        count++;
                    }
                    Toast.makeText(getApplicationContext(),final_mood,Toast.LENGTH_SHORT).show();
                    textmood.setText(final_mood);
                    proceed.setEnabled(true);
                    ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                    imageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
                }
//                mEditText.setSelection(0);
            }

            mButtonSelectImage.setEnabled(true);
        }
    }
}


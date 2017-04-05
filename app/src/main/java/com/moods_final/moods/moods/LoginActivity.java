package com.moods_final.moods.moods;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moods_final.moods.feed.User;
import com.moods_final.moods.feeds.Facebook_complete;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private LoginButton loginButton;
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private CallbackManager callbackManager;
    private static final String TAG = "FacebookLogin";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    String fb_id="",fb_name="",fb_birthday="",fb_image="",fb_gender="";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth= FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {
                FirebaseUser user = firebaseAuth1.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Toast.makeText(getApplicationContext(),"Already signed in",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(getApplicationContext(), RecognizeActivity.class));

                } else {
                    // User is signed out
                    //  Toast.makeText(getApplicationContext(),"Signed out!",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

      /*  if(firebaseAuth.getCurrentUser()!=null){
            //profile activity here
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }*/

        editTextEmail =(AutoCompleteTextView)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        textViewSignup=(TextView)findViewById(R.id.textViewSignUp);
        buttonSignIn=(Button)findViewById(R.id.buttonSignin);

        progressDialog = new ProgressDialog(this);


        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        loginButton = (LoginButton) findViewById(R.id.login_button2);
        loginButton.setReadPermissions(Arrays.asList("user_friends","user_likes","user_posts","user_birthday","user_photos"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                goMainScreen();
                get_main_data();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "facebook:onError", error);
            }
        });


    }
    public void get_main_data()
    {


        FetchBasicDetails fbd=new FetchBasicDetails();
        fbd.execute();



    }

    class FetchBasicDetails extends AsyncTask<Void,Void,Void> {
//        ProgressDialog pg=new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pg.setMessage("Fetching basic details");
//            pg.show();
            fb_id = "";
            fb_name ="";
            fb_gender ="";
            fb_birthday="";
            fb_image="";

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            pg.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {


            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            try {
                                Log.e("response",object.toString());
                                JSONObject jsonObject = response.getJSONObject();
                                fb_id = jsonObject.getString("id");
                                fb_name = jsonObject.getString("name");
                                fb_gender = jsonObject.getString("gender");
                                fb_birthday=jsonObject.getString("birthday");
                                fb_image=jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");

//                                Adding in shared prefer
                                SharedPreferences.Editor editor = getSharedPreferences("FB_USER", MODE_PRIVATE).edit();
                                editor.putString("own_name", fb_name);
                                editor.putString("own_id", fb_id);
                                editor.commit();

                                Log.d("INFO","Name "+fb_name+" id: "+fb_id +" fb_birthday: "+fb_birthday );
                                Log.d("photo",fb_image);
                            }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }catch (NullPointerException w)
                            {
                                w.printStackTrace();
                            }

                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,birthday,name,picture.height(300),gender");
            request.setParameters(parameters);
            request.executeAsync();
            write_in_Firebase();
//            Glide.with(getContext()).load(fb_image).into(iv1);

            return null;
        }
    }
    public void write_in_Firebase()
    {

        User user=new User();
        user.set_basic_fb(fb_id,fb_name,fb_birthday,fb_image,fb_gender,"Facebook");
//        user.set_likes(likes);

        user.update_firedb();

    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this,RecognizeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    // [END auth_with_facebook]



    private void userLogin(){
        String email=editTextEmail.getText().toString().trim();
        String password= editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is Empty
            Toast.makeText(this,"Please enter email!",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is Empty
            Toast.makeText(this,"Please enter password!",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        // If validations are ok
        //we will first show progress bar
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                   .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {

                           progressDialog.dismiss();

                           if(task.isSuccessful()){
                               //start the profile activity
                               finish();
                               startActivity(new Intent(getApplicationContext(), RecognizeActivity.class));

                           }
                       }
                   });
    }

    @Override
    public void onClick(View view) {
        if(view==buttonSignIn) {
            userLogin();
        }
        if(view==textViewSignup){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }
}

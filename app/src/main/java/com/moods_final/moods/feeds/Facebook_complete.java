package com.moods_final.moods.feeds;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.moods_final.moods.feed.User;
import com.moods_final.moods.moods.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Facebook_complete.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Facebook_complete#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Facebook_complete extends Fragment implements View.OnClickListener {
    CallbackManager callbackManager;
    Button btn2,btn3,btn4,btn5;
    ListView listView;
    ImageView iv1;
    ArrayList<String> posts;
    ArrayList<String> likes ;
    TextView mylabel;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    String fb_id="",fb_name="",fb_birthday="",fb_image="",fb_gender="";
    SharedPreferences sharedPreferences;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public Facebook_complete(){

    }
    public static Facebook_complete newInstance(int page, String title) {
        Facebook_complete fragmentFirst = new Facebook_complete();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Facebook_complete.
     */
    // TODO: Rename and change types and number of parameters
    public static Facebook_complete newInstance(String param1, String param2) {
        Facebook_complete fragment = new Facebook_complete();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_facebook_complete, container, false);
//        btn2=(Button)view.findViewById(R.id.button2);
        btn3=(Button)view.findViewById(R.id.button3);
        btn4=(Button)view.findViewById(R.id.button4);
        listView=(ListView) view.findViewById(R.id.list_view);
        mylabel=(TextView)view.findViewById(R.id.mylabel);
        btn5=(Button) view.findViewById(R.id.button5);
//        iv1=(ImageView)view.findViewById(R.id.imageView);
        btn5.setOnClickListener(this);
        likes = new ArrayList<String>();
        posts=new ArrayList<String>();
//        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
//        tv1.setMovementMethod(new ScrollingMovementMethod());
        AppEventsLogger.activateApp(getContext());
        FacebookSdk.sdkInitialize(getContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        final LoginButton loginButton = (LoginButton)view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("user_friends","user_likes","user_posts","user_birthday","user_photos"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                   /*/*/*/*/**/*/*/*/*//**/
                accessToken=loginResult.getAccessToken();
                get_main_data();

            }   //loginResult

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

        });
//        get_main_data();

        return view;//inflater.inflate(R.layout.fragment_facebook_complete, container, false);


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    private void retreiveposts() {
        Fetchdata ft=new Fetchdata();
        ft.execute();
        StringBuilder builder = new StringBuilder();
        for (String details : posts) {
            builder.append(details + "\n");
        }

//        tv1.setText(builder.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("hello","fb");
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    public void onClick(View v) {
//        if(v.getId()==R.id.button2)
//        {
////            Context context=getActivity().getApplicationContext();
////            Intent intent=new Intent(context,Maptest.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            context.startActivity(intent);
//        }
        if(v.getId()==R.id.button3)
        {   Toast.makeText(getContext(),"Retrieving Posts",Toast.LENGTH_LONG).show();
            retreiveposts();
        }
        if(v.getId()==R.id.button4)
        {
            Toast.makeText(getContext(),"Retrieving Likes",Toast.LENGTH_LONG).show();
            Fetchlikes fl= new Fetchlikes();
            fl.execute();
        }
        if(v.getId()==R.id.button5)
        {
            get_main_data();
//            Get_picture gp=new Get_picture();
//            gp.execute();
            Log.e("beforear","");

        }
    }

    ArrayList<String> ar;


    public void setLikesview(){
        mylabel.setText("Likes");
        listView.setAdapter(new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,likes));
    }
    public void setPostsLayout(){
        mylabel.setText("Posts");
        listView.setAdapter(new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,posts));
    }


    public void get_large_image(String id)
    {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        Log.e("777",id);

    GraphRequest request = GraphRequest.newGraphPathRequest(
            accessToken,
            id+"/picture?redirect=0", new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    // Insert your code here
                    Log.e("responsezzz", response.toString());
                    JSONObject jsonObject = response.getJSONObject();

                    try {
                        JSONObject jsa = jsonObject.getJSONObject("data");
                        Log.d("", jsa.toString());
                        Log.d("aaa:::", jsa.getString("url"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


            });
        Bundle parameters = new Bundle();

        parameters.putString("redirect", "0");
        request.setParameters(parameters);
        request.executeAsync();
    }
    public void get_main_data()
    {


        FetchBasicDetails fbd=new FetchBasicDetails();
        fbd.execute();
//       get_picture();
//        Get_picture gp=new Get_picture();
//        gp.execute();


    }
    public void write_in_Firebase()
    {

        User user=new User();
        user.set_basic_fb(fb_id,fb_name,fb_birthday,fb_image,fb_gender,"Facebook");
//        user.set_likes(likes);


        user.update_firedb();
        Toast.makeText(getContext(),"Saved in Database",Toast.LENGTH_SHORT).show();
    }

    class FetchBasicDetails extends AsyncTask<Void,Void,Void>{
//        ProgressDialog pg=new ProgressDialog(getActivity());

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
                            // Insert your code here
//                            Log.e("response",response.toString());
//                            Log.e("response",object.toString());
                            try {
                                Log.e("response",object.toString());
                                JSONObject jsonObject = response.getJSONObject();
                                fb_id = jsonObject.getString("id");
                                fb_name = jsonObject.getString("name");
                                fb_gender = jsonObject.getString("gender");
                                fb_birthday=jsonObject.getString("birthday");
                                fb_image=jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");

//                                Adding in shared prefer
                                SharedPreferences.Editor editor = getContext().getSharedPreferences("FB_USER", MODE_PRIVATE).edit();
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
            request.executeAndWait();
            write_in_Firebase();
//            Glide.with(getContext()).load(fb_image).into(iv1);

            return null;
        }
    }
    class Fetchdata extends AsyncTask<Void,Void,Void> {
//        ProgressDialog pgd=new ProgressDialog(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest;
        final String[] next = {""};
        @Override
        protected void onPreExecute() {
//            pgd.setMessage("Fetching Details");
//            pgd.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            pgd.dismiss();

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("aaa","entered");
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);


            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    "me/posts", new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here
                            JSONObject jsonObject = response.getJSONObject();

                            try {
                                JSONArray jsa = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsa.length(); i++) {
                                    if(!jsa.getJSONObject(i).isNull("story")) {
                                        Log.d("aaa:::", jsa.getJSONObject(i).getString("story"));
                                        posts.add(jsa.getJSONObject(i).getString("story"));
                                    }else if(!jsa.getJSONObject(i).isNull("message")) {
                                        Log.d("aaa:::", jsa.getJSONObject(i).getString("message"));
                                        posts.add(jsa.getJSONObject(i).getString("message"));
                                    }
                                }

                                if(jsonObject.getJSONObject("paging").has("next")) {
                                    Object aa = jsonObject.get("paging");
                                    JSONObject a = new JSONObject(aa.toString());
                                    Log.d("aaa::", a.getString("next"));
                                    next[0] = a.getString("next");
                                    make_request(next[0]);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });

            request.executeAndWait();
        /*Volley Request*/

            Log.d("aaa","Completed Graph Request");

            return null;
        }
        void make_request(String current_URL)
        {
            stringRequest = new StringRequest(Request.Method.GET, current_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("aa", response.toString());
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray jsa = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsa.length(); i++) {
                            Log.d("bbb::", i + "::::" + jsa.length());
                            if(!jsa.getJSONObject(i).isNull("story")) {
                                Log.d("aaa:::", jsa.getJSONObject(i).getString("story"));
                                posts.add(jsa.getJSONObject(i).getString("story"));
                                Log.d("aaa::", i + "::::" + jsa.length());
                            }
                            else if(!jsa.getJSONObject(i).isNull("message")){
                                Log.d("aaa:::", jsa.getJSONObject(i).getString("message"));
                                Log.d("aaa::", i + "::::" + jsa.length());
                                posts.add(jsa.getJSONObject(i).getString("message"));
                            }
                        }
                        /*
                        Log.e("erorr112",jsonObject.getJSONObject("paging").getString("next"));
*/                      String retvaluel=get_next(jsonObject);
                        if(retvaluel!="")
                        {
                            make_request(retvaluel);
                        }else
                        {
                            setPostsLayout();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(stringRequest);
        }
        String get_next(JSONObject jsonObject)
        {
            String nextURL="";
            try {
                if(jsonObject.has("paging")&&jsonObject.getJSONObject("paging").has("next"))
                {
                    nextURL = jsonObject.getJSONObject("paging").getString("next");

                    Log.e("nextURL",nextURL);

                }else{
                    nextURL="";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return nextURL;
        }
    }
    class Get_picture extends AsyncTask<Void,Void,Void>{
        ProgressDialog pg=new ProgressDialog(getContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setMessage("Fetching pictures");
            pg.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pg.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ar=new ArrayList<>();
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    "/me/photos/uploaded",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here
                            try {
                                Log.e("response",response.toString());
                                JSONObject jsonObject = response.getJSONObject();
                                JSONArray jsonArray_data=jsonObject.getJSONArray("data");
                                for (int i=0;i<jsonArray_data.length();i++)
                                {

                                    JSONObject js=jsonArray_data.getJSONObject(i);
                                    ar.add(js.getString("id"));
                                    Log.d("111",i+" "+js.getString("id"));
                                }

                            }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            for(int i=0;i<ar.size();i++)
                            {
                                Log.e("inar",i+"");
                                get_large_image(ar.get(i));
                            }
                        }
                    });

            request.executeAndWait();

            return null;
        }
    }

    class Fetchlikes extends  AsyncTask<Void,Void,Void>{
//        ProgressDialog pg=new ProgressDialog(getActivity());

        @Override
        protected void onPostExecute(Void aVoid) {
//            pg.dismiss();
            setLikesview();
        }

        @Override
        protected Void doInBackground(Void... params) {

            final String[] afterString = {""};
            final Boolean[] noData = {true};
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
            final HashMap<String, Object> result = new HashMap<>();
            do{
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        accessToken,
                        "/me/likes",new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                // Insert your code here
                                JSONObject jsonObject = response.getJSONObject();
                                try{
                                    if(jsonObject.length() > 1) {

                                        JSONObject jsonFacebook = (JSONObject) new JSONTokener(jsonObject.toString()).nextValue();
                                        JSONObject likes_paging = (JSONObject) new JSONTokener(jsonFacebook.getJSONObject("paging").toString()).nextValue();


                                        for (int i = 0; i < jsonFacebook.getJSONArray("data").length(); i++) {
                                            likes.add(jsonFacebook.getJSONArray("data").getJSONObject(i).getString("name"));
                                            Log.d("aaa:::::::",jsonFacebook.getJSONArray("data").getJSONObject(i).getString("name"));
                                            result.put("uid", jsonFacebook.getJSONArray("data").getJSONObject(i).getString("id"));
                                            }
                                        /*

                                        result.put("author", "author_value5");
                                        result.put("title", "title_value5");
                                        result.put("body", "body_value5");*/

//                                        String key = myRef.child("Users").push().getKey();
//                                        Map<String,Object> k=new HashMap<>();
//                                        k.put("/Users"+fb_id,result);
//                                        myRef.updateChildren(k);


                                        afterString[0] = (String) likes_paging.getJSONObject("cursors").get("after");

                                    }else{
                                        noData[0] = false;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("pretty", "0");
                parameters.putString("limit", "100");
                parameters.putString("after", afterString[0]);
                request.setParameters(parameters);
                request.executeAndWait();
            }while(noData[0] == true);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
//            pg.dismiss();

        }

        @Override
        protected void onPreExecute() {
//            pg.setMessage("Fetching suer likes");
//            pg.show();
        }
    }
}

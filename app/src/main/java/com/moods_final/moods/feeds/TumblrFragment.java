package com.moods_final.moods.feeds;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.moods_final.moods.moods.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TumblrFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TumblrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TumblrFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button tumblrbtn;
    com.android.volley.RequestQueue requestQueue;
    ArrayList<ArrayList<String>> articles = new ArrayList<ArrayList<String>>();
    ArrayList<String> moodtags=new ArrayList<String>();
    ListView listView;
    String mood="happiness";
    int flag=0;


    private OnFragmentInteractionListener mListener;

    public TumblrFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TumblrFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TumblrFragment newInstance(String param1, String param2) {
        TumblrFragment fragment = new TumblrFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_tumblr, container, false);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        AsyncTaskRunner runner = new AsyncTaskRunner();
        tumblrbtn=(Button)v.findViewById(R.id.tumblrbtn);
        tumblrbtn.setOnClickListener(this);
        runner.execute();


        return v;
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

    @Override
    public void onClick(View v) {
        if(v==tumblrbtn)
        {
            try {
                populate();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void getmapping() throws IOException {   String url = "http://moods.esy.es/feed.php?mood="+mood;

        /*requestQueue = Volley.newRequestQueue(getApplicationContext());
        //String id = editTextId.getText().toString().trim();
        Log.e("aaa","in getmapping()");


        Log.e("eee","url="+url);

        Log.e("aaa","before request");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("aaa","inside onResponse");
                String moodtag = "";

                Log.e("aaa","Json="+response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");

                    for(int i=0;i<result.length();i++) {
                        Log.e("aaa","tag="+result.getJSONObject(i).getString("keyword"));
                        JSONObject Data = result.getJSONObject(i);
                        moodtag = Data.getString("keyword");
                        Log.e("aaa=", moodtag);
                        moodtags.add(""+moodtag);
                    }
                    flag=1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("aaa","in errorResponse()");
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });


        requestQueue.add(stringRequest);
        */
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        ResponseHandler<String> responseHandler = new BasicResponseHandler();

        String response= null;

        response = client.execute(get, responseHandler);
        Log.e("aaa",response);
        String moodtag = "";

        Log.e("aaa","Json="+response);
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            //    result.length()
            for(int i=0;i<(result.length()>8?8:result.length());i++) {
                Log.e("aaa","tag="+result.getJSONObject(i).getString("keyword"));
                JSONObject Data = result.getJSONObject(i);
                moodtag = Data.getString("keyword");
                Log.e("aaa=", moodtag);
                moodtags.add(""+moodtag);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e("aaa","end of getmapping()");




    }

    private void populate() throws IOException, JSONException {


        Log.e("bbb","in populate()");
        flag=0; int k=0;
        for(int i=0;i<moodtags.size();i++) {

            String searchUrl = "https://api.tumblr.com/v2/tagged?tag=" + moodtags.get(i).toString() + "&api_key=FgJKYlReQoWRRMRTZZe3X1vOcvi0OKoYFYbopUP0sI9UNQlw1Y";
               /* requestQueue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, searchUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("aaa", "inside onResponse");
                        //showJSON(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject j1[] = null;
                            JSONArray arr1, arr = null;
                            String image, summary, url, blog;

                            arr = jsonObject.getJSONArray("response");
                            Log.e("aaa", response);
                            //JSONArray photos
                            for (int j = 0; j < arr.length(); j++) {
                                blog = jsonObject.getJSONArray("response").getJSONObject(j).getString("blog_name");/*getJSONArray("photos")*/
            //arr = jsonObject.getJSONArray("response");
            //Log.e("aaa","\n\n"+arr.get(0).toString());

             /*                   Log.e("mmm", "blog name=" + blog);
                                summary = arr.getJSONObject(j).getString("summary");
                                url = arr.getJSONObject(j).getString("post_url");
                                Log.e("mmm", "summary=" + summary + " ,url=" + url);
                                arr1 = arr.getJSONObject(j).getJSONArray("photos");
                                image = arr1.getJSONObject(0).getJSONArray("alt_sizes").getJSONObject(0).getString("url");
                                Log.e("mmm", "image=" + image);
                                articles.add(new ArrayList<String>());
                                articles.get(j).add("blog name=" + blog);
                                articles.get(j).add("\n\nsummary=" + summary);
                                articles.get(j).add("\n\nimage url=" + image);
                                articles.get(j).add("\n\npost url=" + url);
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.e("aaa", "JSONException1");
                            e.printStackTrace();
                        }
                        setLayout();

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("aaa", "in errorResponse1()");
                                //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });


                requestQueue.add(stringRequest);
            }*/

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(searchUrl);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            String responseBody = null;

            responseBody = client.execute(get, responseHandler);
            Log.e("aaa",responseBody);
            try {
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONObject j1[] = null;
                JSONArray arr1, arr = null;
                String image="", summary="", url="", blog="";

                arr = jsonObject.getJSONArray("response");
                Log.e("aaa", responseBody);
                //JSONArray photos
                //                <arr.length()
                for (int j = 0; j < 2; j++) {
                    try {
                        blog = jsonObject.getJSONArray("response").getJSONObject(j).getString("blog_name");/*getJSONArray("photos")*/

                        Log.e("mmm", "blog name=" + blog);
                        summary = arr.getJSONObject(i).getString("summary");
                        url = arr.getJSONObject(j).getString("post_url");
                        Log.e("mmm", "summary=" + summary + " ,url=" + url);
                        arr1 = arr.getJSONObject(j).getJSONArray("photos");
                        image = arr1.getJSONObject(0).getJSONArray("alt_sizes").getJSONObject(0).getString("url");
                        Log.e("mmm", "image=" + image);
//                  if(i==moodtags.size()-1)
//                      flag=1;
                        if(blog!=""&&summary!=""&&image!=""&&url!=""){
                            articles.add(new ArrayList<String>());
                            articles.get(k-flag).add("blog name=" + blog);
                            articles.get(k-flag).add("\n\nsummary=" + summary);
                            articles.get(k-flag).add("\n\nimage url=" + image);
                            articles.get(k-flag).add("\n\npost url=" + url);
                            k++;
                        }
                    }
                    catch (JSONException e){
                        Log.e("aaa", "JSONException1");
                        e.printStackTrace();
//                  articles.add(new ArrayList<String>());
//                  articles.get(j).add("blog name=" + blog);
//                  articles.get(j).add("\n\nsummary=" + summary);
//                  articles.get(j).add("\n\nimage url=" + image);
//                  articles.get(j).add("\n\npost url=" + url);
                    }

                }
            }catch (JSONException e){
                Log.e("aaa", "JSONException2");
                e.printStackTrace();
            }


            Log.e("bbb", "end of populate()");
        }
        setLayout();
    }
    public void setLayout()
    {    Log.e("aaa","inside setLayout");
        if(articles.size()==0) {
            articles.add(new ArrayList<String>());
            articles.get(0).add("blog name=" + "DUMMY");
        }
        Log.e("aaa","setting the view");

        listView = (ListView)getView().findViewById(android.R.id.list);
        listView.setAdapter(new ArrayAdapter(
                getContext(),android.R.layout.simple_list_item_1, articles
                /*this.populate()*/));
        tumblrbtn.setVisibility(View.INVISIBLE);
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {


        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Loading mood tags..."); // Calls onProgressUpdate()
            try {
                getmapping();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "done";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation

            //while(flag!=1);

            progressDialog.dismiss();
            //finalResult.setText(result);
//            setLayout();
            for(int i=0;i<articles.size();i++)
                Log.e("bbb","article["+i+"]="+articles.get(i).toString());

        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(),
                    "ProgressDialog",
                    "Waiting for data");

        }


        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);

        }
    }

}
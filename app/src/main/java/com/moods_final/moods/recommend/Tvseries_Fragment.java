package com.moods_final.moods.recommend;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.moods_final.moods.moods.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tvseries_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tvseries_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tvseries_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    String myJSON;
    private static final String TAG_RESULTS="result";
    private static final String TAG_TITLE = "title";
    private static final String TAG_GENRE ="genre";
    private static final String TAG_YEAR ="year";
    private static final String TAG_OUTLINE ="outline";
    private static final String TAG_RATING ="user_rating";
    String m="";
    JSONArray peoples = null;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new PostDataAsyncTask().execute();

    }

    ArrayList<HashMap<String, String>> personList;

    ListView list;
    private static final String TAG = "TVseriesActivity.java";


    public Tvseries_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tvseries_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Tvseries_Fragment newInstance(String param1, String param2) {
        Tvseries_Fragment fragment = new Tvseries_Fragment();
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
        View v= inflater.inflate(R.layout.fragment_tvseries_, container, false);
        list = (ListView) v.findViewById(R.id.listView);
        m = getActivity().getIntent().getStringExtra("mood");
        personList = new ArrayList<HashMap<String,String>>();

        // we are going to use asynctask to prevent network on main thread exception
        Toast.makeText(getContext(),m, Toast.LENGTH_LONG).show();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public class PostDataAsyncTask extends AsyncTask<String, String, String> {
        ProgressDialog pg=new ProgressDialog(getContext());
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setMessage("Fetching Quotes");
            pg.show();
            // do stuff before posting data
        }

        @Override
        protected String doInBackground(String... strings) {
            String b;
            try {

                b=  postText();
                return b;


            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String responseStr) {
            // do stuff after posting data
            myJSON=responseStr;
            showList();
            pg.dismiss();
        }
    }
    // this will post our text data
    private String postText(){
        String a;
        try{
            // url where the data will be posted
            String postReceiverUrl = "http://moods.esy.es/t6.php";
            Log.v(TAG, "postURL: " + postReceiverUrl);

            // HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<>(1);
            nameValuePairs.add(new BasicNameValuePair("mood", m));


            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //httpPost.setHeader("Content-type", "application/json");
            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity);
                Log.v(TAG, "Response: " +  responseStr);
                a=responseStr;

                /*InputStream inputStream = null;
                String result = null;
                inputStream =resEntity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
*/
                return a;
                // you can add an if statement here and do other actions based on the response
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String title = c.getString(TAG_TITLE);
                String genre = c.getString(TAG_GENRE);
                String year = c.getString(TAG_YEAR);
                String outline = c.getString(TAG_OUTLINE);
                String user_rating = c.getString(TAG_RATING);
                HashMap<String,String> persons = new HashMap<String,String>();
                persons.put(TAG_TITLE,title);
                persons.put(TAG_GENRE,genre);
                persons.put(TAG_YEAR,year);
                persons.put(TAG_OUTLINE,outline);
                persons.put(TAG_RATING,user_rating);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    getContext(), personList, R.layout.list_item_tvseries,
                    new String[]{TAG_TITLE,TAG_GENRE,TAG_YEAR,TAG_OUTLINE,TAG_RATING},
                    new int[]{R.id.title, R.id.genre,R.id.year,R.id.outline,R.id.user_rating}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

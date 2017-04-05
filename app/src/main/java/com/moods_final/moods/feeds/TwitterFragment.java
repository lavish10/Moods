package com.moods_final.moods.feeds;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moods_final.moods.feed.BlankFragment;
import com.moods_final.moods.moods.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TwitterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TwitterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TwitterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    static String TWITTER_CONSUMER_KEY = "b77moNvQb92ubPEtq3LSFqf64"; // place your consumer key here
    static String TWITTER_CONSUMER_SECRET = "AL90fyFfDTrZe58XbtMZVN17A2wFPlbB1uiQe1vqyM0tDgmJRN"; // place your consumer secret here

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Login button
    ArrayList<String> usernm= new ArrayList<String>();
    ArrayList<String> tweeet= new ArrayList<String>();
    Button btnLoginTwitter;
    // Update status button
    Button btnUpdateStatus;
    // Logout button
    Button btnLogoutTwitter;
    // EditText for update
    EditText txtUpdate;
    Button retpost;
    // lbl update
    TextView lblUpdate;
    TextView lblUserName;
    ListView lv;
    CustomList customList;
    // Progress dialog
    ProgressDialog pDialog;

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    // Internet Connection detector
    private ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();


    public TwitterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TwitterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TwitterFragment newInstance(String param1, String param2) {
        TwitterFragment fragment = new TwitterFragment();
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
        View v= inflater.inflate(R.layout.fragment_twitter, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);



            cd = new ConnectionDetector(getContext());

            // Check if Internet present
            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                alert.showAlertDialog(getContext(), "Internet Connection Error",
                        "Please connect to working Internet connection", false);
                // stop executing code by return

            }

            // Check if twitter keys are set
            if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
                // Internet Connection is not present
                alert.showAlertDialog(getContext(), "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
                // stop executing code by return

            }

            // All UI elements
            btnLoginTwitter = (Button)v.findViewById(R.id.btnLoginTwitter);
            btnUpdateStatus = (Button)v.findViewById(R.id.btnUpdateStatus);
            btnLogoutTwitter = (Button)v.findViewById(R.id.btnLogoutTwitter);
            txtUpdate = (EditText)v.findViewById(R.id.txtUpdateStatus);
            lblUpdate = (TextView)v.findViewById(R.id.lblUpdate);
            lblUserName = (TextView)v.findViewById(R.id.lblUserName);
            retpost=(Button)v.findViewById(R.id.retpost);
            lv=(ListView)v.findViewById(R.id.lv);
            // Shared Preferences
            mSharedPreferences = getActivity().getSharedPreferences(
                    "MyPref", 0);

            /**
             * Twitter login button click event will call loginToTwitter() function
             * */
            btnLoginTwitter.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // Call login twitter function
                    loginToTwitter();
                }
            });


            retpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //retfeed();
                    search();
                }
            });
            /**
             * Button click event for logout from twitter
             * */
            btnLogoutTwitter.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // Call logout twitter function
                    logoutFromTwitter();
                }
            });

            /** This if conditions is tested once is
             * redirected from twitter page. Parse the uri to get oAuth
             * Verifier
             * */
            if (!isTwitterLoggedInAlready()) {
                Uri uri = getActivity().getIntent().getData();
                if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                    // oAuth verifier
                    String verifier = uri
                            .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                    try {
                        // Get the access token
                        AccessToken accessToken = twitter.getOAuthAccessToken(
                                requestToken, verifier);

                        // Shared Preferences
                        SharedPreferences.Editor e = mSharedPreferences.edit();

                        // After getting access token, access token secret
                        // store them in application preferences
                        e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                        e.putString(PREF_KEY_OAUTH_SECRET,
                                accessToken.getTokenSecret());
                        // Store login status - true
                        e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                        e.commit(); // save changes

                        Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

                        // Hide login button
                        btnLoginTwitter.setVisibility(View.GONE);
                        btnLogoutTwitter.setVisibility(View.VISIBLE);
                        // Show Update Twitter
                        //lblUpdate.setVisibility(View.VISIBLE);
                        //txtUpdate.setVisibility(View.VISIBLE);
                        //btnUpdateStatus.setVisibility(View.VISIBLE);
                        retpost.setVisibility(View.VISIBLE);

                        // Getting user details from twitter
                        // For now i am getting his name only
                        long userID = accessToken.getUserId();
                        User user = twitter.showUser(userID);
                        String username = user.getName();

                        // Displaying in xml ui
                        lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
                    } catch (Exception e) {
                        // Check log for login errors
                        Log.e("Twitter Login Error", "> " + e.getMessage());
                    }
                }
            }

        }




        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void retfeed(){
        try {
            // gets Twitter instance with default credentials
            // twitter = new TwitterFactory().getInstance();
//            User user = twitter.verifyCredentials();
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
            AccessToken accessToken = new AccessToken(access_token, access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

            List<Status> statuses = twitter.getUserTimeline();
            Log.e("Feed start","Showing @" + "'s home timeline.");
            for (Status status : statuses) {
                Log.e("Feed","@" + status.getUser().getScreenName() + " - " + status.getText());
            }
//			GeoLocation loc = statuses.get(0).getGeoLocation();
//			Log.e("geoloc",loc.toString());
        } catch (TwitterException te) {
            te.printStackTrace();
            Log.e("aaaa","Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
    }
    public void search(){
        String[] arr = {"#india", "#demonetisation","#notesban", "#jayalalitha","#raees","#transformingindia",
                "#ndtvnewsbeeps","#bollywood","#starscreenawards","#celebrity","#tech","#business"};


        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

        String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
        // Access Token Secret
        String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

        AccessToken accessToken = new AccessToken(access_token, access_token_secret);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

        int i=0;
        List<Status> tweets;
        try {

            for(int j=0;j<12;j++) {
                Query query = new Query(arr[j].toString());

                QueryResult result;
                int count = 0;
                do {
                    result = twitter.search(query);
                    tweets = result.getTweets();
                    for (Status tweet : tweets) {
                        Log.e("ddd", "@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
//                        tagposts.add(new ArrayList<String>());
                        usernm.add("" + tweet.getUser().getScreenName());
                        tweeet.add("" + tweet.getText());
                        count++;

                    }
                    if (count >= 4)
                        break;
                } while ((query = result.nextQuery()) != null);
            }

        } catch (TwitterException te) {
            te.printStackTrace();
            Log.e("ddd","Failed to search tweets: " + te.getMessage());

        }
        Log.e("aaaa","setting listview adapter");
        btnLogoutTwitter.setVisibility(View.GONE);
        retpost.setVisibility(View.GONE);
        lblUserName.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);
//        lv.setAdapter(new ArrayAdapter(
//                getContext(), android.R.layout.simple_list_item_1,
//                tagposts));
        customList = new CustomList(getActivity(), usernm.toArray(new String[usernm.size()]),
                tweeet.toArray(new String[tweeet.size()]) );
        lv.setAdapter(customList);
    }


    /**
     * Function to login twitter
     * */
    private void loginToTwitter() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {



            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter
                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(requestToken.getAuthenticationURL())));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {
            // user already logged into twitter
            Toast.makeText(getContext(),
                    "Already Logged into twitter", Toast.LENGTH_LONG).show();

            setloggedinlayout();
        }

    }
    public void setloggedinlayout(){
        btnLoginTwitter.setVisibility(View.GONE);
        btnLogoutTwitter.setVisibility(View.VISIBLE);
        Log.e("xyz","logoutbtn visible");
        lblUserName.setVisibility(View.VISIBLE);

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

        String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
        // Access Token Secret
        String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

        AccessToken accessToken = new AccessToken(access_token, access_token_secret);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
        long userID = accessToken.getUserId();
        User user = null;
        try {
            user = twitter.showUser(userID);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        String username = user.getName();

        // Displaying in xml ui
        lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
        //lblUpdate.setVisibility(View.VISIBLE);
        //txtUpdate.setVisibility(View.VISIBLE);
        //btnUpdateStatus.setVisibility(View.VISIBLE);




        retpost.setVisibility(View.VISIBLE);


    }

    /**
     * Function to update status
     * */
    class updateTwitterStatus extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                twitter4j.Status response = twitter.updateStatus(status);

                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread

        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Function to logout from twitter
     * It will just clear the application shared preferences
     * */
    private void logoutFromTwitter() {
        // Clear the shared preferences
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(PREF_KEY_OAUTH_TOKEN);
        e.remove(PREF_KEY_OAUTH_SECRET);
        e.remove(PREF_KEY_TWITTER_LOGIN);
        e.commit();

        // After this take the appropriate action
        // I am showing the hiding/showing buttons again
        // You might not needed this code
        btnLogoutTwitter.setVisibility(View.GONE);
        btnUpdateStatus.setVisibility(View.GONE);
        txtUpdate.setVisibility(View.GONE);
        lblUpdate.setVisibility(View.GONE);
        lblUserName.setText("");
        lblUserName.setVisibility(View.GONE);

        btnLoginTwitter.setVisibility(View.VISIBLE);
    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     * */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public void onResume() {
        super.onResume();
    }

    public class CustomList extends ArrayAdapter<String> {
        private String[] names;
        private String[] desc;
        private Activity context;

        public CustomList(Activity context, String[] names, String[] desc) {
            super(context, R.layout.item_list, names);
            this.context = context;
            this.names = names;
            this.desc = desc;


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.item_list_twitter, null, true);
            TextView textViewName = (TextView) listViewItem.findViewById(R.id.list_title);
            TextView textViewDesc = (TextView) listViewItem.findViewById(R.id.list_desc);


            textViewName.setText(names[position]);
            textViewDesc.setText(desc[position]);
//            image.setImageResource(imageid[position]);

            return  listViewItem;
        }
    }

}

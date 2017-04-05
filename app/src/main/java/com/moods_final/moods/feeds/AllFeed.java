package com.moods_final.moods.feeds;;


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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.moods_final.moods.moods.NextActivity;
import com.moods_final.moods.moods.R;
import com.moods_final.moods.recommend.Events_Fragment;
import com.moods_final.moods.recommend.Foursquare_Fragment;
import com.moods_final.moods.recommend.Quotes_Fragment;
import com.moods_final.moods.recommend.Tvseries_Fragment;

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

public class AllFeed extends AppCompatActivity implements View.OnClickListener{
	// Constants
	/**
	 * Register your here app https://dev.twitter.com/apps/new and get your
	 * consumer key and secret
	 * */
	String myJSON;
	EditText mood;
	Button movies;
	Button events;
	Button quotes;
	Button places;
	Button tvseries;
	private static final String TAG_RESULTS="result";
	private static final String TAG_TITLE = "title";
	private static final String TAG_GENRE ="genre";
	private static final String TAG_YEAR ="year";
	private static final String TAG_OUTLINE ="outline";
	private static final String TAG_RATING ="user_rating";
	public static String m="";
	JSONArray peoples = null;

	ArrayList<HashMap<String, String>> personList;

	ListView list;
	private static final String TAG = "AllFeed.java";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter);

		// Adding Toolbar to Main screen
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// Setting ViewPager for each Tabs
		ViewPager viewPager = (ViewPager) findViewById(R.id.vpPager);
		setupViewPager(viewPager);
		// Set Tabs inside Toolbar
		TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);
		viewPager.setOffscreenPageLimit(3);

	}

	protected void onResume() {
		super.onResume();
	}

	// Add Fragments to Tabs
	private void setupViewPager(ViewPager viewPager) {
		Adapter adapter = new Adapter(getSupportFragmentManager());
		adapter.addFragment(new Facebook_complete(), "Facebook");
		adapter.addFragment(new TwitterFragment(), "Twitter");
		adapter.addFragment(new TumblrFragment(), "Tumblr");
//		adapter.addFragment(new Foursqu.are_Fragment(), "Places");
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(3);
	}

	static class Adapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public Adapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(android.support.v4.app.Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}


	// this will post our text data
	private String postText(){
		String a;
		try{
			// url where the data will be posted
			String postReceiverUrl = "http://moodsmoods.16mb.com/t2.php";
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




	@Override
	public void onClick(View v) {
/*

        if(v==movies)
        {
            mood=(EditText) findViewById(R.id.Mood);
            m=  (String)mood.getText().toString();
            Toast.makeText(this, m, Toast.LENGTH_LONG).show();
            new PostDataAsyncTask().execute();
          //  getData();
        }
        else if(v==events)
        {
            mood=(EditText) findViewById(R.id.Mood);
            m=(String)mood.getText().toString();
            Intent intent = new Intent(this,EventsActivity.class);
            intent.putExtra("m",m);
            startActivity(intent);
        }
        else if (v==quotes)
        {
            mood=(EditText) findViewById(R.id.Mood);
            m=(String)mood.getText().toString();
            Intent intent = new Intent(this,QuotesActivity.class);
            intent.putExtra("m",m);
            startActivity(intent);
        }
        else if(v==places)
        {
            mood=(EditText) findViewById(R.id.Mood);
            m=(String)mood.getText().toString();
            Intent intent = new Intent(this,MapsActivity.class);
            intent.putExtra("m",m);
            startActivity(intent);
        }
        else if(v==tvseries)
        {
            mood=(EditText) findViewById(R.id.Mood);
            m=(String)mood.getText().toString();
            Intent intent = new Intent(this,TvseriesActivity.class);
            intent.putExtra("m",m);
            startActivity(intent);
        }
*/

	}

	public class PostDataAsyncTask extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
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
		}
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
					AllFeed.this, personList, R.layout.list_item,
					new String[]{TAG_TITLE,TAG_GENRE,TAG_YEAR,TAG_OUTLINE,TAG_RATING},
					new int[]{R.id.title, R.id.genre,R.id.year,R.id.outline,R.id.user_rating}
			);

			list.setAdapter(adapter);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}



}

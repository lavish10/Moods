package com.moods_final.moods.recommend;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.moods_final.moods.moods.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AndroidFoursquare extends ListActivity implements LocationListener {
	ArrayList<FoursquareVenue> venuesList;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Context context;
	public static double latitude,longitude;

	String provider;
	//protected String latitude, longitude;
	protected boolean gps_enabled, network_enabled;
	// the foursquare client_id and the client_secret
String m="";
	final String CLIENT_ID = "LLQNDTYKWKSIVIVHRAWJBWVXUCHODXNDS3UBTPAXM0VGUZVF";
	final String CLIENT_SECRET = "0WAH0KF123KXUUF0ERQRENWQMS3TDWSLDOF2RYKNLBTRGAEH";

	ArrayAdapter<String> myAdapter;
	String myJSON;
	public static String id="";
	private static final String TAG_RESULTS="result";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_ID ="id";

	JSONArray peoples = null;

	public static ArrayList<HashMap<String, String>> personList;

	ListView list;
	private static final String TAG = "AndroidFoursquare.java";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foursquare);

		m = getIntent().getStringExtra("mood");
		personList = new ArrayList<HashMap<String,String>>();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		getSystemService(Context.LOCATION_SERVICE);
// getting GPS status
		gps_enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
// getting network status
		network_enabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (gps_enabled) {
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		}
		else if (network_enabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		};
		Toast.makeText(this,m, Toast.LENGTH_LONG).show();
		new PostDataAsyncTask().execute();
		// start the AsyncTask that makes the call for the venus search.
		new fourquare().execute();
	}

	@Override
	public void onLocationChanged(Location location) {
		TextView txtLat = (TextView) findViewById(R.id.textView1);
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	//	Toast.makeText(this, (int) latitude, Toast.LENGTH_LONG).show();
	//	Toast.makeText(this, (int) longitude, Toast.LENGTH_LONG).show();

		txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
		//geocoder
		String filterAddress = "";
		String result = null;
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses =
					geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (addresses!= null && addresses.size() > 0) {
				Address address = addresses.get(0);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
					sb.append(address.getAddressLine(i)).append("\n");
				}
				// sb.append(address.getLocality()).append("\n");
				//sb.append(address.getPostalCode()).append("\n");
				sb.append(address.getCountryName());
				result = sb.toString();
				 TextView textview = (TextView)findViewById(R.id.test);
				textview.setText(result);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("Latitude","status");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("Latitude","enable");
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("Latitude","disable");
	}

	public class PostDataAsyncTask extends AsyncTask<String, String, String> {
		//ProgressDialog Asyncdialog = new ProgressDialog(AndroidFoursquare.this);
		protected void onPreExecute() {
			super.onPreExecute();

			// do stuff before posting data
		}

		@Override
		protected String doInBackground(String... strings) {
			String b;
			try {

				b=postText();
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
	// this will post our text data
	private String postText(){
		String a;
		try{
			// url where the data will be posted
			String postReceiverUrl = "http://moods.esy.es/t5.php";
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
				String category = c.getString(TAG_CATEGORY);
				String id = c.getString(TAG_ID);
				//id=id.replaceAll("id=","");
				//id=id.replaceAll("'{'","");
				//id=id.replaceAll("}","");
				HashMap<String,String> persons = new HashMap<String,String>();
				//persons.put(TAG_CATEGORY,category);
				persons.put(TAG_ID,id);

				personList.add(persons);
			}
			for(int i=0;i<personList.size();i++)
			{
				if(i==personList.size()-1) {
					id+= personList.get(i).toString().substring(4,28);
				}
				else {
					id+= personList.get(i).toString().substring(4,28) + ",";
				}

			}
			Log.v(TAG, "id: " +  id);
			Toast.makeText(AndroidFoursquare.this,id, Toast.LENGTH_LONG).show();
			/*ListAdapter adapter = new SimpleAdapter(
					this, personList, R.layout.row_layout,
					new String[]{TAG_CATEGORY,TAG_ID},
					new int[]{R.id.category, R.id.ids}
			);

			list.setAdapter(adapter);*/

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private class fourquare extends AsyncTask<View, Void, String> {
        ProgressDialog Asyncdialog = new ProgressDialog(AndroidFoursquare.this);
		String temp;

		@Override
		protected String doInBackground(View... urls) {
			// make Call to the url
			temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&v=20130815&ll="+latitude+","+longitude+"&categoryId="+id/*4d4b7104d754a06370d81259,4d4b7105d754a06374d81259*/+"&radius=5000");
			return "";
		}

		@Override
		protected void onPreExecute() {
            super.onPreExecute();
            Asyncdialog.setMessage("Loading...");
            Asyncdialog.show();
			// we can start a progress bar here
		}

		@Override
		protected void onPostExecute(String result) {
			if (temp == null) {

			} else {
				// all things went right

				// parseFoursquare venues search result
				venuesList = (ArrayList<FoursquareVenue>) parseFoursquare(temp);

				List<String> listTitle = new ArrayList<String>();

				for (int i = 0; i < venuesList.size(); i++) {
					// make a list of the venus that are loaded in the list.

					// show the name, the category and the city
                                listTitle.add(venuesList.get(i).getName() + ", " + venuesList.get(i).getCategory() + ", " + venuesList.get(i).getCity());

				}
                if (Asyncdialog.isShowing()) {
                    Asyncdialog.dismiss();
                }
				// set the results to the list
				// and show them in the xml
				myAdapter = new ArrayAdapter<String>(AndroidFoursquare.this, R.layout.row_layout, R.id.listText, listTitle);
				setListAdapter(myAdapter);
			}

		}
	}

	public static String makeCall(String url) {

		// string buffers the url
		StringBuffer buffer_string = new StringBuffer(url);
		String replyString = "";

		// instanciate an HttpClient
		HttpClient httpclient = new DefaultHttpClient();
		// instanciate an HttpGet
		HttpGet httpget = new HttpGet(buffer_string.toString());

		try {
			// get the responce of the httpclient execution of the url
			HttpResponse response = httpclient.execute(httpget);
			InputStream is = response.getEntity().getContent();

			// buffer input stream the result
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(20);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			// the result as a string is ready for parsing
			replyString = new String(baf.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// trim the whitespaces
		return replyString.trim();
	}

	private static ArrayList<FoursquareVenue> parseFoursquare(final String response) {

		ArrayList<FoursquareVenue> temp = new ArrayList<FoursquareVenue>();
		try {

			// make a jsonObject in order to parse the response
			JSONObject jsonObject = new JSONObject(response);

			// make a jsonObject in order to parse the response
			if (jsonObject.has("response")) {
				if (jsonObject.getJSONObject("response").has("venues")) {
					JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");

					for (int i = 0; i < jsonArray.length(); i++) {
						FoursquareVenue poi = new FoursquareVenue();
						if (jsonArray.getJSONObject(i).has("name")) {
							poi.setName(jsonArray.getJSONObject(i).getString("name"));
							if (jsonArray.getJSONObject(i).has("location")) {
								if (jsonArray.getJSONObject(i).getJSONObject("location").has("address")) {
									if (jsonArray.getJSONObject(i).getJSONObject("location").has("city")) {
										poi.setCity(jsonArray.getJSONObject(i).getJSONObject("location").getString("city"));
									}
									if (jsonArray.getJSONObject(i).has("categories")) {
										if (jsonArray.getJSONObject(i).getJSONArray("categories").length() > 0) {
											if (jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("icon")) {
												poi.setCategory(jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name"));
											}
										}
									}

									temp.add(poi);
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FoursquareVenue>();
		}
		return temp;

	}
}

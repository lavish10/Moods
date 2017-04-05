package com.moods_final.moods.insta_mood;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.moods_final.moods.moods.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ly.kite.instagramphotopicker.InstagramPhoto;
import ly.kite.instagramphotopicker.InstagramPhotoPicker;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import static android.R.attr.entries;


public class Insta extends ActionBarActivity {

    private static final String CLIENT_ID = "aa314a392fdd4de7aa287a6614ea8897";
    //private static final String REDIRECT_URI = "psapp://instagram-callback";
    private static final String REDIRECT_URI = "http://instagram-callback";
    private static final int REQUEST_CODE_INSTAGRAM_PICKER = 88;
    LineGraphSeries<DataPoint> series;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insta_mood);
       /* PieChart pieChart = (PieChart) findViewById(R.id.chart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(12f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

        PieData data = new PieData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        pieChart.setDescription("Description");
        pieChart.setData(data);

        pieChart.animateY(5000);
*/
    }
public void setGraph(){

    GraphView graph = (GraphView) findViewById(R.id.graph);
    graph.addSeries(series);
}
    public void onButtonLaunchInstagramPickerClicked(View view) {
        InstagramPhotoPicker.startPhotoPickerForResult(this, CLIENT_ID, REDIRECT_URI, REQUEST_CODE_INSTAGRAM_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_INSTAGRAM_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                InstagramPhoto[] instagramPhotos = InstagramPhotoPicker.getResultPhotos(data);
                Toast.makeText(this, "User selected " + instagramPhotos.length + " Instagram photos", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < instagramPhotos.length; ++i) {
                    Log.i("aaaa", "Photo: " + instagramPhotos[i].getFullURL());
                    abc(instagramPhotos[i].getFullURL()+"");
                }


             } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Instagram Picking Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("dbotha", "Unknown result code: " + resultCode);
            }
        }
    }
    private void abc(String url) {

        new HttpAsyncTask(url).execute();

}

    public static String POST(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost("https://api.projectoxford.ai/emotion/v1.0/recognize");

            String json = "";

            // 3. build jsonObject
            //String movie=movie_name.getText().toString();
            JSONObject jsonObject = new JSONObject();
            //jsonObject.accumulate("movie", movie);


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity("{'url':\'"+url+"\'}");

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Ocp-Apim-Subscription-Key", "98c5f335e32f4d10b735f92b61b9c371");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
//            Log.d("InputStream", e.printStackTrace());
            e.printStackTrace();

        }

        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        String var="";
        String resp="";
        public HttpAsyncTask(String url)
        {
            var=url;
        }
        @Override
        protected String doInBackground(String... urls) {



            resp= POST(var);
            return resp;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), resp, Toast.LENGTH_LONG).show();
            try {
                JSONArray jarr=new JSONArray(resp.toString());
                JSONObject job=jarr.getJSONObject(0);

                Log.e("result",job.getJSONObject("scores").getString("anger"));
                Log.e("result",job.getJSONObject("scores").getString("contempt"));
                Log.e("result",job.getJSONObject("scores").getString("disgust"));
                Log.e("result",job.getJSONObject("scores").getString("fear"));
                Log.e("result",job.getJSONObject("scores").getString("happiness"));
                Log.e("result",job.getJSONObject("scores").getString("neutral"));
                Log.e("result",job.getJSONObject("scores").getString("sadness"));
                Log.e("result",job.getJSONObject("scores").getString("surprise"));
                 series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0, Double.parseDouble(job.getJSONObject("scores").getString("anger"))*1000),
                        new DataPoint(1, Double.parseDouble(job.getJSONObject("scores").getString("contempt"))*1000),
                        new DataPoint(2, Double.parseDouble(job.getJSONObject("scores").getString("disgust"))*1000),
                        new DataPoint(3, Double.parseDouble(job.getJSONObject("scores").getString("fear"))*1000),
                        new DataPoint(4, Double.parseDouble(job.getJSONObject("scores").getString("happiness"))*1000),
                         new DataPoint(5, Double.parseDouble(job.getJSONObject("scores").getString("neutral"))*1000),
                         new DataPoint(6, Double.parseDouble(job.getJSONObject("scores").getString("sadness"))*1000),
                         new DataPoint(7, Double.parseDouble(job.getJSONObject("scores").getString("surprise"))*1000),
                });
                setGraph();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static  String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}

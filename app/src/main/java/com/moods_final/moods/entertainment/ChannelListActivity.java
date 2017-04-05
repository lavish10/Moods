package com.moods_final.moods.entertainment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moods_final.moods.feed.BlankFragment;
import com.moods_final.moods.moods.R;

import org.apache.http.client.ClientProtocolException;
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
import java.util.IllegalFormatPrecisionException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aditya Mishra on 04-12-2016.
 */

/**
 * Second activity from which the user can select one of the channel.
 */
public class ChannelListActivity extends Activity implements AdapterView.OnItemClickListener {

    public ListView listView;
    public ListView lvTopics;
    String chId;
    CustomList customList;
    ArrayList<String> id = new ArrayList<String>();
    ArrayList<String> title = new ArrayList<String>();
    ArrayList<String> image = new ArrayList<String>();
    ArrayList<ArrayList<String>> topics = new ArrayList<ArrayList<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R
                .layout.demo_home);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        chId=getIntent().getStringExtra("chId");
//        chId=chId.substring(3);
        Log.e("Channel id=",chId);

        listView = (ListView) findViewById(R.id.list);
        lvTopics=(ListView) findViewById(R.id.topics);

        //DemoArrayAdapter adapter = new DemoArrayAdapter(this, R.layout.list_item_youtube, activities);
        //AsyncTaskRunner runner = new AsyncTaskRunner();

        //runner.execute();
        /*lvTopics.setAdapter(new ArrayAdapter(
                this, android.R.layout.simple_list_item_1,
                this.getTopics()));
*/      getChannelData();

        customList = new CustomList(ChannelListActivity.this, title.toArray(new String[title.size()]),
                               image.toArray(new String[image.size()]));
//        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,
//                this.getChannelData()));
//        listView.setOnItemClickListener(this);

            listView.setAdapter(customList);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id1) {

                        //Take action here.
                        Log.e("press",":"+title.get(position).toString());
                        Intent intent = new Intent().putExtra("pid", id.get(position).toString());
                        intent.setComponent(new ComponentName(getPackageName(), "com.moods_final.moods.entertainment.VideoListDemoActivity"));
                        startActivity(intent);
                    }
                }
        );


    }

    public ArrayList<ArrayList<String>> getTopics(){


        return topics;
    }

    public void getChannelData() {

        try {
            // channel id is of youtube's music autogen channel (a.k.a topic)
            String searchUrl = "https://www.googleapis.com/youtube/v3/search?key=AIzaSyBHCABhIbvbpAknRONYBUyGdEBWSYawbLQ&channelId="+chId+"&part=snippet,id&order=date&maxResults=50";
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(searchUrl);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            String responseBody = null;

            responseBody = client.execute(get, responseHandler);

            Log.e("aaa",responseBody);

            JSONObject jsonObject = new JSONObject(responseBody);
            JSONObject j=null;
            JSONArray arr =jsonObject.getJSONArray("items");
            String image1, title1, id1;
            for(int i=0;i<arr.length();i++)
                Log.e("aab",arr.getJSONObject(i).toString()+"\n");


            //JSONArray  for all playlists
            for (int i = 0; i < arr.length(); i++) {
                Log.e("ddd","innnnnn");
                j = new JSONObject(arr.getJSONObject(i).toString());/*getJSONArray("photos")*/

                id1=j.getString("id");
                j=new JSONObject(id1);
                id1=j.getString("playlistId");
                //arr = jsonObject.getJSONArray("response");
                //Log.e("aaa","\n\n"+arr.get(0).toString());

                Log.e("mmm", "id=" + id1);
                title1 = arr.getJSONObject(i).getJSONObject("snippet").getString("title");

                Log.e("mmm", "title=" + title1);
                //arr1 = arr.getJSONObject(i).getJSONArray("photos");
                image1 = arr.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");
                Log.e("mmm", "image=" + image1);
                //playlists.add(new ArrayList<String>());
                id.add("" + id1);
                title.add("" + title1);
                image.add("" + image1);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.e("aaa", "JSONException");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return playlists;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//    Demo clickedDemo = (Demo) .get(position);
//      Log.e("name",activities.get(position).toString());

//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName(getPackageName(), "com.examples.youtubeapidemo.VideoListDemoActivity" /*clickedDemo.className*/));
//        Log.e("aaa", getPackageName().toString());
//        // Log.e("aaa",clickedDemo.className);
//        startActivity(intent);
    }
    public class CustomList extends ArrayAdapter<String> {
        private String[] names;
        private String[] imageid;
        private Activity context;

        public CustomList(Activity context, String[] names,  String[] imageid) {
            super(context, R.layout.item_list, names);
            this.context = context;
            this.names = names;

            this.imageid = imageid;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.item_list_custom, null, true);
            TextView textViewName = (TextView) listViewItem.findViewById(R.id.list_title);
            ImageView image = (ImageView) listViewItem.findViewById(R.id.list_avatar);

            textViewName.setText(names[position]);
//            image.setImageResource(imageid[position]);
            try {
                Glide.with(getContext())
                        .load(imageid[position]).into(image); // Uri of the picture
                //.transform(new CircleTransform());
            }catch (IllegalArgumentException e)
            {
//                Glide.with(getContext())
//                        .load(imageid[position]).into(image);
                e.printStackTrace();
            }
            return  listViewItem;
        }
    }



}



//  private final class Demo implements DemoListViewItem {
//
//    public final String title;
//    public final int minVersion;
//    public final String className;
//
//    public Demo(String title, String className, int minVersion) {
//      this.className = className;
//        Log.e("name:",className);
//      this.title = title;
//      this.minVersion = minVersion;
//    }
//
//    @Override
//    public boolean isEnabled() {
//      return android.os.Build.VERSION.SDK_INT >= minVersion;
//    }
//
//    @Override
//    public String getDisabledText() {
//      String itemDisabledText = getString(R.string.list_item_disabled);
//      return String.format(itemDisabledText, minVersion);
//    }
//
//    @Override
//    public String getTitle() {
//      return title;
//    }
//
//  }
//
//}


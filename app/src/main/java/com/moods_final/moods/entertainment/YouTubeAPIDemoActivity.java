/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moods_final.moods.entertainment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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

//import com.examples.youtubeapidemo.adapter.DemoArrayAdapter;
//import com.examples.youtubeapidemo.adapter.DemoListViewItem;

/**
 * Main activity from which the user can select one of the other demo activities.
 */
public class YouTubeAPIDemoActivity extends Activity implements OnItemClickListener {


    public ListView lvTopics;

    //ArrayList<ArrayList<String>> playlists = new ArrayList<ArrayList<String>>();
    ArrayList<String> topics = new ArrayList<String>();
    ArrayList<String> topid = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        lvTopics=(ListView) findViewById(R.id.topics);


        //AsyncTaskRunner runner = new AsyncTaskRunner();

        //runner.execute();
        try {
            lvTopics.setAdapter(new ArrayAdapter(
                    this, android.R.layout.simple_list_item_1,
                    this.getTopics()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        lvTopics.setOnItemClickListener(
                new OnItemClickListener()
                {


                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {

                        //Take action here.
                        Log.e("pressed",":"+topics.get(position).toString());

                        Intent intent = new Intent().putExtra("chId", topid.get(position).toString());;
                        intent.setComponent(new ComponentName(getPackageName(), "com.moods_final.moods.entertainment.ChannelListActivity"));
                        startActivity(intent);

                    }




                }
        );


    }

    public ArrayList<String> getTopics() throws IOException {
        String searchUrl = "http://moods.esy.es/youtube.php";
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(searchUrl);

        ResponseHandler<String> responseHandler = new BasicResponseHandler();

        String responseBody = null;

        responseBody = client.execute(get, responseHandler);

        Log.e("aaa",responseBody);

        String id = "";
        String title = "";

        Log.e("aaa","in showJson()");
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray result = jsonObject.getJSONArray("result");


            for(int i = 0; i < result.length(); i++) {
                JSONObject Data = result.getJSONObject(i);

                id = Data.getString("id");
                title = Data.getString("title");
//                topics.add(new String());

                topid.add("" + id);
                topics.add("" + title);
                Log.e("aaa",id+"  --- "+title);
        }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return topics;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }




}




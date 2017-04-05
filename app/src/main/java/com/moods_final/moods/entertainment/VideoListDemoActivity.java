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
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.moods_final.moods.moods.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A sample Activity showing how to manage multiple YouTubeThumbnailViews in an adapter for display
 * in a List. When the list items are clicked, the video is played by using a YouTubePlayerFragment.
 * <p>
 * The demo supports custom fullscreen and transitioning between portrait and landscape without
 * rebuffering.
 */
//@TargetApi(13)
public final class VideoListDemoActivity extends Activity implements OnFullscreenListener {

  /** The duration of the animation sliding up the video in portrait. */
  private static final int ANIMATION_DURATION_MILLIS = 300;
  /** The padding between the video list and the video in landscape orientation. */
  private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;
  public static int first=1;
  private VideoListFragment listFragment;
  private VideoFragment videoFragment;

  static View videoBox;
//  public static ListView a;
  private View closeButton;
  private String pid;
  private boolean isFullscreen;
  public static ArrayList<ArrayList<String>> videos = new ArrayList<ArrayList<String>>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    videoBox = findViewById(R.id.video_box);

    //pid="PLFgquLnL59ak455PATxZHAZQVjCt2Wycc";
    pid=getIntent().getStringExtra("pid");
    pid=pid.substring(3);
    Log.e("PID",pid);


    AsyncTaskRunner runner = new AsyncTaskRunner();

    runner.execute();

//    getVideosData();
//    layout();


  }

  public void getVideosData() {

    try {
        videos.clear();
      String searchUrl = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&playlistId="+pid+"&key=AIzaSyBHCABhIbvbpAknRONYBUyGdEBWSYawbLQ";
      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet(searchUrl);

      ResponseHandler<String> responseHandler = new BasicResponseHandler();

      String responseBody = null;

        responseBody = client.execute(get, responseHandler);
      Log.e("aaa",responseBody);

        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject j = null;
        JSONArray arr = jsonObject.getJSONArray("items");
        String  title,id,snippet;


        //JSONArray photos
        for (int i = 0; i < arr.length(); i++) {
          j = new JSONObject(arr.getJSONObject(i).toString());

          snippet=j.getString("snippet");
          j=new JSONObject(snippet);
          title=j.getString("title");
            Log.e("mmm","title="+title);

          id = arr.getJSONObject(i).getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");
          Log.e("mmm","vid="+id);

          //id= jsonObject.getJSONArray("response").getJSONObject(i).getString("blog_name");


          /*getJSONArray("photos")*/
          //arr = jsonObject.getJSONArray("response");
          //Log.e("aaa","\n\n"+arr.get(0).toString());

          /*Log.e("mmm", "blog name=" + blog);
          summary = arr.getJSONObject(i).getString("summary");
          url = arr.getJSONObject(i).getString("post_url");
          Log.e("mmm", "summary=" + summary + " ,url=" + url);
          //arr1 = arr.getJSONObject(i).getJSONArray("photos");
          //image = arr1.getJSONObject(0).getJSONArray("alt_sizes").getJSONObject(0).getString("url");
         // Log.e("mmm", "image=" + image);
          */videos.add(new ArrayList<String>());
          videos.get(i).add("" + title);
          videos.get(i).add("" + id);

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
    }



  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    layout();
  }

  @Override
  public void onFullscreen(boolean isFullscreen) {
    this.isFullscreen = isFullscreen;

    layout();
  }

  /**
   * Sets up the layout programatically for the three different states. Portrait, landscape or
   * fullscreen+landscape. This has to be done programmatically because we handle the orientation
   * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
   * do not get reloaded.
   */
  private void layout() {
    boolean isPortrait =
        getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

    listFragment.getView().setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
    listFragment.setLabelVisibility(isPortrait);
    closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

    if (isFullscreen) {
      videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
      setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
      setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
    } else if (isPortrait) {
      setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
      setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
      setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
    } else {
      videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
      int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
      setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
      int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
      setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
      setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
          Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    }
  }

  public void onClickClose(@SuppressWarnings("unused") View view) {
    listFragment.getListView().clearChoices();
    listFragment.getListView().requestLayout();
    videoFragment.pause();
    videoBox.animate()
        .translationYBy(videoBox.getHeight())
        .setDuration(ANIMATION_DURATION_MILLIS)
        .withEndAction(new Runnable() {
          @Override
          public void run() {
            videoBox.setVisibility(View.INVISIBLE);
          }
        });
  }
  public void setLp(){
    videoBox = findViewById(R.id.video_box);
    videoBox.setVisibility(View.INVISIBLE);

  }
  public void checkLp(){

  }

  private class AsyncTaskRunner extends AsyncTask<String, String, String> {

    private String resp;
    ProgressDialog progressDialog;

    @Override
    protected String doInBackground(String... params) {
      publishProgress("Loading..."); // Calls onProgressUpdate()
      getVideosData();

      return "done";
    }


    @Override
    protected void onPostExecute(String result) {
      // execution of result of Long time consuming operation
      progressDialog.dismiss();
      //finalResult.setText(result);
      setContentView(R.layout.video_list_demo);
      videoBox = findViewById(R.id.video_box);
      listFragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
      videoFragment =
              (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);


      closeButton = findViewById(R.id.close_button);
setLp();

      layout();
    }


    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(VideoListDemoActivity.this,
              "ProgressDialog",
              "Waiting for data");
    }


    @Override
    protected void onProgressUpdate(String... text) {
      //finalResult.setText(text[0]);

    }
  }

  /**
   * A fragment that shows a static list of videos.
   */

  public static final class VideoListFragment extends ListFragment {

    private final List<VideoEntry> VIDEO_LIST;
     {
      List<VideoEntry> list = new ArrayList<VideoEntry>();
       //list.add(new VideoEntry("YouTube Collection", "Y_UmWdcTrrc"));
//      list.add(new VideoEntry("GMail Tap", "1KhZKNZO8mQ"));
//      list.add(new VideoEntry("Chrome Multitask", "UiLSiqyDf4Y"));
//      list.add(new VideoEntry("Google Fiber", "re0VRK6ouwI"));
//      list.add(new VideoEntry("Autocompleter", "blB_X38YSxQ"));
//      list.add(new VideoEntry("GMail Motion", "Bu927_ul_X0"));
//      list.add(new VideoEntry("Translate for Animals", "3I24bSteJpw"));
        Log.e("SIZE",VideoListDemoActivity.videos.size()+"");
        for(int i=0;i<VideoListDemoActivity.videos.size();i++)
            list.add(new VideoEntry(VideoListDemoActivity.videos.get(i).get(0).toString(), VideoListDemoActivity.videos.get(i).get(1).toString()));
           // VIDEO_LIST = Collections.unmodifiableList(list);
       VIDEO_LIST =list;
        for(int i=0;i<VIDEO_LIST.size();i++)
            Log.e("VDL",VIDEO_LIST.get(i).toString());
      //
    }

    private PageAdapter adapter;
    //private View videoBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      adapter = new PageAdapter(getActivity(), VIDEO_LIST);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      //videoBox = getActivity().findViewById(R.id.video_box);
      getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
      String videoId = VIDEO_LIST.get(position).videoId;

      VideoFragment videoFragment =
          (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
      videoFragment.setVideoId(videoId);
//        checkLp();

      // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
      if (videoBox.getVisibility() != View.VISIBLE) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
          // Initially translate off the screen so that it can be animated in from below.
          videoBox.setTranslationY(videoBox.getHeight());
        }
        videoBox.setVisibility(View.VISIBLE);
      }

      // If the fragment is off the screen, we animate it in.
      if (videoBox.getTranslationY() > 0) {
        videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
      }

    }

    @Override
    public void onDestroyView() {
      super.onDestroyView();

      adapter.releaseLoaders();
    }

    public void setLabelVisibility(boolean visible) {
      adapter.setLabelVisibility(visible);
    }

  }

  /**
   * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
   * of them only once and keeping track of the loader of each one. When the ListFragment gets
   * destroyed it releases all the loaders.
   */
  private static final class PageAdapter extends BaseAdapter {

    private final List<VideoEntry> entries;
    private final List<View> entryViews;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final LayoutInflater inflater;
    private final ThumbnailListener thumbnailListener;

    private boolean labelsVisible;

    public PageAdapter(Context context, List<VideoEntry> entries) {
      this.entries = entries;

      entryViews = new ArrayList<View>();
      thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
      inflater = LayoutInflater.from(context);
      thumbnailListener = new ThumbnailListener();

      labelsVisible = true;
    }

    public void releaseLoaders() {
      for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
        loader.release();
      }
    }

    public void setLabelVisibility(boolean visible) {
      labelsVisible = visible;
      for (View view : entryViews) {
        view.findViewById(R.id.text).setVisibility(visible ? View.VISIBLE : View.GONE);
      }
    }

    @Override
    public int getCount() {
      return entries.size();
    }

    @Override
    public VideoEntry getItem(int position) {
      return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;
      VideoEntry entry = entries.get(position);

      // There are three cases here
      if (view == null) {
        // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
        view = inflater.inflate(R.layout.video_list_item, parent, false);
        YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
        thumbnail.setTag(entry.videoId);
        thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
      } else {
        YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
        YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
        if (loader == null) {
          // 2) The view is already created, and is currently being initialized. We store the
          //    current videoId in the tag.
          thumbnail.setTag(entry.videoId);
        } else {
          // 3) The view is already created and already initialized. Simply set the right videoId
          //    on the loader.
          thumbnail.setImageResource(R.drawable.loading_thumbnail);
          loader.setVideo(entry.videoId);
        }
      }
      TextView label = ((TextView) view.findViewById(R.id.text));
      label.setText(entry.text);
      label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
      return view;
    }

    private final class ThumbnailListener implements
        YouTubeThumbnailView.OnInitializedListener,
        YouTubeThumbnailLoader.OnThumbnailLoadedListener {

      @Override
      public void onInitializationSuccess(
          YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
        loader.setOnThumbnailLoadedListener(this);
        thumbnailViewToLoaderMap.put(view, loader);
        view.setImageResource(R.drawable.loading_thumbnail);
        String videoId = (String) view.getTag();
        loader.setVideo(videoId);
      }

      @Override
      public void onInitializationFailure(
          YouTubeThumbnailView view, YouTubeInitializationResult loader) {
        view.setImageResource(R.drawable.no_thumbnail);
      }

      @Override
      public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
      }

      @Override
      public void onThumbnailError(YouTubeThumbnailView view, ErrorReason errorReason) {
        view.setImageResource(R.drawable.no_thumbnail);
      }
    }

  }

  public static final class VideoFragment extends YouTubePlayerFragment
      implements OnInitializedListener {

    private YouTubePlayer player;
    private String videoId;

    public static VideoFragment newInstance() {
      return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public void onDestroy() {
      if (player != null) {
        player.release();
      }
      super.onDestroy();
    }

    public void setVideoId(String videoId) {
      if (videoId != null && !videoId.equals(this.videoId)) {
        this.videoId = videoId;
        if (player != null) {
          player.cueVideo(videoId);
        }
      }
    }

    public void pause() {
      if (player != null) {
        player.pause();
      }
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean restored) {
      this.player = player;
      player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
      player.setOnFullscreenListener((VideoListDemoActivity) getActivity());
      if (!restored && videoId != null) {
        player.cueVideo(videoId);
      }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
      this.player = null;
    }

  }

  private static final class VideoEntry {
    private final String text;
    private final String videoId;

    public VideoEntry(String text, String videoId) {
      this.text = text;
      this.videoId = videoId;
    }
  }

  // Utility methods for layouting.

  private int dpToPx(int dp) {
    return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
  }

  private static void setLayoutSize(View view, int width, int height) {
    LayoutParams params = view.getLayoutParams();
    params.width = width;
    params.height = height;
    view.setLayoutParams(params);
  }

  private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
    params.width = width;
    params.height = height;
    params.gravity = gravity;
    view.setLayoutParams(params);
  }

}

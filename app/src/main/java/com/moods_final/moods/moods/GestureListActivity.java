package com.moods_final.moods.moods;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Set;

public class GestureListActivity extends AppCompatActivity {
    private static final String TAG = "GestureListActivity";
    private String mCurrentGestureName,newName;
    private ListView mGestureListView;
    private static ArrayList<GestureHolder> mGestureList;
    private GestureAdapter mGestureAdapter;
    private GestureLibrary gLib;
    private FirebaseAuth firebaseAuth;

    //private ImageView mMenuItemView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestures_list);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();
        Log.d(TAG, getApplicationInfo().dataDir);

       openOptionsMenu();

        mGestureListView = (ListView) findViewById((R.id.gestures_list));
        mGestureList = new ArrayList<GestureHolder>();
        listMake();
        mGestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        mGestureListView.setLongClickable(true);
        mGestureListView.setAdapter(mGestureAdapter);

        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(mGestureListView);
    }

    @Override
    public void onAttachedToWindow() {
        openOptionsMenu();
    };

    @Override
    public void onResume(){
                   super.onResume();
        setContentView(R.layout.gestures_list);
        Log.d(TAG, getApplicationInfo().dataDir);

        openOptionsMenu();

        mGestureListView = (ListView) findViewById((R.id.gestures_list));
        mGestureList = new ArrayList<GestureHolder>();
        listMake();
        mGestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        mGestureListView.setLongClickable(true);
        mGestureListView.setAdapter(mGestureAdapter);
        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(mGestureListView);
    }

    private void listMake() {
        try {
            mGestureList = new ArrayList<GestureHolder>();
            gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
            gLib.load();
            Set<String> gestureSet = gLib.getGestureEntries();
            for(String gestureName: gestureSet){
                ArrayList<Gesture> list = gLib.getGestures(gestureName);
                for(Gesture g : list) {
                    mGestureList.add(new GestureHolder(g, gestureName));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateMenu(View view){
        //ImageView idView = (ImageView) view.findViewById(R.id.gesture_id);
        //Log.d(TAG, "ha ha" + idView.getText().toString());
        LinearLayout vwParentRow = (LinearLayout)view.getParent().getParent();
        TextView tv = (TextView)vwParentRow.findViewById(R.id.gesture_name_ref);
        mCurrentGestureName = tv.getText().toString();
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.gesture_item_options, popup.getMenu());
        popup.show();
    }

    public void addButtonClick(View view){
        Intent saveGesture = new Intent(GestureListActivity.this, SaveGestureActivity.class);
        startActivity(saveGesture);
    }

    public void testButtonClick(View view){
        Intent testGesture = new Intent(GestureListActivity.this, GestureActivity.class);
        startActivity(testGesture);
    }

    public void deleteButtonClick(MenuItem item){
        gLib.removeEntry(mCurrentGestureName);
        gLib.save();
        mCurrentGestureName = "";
        onResume();
    }

    public void renameButtonClick(MenuItem item){

        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle(getString(R.string.enter_new_name));
        //namePopup.setMessage(R.string.enter_name);

        final EditText nameField = new EditText(this);
        namePopup.setView(nameField);

        namePopup.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameField.getText().toString().matches("")) {
                    newName = nameField.getText().toString();
                    saveGesture();
                } else {
                    renameButtonClick(null);  //TODO : validation
                    showToast(getString(R.string.invalid_name));
                }
            }
        });
        namePopup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newName = "";
                mCurrentGestureName = "";
                return;
            }
        });

        namePopup.show();
    }

    private void saveGesture() {
        ArrayList<Gesture> list = gLib.getGestures(mCurrentGestureName);
        if (list.size() > 0) {
            gLib.removeEntry(mCurrentGestureName);
            gLib.addGesture(newName, list.get(0));
            if (gLib.save()) {
                Log.e(TAG, "gesture renamed!");
                onResume();
            }
        }
        newName = "";
    }
    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
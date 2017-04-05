package com.moods_final.moods.feed;

/*import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Chat_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        Intent i=getIntent();
        String sec_person_id=i.getStringExtra("send_id");
        String sec_person_name=i.getStringExtra("send_name");
    }
}*/
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moods_final.moods.moods.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_Activity extends AppCompatActivity implements View.OnClickListener{

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.sendButton)
        {

            final FriendlyMessage friendlyMessage = new
                    FriendlyMessage(mMessageEditText.getText().toString(),
                    first_person_name,
                    mPhotoUrl,
                    first_person_id);    //Sending person

            mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean val1=false,val2=false;
                    int cnt=2,flag=3;
                    for (DataSnapshot children : dataSnapshot.child(MESSAGES_CHILD).getChildren()) {



                        if(children.getKey().startsWith(first_person_id)&&children.getKey().endsWith(sec_person_id))
                        {
                            flag=0;
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(first_person_id + "," + sec_person_id).push().setValue(friendlyMessage);
                            Log.e("TAG1",children.getKey().toString());
                            break;
                        }
                        else if(children.getKey().startsWith(sec_person_id)&&children.getKey().endsWith(first_person_id))
                        {
                            flag=0;
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(sec_person_id+ "," + first_person_id).push().setValue(friendlyMessage);
                            Log.e("TAG2",children.getKey().toString());
                            break;
                        }
//
                        mMessageEditText.setText("");



                        Log.e("TAG3",children.getKey().toString());
                    }
                    if(flag==3){

                        Map<String,String> mp=new HashMap<String, String>();
                        mp.put("name",friendlyMessage.getName());
                        mp.put("source",friendlyMessage.getSource());
                        mp.put("text",friendlyMessage.getText());
//                                mp.put("with_name",sec_person_name);
//                                Log.e("case ","2");
                        Map<String,Object> l=new HashMap<String, Object>();
                        l.put("message",mp);
                        Log.e("TAG4","d");

                        mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(first_person_id+","+sec_person_id).push().setValue(friendlyMessage) ;

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    dataSnapshot
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    private static final String TAG = "FeedActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;



    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    String sec_person_id;
    String sec_person_name;
    String first_person_id;
    String first_person_name;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    String finalmainkey="" ;
    private String get_sequence_order()
    {
        mFirebaseDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot children : dataSnapshot.child(MESSAGES_CHILD).getChildren()) {



                    if(children.getKey().startsWith(first_person_id)&&children.getKey().endsWith(sec_person_id))
                    {
                        finalmainkey=first_person_id+","+sec_person_id;
                        break;
                    }
                    else if(children.getKey().startsWith(sec_person_id)&&children.getKey().endsWith(first_person_id))
                    {
                        finalmainkey=sec_person_id+","+first_person_id;
                            break;
                    }
                    mMessageEditText.setText("");



                    Log.e("TAG77","");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            return finalmainkey;
    }

    public void setLayout(){
                    setContentView(R.layout.activity_chat_);
            //            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecycler);
        mLinearLayoutManager = new LinearLayoutManager(Chat_Activity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);

        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

                    mSendButton.setOnClickListener(this);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
                            MessageViewHolder>(
                            FriendlyMessage.class,
                            R.layout.item_message,
                            MessageViewHolder.class,
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(finalmainkey)) {

                        @Override
                        protected void populateViewHolder(MessageViewHolder viewHolder,
                                                          FriendlyMessage friendlyMessage, int position) {
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            viewHolder.messageTextView.setText(friendlyMessage.getText());
                            viewHolder.messengerTextView.setText(friendlyMessage.getName());
                            if (friendlyMessage.getPhotoUrl() == null) {
                                viewHolder.messengerImageView
                                        .setImageDrawable(ContextCompat
                                                .getDrawable(Chat_Activity.this,
                                                        R.drawable.ic_account_circle_black_36dp));
                            } else {
                                Glide.with(Chat_Activity.this)
                                        .load(friendlyMessage.getPhotoUrl())
                                        .into(viewHolder.messengerImageView);
                            }
                        }
                    };
                    mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount) {
                            super.onItemRangeInserted(positionStart, itemCount);
                            int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                            int lastVisiblePosition =
                                    mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                            // If the recycler view is initially being loaded or the
                            // user is at the bottom of the list, scroll to the bottom
                            // of the list to show the newly added message.
                            if (lastVisiblePosition == -1 ||
                                    (positionStart >= (friendlyMessageCount - 1) &&
                                            lastVisiblePosition == (positionStart - 1))) {
                                mMessageRecyclerView.scrollToPosition(positionStart);
                            }
                        }
                    });
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

                    Getmessage_id g=new Getmessage_id();
                    g.execute();

    }

    @Override
    public void onBackPressed() {
//        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent=getIntent();
        sec_person_id=intent.getStringExtra("send_id");
        sec_person_name=intent.getStringExtra("send_name");
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        mUsername=sec_person_name;
        /*Firebase Code*/
        mFirebaseAuth= FirebaseAuth.getInstance();
        mFirebaseUser=mFirebaseAuth.getCurrentUser();


//        Getting own data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("FB_USER", MODE_PRIVATE);
        first_person_name = prefs.getString("own_name", "No name");//"No name defined" is the default value.
        first_person_id = prefs.getString("own_id", " "); //0 is the default value.
        setTitle(sec_person_name);

        FetchKey k=new FetchKey();
        k.execute();

        Log.e("aaa","555");




//                return finalmainkey;





//        getSupportActionBar().setIcon(R.drawable.cast_album_art_placeholder);
        Toast.makeText(getApplicationContext(),"11"+first_person_id+first_person_name,Toast.LENGTH_SHORT).show();
//        Data Fetched from SharedPref



        Log.e("eooror",finalmainkey);









    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public class Getmessage_id extends AsyncTask<Void,Void,Void>{

        ProgressDialog pg=new ProgressDialog(Chat_Activity.this);

        @Override
        protected Void doInBackground(Void... params) {
            mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
                    MessageViewHolder>(
                    FriendlyMessage.class,
                    R.layout.item_message,
                    MessageViewHolder.class,
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(finalmainkey)) {

                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder,
                                                  FriendlyMessage friendlyMessage, int position) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messengerTextView.setText(friendlyMessage.getName());
                    if (friendlyMessage.getPhotoUrl() == null) {
                        viewHolder.messengerImageView
                                .setImageDrawable(ContextCompat
                                        .getDrawable(Chat_Activity.this,
                                                R.drawable.ic_account_circle_black_36dp));
                    } else {
                        Glide.with(Chat_Activity.this)
                                .load(friendlyMessage.getPhotoUrl())
                                .into(viewHolder.messengerImageView);
                    }
                }
            };

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setMessage("Loading");
            pg.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pg.dismiss();
        }
    }
    public class FetchKey extends AsyncTask<Void,Void,Void> {
        ProgressDialog pg=new ProgressDialog(Chat_Activity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setMessage("Fetching Key");
            pg.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pg.dismiss();



        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.e("aab","555");
            mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("aab","555");
                    for (DataSnapshot children : dataSnapshot.child(MESSAGES_CHILD).getChildren()) {
                        Log.e("aac","555");


                        if(children.getKey().startsWith(first_person_id)&&children.getKey().endsWith(sec_person_id))
                        {
                            finalmainkey=first_person_id+","+sec_person_id;
                            break;
                        }
                        else if(children.getKey().startsWith(sec_person_id)&&children.getKey().endsWith(first_person_id))
                        {
                            finalmainkey=sec_person_id+","+first_person_id;
                            break;
                        }
//                            mMessageEditText.setText("");



                        Log.e("TAG77","");
                    }
                    Log.e("aaa","555"+finalmainkey);
                    setLayout();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;

        }
    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId())
//        {
//            case R.id.sign_out_menu:
//                mFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                mUsername=ANONYMOUS;
//                startActivity(new Intent(FeedActivity.this,SignInActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }


}


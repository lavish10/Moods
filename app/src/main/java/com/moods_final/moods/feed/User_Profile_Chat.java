package com.moods_final.moods.feed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moods_final.moods.moods.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link User_Profile_Chat.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link User_Profile_Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User_Profile_Chat extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView imageView;
    private TextView user_name;
    private TextView user_age;
    private TextView user_gender;
    private OnFragmentInteractionListener mListener;

    public User_Profile_Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment User_Profile_Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static User_Profile_Chat newInstance(String param1, String param2) {
        User_Profile_Chat fragment = new User_Profile_Chat();
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
        View v= inflater.inflate(R.layout.fragment_user__profile__chat, container, false);
        imageView=(ImageView)v.findViewById(R.id.user_image);
        user_name=(TextView)v.findViewById(R.id.user_name);
        user_gender=(TextView)v.findViewById(R.id.user_gender);
        user_age=(TextView)v.findViewById(R.id.user_age);
        AppEventsLogger.activateApp(getContext());
        FacebookSdk.sdkInitialize(getContext());

        FetchDetails fd=new FetchDetails();
        fd.execute();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
    public void getDetails(){
        SharedPreferences prefs = getActivity().getSharedPreferences("FB_USER", MODE_PRIVATE);
//        user_name.setText(prefs.getString("own_name", "No name"));//"No name defined" is the default value.
        final String user_id_sp= prefs.getString("own_id", " "); //0 is the default value.

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
//            DataSnapshot d= myRef;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e("snapshot",dataSnapshot.toString());
                for (DataSnapshot children : dataSnapshot.child("Users").getChildren()) {
                    {
                        if(children.child("F_Id").getValue().toString().equals(user_id_sp))
                        {

                                    Glide.with(getContext()).load(children.child("Profile_pic").getValue().toString()).into(imageView);
                                    user_name.setText(children.child("Name").getValue().toString());
                                    user_age.setText(children.child("Age").getValue().toString());
                                    user_gender.setText(children.child("Gender").getValue().toString());

                        }
/*                        Integer age=Integer.parseInt(children.child("Age").getValue().toString());
                        String gender=children.child("Gender").getValue().toString();
                        if((age<=chat_max_age&&age>=chat_min_age)&&getGenderFilter(gender)) {
                            Log.e("aaa", children.child("F_Id").getValue().toString());
                            all_users.add(children.child("Name").getValue().toString());
                            all_ids.add(children.child("F_Id").getValue().toString());
                            Log.e("sds", children.child("Name").getValue().toString());
//                            all_user_id.put(children.child("F_Id").getValue().toString(),children.child("Name").getValue().toString());*/
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        }
    public class FetchDetails extends AsyncTask<Void,Void,Void>{
        ProgressDialog pg=new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setMessage("Fetching Profile");
            pg.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pg.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getDetails();

            return null;

        }
    }
}

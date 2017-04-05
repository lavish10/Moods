package com.moods_final.moods.feed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moods_final.moods.moods.R;
import com.moods_final.moods.moods.other.CircleTransform;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button blank_button1;
    TextView blank_textView1;
    ListView lv1;
//    RecyclerView rl1;
    ArrayList<String> all_users;
    ArrayList<String> all_ids;
    ArrayList<String> all_images;
    public static int chat_min_age;
    public static int chat_max_age;
    public static ArrayList<String> chat_gender;
//
    ArrayAdapter<String> arrayAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.blank_button1)
        {
            all_ids=new ArrayList<String>();
            all_users=new ArrayList<String>();
            all_images=new ArrayList<String>();
            getUsers g=new getUsers();
            g.execute();

//            Log.e("Firebase,",myRef.);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
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
        all_users=new ArrayList<String>();
        all_ids=new ArrayList<String>();
        chat_gender=new ArrayList<String>();

    }
FirebaseAuth firebaseAuth;
    CustomList customList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_blank, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        blank_button1=(Button)v.findViewById(R.id.blank_button1);
//        blank_textView1=(TextView) v.findViewById(R.id.blank_textView1);
        lv1=(ListView)v.findViewById(R.id.lv1);

//        customList = new CustomList(getActivity(), all_users.toArray(new String[all_users.size()]),
//                all_ids.toArray(new String[all_ids.size()]),
//                all_images.toArray(new Integer[all_images.size()]));

        blank_button1.setOnClickListener(this);

        getUsers g= new getUsers();
        g.execute();
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text_name = all_users.get(position);
                String id_a=all_ids.get(position);
                Toast.makeText(getContext(),text_name+id_a,Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getActivity(),Chat_Activity.class);
                i.putExtra("send_id",id_a);
                i.putExtra("send_name",text_name);
                startActivity(i);


            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/
    public boolean getGenderFilter(String gender){
        boolean flag=false;
        for(int i=0;i<chat_gender.size();i++)
        {
            if(chat_gender.get(i).equals(gender)){
                return true;
            }
        }
        return false;
    }
    public  void get_Data_Users(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
//            DataSnapshot d= myRef;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e("snapshot",dataSnapshot.toString());
                for (DataSnapshot children : dataSnapshot.child("Users").getChildren()) {
                    //if(children.child("F_Id").exists())
                    {

                        Integer age=Integer.parseInt(children.child("Age").getValue().toString());
                        String gender=children.child("Gender").getValue().toString();
                        if((age<=chat_max_age&&age>=chat_min_age)&&getGenderFilter(gender)) {
                            Log.e("aaa", children.child("F_Id").getValue().toString());
                            all_users.add(children.child("Name").getValue().toString());
                            all_ids.add(children.child("F_Id").getValue().toString());
                            all_images.add(children.child("Profile_pic").getValue().toString());
                            Log.e("sds", children.child("Name").getValue().toString());
//                            all_user_id.put(children.child("F_Id").getValue().toString(),children.child("Name").getValue().toString());
                        }
                    }

                }
                try {
                    customList = new CustomList(getActivity(), all_users.toArray(new String[all_users.size()]),
                            all_ids.toArray(new String[all_ids.size()]),
                            all_images.toArray(new String[all_ids.size()]));

                }catch (NullPointerException e){
                    e.printStackTrace();
                }

//                arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,all_users);
                lv1.setAdapter(customList);
                Log.e("list",all_users.toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e("Firebase,",myRef.child("Users").getParent().toString());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onStart(){
        super.onStart();
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

    public class getUsers extends AsyncTask<Void,Void,Void>{
        ProgressDialog pg=new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {

            pg.setMessage("Getting nearby Users");
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pg.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            get_Data_Users();
            return null;
        }


    }
    public class CustomList extends ArrayAdapter<String> {
        private String[] names;
        private String[] desc;
        private String[] imageid;
        private Activity context;

        public CustomList(Activity context, String[] names, String[] desc, String[] imageid) {
            super(context, R.layout.item_list, names);
            this.context = context;
            this.names = names;
            this.desc = desc;
            this.imageid = imageid;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.item_list, null, true);
            TextView textViewName = (TextView) listViewItem.findViewById(R.id.list_title);
            TextView textViewDesc = (TextView) listViewItem.findViewById(R.id.list_desc);
            CircleImageView image = (CircleImageView) listViewItem.findViewById(R.id.list_avatar);

            textViewName.setText(names[position]);
            textViewDesc.setText(desc[position]);
//            image.setImageResource(imageid[position]);
            Glide.with(getContext())
                    .load(imageid[position]).into(image); // Uri of the picture
                    //.transform(new CircleTransform());

            return  listViewItem;
        }
    }
}

package com.moods_final.moods.feed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.moods_final.moods.moods.R;
import com.moods_final.moods.feed.BlankFragment.*;
import com.moods_final.moods.feed.BlankFragment.getUsers.*;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Filters.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Filters#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Filters extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button submit;
    private EditText minage;
    private EditText maxage;
    private CheckBox male;
    private CheckBox female;
    private OnFragmentInteractionListener mListener;

    public Filters() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Filters.
     */
    // TODO: Rename and change types and number of parameters
    public static Filters newInstance(String param1, String param2) {
        Filters fragment = new Filters();
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
        View v= inflater.inflate(R.layout.fragment_filters, container, false);
        submit=(Button)v.findViewById(R.id.updatebtn);
        submit.setOnClickListener(this);
        minage=(EditText)v.findViewById(R.id.minAge);
        maxage=(EditText)v.findViewById(R.id.maxAge);
        male=(CheckBox)v.findViewById(R.id.mrb);
        female=(CheckBox)v.findViewById(R.id.frb);

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

    @Override
    public void onClick(View v) {
        if(v==submit)
        {
            BlankFragment.chat_min_age=Integer.parseInt(minage.getText().toString());
            BlankFragment.chat_max_age=Integer.parseInt(maxage.getText().toString());
            BlankFragment.chat_gender =new ArrayList<String>();
            if(male.isChecked())
            {
                BlankFragment.chat_gender.add("male");
            }
            if(female.isChecked())
            {
                BlankFragment.chat_gender.add("female");
            }
            Toast.makeText(getContext(),"Filters Updated",Toast.LENGTH_SHORT).show();
        }
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
}

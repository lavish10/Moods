package com.moods_final.moods.feed;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lavish on 27/11/16.
 */
@IgnoreExtraProperties
public class User {
    public class location{
        public double latitude;
        public double longitude;
    }
    public String fid;
    public String Name;
    public String Gender;
    public Date Birthday;
    public String image_url;
    public String Signuptype;
    public int age;
    public HashMap<String,String> likes;
    public HashMap<String,String> block_list;
    public HashMap<String,String> friends;
    public Date Member_since;
    public Date Last_login;

    public void set_basic_fb(String fid,String Name,String Date,String image_url,String Gender, String Signuptype)
    {
        this.fid=fid;
        this.Name=Name;
//        this.Birthday=new Date(Date);
        this.image_url=image_url;
        this.Signuptype=Signuptype;
        this.Gender=Gender;
        this.Signuptype=Signuptype;
        this.Member_since=new Date();
        this.Last_login=new Date();
        likes=new HashMap<String, String>();
        block_list=new HashMap<String,String>();
        friends=new HashMap<String, String>();
        DateFormat sourceFormat = new SimpleDateFormat("MM/DD/yyyy");

        try {
            this.Birthday = sourceFormat.parse(Date);
            Date d=new Date();
            int diffy=d.getYear()-this.Birthday.getYear();
            int diffm=d.getMonth()-this.Birthday.getMonth();
            int diffd=d.getDay()-this.Birthday.getDay();
            int cntd=0,cntm=0,cnty=0;
            if(diffm>=0&&diffd>=0)
            {
                this.age=diffy+1;
            }else
            {
                this.age=diffy;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void set_likes(HashMap<String,String> v)
    {
            likes=v;
    }

    public void set_friends(HashMap<String,String> v) {
        friends=v;
    }
    public void update_firedb()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        HashMap<String, Object> result = new HashMap<>();
        result.put("Name",Name);
        result.put("F_Id",fid);
        result.put("Gender",Gender);
        result.put("Age",age);
        result.put("Profile_pic",image_url);
        result.put("Likes",likes);
        result.put("Friends",friends);
        result.put("Block_list",block_list);
        result.put("SignupType",Signuptype);
        result.put("Member_since",Member_since);


        String key = myRef.child("Users").push().getKey();
        Map<String,Object>k=new HashMap<>();
        k.put("/User_"+fid,result);
        myRef.child("Users").updateChildren(k);
    }
}

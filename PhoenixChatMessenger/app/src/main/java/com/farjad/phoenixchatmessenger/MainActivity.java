package com.farjad.phoenixchatmessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import Fragments.ChatFragment;
import Fragments.ProfileFragment;
import Fragments.UsersFragment;
import Model.Chats;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import notification.MyFirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    CircleImageView profileImage;
    TextView userName;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        profileImage =findViewById(R.id.profile_image);
        userName = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        //then created new package and added new class user
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //now storing data to the newly created class
                User user =dataSnapshot.getValue(User.class);
                assert user != null;
                userName.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileImage);
                    //after that created new directory in res named menu and then created resource file menu then inflating menu
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager= findViewById(R.id.viewPager);
        //now create class viewpageradapter below
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread =0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats =  snapshot.getValue(Chats.class);
                    assert chats != null;
                    if(chats.getReceiver().equals(firebaseUser.getUid()) && !chats.isIsseen()){
                        unread++;

                    }
                    else if(chats.isIsseen()){
                        unread=0;
                        NotificationManager notificationManager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                        assert notificationManager != null;
                        notificationManager.cancelAll();
                    }
                }
                if(unread==0){
                    viewPagerAdapter.addfragment(new ChatFragment(),"Chats");
                }else {
                    viewPagerAdapter.addfragment(new ChatFragment(),"Chats ("+unread+")");
                }
                //add new package name fragment and add new black fragment chat and user
                viewPagerAdapter.addfragment(new UsersFragment(),"Users");
                viewPagerAdapter.addfragment(new ProfileFragment(),"Profile");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    //now inlflating menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return false;
    }
    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments =new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        void addfragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }
        //ctrl+o

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    private void status (String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap <String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}

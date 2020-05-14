package com.farjad.phoenixchatmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Adapter.MessageAdapter;
import Fragments.APIService;
import Model.Chats;
import Model.User;
import notification.Client;
import notification.Data;
import notification.MyResponse;
import notification.Sender;
import notification.Token;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
        CircleImageView profile_image;
        TextView userName;
        FirebaseUser fUser;
        DatabaseReference reference ;
        Intent intent;
        ImageButton btn_send;
        EditText txt_send;
        String userid;
        MessageAdapter messageAdapter;
        List<Chats> chatsList ;
        RecyclerView recyclerView;
        APIService apiService;
        boolean notify =false;

        ValueEventListener seenListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        //finish on backpressed
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            //finish();
        });
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView =findViewById(R.id.recyclerView_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image=findViewById(R.id.profile_image);
        userName =findViewById(R.id.username);
        btn_send=findViewById(R.id.btn_send);
        txt_send =findViewById(R.id.message_send);
        userName =findViewById(R.id.username);

        intent =getIntent();
        userid = intent.getStringExtra("userID");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                userName.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                readMessage(fUser.getUid(),userid,user.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            seenMessage(userid);
    }
    private void sendMessage(String sender,String receiver,String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap <String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        reference.child("Chats").push().setValue(hashMap);
        //add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fUser.getUid())
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String msg =message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify) {
                    assert user != null;
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference token  = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fUser.getUid(),R.mipmap.ic_launcher,username+":"+msg,"New Message",userid);
                    assert token != null;
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        assert response.body() != null;
                                        if(response.body().success!=1){
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if(chats.getReceiver().equals(fUser.getUid()) && chats.getSender().equals(userid)){
                        HashMap<String,Object> hashMap =  new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readMessage(final String myId, final String userid, final String imageURL){
        chatsList =new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if(chats.getReceiver().equals(userid)&&chats.getSender().equals(myId)||
                    chats.getSender().equals(userid)&& chats.getReceiver().equals(myId)){
                        chatsList.add(chats);
                    }
                }
                messageAdapter =new MessageAdapter(MessageActivity.this,chatsList,imageURL);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendMessage(View view) {
        notify = true;
        String msg = txt_send.getText().toString();
        if (!msg.equals("")) {
            sendMessage(fUser.getUid(), userid, msg);
        }
        else {
            Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        }

        txt_send.setText("");

    }
    //to not show notification if in chat
    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }
    private void status (String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap <String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        hashMap.put("isseen",false);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }
}

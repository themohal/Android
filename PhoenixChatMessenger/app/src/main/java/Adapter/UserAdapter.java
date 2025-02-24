package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.farjad.phoenixchatmessenger.MessageActivity;
import com.farjad.phoenixchatmessenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.Chats;
import Model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext ;
    private List <User> mUser;
    //for online and offline status
    private boolean isChat;
    private String theLastMessage;

    public UserAdapter(Context mContext,List<User> mUser,boolean isChat){
        this.mContext =mContext;
        this.mUser =mUser;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user =mUser.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        if(isChat){
            lastMessage(user.getId(),holder.lastMessage);
        }else {
            holder.lastMessage.setVisibility(View.GONE);
        }
        //for online and offline status
        if(isChat){
            if(user.getStatus().equals("online")){
                holder.img_online.setVisibility(View.VISIBLE);
                holder.img_offline.setVisibility(View.GONE);
            }else {
                holder.img_offline.setVisibility(View.VISIBLE);
                holder.img_online.setVisibility(View.GONE);
            }
        }else {
            holder.img_offline.setVisibility(View.GONE);
            holder.img_online.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent =new Intent(mContext, MessageActivity.class);
            intent.putExtra("userID",user.getId());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        ImageView img_online;
        ImageView img_offline;
        TextView lastMessage;
        ViewHolder(View itemView){
            super(itemView);
            username =itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_online = itemView.findViewById(R.id.img_online);
            img_offline = itemView.findViewById(R.id.img_offline);
            lastMessage = itemView.findViewById(R.id.lastMessage);

        }
   }
   //check for the last message
    private void lastMessage(final String userId, final TextView lastMessage){
        theLastMessage ="default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    assert firebaseUser != null;
                    if(chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(userId) ||
                            chats.getReceiver().equals(userId) && chats.getSender().equals(firebaseUser.getUid())){
                    theLastMessage =chats.getMessage();
                    }
                }
                if ("default".equals(theLastMessage)) {
                    lastMessage.setText(R.string.user_adpter_last_message);
                } else {
                    lastMessage.setText(theLastMessage);
                }
                theLastMessage ="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

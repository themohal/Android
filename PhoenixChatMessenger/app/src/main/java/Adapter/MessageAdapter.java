package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.farjad.phoenixchatmessenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import Model.Chats;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT =0;
    private static final int MSG_TYPE_RIGHT =1;

    private Context mContext ;
    private List<Chats> mChat;
    private String imageURL;

    public MessageAdapter(Context mContext,List<Chats> mChat,String imageURL){
        this.mContext =mContext;
        this.mChat =mChat;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("ViewType:",""+viewType);
        if (viewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else  {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chats chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        if(imageURL.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(imageURL).into(holder.profile_image);
        }
        if(position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText(R.string.holder_seen);
            }else {
                holder.txt_seen.setText(R.string.holder_delivered);
            }
        }else {//checking for last message
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView show_message;
        public ImageView profile_image;
        TextView txt_seen;
        ViewHolder(View itemView){
            super(itemView);
            show_message =itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen= itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("POSITION:",""+position);
        assert fuser != null;
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
package com.decima.blogger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decima.blogger.R;
import com.decima.blogger.listeners.ListItemSelectListener;
import com.decima.blogger.model.UserModel;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.UserViewHolder> {

    private final List<UserModel> data;
    private final Context context;
    private ListItemSelectListener<UserModel> listener;

    public ChatUserAdapter(List<UserModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setListener(ListItemSelectListener<UserModel> listener){
        this.listener = listener;
    }



    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_user_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        final UserModel userModel = data.get(position);
        holder.userName.setText(userModel.getUserName());
        Glide.with(context).asBitmap().load(userModel.getImage()).into(holder.profileImage);

        holder.itemView.setOnClickListener(view -> listener.onSelectItem(userModel));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        ImageView profileImage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profileImage = itemView.findViewById(R.id.img_profile);
            this.userName = itemView.findViewById(R.id.tv_user_name);
        }
    }

}

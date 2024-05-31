package com.decima.blogger.activity;

import static utils.Constance.EXTRA_KEY_DATA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.decima.blogger.R;
import com.decima.blogger.adapters.ChatUserAdapter;
import com.decima.blogger.databinding.ActivityChatListBinding;
import com.decima.blogger.listeners.ListItemSelectListener;
import com.decima.blogger.model.UserModel;

import utils.Constance;

public class ChatListActivity extends AppCompatActivity {

    private ActivityChatListBinding chatListBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        chatListBinding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(chatListBinding.getRoot());

        setAdapter();
    }

    private void setAdapter() {
        ChatUserAdapter adapter = new ChatUserAdapter(Constance._chat_users,this);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        chatListBinding.chatUserList.setLayoutManager(manager);
        chatListBinding.chatUserList.setAdapter(adapter);

        adapter.setListener(this::startChatActivity);
    }

    private void startChatActivity(UserModel data) {
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra(EXTRA_KEY_DATA,data);
        startActivity(intent);
    }
}
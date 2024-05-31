package com.decima.blogger.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.decima.blogger.R;
import com.decima.blogger.adapters.ChatAdapter;
import com.decima.blogger.databinding.ActivityChatBinding;
import com.decima.blogger.databinding.ActivitySearchBinding;
import com.decima.blogger.db.DatabaseService;
import com.decima.blogger.model.ChatItemModel;
import com.decima.blogger.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import utils.Constance;
import utils.ListItemSpace;

public class SearchActivity extends AppCompatActivity {


    private UserModel userModel;
    private MutableLiveData<List<ChatItemModel>> chatData = new MutableLiveData<>();
    private ActivitySearchBinding binding;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        readExtras(getIntent());
        setAdapter();
        setupToolbar();
        readInputs();
        observeResult();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }

    private void setAdapter() {
        chatAdapter = new ChatAdapter(this,true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.searchList.setLayoutManager(layoutManager);
        binding.searchList.setAdapter(chatAdapter);
        chatAdapter.setData(new ArrayList<>());
        binding.searchList.setItemAnimator(new DefaultItemAnimator() );
        binding.searchList.addItemDecoration(new ListItemSpace());
    }

    private void observeResult() {
        chatData.observe(this, new Observer<List<ChatItemModel>>() {
            @Override
            public void onChanged(List<ChatItemModel> chatItemModels) {
                if(chatAdapter != null)
                    chatAdapter.setData(chatItemModels);
            }
        });
    }

    private void readInputs() {

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                DatabaseService.getInstance(SearchActivity.this)
                        .searchMessage(charSequence.toString().trim(),chatData);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void readExtras(Intent intent){
        if(intent.hasExtra(Constance.EXTRA_KEY_DATA)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                userModel = intent.getExtras().getSerializable(Constance.EXTRA_KEY_DATA, UserModel.class);
            } else{
                userModel = (UserModel) intent.getExtras().getSerializable(Constance.EXTRA_KEY_DATA);
            }
        }
        else
            finish();
    }

}
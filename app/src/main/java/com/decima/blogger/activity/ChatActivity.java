package com.decima.blogger.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.decima.blogger.R;
import com.decima.blogger.adapters.ChatAdapter;
import com.decima.blogger.databinding.ActivityChatBinding;
import com.decima.blogger.databinding.ActivityChatListBinding;
import com.decima.blogger.db.DatabaseService;
import com.decima.blogger.fragment.BottomSheetFragment;
import com.decima.blogger.listeners.ProcessListener;
import com.decima.blogger.model.ChatItemModel;
import com.decima.blogger.model.UserModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.Constance;
import utils.ListItemSpace;
import utils.Utils;

public class ChatActivity extends AppCompatActivity {

    private static int PICK_IMAGE = 1001;

    private ActivityChatBinding binding;
    private UserModel userModel;
    private MutableLiveData<List<ChatItemModel>> chatList;
    private ChatAdapter adapter;
    public static final int PERMISSION_CAMERA = 1001;
    public static final int PERMISSION_GALERY = 1002;
    public static final int REQUEST_CAMERA = 2;
    private File mPhotoFile;
    private boolean isUpdate = false;
    public List<ChatItemModel> selectedList = new ArrayList<>();
    public boolean isSelectedOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        readExras(getIntent());
        setupToolbar();
        setUserData();
        setInputLayout();
        setAdapter();
        observeChatList();
        getChatData();
        actionBottomSheet();
    }

    private void actionBottomSheet() {
        binding.imgCamera.setOnClickListener(view -> {

            BottomSheetFragment bottomSheetFragment = BottomSheetFragment.newInstance();
            bottomSheetFragment.setListenr(new BottomSheetFragment.OnActionSelect() {
                @Override
                public void onSelectAction(int action) {
                    oprnAction(action);
                    bottomSheetFragment.dismiss();
                }
            });
            
            bottomSheetFragment.show(getSupportFragmentManager(),"tag");
            
        });
    }

    private void oprnAction(int action) {
        if(action == 1){

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
                openCamera();
            else
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
        }
        else{
                openGallery();

        }
    }

    private void openCamera() {
        if(mPhotoFile != null) mPhotoFile = null;
        mPhotoFile = new File(getFilesDir(),"_IMG_BLOGGER_"+System.currentTimeMillis());
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(this,"com.decima.blogger.fileprovider",mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        List<ResolveInfo> cameraActivities = getPackageManager().queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo activity : cameraActivities){
           grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(captureImage,REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CAMERA && grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openCamera();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getImage(){

        Uri uri = FileProvider.getUriForFile(this,"com.decima.blogger.fileprovider",mPhotoFile);
        revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        final Bitmap scaledBitmap = Utils.getScaledBitmap(mPhotoFile.getPath(),this) ;// Utils.getScaledBitmap(mPhotoFile.getPath(), 3, 3);
        sendImage(scaledBitmap);
        mPhotoFile = null;

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void setAdapter() {
        adapter = new ChatAdapter(this,false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        binding.rvChat.setLayoutManager(layoutManager);
        binding.rvChat.setAdapter(adapter);
        adapter.setData(new ArrayList<>());
        binding.rvChat.setItemAnimator(new DefaultItemAnimator() );
        binding.rvChat.addItemDecoration(new ListItemSpace());
    }

    private void setInputLayout() {

        binding.imgSend.setOnClickListener(view -> {
            sendNewMessage(binding.edtMessage.getText().toString(),Constance._MESSAGE_TYPE_TEXT);
        });

        binding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(isUpdate && charSequence.length() == 0)
                    isUpdate = false;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.deleteBtn.setOnClickListener(view -> {
            if(isSelectedOn && selectedList.size() > 0){
                showDeleteConfirmDilaog();
            }
        });

        binding.searchBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this,SearchActivity.class);
            intent.putExtra(Constance.EXTRA_KEY_DATA,userModel);
            startActivity(intent);
        });

    }

    private void init(){
        chatList = new MutableLiveData<>();
    }

    private void sendNewMessage(String message, int type) {

        if(message.equals(""))
            return;

        if(isUpdate) {
            updateImage(selectedList.get(0));
            isUpdate = false;
            return;
        }

       // String message = binding.edtMessage.getText().toString();


        ChatItemModel chatItemModel = new ChatItemModel();
        chatItemModel.setMessage(message);
        chatItemModel.setDeleverd(true);
        chatItemModel.setReaded(false);
        chatItemModel.setSelected(false);
        chatItemModel.setTime(System.currentTimeMillis());
        chatItemModel.setUser_id(userModel.getId());
        chatItemModel.setMessageType(type);
        chatItemModel.setMine(true);
        chatItemModel.setSenderId(0);

        DatabaseService.getInstance(this)
                .insertNewMessage(chatItemModel);

        new Handler().postDelayed(this::getChatData, 1000);
        binding.edtMessage.getText().clear();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }

    private void setUserData() {
        Glide.with(this).asBitmap().load(userModel.getImage())
                .into(binding.imgProfile);

        binding.tvUserName.setText(userModel.getUserName());
    }

    private void readExras(Intent intent) {

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

    private void observeChatList(){
        chatList.observe(this, chatItemModels -> {
            runOnUiThread(()->{
                adapter.setData(chatItemModels);
            });
        });
    }

    private void getChatData(){
        DatabaseService.getInstance(this)
                .getChatByUserId(userModel,chatList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            processPickImage(data);
        }
        else if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            getImage();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processPickImage(Intent data) {
        if(data == null)
            return;

        final Uri uri = data.getData();
        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            sendImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendImage(Bitmap bitmap) {
        final String s = Utils.bitmapToBase64(bitmap);
        sendNewMessage(s,Constance._MESSAGE_TYPE_IMAGES);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getOrder() == 1){
            isUpdate = true;
            final ChatItemModel chatItemModel = chatList.getValue().get(item.getGroupId());
            final String message = chatItemModel.getMessage();
            binding.edtMessage.setText(message);
         //   chatItemModel.setMessage(binding.edtMessage.getText().toString().trim());
            selectedList.clear();
            selectedList.add(chatItemModel);
          //  updateImage(chatItemModel);
        }
        else if(item.getOrder() == 2){
            isSelectedOn = true;
            selectFristItem(item.getGroupId());
            animateView(150);
        }

        return true;
    }

    private void selectFristItem(int groupId) {
        adapter.selectItem(groupId);
        binding.searchBtn.setVisibility(View.GONE);
        binding.deleteBtn.setVisibility(View.VISIBLE);

    }

    private void updateImage(ChatItemModel chatItemModel) {
        chatItemModel.setMessage(binding.edtMessage.getText().toString().trim());
        DatabaseService.getInstance(this).updateMessage(chatItemModel);
        new Handler().postDelayed(this::getChatData,1000);
        isUpdate = false;
        binding.edtMessage.getText().clear();
        selectedList.clear();
    }

    @Override
    public void onBackPressed() {
        if(isSelectedOn)
        {
            binding.deleteBtn.setVisibility(View.GONE);
            binding.searchBtn.setVisibility(View.VISIBLE);
            selectedList.clear();
            isSelectedOn = false;
            adapter.resetData();
            animateView(0);
            return;
        }
        super.onBackPressed();
    }


    public void animateView(int y){
        binding.inputParent.animate()
                .translationY(y).setDuration(300)
                .start();
    }

    private void showDeleteConfirmDilaog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Delete Selected message")
                .setTitle("Confirm!")
                .setPositiveButton("delete", (dialogInterface, i) -> {
                    DatabaseService.getInstance(ChatActivity.this)
                            .deleteChat(selectedList, () -> {
                                runOnUiThread(() -> {
                                    isSelectedOn = false;
                                    selectedList.clear();
                                    binding.deleteBtn.setVisibility(View.GONE);
                                    binding.searchBtn.setVisibility(View.VISIBLE);
                                    animateView(0);
                                });
                                getChatData();
                            });
                    dialogInterface.dismiss();
                })
                .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }
}
package com.decima.blogger.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.decima.blogger.listeners.ProcessListener;
import com.decima.blogger.model.ChatItemModel;
import com.decima.blogger.model.UserModel;

import java.util.List;
import java.util.concurrent.Executors;

import utils.Constance;

public class DatabaseService {

    private static DatabaseService databaseService;
    private BloggerDatabase database;

    public synchronized static DatabaseService getInstance(Context context){
        if(databaseService == null)
            databaseService = new DatabaseService(context);

        return databaseService;
    }


    private DatabaseService(Context context){
        this.database = BloggerDatabase.getInstance(context);
    }

    public void insertNewMessage(ChatItemModel chatItemModel){
        Executors.newSingleThreadExecutor()
                .execute(()->database.getDao().insertNewMessage(chatItemModel));
    }

    public void getChatByUserId(UserModel userModel, MutableLiveData<List<ChatItemModel>> liveData){
        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    final List<ChatItemModel> allChatByUserId = database.getDao().getAllChatByUserId(userModel.getId());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                      @Override
                      public void run() {
                          liveData.setValue(allChatByUserId);
                      }
                  });

                });
    }

    public void deleteChat(List<ChatItemModel> chatItemModel,ProcessListener listener){
        Executors.newSingleThreadExecutor()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        database.getDao().deleteChatItem(chatItemModel);
                        if(listener != null)
                            listener.onComplete();
                    }
                });
    }

    public void updateMessage(ChatItemModel chatItemModel) {
        Executors.newSingleThreadExecutor()
                .execute(() -> database.getDao().updateChatItem(chatItemModel));
    }

    public void searchMessage(String msg, MutableLiveData<List<ChatItemModel>> mutableLiveData){
        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    final List<ChatItemModel> chatItemModels = database.getDao().searchMessage(Constance._MESSAGE_TYPE_TEXT, "%" + msg + "%");
                    new Handler(Looper.getMainLooper())
                            .post(() -> mutableLiveData.setValue(chatItemModels));
                });
    }
}

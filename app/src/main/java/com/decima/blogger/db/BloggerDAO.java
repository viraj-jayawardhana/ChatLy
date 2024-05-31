package com.decima.blogger.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.decima.blogger.model.ChatItemModel;

import java.util.List;

@Dao
public interface BloggerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewMessage(ChatItemModel itemModel);

    @Query("SELECT * FROM tbl_chats WHERE user_id =:id ORDER BY time DESC")//
    List<ChatItemModel> getAllChatByUserId(int id);

    @Delete
    void deleteChatItem(List<ChatItemModel> itemModels);

    @Update
    void updateChatItem(ChatItemModel itemModel);

    @Query("SELECT * FROM tbl_chats WHERE messageType =:type AND message LIKE :msg")
    List<ChatItemModel> searchMessage(int type,String msg);

//    @Query("SELECT * FROM tbl_chats WHERE user_id =:id AND ")
//    List<ChatItemModel> searchChatMessage(String text, int id);

}

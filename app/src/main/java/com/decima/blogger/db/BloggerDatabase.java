package com.decima.blogger.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.decima.blogger.model.ChatItemModel;

@Database(entities = {ChatItemModel.class},version = 2)
public abstract class BloggerDatabase extends RoomDatabase {


    private static BloggerDatabase instance;

    public synchronized static BloggerDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context,BloggerDatabase.class,"blogger.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    public abstract BloggerDAO getDao();

}

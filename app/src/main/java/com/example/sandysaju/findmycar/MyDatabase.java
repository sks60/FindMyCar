package com.example.sandysaju.findmycar;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = Configuration.class, version = 1)
abstract class MyDatabase extends RoomDatabase {

    public abstract CarLocation_DAO carLocation_DAO();
    private static MyDatabase databaseInstance;

    static MyDatabase getDatabase(final Context context)
    {
        if (databaseInstance == null)
        {
            synchronized (MyDatabase.class)
            {
                if (databaseInstance == null)
                {
                    databaseInstance = Room.databaseBuilder(context.getApplicationContext() , MyDatabase.class , "MyDatabase").build();
                }
            }
        }
        return databaseInstance;
    }
}

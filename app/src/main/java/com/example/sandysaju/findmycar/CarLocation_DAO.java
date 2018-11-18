package com.example.sandysaju.findmycar;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface CarLocation_DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void set(Configuration value);

    @Query("Select * from CarLocation_Table where `key` = :key")
    Configuration get(String key);

}

package com.example.sandysaju.findmycar;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "CarLocation_Table")
public class Configuration {
    @NonNull
    @PrimaryKey
    private String key;

    public String getKey() {
        return key;
    }

    public Configuration(@NonNull String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;


}

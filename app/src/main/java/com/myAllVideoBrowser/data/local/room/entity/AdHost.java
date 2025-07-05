package com.myAllVideoBrowser.data.local.room.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "AdHost")
public class AdHost {

    @PrimaryKey
    @NonNull
    public String host;

    @Ignore
    public AdHost(@NonNull String host) {
        this.host = host;
    }

    // Default constructor required by Room
    public AdHost() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(@NonNull String host) {
        this.host = host;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        AdHost adHost = (AdHost) other;
        return host != null ? host.equals(adHost.host) : adHost.host == null;
    }

    @Override
    public int hashCode() {
        return host != null ? host.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AdHost{" +
                "host='" + host + '\'' +
                '}';
    }

}
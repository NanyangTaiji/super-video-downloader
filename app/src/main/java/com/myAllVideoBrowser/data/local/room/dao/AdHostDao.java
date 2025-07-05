package com.myAllVideoBrowser.data.local.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.myAllVideoBrowser.data.local.room.entity.AdHost;

import java.util.List;
import java.util.Set;

@Dao
public interface AdHostDao {

    @Query("SELECT COUNT(*) FROM AdHost")
    int getHostsCount();



    @Query("SELECT * FROM AdHost")
    List<AdHost> getAdHosts();

    @Query("SELECT EXISTS(SELECT * FROM AdHost WHERE host = :host)")
    boolean isAdHost(String host);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAdHost(AdHost adHost);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAdHosts(Set<AdHost> adHosts);

    @Delete
    void deleteAdHosts(Set<AdHost> adHosts);

    @Query("DELETE FROM AdHost")
    void deleteAllAdHosts();

    @Delete
    void deleteAdHost(AdHost host);
}

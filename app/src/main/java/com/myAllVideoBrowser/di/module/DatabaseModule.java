package com.myAllVideoBrowser.di.module;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.myAllVideoBrowser.DLApplication;
import com.myAllVideoBrowser.data.local.room.AppDatabase;
import com.myAllVideoBrowser.data.local.room.dao.AdHostDao;
import com.myAllVideoBrowser.data.local.room.dao.ConfigDao;
import com.myAllVideoBrowser.data.local.room.dao.HistoryDao;
import com.myAllVideoBrowser.data.local.room.dao.PageDao;
import com.myAllVideoBrowser.data.local.room.dao.ProgressDao;
import com.myAllVideoBrowser.data.local.room.dao.VideoDao;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

class UserSqlUtils {
    public String createTable = "CREATE TABLE IF NOT EXISTS AdHost (host TEXT NOT NULL, PRIMARY KEY(host))";
}

@Module
public class DatabaseModule {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL(new UserSqlUtils().createTable);
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE ProgressInfo ADD progressDownloaded INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE ProgressInfo ADD progressTotal INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE PageInfo ADD COLUMN `order` INTEGER NOT NULL DEFAULT 0");
        }
    };

    @Singleton
    @Provides
    public AppDatabase provideDatabase(DLApplication application) {
        return Room.databaseBuilder(application, AppDatabase.class, "dl.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build();
    }

    @Singleton
    @Provides
    public ConfigDao provideConfigDao(AppDatabase database) {
        return database.configDao();
    }

    @Singleton
    @Provides
    public VideoDao provideCommentDao(AppDatabase database) {
        return database.videoDao();
    }

    @Singleton
    @Provides
    public ProgressDao provideProgressDao(AppDatabase database) {
        return database.progressDao();
    }

    @Singleton
    @Provides
    public HistoryDao provideHistoryDao(AppDatabase database) {
        return database.historyDao();
    }

    @Singleton
    @Provides
    public PageDao providePageDao(AppDatabase database) {
        return database.pageDao();
    }

    @Singleton
    @Provides
    public AdHostDao provideAdHostDao(AppDatabase database) {
        return database.adHostDao();
    }
}

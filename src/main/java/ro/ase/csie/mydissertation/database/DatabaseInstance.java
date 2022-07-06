package ro.ase.csie.mydissertation.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;

import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteCard;
import ro.ase.csie.mydissertation.models.ByteFolder;

@Database(version = 1, entities = {ByteAccount.class, ByteCard.class, ByteFolder.class}, exportSchema = false)
public abstract class DatabaseInstance extends RoomDatabase {

    private static final String DB_NAME = "pwd_db.db";
    private static volatile DatabaseInstance instance;

    public abstract AccountDao getAccountDao();
    public abstract CardDao getCardDao();
    public abstract FolderDao getFolderDao();

    public static synchronized DatabaseInstance getInstance(final Context context) {
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext()
                            , DatabaseInstance.class
                            , DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build();
        }

        return instance;
    }

}

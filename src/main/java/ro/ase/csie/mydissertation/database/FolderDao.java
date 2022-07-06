package ro.ase.csie.mydissertation.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ro.ase.csie.mydissertation.models.ByteFolder;

@Dao
public interface FolderDao {

    @Query("Select * from ByteFolder")
    List<ByteFolder> getFolders();

    @Query("Select * from ByteFolder where folderID = :folderID")
    ByteFolder getFolder(int folderID);

    @Query("Select * from ByteFolder where folderName = :folderName")
    ByteFolder getFolderByName(String folderName);

    @Query("Select folderName from ByteFolder")
    List<String> getFoldersNames();

    @Insert
    void insertFolder(ByteFolder folder);

    @Update
    void updateFolder(ByteFolder folder);

    @Delete
    void deleteFolder(ByteFolder folder);

}

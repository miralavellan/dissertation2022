package ro.ase.csie.mydissertation.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ro.ase.csie.mydissertation.models.ByteAccount;

@Dao
public interface AccountDao {

    @Query("Select * from ByteAccount")
    List<ByteAccount> getAccounts();

    @Query("Select * from ByteAccount where folderID = :folderID")
    List<ByteAccount> getAccountsFromFolder(int folderID);

    @Query("Select * from ByteAccount where accID = :accID")
    ByteAccount getAccount(int accID);

    @Query("Select * from ByteAccount where accHost like '%' || :text || '%' or accUser like '%' || :text || '%'")
    List<ByteAccount> filterByHostOrUser(String text);

    @Insert
    void insertAccount(ByteAccount account);

    @Update
    void updateAccount(ByteAccount account);

    @Delete
    void deleteAccount(ByteAccount account);

}

package ro.ase.csie.mydissertation.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteCard;

@Dao
public interface CardDao {

    @Query("Select * from ByteCard")
    List<ByteCard> getCards();

    @Query("Select * from ByteCard where folderID = :folderID")
    List<ByteCard> getCardsFromFolder(int folderID);

    @Query("Select * from ByteCard where cardID = :cardID")
    ByteCard getCard(int cardID);

    @Query("Select * from ByteCard where cardHost like '%' || :text || '%' or cardName like '%' || :text || '%'")
    List<ByteCard> filterByHostOrName(String text);

    @Insert
    void insertCard(ByteCard card);

    @Update
    void updateCard(ByteCard card);

    @Delete
    void deleteCard(ByteCard card);

}

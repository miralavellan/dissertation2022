package ro.ase.csie.mydissertation.models;

import static ro.ase.csie.mydissertation.Constants.COUNT_ACCOUNTS;
import static ro.ase.csie.mydissertation.Constants.COUNT_ALL;
import static ro.ase.csie.mydissertation.Constants.COUNT_CARDS;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.CardDao;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.database.FolderDao;
import ro.ase.csie.mydissertation.database.FolderService;

@Entity(tableName = "ByteFolder")
public class ByteFolder implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int folderID;
    private String folderName;

    @Ignore
    public ByteFolder() {
        this.folderName = "Default";
    }

    public ByteFolder(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderID() {
        return folderID;
    }

    public void setFolderID(int folderID) {
        this.folderID = folderID;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderEntriesCount(Context context, int which){

        DatabaseInstance db = DatabaseInstance.getInstance(context);

        AccountDao accountDao = db.getAccountDao();
        AccountService accountService = new AccountService(accountDao);
        CardDao cardDao = db.getCardDao();
        CardService cardService = new CardService(cardDao);

        switch (which) {
            case COUNT_ALL:
                return accountService.getAccountsFromFolder(folderID).size() + cardService.getCardsFromFolder(folderID).size();
            case COUNT_ACCOUNTS:
                return accountService.getAccountsFromFolder(folderID).size();
            case COUNT_CARDS:
                return cardService.getCardsFromFolder(folderID).size();
            default:
                return 0;

        }
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(folderID);
        stringBuilder.append("%#%#%");
        stringBuilder.append(folderName);

        return stringBuilder.toString();
    }
}

package ro.ase.csie.mydissertation.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "ByteCard", foreignKeys = {@ForeignKey(entity = ByteFolder.class, parentColumns = "folderID", childColumns = "folderID", onDelete = CASCADE)})
public class ByteCard implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int cardID;
    private String cardHost;
    private String cardName;
    private String cardAESPIN;

    @ColumnInfo(index = true)
    private int folderID;

    private long cardUnixTime;

    private String cardNumber;
    private String cardValidThru;
    private String cardCVV;

    @Ignore
    public ByteCard() {
        this.cardHost = "default";
        this.cardName = "default";
        this.cardAESPIN = "default";
        this.folderID = 1;
        this.cardUnixTime = System.currentTimeMillis()/1000;
        this.cardNumber = "default";
        this.cardValidThru = "default";
        this.cardCVV = "default";
    }

    public ByteCard(String cardHost, String cardName, String cardAESPIN, int folderID) {
        this.cardHost = cardHost;
        this.cardName = cardName;
        this.cardAESPIN = cardAESPIN;
        this.folderID = folderID;
        this.cardUnixTime = System.currentTimeMillis()/1000;
        this.cardNumber = "default";
        this.cardValidThru = "default";
        this.cardCVV = "default";
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public String getCardHost() {
        return cardHost;
    }

    public void setCardHost(String cardHost) {
        this.cardHost = cardHost;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardAESPIN() {
        return cardAESPIN;
    }

    public void setCardAESPIN(String cardAESPIN) {
        this.cardAESPIN = cardAESPIN;
    }

    public int getFolderID() {
        return folderID;
    }

    public void setFolderID(int folderID) {
        this.folderID = folderID;
    }

    public long getCardUnixTime() {
        return cardUnixTime;
    }

    public void setCardUnixTime(long cardUnixTime) {
        this.cardUnixTime = cardUnixTime;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardValidThru() {
        return cardValidThru;
    }

    public void setCardValidThru(String cardValidThru) {
        this.cardValidThru = cardValidThru;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cardID);
        stringBuilder.append("%#%#%");
        stringBuilder.append(cardHost);
        stringBuilder.append("%#%#%");
        stringBuilder.append(cardName);
        stringBuilder.append("%#%#%");
        stringBuilder.append(cardAESPIN);
        stringBuilder.append("%#%#%");
        stringBuilder.append(cardNumber);
        stringBuilder.append("%#%#%");
        stringBuilder.append(cardValidThru);
        stringBuilder.append("%#%#%");
        stringBuilder.append(cardCVV);
        stringBuilder.append("%#%#%");
        stringBuilder.append(cardUnixTime);
        stringBuilder.append("%#%#%");
        stringBuilder.append(folderID);

        return stringBuilder.toString();
    }
}

package ro.ase.csie.mydissertation.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

import java.io.Serializable;

@Entity(tableName = "ByteAccount", foreignKeys = {@ForeignKey(entity = ByteFolder.class, parentColumns = "folderID", childColumns = "folderID", onDelete = CASCADE)})
public class ByteAccount implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int accID;
    private String accHost;
    private String accUser;
    private String accAESPass;

    @ColumnInfo(index = true)
    private int folderID;

    private long accUnixTime;

    @Ignore
    public ByteAccount() {
        this.accHost = "default";
        this.accUser = "default";
        this.accAESPass = "default";
        this.folderID = 1;
        this.accUnixTime = System.currentTimeMillis()/1000;
    }

    public ByteAccount(String accHost, String accUser, String accAESPass, int folderID) {
        this.accHost = accHost;
        this.accUser = accUser;
        this.accAESPass = accAESPass;
        this.folderID = folderID;
        this.accUnixTime = System.currentTimeMillis()/1000;
    }

    public int getAccID() {
        return accID;
    }

    public void setAccID(int accID) {
        this.accID = accID;
    }

    public String getAccHost() {
        return accHost;
    }

    public void setAccHost(String accHost) {
        this.accHost = accHost;
    }

    public String getAccUser() {
        return accUser;
    }

    public void setAccUser(String accUser) {
        this.accUser = accUser;
    }

    public String getAccAESPass() {
        return accAESPass;
    }

    public void setAccAESPass(String accAESPass) {
        this.accAESPass = accAESPass;
    }

    public int getFolderID() {
        return folderID;
    }

    public void setFolderID(int folderID) {
        this.folderID = folderID;
    }

    public long getAccUnixTime() {
        return accUnixTime;
    }

    public void setAccUnixTime(long accUnixTime) {
        this.accUnixTime = accUnixTime;
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(accID);
        stringBuilder.append("%#%#%");
        stringBuilder.append(accHost);
        stringBuilder.append("%#%#%");
        stringBuilder.append(accUser);
        stringBuilder.append("%#%#%");
        stringBuilder.append(accAESPass);
        stringBuilder.append("%#%#%");
        stringBuilder.append(accUnixTime);
        stringBuilder.append("%#%#%");
        stringBuilder.append(folderID);

        return stringBuilder.toString();
    }
}

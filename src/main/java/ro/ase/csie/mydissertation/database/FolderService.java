package ro.ase.csie.mydissertation.database;

import java.util.List;

import ro.ase.csie.mydissertation.models.ByteFolder;

public class FolderService implements FolderDao{

    private FolderDao tableInstance;

    public FolderService(FolderDao tableInstance) {
        this.tableInstance = tableInstance;
    }

    @Override
    public List<ByteFolder> getFolders() {
        return tableInstance.getFolders();
    }

    @Override
    public ByteFolder getFolder(int folderID) {
        return tableInstance.getFolder(folderID);
    }

    @Override
    public ByteFolder getFolderByName(String folderName) {
        return tableInstance.getFolderByName(folderName);
    }

    @Override
    public List<String> getFoldersNames() {
        return tableInstance.getFoldersNames();
    }

    @Override
    public void insertFolder(ByteFolder folder) {
        tableInstance.insertFolder(folder);
    }

    @Override
    public void updateFolder(ByteFolder folder) {
        tableInstance.updateFolder(folder);
    }

    @Override
    public void deleteFolder(ByteFolder folder) {
        tableInstance.deleteFolder(folder);
    }
}

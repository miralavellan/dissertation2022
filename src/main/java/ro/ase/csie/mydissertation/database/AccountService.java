package ro.ase.csie.mydissertation.database;

import java.util.List;

import ro.ase.csie.mydissertation.models.ByteAccount;

public class AccountService implements AccountDao{

    private AccountDao tableInstance;

    public AccountService(AccountDao tableInstance) {
        this.tableInstance = tableInstance;
    }

    @Override
    public List<ByteAccount> getAccounts() {
        return tableInstance.getAccounts();
    }

    @Override
    public List<ByteAccount> getAccountsFromFolder(int folderID) {
        return tableInstance.getAccountsFromFolder(folderID);
    }

    @Override
    public ByteAccount getAccount(int accID) {
        return tableInstance.getAccount(accID);
    }

    @Override
    public List<ByteAccount> filterByHostOrUser(String text) {
        return tableInstance.filterByHostOrUser(text);
    }

    @Override
    public void insertAccount(ByteAccount account) {
        tableInstance.insertAccount(account);
    }

    @Override
    public void updateAccount(ByteAccount account) {
        tableInstance.updateAccount(account);
    }

    @Override
    public void deleteAccount(ByteAccount account) {
        tableInstance.deleteAccount(account);
    }
}

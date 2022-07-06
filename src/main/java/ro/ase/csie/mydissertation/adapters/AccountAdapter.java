package ro.ase.csie.mydissertation.adapters;

import static ro.ase.csie.mydissertation.Constants.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

import ro.ase.csie.mydissertation.R;
import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.utilities.Utilities;

public class AccountAdapter extends BaseAdapter {

    private Context context;
    private List<ByteAccount> byteAccounts;

    private DatabaseInstance db;
    private AccountDao accountDao;
    private AccountService accountService;

    private int sorting = SORTING_DEFAULT;
    private final int layout;

    public AccountAdapter(Context context, List<ByteAccount> byteAccounts, int layout) {
        this.context = context;
        this.byteAccounts = byteAccounts;
        this.layout = layout;
    }

    public void updateList(){
        db = DatabaseInstance.getInstance(context.getApplicationContext());
        accountDao = db.getAccountDao();
        accountService = new AccountService(accountDao);

        byteAccounts.clear();
        byteAccounts.addAll(accountService.getAccounts());
        this.notifyDataSetChanged();
    }

    public void updateList(List<ByteAccount> list){
        byteAccounts.clear();
        byteAccounts.addAll(list);
        this.notifyDataSetChanged();
    }

    public void updateListFolder(int folderID){
        db = DatabaseInstance.getInstance(context.getApplicationContext());
        accountDao = db.getAccountDao();
        accountService = new AccountService(accountDao);

        byteAccounts.clear();
        byteAccounts.addAll(accountService.getAccountsFromFolder(folderID));
        this.notifyDataSetChanged();
    }

    public void sortListDefault(){
        Utilities.sortAccountsDefault(byteAccounts);
        notifyDataSetChanged();
        sorting = SORTING_DEFAULT;
    }

    public void sortListAlphabetically(){
        Utilities.sortAccountsAlphabeticallyByHost(byteAccounts);
        notifyDataSetChanged();
        sorting = SORTING_ALPHA;
    }

    public void sortListByDate(){
        Utilities.sortAccountsByDate(byteAccounts);
        notifyDataSetChanged();
        sorting = SORTING_DATE;
    }

    // todo abandoned

    @Override
    public int getItemViewType(int position) {

        if(byteAccounts.get(position) != null){
            return SORT_ITEM;
        } else {
            switch (sorting){
                case SORTING_DEFAULT:
                    return SORT_DEFAULT_HEADER;
                case SORTING_ALPHA:
                    return SORT_ALPHA_HEADER;
                case SORTING_DATE:
                    return SORT_DATE_HEADER;
            }
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return byteAccounts.size();
    }

    @Override
    public Object getItem(int i) {
        return byteAccounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return byteAccounts.get(i).getAccID();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(layout == R.layout.list_view_adapter_account){
            ListAccountsHolder listAccountsHolder = null;

            if (view == null){
                view = LayoutInflater
                        .from(context)
                        .inflate(layout, viewGroup, false);
                listAccountsHolder = new ListAccountsHolder(view);
                view.setTag(listAccountsHolder);
            } else{
                listAccountsHolder = (ListAccountsHolder) view.getTag();
            }

            ByteAccount byteAccount = (ByteAccount) getItem(i);

            listAccountsHolder.tvAccImage.setText(byteAccount.getAccHost().substring(0, 1).toUpperCase(Locale.ROOT));
            listAccountsHolder.tvAccHost.setText(byteAccount.getAccHost());
            listAccountsHolder.tvAccUser.setText(byteAccount.getAccUser());

        } else {
            ListAccountsHolderFolder listAccountsHolder = null;

            if (view == null){
                view = LayoutInflater
                        .from(context)
                        .inflate(layout, viewGroup, false);
                listAccountsHolder = new ListAccountsHolderFolder(view);
                view.setTag(listAccountsHolder);
            } else{
                listAccountsHolder = (ListAccountsHolderFolder) view.getTag();
            }

            ByteAccount byteAccount = (ByteAccount) getItem(i);

            listAccountsHolder.tvAccImage.setText(byteAccount.getAccHost().substring(0, 1).toUpperCase(Locale.ROOT));
            listAccountsHolder.tvAccHost.setText(byteAccount.getAccHost());
            listAccountsHolder.tvAccUser.setText(byteAccount.getAccUser());
        }

        return view;
    }

    private static class ListAccountsHolder {
        public TextView tvAccImage;
        public TextView tvAccHost;
        public TextView tvAccUser;

        ListAccountsHolder(View view){
            tvAccImage = view.findViewById(R.id.tvAccImage);
            tvAccHost = view.findViewById(R.id.tvAccHost);
            tvAccUser = view.findViewById(R.id.tvAccUser);
        }
    }

    private static class ListAccountsHolderFolder {
        public TextView tvAccImage;
        public TextView tvAccHost;
        public TextView tvAccUser;

        ListAccountsHolderFolder(View view){
            tvAccImage = view.findViewById(R.id.tvAccImageFolder);
            tvAccHost = view.findViewById(R.id.tvAccHostFolder);
            tvAccUser = view.findViewById(R.id.tvAccUserFolder);
        }
    }
}

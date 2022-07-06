package ro.ase.csie.mydissertation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.ase.csie.mydissertation.R;
import ro.ase.csie.mydissertation.database.AccountDao;
import ro.ase.csie.mydissertation.database.AccountService;
import ro.ase.csie.mydissertation.database.CardDao;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteFolder;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private List<String> categories;

    private DatabaseInstance db;
    private AccountDao accountDao;
    private AccountService accountService;
    private CardDao cardDao;
    private CardService cardService;

    public CategoryAdapter(Context context) {
        this.context = context;
        this.categories = new ArrayList<String>();
        categories.add("Accounts");
        categories.add("Cards");
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListCategoriesHolder listCategoriesHolder = null;

        if(convertView == null){
            convertView = LayoutInflater
                    .from(context)
                    .inflate(R.layout.list_view_adapter_folder, parent, false);
            listCategoriesHolder = new ListCategoriesHolder(convertView);
            convertView.setTag(listCategoriesHolder);
        } else{
            listCategoriesHolder = (ListCategoriesHolder) convertView.getTag();
        }

        db = DatabaseInstance.getInstance(context.getApplicationContext());
        accountDao = db.getAccountDao();
        accountService = new AccountService(accountDao);
        cardDao = db.getCardDao();
        cardService = new CardService(cardDao);

        String string = (String) getItem(position);

        listCategoriesHolder.tvFolderName.setText(string);

        if(position == 0){
            listCategoriesHolder.imgFolderIcon.setImageResource(R.drawable.ic_email_24);
            listCategoriesHolder.tvFolderCount.setText(String.valueOf(accountService.getAccounts().size()));
        } else{
            if(position == 1){
                listCategoriesHolder.imgFolderIcon.setImageResource(R.drawable.ic_credit_card_24);
                listCategoriesHolder.tvFolderCount.setText(String.valueOf(cardService.getCards().size()));
            }
        }

        return convertView;
    }

    private static class ListCategoriesHolder {
        public ImageView imgFolderIcon;
        public TextView tvFolderName;
        public TextView tvFolderCount;

        ListCategoriesHolder(View view){
            imgFolderIcon = view.findViewById(R.id.imgFolderIcon);
            tvFolderName = view.findViewById(R.id.tvFolderName);
            tvFolderCount = view.findViewById(R.id.tvFolderCount);
        }
    }
}

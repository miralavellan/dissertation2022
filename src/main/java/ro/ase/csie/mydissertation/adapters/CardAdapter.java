package ro.ase.csie.mydissertation.adapters;

import static ro.ase.csie.mydissertation.Constants.SORTING_ALPHA;
import static ro.ase.csie.mydissertation.Constants.SORTING_DATE;
import static ro.ase.csie.mydissertation.Constants.SORTING_DEFAULT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ro.ase.csie.mydissertation.R;
import ro.ase.csie.mydissertation.database.CardDao;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteCard;
import ro.ase.csie.mydissertation.utilities.Utilities;

public class CardAdapter extends BaseAdapter {

    private Context context;
    private List<ByteCard> byteCards;

    private DatabaseInstance db;
    private CardDao cardDao;
    private CardService cardService;

    private int layout;

    public CardAdapter(Context context, List<ByteCard> byteCards, int layout) {
        this.context = context;
        this.byteCards = byteCards;
        this.layout = layout;
    }

    public void updateList(){
        db = DatabaseInstance.getInstance(context.getApplicationContext());
        cardDao = db.getCardDao();
        cardService = new CardService(cardDao);

        byteCards.clear();
        byteCards.addAll(cardService.getCards());
        this.notifyDataSetChanged();
    }

    public void updateList(List<ByteCard> list){
        byteCards.clear();
        byteCards.addAll(list);
        this.notifyDataSetChanged();
    }

    public void updateListFolder(int folderID){
        db = DatabaseInstance.getInstance(context.getApplicationContext());
        cardDao = db.getCardDao();
        cardService = new CardService(cardDao);

        byteCards.clear();
        byteCards.addAll(cardService.getCardsFromFolder(folderID));
        this.notifyDataSetChanged();
    }

    public void sortListDefault(){
        Utilities.sortCardsDefault(byteCards);
        notifyDataSetChanged();
    }

    public void sortListAlphabetically(){
        Utilities.sortCardsAlphabeticallyByHost(byteCards);
        notifyDataSetChanged();
    }

    public void sortListByDate(){
        Utilities.sortCardsByDate(byteCards);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return byteCards.size();
    }

    @Override
    public Object getItem(int position) {
        return byteCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return byteCards.get(position).getCardID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(layout == R.layout.list_view_adapter_account){
            ListCardsHolder listCardsHolder = null;

            if(convertView == null){
                convertView = LayoutInflater
                        .from(context)
                        .inflate(layout, parent, false);
                listCardsHolder = new ListCardsHolder(convertView);
                convertView.setTag(listCardsHolder);
            } else{
                listCardsHolder = (ListCardsHolder) convertView.getTag();
            }

            ByteCard byteCard = (ByteCard) getItem(position);

            listCardsHolder.tvAccImage.setText(byteCard.getCardHost().substring(0, 1).toUpperCase(Locale.ROOT));
            listCardsHolder.tvAccHost.setText(byteCard.getCardHost());
            listCardsHolder.tvAccUser.setText(byteCard.getCardName());
        } else {
            ListCardsHolderFolder listCardsHolder = null;

            if(convertView == null){
                convertView = LayoutInflater
                        .from(context)
                        .inflate(layout, parent, false);
                listCardsHolder = new ListCardsHolderFolder(convertView);
                convertView.setTag(listCardsHolder);
            } else{
                listCardsHolder = (ListCardsHolderFolder) convertView.getTag();
            }

            ByteCard byteCard = (ByteCard) getItem(position);

            listCardsHolder.tvAccImage.setText(byteCard.getCardHost().substring(0, 1).toUpperCase(Locale.ROOT));
            listCardsHolder.tvAccHost.setText(byteCard.getCardHost());
            listCardsHolder.tvAccUser.setText(byteCard.getCardName());
        }

        return convertView;
    }

    private static class ListCardsHolder {
        public TextView tvAccImage;
        public TextView tvAccHost;
        public TextView tvAccUser;

        ListCardsHolder(View view){
            tvAccImage = view.findViewById(R.id.tvAccImage);
            tvAccHost = view.findViewById(R.id.tvAccHost);
            tvAccUser = view.findViewById(R.id.tvAccUser);
        }
    }

    private static class ListCardsHolderFolder {
        public TextView tvAccImage;
        public TextView tvAccHost;
        public TextView tvAccUser;

        ListCardsHolderFolder(View view){
            tvAccImage = view.findViewById(R.id.tvAccImageFolder);
            tvAccHost = view.findViewById(R.id.tvAccHostFolder);
            tvAccUser = view.findViewById(R.id.tvAccUserFolder);
        }
    }
}

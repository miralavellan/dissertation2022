package ro.ase.csie.mydissertation.utilities;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteCard;

public abstract class Utilities {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void sortAccountsAlphabeticallyByHost(List<ByteAccount> list){
        Collections.sort(list, new Comparator<ByteAccount>() {
            @Override
            public int compare(ByteAccount o1, ByteAccount o2) {
                return o1.getAccHost().compareToIgnoreCase(o2.getAccHost());
            }
        });
    }
    public static void sortAccountsByDate(List<ByteAccount> list){
        Collections.sort(list, new Comparator<ByteAccount>() {
            @Override
            public int compare(ByteAccount o1, ByteAccount o2) {
                return (int) (o1.getAccUnixTime() - o2.getAccUnixTime());
            }
        });
    }
    public static void sortAccountsDefault(List<ByteAccount> list){
        Collections.sort(list, new Comparator<ByteAccount>() {
            @Override
            public int compare(ByteAccount o1, ByteAccount o2) {
                return o1.getAccID() - o2.getAccID();
            }
        });
    }

    public static void sortCardsAlphabeticallyByHost(List<ByteCard> list){
        Collections.sort(list, new Comparator<ByteCard>() {
            @Override
            public int compare(ByteCard o1, ByteCard o2) {
                return o1.getCardHost().compareTo(o2.getCardHost());
            }
        });
    }
    public static void sortCardsByDate(List<ByteCard> list){
        Collections.sort(list, new Comparator<ByteCard>() {
            @Override
            public int compare(ByteCard o1, ByteCard o2) {
                return (int)(o1.getCardUnixTime() - o2.getCardUnixTime());
            }
        });
    }
    public static void sortCardsDefault(List<ByteCard> list){
        Collections.sort(list, new Comparator<ByteCard>() {
            @Override
            public int compare(ByteCard o1, ByteCard o2) {
                return o1.getCardID() - o2.getCardID();
            }
        });
    }
}
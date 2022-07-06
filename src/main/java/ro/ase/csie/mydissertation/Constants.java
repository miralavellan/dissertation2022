package ro.ase.csie.mydissertation;

public abstract class Constants {

    //INTS
    public static final int ADD_ACC = 1;
    public static final int ADD_CARD = 2;

    public static final int VIEW_ACC = 3;
    public static final int VIEW_CARD = 4;

    public static final int VIEW_ACCS = 5;
    public static final int VIEW_CARDS = 6;

    public static final int CHOOSE_DATABASE = 7;

    public static final int EDIT_ACC = 5;
    public static final int EDIT_CARD = 6;

    public static final int RESULT_ADDED = 2;
    public static final int RESULT_EDITED = 3;
    public static final int RESULT_DELETED = 4;

    public static final int COPY_NAME = 1;
    public static final int COPY_PIN = 2;
    public static final int COPY_NUMBER = 3;
    public static final int COPY_VALID_THRU = 4;
    public static final int COPY_CVV = 5;

    public static final int SEE_PIN = 1;
    public static final int HIDE_PIN = 2;
    public static final int SEE_NUMBER = 3;
    public static final int HIDE_NUMBER = 4;
    public static final int SEE_VALID_THRU = 5;
    public static final int HIDE_VALID_THRU = 6;
    public static final int SEE_CVV = 7;
    public static final int HIDE_CVV = 8;

    public static final int SEE_PASSWORD = 1;
    public static final int HIDE_PASSWORD = 2;

    public static final int VIEW_FOLDER = 1;

    public static final int SORTING_DEFAULT = 0;
    public static final int SORTING_ALPHA = 1;
    public static final int SORTING_DATE = 2;

    public static final int SORT_ITEM = 0;
    public static final int SORT_DEFAULT_HEADER = 0;
    public static final int SORT_ALPHA_HEADER = 1;
    public static final int SORT_DATE_HEADER = 2;

    public static final int COUNT_ALL = 0;
    public static final int COUNT_ACCOUNTS = 1;
    public static final int COUNT_CARDS = 2;

    //STRINGS

    public static final String FIRST_RUN = "firstrun";

    public static final String ACCOUNT_ID = "account id";
    public static final String CARD_ID = "card id";

    public static final String SHARED_PREFS_1 = "prefmaster";
    public static final String SHARED_PREFS_1_USER = "masteruser";
    public static final String SHARED_PREFS_1_PASSWORD = "masterpassword";
    public static final String SHARED_PREFS_1_SALT = "mastersalt";
    public static final String SHARED_PREFS_1_HINT = "masterhint";
    public static final String SHARED_PREFS_1_BIOMETRICS = "biometrics";

    public static final String KEY_STORE = "AndroidKeyStore";
    public static final String AES_MODE = "AES/GCM/NoPadding";

    public static final String MASTER_KEY = "MasterKey";
    public static final String TEST_IV = "TestIV";
}

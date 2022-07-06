package ro.ase.csie.mydissertation.utilities;

import static ro.ase.csie.mydissertation.Constants.FIRST_RUN;
import static ro.ase.csie.mydissertation.Constants.SHARED_PREFS_1;
import static ro.ase.csie.mydissertation.Constants.SHARED_PREFS_1_BIOMETRICS;

import android.content.Context;
import android.content.SharedPreferences;

import ro.ase.csie.mydissertation.R;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.models.ByteFolder;

public abstract class ByteDatabase {
    public static void prepopulateDatabase(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
        if(sp.getBoolean(FIRST_RUN, true)){
            DatabaseInstance instance = DatabaseInstance.getInstance(context);

            instance.getFolderDao().insertFolder(new ByteFolder("default"));

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(FIRST_RUN, false);
            editor.apply();
        }
    }

    public static void setBiometrics(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_1, Context.MODE_PRIVATE);
        if(sp.getBoolean(FIRST_RUN, true)){
            SharedPreferences.Editor editor = sp.edit();

            editor.putBoolean(SHARED_PREFS_1_BIOMETRICS, false);
            editor.putBoolean(FIRST_RUN, false);
            editor.apply();
        }
    }
}

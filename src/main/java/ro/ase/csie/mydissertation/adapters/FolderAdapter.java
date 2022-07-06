package ro.ase.csie.mydissertation.adapters;

import static ro.ase.csie.mydissertation.Constants.COUNT_ALL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ro.ase.csie.mydissertation.R;
import ro.ase.csie.mydissertation.database.CardService;
import ro.ase.csie.mydissertation.database.DatabaseInstance;
import ro.ase.csie.mydissertation.database.FolderDao;
import ro.ase.csie.mydissertation.database.FolderService;
import ro.ase.csie.mydissertation.models.ByteCard;
import ro.ase.csie.mydissertation.models.ByteFolder;

public class FolderAdapter extends BaseAdapter {

    private Context context;
    private List<ByteFolder> byteFolders;

    private DatabaseInstance db;
    private FolderDao folderDao;
    private FolderService folderService;

    public FolderAdapter(Context context, List<ByteFolder> byteFolders) {
        this.context = context;
        this.byteFolders = byteFolders;

        db = DatabaseInstance.getInstance(context.getApplicationContext());
        folderDao = db.getFolderDao();
        folderService = new FolderService(folderDao);
    }

    public void updateList(){
        byteFolders.clear();
        byteFolders.addAll(folderService.getFolders());
        this.notifyDataSetChanged();
    }

    public void updateList(List<ByteFolder> list){
        byteFolders.clear();
        byteFolders.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return byteFolders.size();
    }

    @Override
    public Object getItem(int position) {
        return byteFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return byteFolders.get(position).getFolderID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListFoldersHolder listFoldersHolder = null;

        if(convertView == null){
            convertView = LayoutInflater
                    .from(context)
                    .inflate(R.layout.list_view_adapter_folder, parent, false);
            listFoldersHolder = new ListFoldersHolder(convertView);
            convertView.setTag(listFoldersHolder);
        } else{
            listFoldersHolder = (ListFoldersHolder) convertView.getTag();
        }

        ByteFolder byteFolder = (ByteFolder) getItem(position);

        listFoldersHolder.imgFolderIcon.setImageResource(R.drawable.ic_folder_24);
        listFoldersHolder.tvFolderName.setText(byteFolder.getFolderName());
        listFoldersHolder.tvFolderCount.setText(String.valueOf(byteFolder.getFolderEntriesCount(context, COUNT_ALL)));

        return convertView;
    }

    private static class ListFoldersHolder {
        public ImageView imgFolderIcon;
        public TextView tvFolderName;
        public TextView tvFolderCount;

        ListFoldersHolder(View view){
            imgFolderIcon = view.findViewById(R.id.imgFolderIcon);
            tvFolderName = view.findViewById(R.id.tvFolderName);
            tvFolderCount = view.findViewById(R.id.tvFolderCount);
        }
    }
}

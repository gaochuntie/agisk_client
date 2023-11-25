package atms.app.agiskclient.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import java.io.File;
import java.util.List;

import atms.app.agiskclient.R;

public class xmlListAdapter extends RecyclerView.Adapter{
    List<xmlItem> xmlItemList_m=null;
    private String xmlListAdapter_TAG="XML_ADAPTER";

    /////////////////////

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.xml_list_item, parent, false);
        return new xmlItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        xmlItemViewHolder xmlholder=(xmlItemViewHolder) holder;
        xmlholder.xml_index.setText(String.valueOf((xmlItemList_m.get(position)).getNum()));
        xmlholder.xml_name.setText(xmlItemList_m.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return xmlItemList_m.size();
    }

    /**
     * Constructor
     */

    public xmlListAdapter(List<xmlItem> xmlItemList){
        this.xmlItemList_m=xmlItemList;
    }

    /**
     * My holder
     */
    class xmlItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView xml_index;
        private final TextView xml_name;
        private final Button xml_delete;

        public xmlItemViewHolder(@NonNull View itemView) {
            super(itemView);
            xml_index = itemView.findViewById(R.id.xml_item_index);
            xml_name = itemView.findViewById(R.id.xml_item_name);
            xml_delete = itemView.findViewById(R.id.xml_item_delete);

            xml_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position =getLayoutPosition();
                    MessageDialog.show("Deletion", "Delete xml : "+
                            (xmlItemList_m.get(position)).name
                            + " ?"
                            , "Delete").setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog baseDialog, View v) {
                            if (!deleteXml(position)){
                                //Delete failed
                                MessageDialog.show("Failed", "Delete failed.", "Cancel");
                                return false;
                            }
                            //delete success
                            return false;
                        }
                    });



                }
            });
        }
    }

    private boolean deleteXml(int index) {

        try {
            File xmlfile = new File(xmlItemList_m.get(index).getPath());
            Log.d(xmlListAdapter_TAG,"Try delete "+ xmlItemList_m.get(index).getPath());
            xmlfile.delete();
            xmlItemList_m.remove(index);
            this.notifyItemRemoved(index);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //
    public static class xmlItem{
        int num=0;
        String name = "unknown";
        @Nullable
        String path=null;

        public xmlItem(int num, String name) {
            this.num=num;
            this.name=name;
        }
        @Nullable
        public void setPath(String path) {
            this.path = path;
        }

        public int getNum() {
            return num;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public String getPath() {
            return path;
        }
    }
}

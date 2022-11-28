package atms.app.agiskclient.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import atms.app.agiskclient.Data.romListData;
import atms.app.agiskclient.R;

public class romListAdapter extends RecyclerView.Adapter<romListAdapter.ViewHolder> {

    private final String[] localDataSet;
    romItemOnClickListener onClickListener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView romName;
        private final ImageView romPic;
        private final ImageView xmllock;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            romName = (TextView) view.findViewById(R.id.romName);
            romPic = (ImageView) view.findViewById(R.id.romPicture);
            xmllock = view.findViewById(R.id.xmlLockPt);
        }

        public TextView getTextView() {
            return romName;
        }

        public ImageView getRomPic() {
            return romPic;
        }

        public ImageView getXmllock() {
            return xmllock;
        }

        public void lockxml() {
            xmllock.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public romListAdapter(String[] dataSet, romItemOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        localDataSet = dataSet;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rom_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onItemClick(position);
            }
        });
        if (position == getItemCount() - 1) {
            viewHolder.getTextView().setText("");
            viewHolder.getRomPic().setImageResource(R.drawable.ic_install_button_foreground);
            return;
        } else {
            /**
             * set rom picture/background picture
             */
            viewHolder.getRomPic().setImageBitmap(null);
            if (romListData.getRom(position).getRompicture() != null) {
                viewHolder.getRomPic().setImageBitmap(romListData.getRom(position).getRompicture().srcbitmap);
            }
            /**
             * mark encrypted xml
             */
            if (romListData.getRom(position).getOrigConfig().isEncrypted()) {
                viewHolder.lockxml();
            }
            viewHolder.getTextView().setText(localDataSet[position]);
        }
        setScaleAnimation(viewHolder.itemView);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(300);
        view.startAnimation(anim);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}

package atms.app.agiskclient.UIcomponents;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import atms.app.agiskclient.R;

public class CopiableTextView extends androidx.appcompat.widget.AppCompatTextView {

    public CopiableTextView(Context context) {
        super(context);
        initialize();
    }

    public CopiableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CopiableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        // Enable text selection
        setTextIsSelectable(true);

        // Set the long click listener to show the context menu
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu resource for the context menu
                mode.getMenuInflater().inflate(R.menu.copy_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Handle the click on the "Copy" menu item
                if (item.getItemId() == R.id.action_copy) {
                    copyToClipboard(getContext(), getText().toString());
                    Toast.makeText(getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                    mode.finish(); // Close the context menu
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // No action needed
            }
        });
    }

    private void copyToClipboard(Context context, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }
}

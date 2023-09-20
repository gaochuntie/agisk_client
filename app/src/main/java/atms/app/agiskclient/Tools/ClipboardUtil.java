package atms.app.agiskclient.Tools;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {

    // Method to copy text to the clipboard
    public static void copyToClipboard(Context context, String textToCopy) {
        // Get the clipboard manager
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // Create a new ClipData object with the text to copy
        ClipData clipData = ClipData.newPlainText("label", textToCopy);

        // Set the ClipData object to the clipboard
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }
}

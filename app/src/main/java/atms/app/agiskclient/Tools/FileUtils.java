package atms.app.agiskclient.Tools;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    /**
     *
     * @param context
     * @param file_path
     * @return
     */
    public static InputStream getAssetsInputStream(Context context, String file_path) {
        InputStream inputStream = null;

        try {
            // Get a reference to the AssetManager
            AssetManager assetManager = context.getAssets();

            // Open an InputStream to the asset
            inputStream = assetManager.open(file_path);
        } catch (IOException e) {
            // Handle the exception if the file is not found or other errors occur
            e.printStackTrace();
        }

        return inputStream;
    }


    /**
     * example
     * (context,"dir/file","dir/file")
     * @param context
     * @param assetFilePath
     * @param destFilePath
     * @return
     */
    public static boolean copyAssetFileToStorage(Context context, String assetFilePath, String destFilePath) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetManager.open(assetFilePath);

            // Create the destination file and directories if they don't exist
            File destFile = new File(destFilePath);
            File destDir = destFile.getParentFile();
            if (destDir != null && !destDir.exists()) {
                if (!destDir.mkdirs()) {
                    // Failed to create the directories
                    return false;
                }
            }

            outputStream = new FileOutputStream(destFile);

            // Copy the file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Flush the output stream and close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the IOException if needed
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the IOException if needed
            }
        }

        return false;
    }
}

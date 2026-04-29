package com.apexiz.weldviewer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import androidx.core.view.WindowCompat;
import androidx.core.content.FileProvider;
import com.getcapacitor.BridgeActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getBridge().getWebView().addJavascriptInterface(new ApexizAndroidBridge(), "ApexizAndroid");
    }

    private class ApexizAndroidBridge {
        @JavascriptInterface
        public void savePptToDownloads(String base64Data, String fileName) {
            runOnUiThread(() -> {
                try {
                    String safeName = sanitizeFileName(fileName);
                    byte[] bytes = Base64.decode(base64Data.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                    saveBytesToDownloads(bytes, safeName);
                    Toast.makeText(MainActivity.this, "PPT saved to Downloads: " + safeName, Toast.LENGTH_LONG).show();
                } catch (Exception err) {
                    Toast.makeText(MainActivity.this, "PPT download failed: " + err.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void saveAndSharePpt(String base64Data, String fileName) {
            runOnUiThread(() -> {
                try {
                    String safeName = sanitizeFileName(fileName);
                    byte[] bytes = Base64.decode(base64Data.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                    File outDir = new File(getCacheDir(), "ppt_reports");
                    if (!outDir.exists()) outDir.mkdirs();
                    File outFile = new File(outDir, safeName);
                    try (FileOutputStream stream = new FileOutputStream(outFile)) {
                        stream.write(bytes);
                    }
                    Uri uri = FileProvider.getUriForFile(
                        MainActivity.this,
                        getPackageName() + ".fileprovider",
                        outFile
                    );
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.putExtra(Intent.EXTRA_SUBJECT, safeName);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Share PPT Report"));
                } catch (Exception err) {
                    Toast.makeText(MainActivity.this, "PPT export failed: " + err.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void saveBytesToDownloads(byte[] bytes, String fileName) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            values.put(MediaStore.Downloads.IS_PENDING, 1);
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new Exception("Unable to create Downloads file.");
            try (OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null) throw new Exception("Unable to open Downloads file.");
                stream.write(bytes);
            }
            values.clear();
            values.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(uri, values, null, null);
            return;
        }
        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloads.exists() && !downloads.mkdirs()) throw new Exception("Unable to access Downloads folder.");
        File outFile = new File(downloads, fileName);
        try (FileOutputStream stream = new FileOutputStream(outFile)) {
            stream.write(bytes);
        }
    }

    private String sanitizeFileName(String fileName) {
        String safe = fileName == null ? "Apexiz_Weld_Report.pptx" : fileName.replaceAll("[\\\\/:*?\"<>|]+", "_").trim();
        if (safe.isEmpty()) safe = "Apexiz_Weld_Report.pptx";
        if (!safe.toLowerCase().endsWith(".pptx")) safe += ".pptx";
        return safe;
    }
}

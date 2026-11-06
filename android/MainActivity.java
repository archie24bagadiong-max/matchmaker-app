package com.badminton.matchmaker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    static final int REQ_PICK = 42, REQ_SAVE = 43;
    public ValueCallback<Uri[]> cb;
    public WebView wv;
    public byte[] pending;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        try { getWindow().setStatusBarColor(0xff0f3d2e); } catch (Throwable t) {}
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wv = new WebView(this);
        WebSettings s = wv.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        wv.setWebViewClient(new WebViewClient());
        wv.setWebChromeClient(new Chrome(this));
        wv.addJavascriptInterface(new Bridge(this), "AndroidApp");
        setContentView(wv);
        File f = ensureLocal();
        if (f.exists()) wv.loadUrl("file://" + f.getAbsolutePath());
        else wv.loadUrl("file:///android_asset/index.html");
    }

    /** Copies the bundled page into app storage on first run or after an APK update; keeps downloaded updates otherwise. */
    public File ensureLocal() {
        File f = new File(getFilesDir(), "index.html");
        try {
            boolean copy = !f.exists();
            if (!copy) {
                PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                copy = pi.lastUpdateTime > f.lastModified();
            }
            if (copy) {
                InputStream in = getAssets().open("index.html");
                FileOutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
                out.close();
                in.close();
            }
        } catch (Exception e) { /* fall back to whatever exists */ }
        return f;
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQ_PICK) {
            if (cb != null) {
                cb.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, data));
                cb = null;
            }
            return;
        }
        if (req == REQ_SAVE) {
            byte[] bytes = pending;
            pending = null;
            if (bytes == null || res != RESULT_OK || data == null || data.getData() == null) return;
            try {
                OutputStream os = getContentResolver().openOutputStream(data.getData());
                os.write(bytes);
                os.close();
                Toast.makeText(this, "Report saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Could not save the report", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (wv != null && wv.canGoBack()) wv.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        LocalServer.stopServer();
        super.onDestroy();
    }

    static class Chrome extends WebChromeClient {
        final MainActivity a;
        Chrome(MainActivity a) { this.a = a; }

        @Override
        public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> callback, FileChooserParams p) {
            if (a.cb != null) a.cb.onReceiveValue(null);
            a.cb = callback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            try {
                a.startActivityForResult(i, REQ_PICK);
                return true;
            } catch (Exception e) {
                a.cb = null;
                return false;
            }
        }
    }
}

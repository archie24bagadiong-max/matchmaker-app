package com.badminton.matchmaker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
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
    static final int REQ_PICK = 42, REQ_SAVE = 43, REQ_CAM = 44, REQ_GAL = 45;
    public String photoFor;
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
        if (req == REQ_CAM || req == REQ_GAL) {
            String pid = photoFor;
            photoFor = null;
            if (pid == null || res != RESULT_OK || data == null) return;
            try {
                Bitmap bm = null;
                if (req == REQ_CAM) {
                    Bundle ex = data.getExtras();
                    if (ex != null) bm = (Bitmap) ex.get("data");
                    if (bm == null && data.getData() != null) bm = load(data.getData());
                } else if (data.getData() != null) {
                    bm = load(data.getData());
                }
                if (bm == null) { Toast.makeText(this, "No photo received", Toast.LENGTH_SHORT).show(); return; }
                sendPhoto(pid, square(bm, 320));
            } catch (Throwable e) {
                Toast.makeText(this, "Could not use that photo", Toast.LENGTH_SHORT).show();
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

    /** Loads an image from a content Uri, downsampled, and turned upright using its EXIF orientation. */
    Bitmap load(Uri u) throws Exception {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        InputStream in = getContentResolver().openInputStream(u);
        BitmapFactory.decodeStream(in, null, o);
        in.close();
        int sample = 1;
        while (Math.min(o.outWidth, o.outHeight) / (sample * 2) >= 640) sample *= 2;
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = sample;
        in = getContentResolver().openInputStream(u);
        Bitmap bm = BitmapFactory.decodeStream(in, null, o2);
        in.close();
        if (bm == null) return null;
        int rot = 0;
        try {
            in = getContentResolver().openInputStream(u);
            ExifInterface ex = new ExifInterface(in);
            int ori = ex.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            in.close();
            if (ori == ExifInterface.ORIENTATION_ROTATE_90) rot = 90;
            else if (ori == ExifInterface.ORIENTATION_ROTATE_180) rot = 180;
            else if (ori == ExifInterface.ORIENTATION_ROTATE_270) rot = 270;
        } catch (Throwable t) {}
        if (rot != 0) {
            Matrix m = new Matrix();
            m.postRotate(rot);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
        }
        return bm;
    }

    /** Centre-crops to a square and scales to size x size. */
    static Bitmap square(Bitmap bm, int size) {
        int w = bm.getWidth(), h = bm.getHeight(), d = Math.min(w, h);
        Bitmap c = Bitmap.createBitmap(bm, (w - d) / 2, (h - d) / 2, d, d);
        int out = Math.min(size, d);
        return Bitmap.createScaledBitmap(c, out, out, true);
    }

    void sendPhoto(String pid, Bitmap bm) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 82, bo);
        String url = "data:image/jpeg;base64," + Base64.encodeToString(bo.toByteArray(), Base64.NO_WRAP);
        wv.evaluateJavascript("window.onNativePhoto&&window.onNativePhoto(" + JSONObject.quote(pid) + "," + JSONObject.quote(url) + ")", null);
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

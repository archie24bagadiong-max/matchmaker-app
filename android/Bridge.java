package com.badminton.matchmaker;

import android.content.Intent;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

/** Methods the page can call as window.AndroidApp.* */
public class Bridge {
    final MainActivity a;
    Bridge(MainActivity a) { this.a = a; }

    @JavascriptInterface public int version() { return 6; }

    @JavascriptInterface
    public boolean saveUpdate(String html) {
        try {
            File dir = a.getFilesDir();
            File tmp = new File(dir, "index.tmp");
            FileOutputStream out = new FileOutputStream(tmp);
            out.write(html.getBytes("UTF-8"));
            out.close();
            return tmp.renameTo(new File(dir, "index.html"));
        } catch (Exception e) { return false; }
    }

    @JavascriptInterface
    public void saveFile(String base64, String name) {
        try {
            a.pending = Base64.decode(base64, Base64.DEFAULT);
            final Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            i.putExtra(Intent.EXTRA_TITLE, name);
            a.runOnUiThread(new Runnable() {
                public void run() {
                    try { a.startActivityForResult(i, MainActivity.REQ_SAVE); } catch (Exception e) {}
                }
            });
        } catch (Exception e) {}
    }

    /* ---- player photos ---- */
    @JavascriptInterface
    public void takePhoto(final String pid) {
        a.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    a.photoFor = pid;
                    a.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), MainActivity.REQ_CAM);
                } catch (Exception e) {
                    a.photoFor = null;
                    android.widget.Toast.makeText(a, "No camera app found", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @JavascriptInterface
    public void choosePhoto(final String pid) {
        a.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    a.photoFor = pid;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    a.startActivityForResult(i, MainActivity.REQ_GAL);
                } catch (Exception e) { a.photoFor = null; }
            }
        });
    }

    @JavascriptInterface public void setPhotos(String json) { LocalServer.setPhotos(json); }

    /* ---- local network sharing (Main tablet) ---- */
    @JavascriptInterface public boolean startServer() { return LocalServer.startServer(); }
    @JavascriptInterface public void stopServer() { LocalServer.stopServer(); }
    @JavascriptInterface public boolean serverRunning() { return LocalServer.running(); }
    @JavascriptInterface public int serverPort() { return LocalServer.PORT; }
    @JavascriptInterface public void setShared(String json) { LocalServer.setState(json); }
    @JavascriptInterface public int clientCount() { return LocalServer.recentClients(); }
    @JavascriptInterface public String takeIncoming() { return LocalServer.takeIncoming(); }

    /** IPv4 addresses of this tablet on Wi-Fi / hotspot, comma separated. */
    @JavascriptInterface
    public String localIp() {
        StringBuilder sb = new StringBuilder();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback()) continue;
                for (InetAddress ad : Collections.list(ni.getInetAddresses())) {
                    if (ad instanceof Inet4Address && !ad.isLoopbackAddress()) {
                        String ip = ad.getHostAddress();
                        if (ip.startsWith("169.254.")) continue;
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(ip);
                    }
                }
            }
        } catch (Exception e) {}
        return sb.toString();
    }
}

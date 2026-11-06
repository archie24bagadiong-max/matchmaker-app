package com.badminton.matchmaker;

import android.content.Intent;
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

    @JavascriptInterface public int version() { return 4; }

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

    /* ---- local network sharing (Main tablet) ---- */
    @JavascriptInterface public boolean startServer() { return LocalServer.startServer(); }
    @JavascriptInterface public void stopServer() { LocalServer.stopServer(); }
    @JavascriptInterface public boolean serverRunning() { return LocalServer.running(); }
    @JavascriptInterface public int serverPort() { return LocalServer.PORT; }
    @JavascriptInterface public void setShared(String json) { LocalServer.setState(json); }
    @JavascriptInterface public int clientCount() { return LocalServer.recentClients(); }

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

package com.badminton.matchmaker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Tiny HTTP server: GET /state returns the Main tablet's latest data as JSON. */
public class LocalServer extends Thread {
    public static final int PORT = 8787;
    private static LocalServer instance;
    private static volatile byte[] state = "{}".getBytes();
    private static volatile long rev = 0;
    private static volatile byte[] photos = "{}".getBytes();
    private static final Map<String, Long> clients = new HashMap<String, Long>();
    private static final ConcurrentLinkedQueue<String> incoming = new ConcurrentLinkedQueue<String>();

    /** Next score update sent by a court tablet, or "" when none. */
    public static String takeIncoming() { String x = incoming.poll(); return x == null ? "" : x; }
    private volatile boolean stopped = false;
    private ServerSocket ss;

    public static synchronized boolean startServer() {
        if (instance != null && instance.isAlive()) return true;
        try {
            LocalServer s = new LocalServer();
            s.ss = new ServerSocket();
            s.ss.setReuseAddress(true);
            s.ss.bind(new InetSocketAddress(PORT));
            s.setDaemon(true);
            s.start();
            instance = s;
            return true;
        } catch (Exception e) { return false; }
    }

    public static synchronized void stopServer() {
        if (instance != null) {
            instance.stopped = true;
            try { instance.ss.close(); } catch (Exception e) {}
            instance = null;
        }
    }

    public static synchronized boolean running() { return instance != null && instance.isAlive(); }

    public static void setState(String json) {
        try { state = json.getBytes("UTF-8"); rev++; } catch (Exception e) {}
    }

    public static void setPhotos(String json) {
        try { photos = json.getBytes("UTF-8"); } catch (Exception e) {}
    }

    public static int recentClients() {
        long now = System.currentTimeMillis();
        int n = 0;
        synchronized (clients) {
            for (Long t : clients.values()) if (now - t < 10000) n++;
        }
        return n;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                final Socket c = ss.accept();
                new Thread(new Runnable() { public void run() { handle(c); } }).start();
            } catch (Exception e) {
                if (stopped) break;
            }
        }
    }

    private static void handle(Socket c) {
        try {
            c.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
            String line = in.readLine();
            if (line == null) { c.close(); return; }
            String l;
            int len = 0;
            while ((l = in.readLine()) != null && l.length() > 0) {
                if (l.toLowerCase().startsWith("content-length:")) {
                    try { len = Integer.parseInt(l.substring(15).trim()); } catch (Exception e) {}
                }
            }
            synchronized (clients) { clients.put(c.getInetAddress().getHostAddress(), System.currentTimeMillis()); }
            OutputStream out = c.getOutputStream();
            String head = "Access-Control-Allow-Origin: *\r\nAccess-Control-Allow-Headers: *\r\nCache-Control: no-store\r\nConnection: close\r\n";
            if (line.startsWith("OPTIONS")) {
                out.write(("HTTP/1.1 204 No Content\r\n" + head + "\r\n").getBytes("UTF-8"));
            } else if (line.startsWith("GET /state")) {
                byte[] body = state;
                out.write(("HTTP/1.1 200 OK\r\n" + head + "Content-Type: application/json; charset=utf-8\r\nX-Rev: " + rev
                        + "\r\nContent-Length: " + body.length + "\r\n\r\n").getBytes("UTF-8"));
                out.write(body);
            } else if (line.startsWith("POST /score")) {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[4096];
                while (sb.length() < len && len < 2000000) {
                    int n = in.read(buf, 0, Math.min(buf.length, len - sb.length()));
                    if (n <= 0) break;
                    sb.append(buf, 0, n);
                }
                if (sb.length() > 0) incoming.add(sb.toString());
                byte[] body = "{\"ok\":true}".getBytes("UTF-8");
                out.write(("HTTP/1.1 200 OK\r\n" + head + "Content-Type: application/json\r\nContent-Length: " + body.length + "\r\n\r\n").getBytes("UTF-8"));
                out.write(body);
            } else if (line.startsWith("GET /photos")) {
                byte[] body = photos;
                out.write(("HTTP/1.1 200 OK\r\n" + head + "Content-Type: application/json; charset=utf-8\r\nContent-Length: " + body.length + "\r\n\r\n").getBytes("UTF-8"));
                out.write(body);
            } else if (line.startsWith("GET /ping")) {
                byte[] body = "{\"app\":\"matchmaker\"}".getBytes("UTF-8");
                out.write(("HTTP/1.1 200 OK\r\n" + head + "Content-Type: application/json\r\nContent-Length: " + body.length + "\r\n\r\n").getBytes("UTF-8"));
                out.write(body);
            } else {
                out.write(("HTTP/1.1 404 Not Found\r\n" + head + "Content-Length: 0\r\n\r\n").getBytes("UTF-8"));
            }
            out.flush();
            c.close();
        } catch (Exception e) {
            try { c.close(); } catch (Exception x) {}
        }
    }
}

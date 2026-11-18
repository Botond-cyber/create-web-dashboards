package com.lixyn.webdashboards.net;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lixyn.webdashboards.CreateWebDashboards;
import com.lixyn.webdashboards.core.DisplayUpdate;

/**
 * Thin WebSocket glue. Runs on its own threads (Java-WebSocket's selector + worker threads),
 * so nothing here blocks the Minecraft server thread. {@link #publish} only queues data.
 *
 * Protocol (JSON text frames, server -> client):
 *   {"type":"snapshot","displays":[ {pos,dimension,line,text,time}, ... ]}   on connect
 *   {"type":"display", "pos":..., "dimension":..., "line":..., "text":..., "time":...}  on every change
 */
public class WebDashServer extends WebSocketServer {
    private static final Gson GSON = new Gson();

    /** Latest value per display line, so new clients get the current state immediately. */
    private final Map<String, DisplayUpdate> latest = new ConcurrentHashMap<>();

    public WebDashServer(InetSocketAddress address) {
        super(address);
        setReuseAddr(true);
    }

    /** Called from the Minecraft thread. Cheap: updates a map and queues the send. */
    public void publish(DisplayUpdate update) {
        latest.put(update.key(), update);
        Collection<WebSocket> clients = getConnections();
        if (!clients.isEmpty())
            broadcast(toJson("display", update), clients);
    }

    private static String toJson(String type, DisplayUpdate u) {
        JsonObject o = GSON.toJsonTree(u).getAsJsonObject();
        o.addProperty("type", type);
        return GSON.toJson(o);
    }

    @Override
    public void onStart() {
        CreateWebDashboards.LOGGER.info("WebSocket server listening on {}", getAddress());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        JsonObject snapshot = new JsonObject();
        snapshot.addProperty("type", "snapshot");
        snapshot.add("displays", GSON.toJsonTree(latest.values()));
        conn.send(GSON.toJson(snapshot));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Read-only feed for now; client messages are ignored.
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        CreateWebDashboards.LOGGER.error("WebSocket error{}", conn == null ? " (server)" : "", ex);
    }
}

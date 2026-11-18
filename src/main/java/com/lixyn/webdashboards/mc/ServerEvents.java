package com.lixyn.webdashboards.mc;

import java.net.InetSocketAddress;

import com.lixyn.webdashboards.CreateWebDashboards;
import com.lixyn.webdashboards.core.DisplayUpdate;
import com.lixyn.webdashboards.net.WebDashServer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** Starts/stops the WebSocket server with the Minecraft server and exposes it to the rest of the mod. */
@EventBusSubscriber(modid = CreateWebDashboards.MODID)
public final class ServerEvents {
    /** Localhost only: there is no authentication yet. Use "0.0.0.0" to expose it. */
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8765;

    private static volatile WebDashServer server;

    private ServerEvents() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        WebDashServer s = new WebDashServer(new InetSocketAddress(HOST, PORT));
        server = s;
        s.start(); // spawns its own thread and returns immediately
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        WebDashServer s = server;
        server = null;
        if (s == null)
            return;
        try {
            s.stop(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Safe to call from the Minecraft thread; does nothing when the server isn't running. */
    public static void publish(DisplayUpdate update) {
        WebDashServer s = server;
        if (s != null)
            s.publish(update);
    }
}

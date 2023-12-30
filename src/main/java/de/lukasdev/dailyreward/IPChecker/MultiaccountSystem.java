//©️Lukas Pellny | 2023
package de.lukasdev.dailyreward.IPChecker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class MultiaccountSystem implements Listener {

    private final Map<String, Integer> ipConnections = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String ipAddress = event.getPlayer().getAddress().getHostString();
        if (!canConnect(ipAddress)) {
            event.getPlayer().kickPlayer("Du darfst dich nicht mit mehreren Accounts verbinden.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String ipAddress = event.getPlayer().getAddress().getHostString();
        playerDisconnected(ipAddress);
    }

    private boolean canConnect(String ipAddress) {
        int maxConnections = 1; // Maximale Anzahl der Verbindungen pro IP

        if (ipConnections.containsKey(ipAddress)) {
            int currentConnections = ipConnections.get(ipAddress);

            if (currentConnections < maxConnections) {
                ipConnections.put(ipAddress, currentConnections + 1);
                return true;
            } else {
                return false;
            }
        } else {
            ipConnections.put(ipAddress, 1);
            return true;
        }
    }

    private void playerDisconnected(String ipAddress) {
        if (ipConnections.containsKey(ipAddress)) {
            int currentConnections = ipConnections.get(ipAddress);

            if (currentConnections > 0) {
                ipConnections.put(ipAddress, currentConnections - 1);
            }
        }
    }
}